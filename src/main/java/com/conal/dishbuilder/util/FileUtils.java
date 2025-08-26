package com.conal.dishbuilder.util;


import com.conal.dishbuilder.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class FileUtils {
    public static String loadTemplate(String otpCode, String filePath) {
        return loadTemplate(filePath).replace("{{OTP_CODE}}", otpCode);
    }

    public static String loadTemplate(String filePath) {
        var resource = new ClassPathResource(filePath);
        try {
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load template file: {}", e.getMessage());
            throw new InternalServerException("Failed to load template file");
        }
    }
}
