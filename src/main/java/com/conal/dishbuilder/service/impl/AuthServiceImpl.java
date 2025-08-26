package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.Actions;
import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.constant.ErrorType;
import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.context.UserContextHolder;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;
import com.conal.dishbuilder.dto.request.UpdatePasswordRequest;
import com.conal.dishbuilder.dto.response.SendOTPResponse;
import com.conal.dishbuilder.exception.AttemptExceededException;
import com.conal.dishbuilder.exception.BadRequestException;
import com.conal.dishbuilder.exception.IllegalAccessException;
import com.conal.dishbuilder.exception.InternalServerException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.service.AuthService;
import com.conal.dishbuilder.service.MailService;
import com.conal.dishbuilder.service.RateLimitService;
import com.conal.dishbuilder.util.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.conal.dishbuilder.constant.Constants.OTP_LENGTH;
import static com.conal.dishbuilder.constant.Constants.User.TOO_FAST;
import static com.conal.dishbuilder.constant.Constants.User.TOO_MANY_REQUESTS;


@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final RateLimitService rateLimitService;
    private final RedisUtils redisUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        UserEntity user = userRepository.findByUsernameAndTenantId(request.getUsername(), tenantId)
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));
        var token = new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword());

        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

            String accessToken = jwtUtils.generateAccessToken(userDetails);
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid username or password");
        }

    }

    @Override
    public boolean forgotPassword(String username) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        UserEntity user = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        SendOTPResponse otpResponse = rateLimitService.canSendOtp(Actions.FORGOT_PASSWORD.name(), user.getUsername());

        if (!otpResponse.isCanSend()) {
            throw new AttemptExceededException(otpResponse.getErrorType().equals(ErrorType.TOO_FAST) ? TOO_FAST : TOO_MANY_REQUESTS);
        }
        String otpCode = CommonUtils.genOTP(OTP_LENGTH);

        String htmlContent = FileUtils.loadTemplate(otpCode, Constants.FORGOT_PASSWORD_OTP_TEMPLATE_FILE_PATH);
        boolean isSent = mailService.sendMail(user.getEmail(), Constants.SUBJECT, htmlContent);
        if (isSent) {
            //save the otp to validate
            redisUtils.set(redisUtils.genKey(Actions.FORGOT_PASSWORD.name(), user.getUsername()), otpCode);
            rateLimitService.logSuccessfullySent(Actions.FORGOT_PASSWORD.name(), user.getUsername());
        }
        return true;
    }

    @Override
    public boolean validateOtp(String otp) {
        String username = UserContextHolder.getUserContext();
        String key = redisUtils.genKey(Actions.FORGOT_PASSWORD.name(), username);
        String savedOtp = redisUtils.get(key);
        // Do not need to check the expriry time here since the redis will remove the otp after the default time
        if (StringUtils.isNoneBlank(savedOtp) || !otp.equals(savedOtp)) {
            throw new BadRequestException("Invalid otp");
        }
        // save the verify user
        String validatedKey = redisUtils.genKey(Actions.VALIDATE_OTP.name(), username);
        redisUtils.set(validatedKey, "true");
        return true;
    }

    @Override
    public boolean logout(String refreshToken) {

        return true;
    }

    @Override
    public boolean updatePassword(UpdatePasswordRequest request) {
        String username = UserContextHolder.getUserContext();
        UUID tenantId = TenantContextHolder.getTenantContext();
        String key = redisUtils.genKey(Actions.VALIDATE_OTP.name(), username);
        String isValidated = redisUtils.get(key);
        if (StringUtils.isBlank(isValidated) || isValidated.equals("false")) {
            throw new IllegalAccessException("Verify the  otp first. Before updating new password.");
        }
        UserEntity user = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new NotFoundException("Current user not found."));
        user.setPassword(PasswordUtils.hashPassword(request.getPassword().trim()));
        try {
            userRepository.save(user);
            String otpKey = redisUtils.genKey(Actions.FORGOT_PASSWORD.name(), username);
            if(redisUtils.remove(key) && redisUtils.remove(otpKey)){
                log.info("Remove key: {}",key);
            }else{
                log.error("Remove key error");
            }
        } catch (Exception e) {
            log.error("Error while saving user: {}", e.getMessage());
            throw new InternalServerException("Error while updating password");
        }
        return true;
    }
}
