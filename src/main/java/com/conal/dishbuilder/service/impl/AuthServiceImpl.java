package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.Actions;
import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.constant.ErrorType;
import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.context.UserContextHolder;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;
import com.conal.dishbuilder.dto.response.SendOTPResponse;
import com.conal.dishbuilder.exception.AttemptExceededException;
import com.conal.dishbuilder.exception.BadRequestException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.service.AuthService;
import com.conal.dishbuilder.service.MailService;
import com.conal.dishbuilder.service.RateLimitService;
import com.conal.dishbuilder.util.CommonUtils;
import com.conal.dishbuilder.util.FileUtils;
import com.conal.dishbuilder.util.JwtUtils;
import com.conal.dishbuilder.util.RedisUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        var authenticate = authenticationManager.authenticate(token);

        if (!authenticate.isAuthenticated()) {
            throw new BadRequestException("Invalid username or password");
        }
        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
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
        return false;
    }

    @Override
    public boolean validateOtp(String otp) {
        String username = UserContextHolder.getUserContext();
        String key = redisUtils.genKey(Actions.FORGOT_PASSWORD.name(), username);
        String savedOtp = redisUtils.get(key);
        // Do not need to check the expriry time here since the redis will remove the otp after the default time
        if (StringUtils.isNoneBlank(savedOtp) && !savedOtp.equals(otp)) {
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
}
