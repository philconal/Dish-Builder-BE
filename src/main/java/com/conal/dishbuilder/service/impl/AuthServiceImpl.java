package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.*;
import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.context.UserContextHolder;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;
import com.conal.dishbuilder.dto.request.UpdatePasswordRequest;
import com.conal.dishbuilder.dto.response.SendOTPResponse;
import com.conal.dishbuilder.exception.*;
import com.conal.dishbuilder.exception.IllegalAccessException;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.service.AuthService;
import com.conal.dishbuilder.service.MailService;
import com.conal.dishbuilder.service.RateLimitService;
import com.conal.dishbuilder.util.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        log.info("Login attempt for username: {} in tenant: {}", request.getUsername(), tenantId);

        UserEntity user = userRepository.findByUsernameAndTenantIdAndStatus(request.getUsername(), tenantId, CommonStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Login failed - User not found for username: {}", request.getUsername());
                    return new BadRequestException("Invalid username or password");
                });

        var token = new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword());

        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

            String accessToken = jwtUtils.generateAccessToken(userDetails);
            String refreshToken = jwtUtils.generateRefreshToken(userDetails);
            log.info("Login successful for user: {}", user.getUsername());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .roles(user.getRoles().stream().map(r -> r.getName()).toList())
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for username: {}", request.getUsername());
            throw new BadRequestException("Invalid username or password");
        }
    }

    @Override
    public String forgotPassword(String username) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        log.info("Forgot password requested for username: {}", username);

        UserEntity user = userRepository.findByUsernameAndTenantIdAndStatus(username, tenantId, CommonStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Forgot password - User not found: {}", username);
                    return new NotFoundException("User not found");
                });

        SendOTPResponse otpResponse = rateLimitService.canSendOtp(Actions.FORGOT_PASSWORD.name(), username);
        if (!otpResponse.isCanSend()) {
            String reason = otpResponse.getErrorType().equals(ErrorType.TOO_FAST) ? TOO_FAST : TOO_MANY_REQUESTS;
            log.warn("OTP send blocked for {} due to rate limiting: {}", username, reason);
            throw new AttemptExceededException(reason);
        }

        String otp = CommonUtils.genOTP(OTP_LENGTH);
        log.debug("Generated OTP for user: {} is {}", username, otp); // Consider removing in production

        String htmlContent = FileUtils.loadTemplate(otp, Constants.FORGOT_PASSWORD_OTP_TEMPLATE_FILE_PATH);

        if (!mailService.sendMail(user.getEmail(), Constants.SUBJECT, htmlContent)) {
            log.error("Failed to send OTP email to user: {}", username);
            throw new InternalServerException("Failed to send OTP");
        }

        String otpKey = redisUtils.genKey(Actions.FORGOT_PASSWORD.name(), username);
        String sessionId = UUID.randomUUID().toString();
        String sessionKey = redisUtils.genKey(Actions.VALIDATE_OTP.name(), sessionId);

        redisUtils.set(otpKey, otp, Constants.EXPIRY_TIME);
        redisUtils.set(sessionKey, "false", Constants.EXPIRY_TIME);

        rateLimitService.logSuccessfullySent(Actions.FORGOT_PASSWORD.name(), username);
        log.info("OTP sent and session created for user: {}, sessionId: {}", username, sessionId);

        return sessionId;
    }

    @Override
    public boolean validateOtp(String sessionId, String otp) {
        String username = UserContextHolder.getUserContext();
        log.info("Validating OTP for user: {} with sessionId: {}", username, sessionId);

        String otpKey = redisUtils.genKey(Actions.FORGOT_PASSWORD.name(), username);
        String savedOtp = redisUtils.get(otpKey);

        if (StringUtils.isBlank(savedOtp)) {
            log.warn("No OTP found for user: {}", username);
            throw new BadRequestException("OTP has expired or not requested");
        }

        if (!savedOtp.equals(otp)) {
            log.warn("Incorrect OTP entered for user: {}", username);
            throw new BadRequestException("Invalid OTP");
        }

        String sessionKey = redisUtils.genKey(Actions.VALIDATE_OTP.name(), sessionId);
        redisUtils.set(sessionKey, "true", Constants.EXPIRY_TIME);
        redisUtils.remove(otpKey);

        log.info("OTP successfully validated for user: {}, sessionId: {}", username, sessionId);
        return true;
    }

    @Override
    public void logout(String refreshToken, String accessToken) {
        if (StringUtils.isNotBlank(refreshToken) && jwtUtils.validateToken(refreshToken)
                && StringUtils.isNotBlank(accessToken) && jwtUtils.validateToken(accessToken)) {
            jwtUtils.addTokenIntoBlacklist(accessToken, TokenType.ACCESS);
            jwtUtils.addTokenIntoBlacklist(refreshToken, TokenType.REFRESH);
        }
    }

    @Override
    public boolean updatePassword(UpdatePasswordRequest request) {
        String username = UserContextHolder.getUserContext();
        UUID tenantId = TenantContextHolder.getTenantContext();
        String sessionKey = redisUtils.genKey(Actions.VALIDATE_OTP.name(), request.getSessionId());

        log.info("Password update requested by user: {} with sessionId: {}", username, request.getSessionId());

        String isValidated = redisUtils.get(sessionKey);
        if (!"true".equals(isValidated)) {
            log.warn("Password update denied for user: {} - OTP not validated or session expired", username);
            throw new IllegalAccessException("OTP validation required");
        }

        UserEntity user = userRepository.findByUsernameAndTenantIdAndStatus(username, tenantId, CommonStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("User not found during password update: {}", username);
                    return new NotFoundException("User not found");
                });

        user.setPassword(PasswordUtils.hashPassword(request.getPassword().trim()));

        try {
            userRepository.save(user);
            redisUtils.remove(sessionKey);
            log.info("Password updated successfully for user: {}", username);
            return true;
        } catch (Exception e) {
            log.error("Password update failed for user: {} - {}", username, e.getMessage(), e);
            throw new InternalServerException("Failed to update password");
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken, String accessToken) {
        if (StringUtils.isBlank(refreshToken) || !jwtUtils.validateToken(refreshToken)) {
            throw new ForbiddenException("Invalid refresh token");
        }

        Boolean isBlacklisted = jwtUtils.existsTokenInBlacklist(refreshToken, TokenType.REFRESH);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new ForbiddenException("Refresh token is blacklisted");
        }

        String username = jwtUtils.getUsername(refreshToken);

        UUID tenantId = TenantContextHolder.getTenantContext();

        UserEntity user = userRepository.findByUsernameAndTenantIdAndStatus(username, tenantId, CommonStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Login failed - User not found for username: {}", username);
                    return new BadRequestException("Invalid username or password");
                });

        var token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

            String newAccessToken = jwtUtils.generateAccessToken(userDetails);
            log.info("New access token: {}", newAccessToken);
            jwtUtils.addTokenIntoBlacklist(accessToken, TokenType.ACCESS);
            log.info("Retrieved access token: {}", accessToken);
            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .roles(user.getRoles().stream().map(r -> r.getName()).toList())
                    .build();
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for username: {}", username);
            throw new BadRequestException("Invalid username or password");
        }

    }
}
