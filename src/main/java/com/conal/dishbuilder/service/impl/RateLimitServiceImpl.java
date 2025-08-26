package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.ErrorType;
import com.conal.dishbuilder.dto.response.SendOTPResponse;
import com.conal.dishbuilder.service.RateLimitService;
import com.conal.dishbuilder.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.conal.dishbuilder.constant.Constants.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {
    private final RedisUtils redisUtils;
    private final long RETRY_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30); // 30s
    private final long ONE_HOUR_MILLIS = TimeUnit.HOURS.toMillis(1);


    public SendOTPResponse canSendOtp(String... params) {
        String key = redisUtils.genKey(params);
        long now = System.currentTimeMillis();

        log.info("[OTP][RateLimit] Checking if OTP can be sent. Key: {}", key);

        List<String> timestamps = redisUtils.getRange(key);
        List<Long> validTimestamps = timestamps.stream()
                .map(Long::parseLong)
                .filter(ts -> now - ts <= ONE_HOUR_MILLIS)
                .sorted()
                .toList();

        log.info("[OTP][RateLimit] Found {} valid timestamps in the past hour for key {}", validTimestamps.size(), key);

        // Check cooldown 30s between OTP sends
        if (!validTimestamps.isEmpty() &&
                (now - validTimestamps.get(validTimestamps.size() - 1) < RETRY_DELAY_MILLIS)) {
            log.warn("[OTP][RateLimit] Sending OTP too quickly. Last sent {}ms ago", now - validTimestamps.get(validTimestamps.size() - 1));
            return SendOTPResponse.newBuilder()
                    .setIsCanSend(false)
                    .setErrorType(ErrorType.TOO_FAST)
                    .build();
        }

        if (validTimestamps.size() >= MAX_RETRIES) {
            long oldest = validTimestamps.get(0);
            long allowedTimeMillis = oldest + ONE_HOUR_MILLIS;
            LocalDateTime allowedTime = Instant.ofEpochMilli(allowedTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            String formattedTime = allowedTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            log.warn("[OTP][RateLimit] Maximum OTP limit reached. Next allowed time: {}", formattedTime);
            return SendOTPResponse.newBuilder()
                    .setIsCanSend(false)
                    .setErrorType(ErrorType.TOO_MANY_RETRIES)
                    .build();
        }

        log.info("[OTP][RateLimit] Allowed to send OTP.");
        return SendOTPResponse.newBuilder()
                .setIsCanSend(true).build();
    }


    @Override
    public void logSuccessfullySent(String... params) {
        String key = redisUtils.genKey(params);
        long now = System.currentTimeMillis();
        log.info("[OTP][RateLimit] Logging successful OTP send. Key: {}, Timestamp: {}", key, now);

        Long size = redisUtils.rightPush(key,String.valueOf(now));
        if (size != null && size == 1) {
            redisUtils.expire(key, 1, TimeUnit.HOURS);
            log.info("[OTP][RateLimit] Set expiration to 1 hour for key: {}", key);
        }
    }
}
