package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public boolean sendMail(String to, String subject, String body) {
        log.info("Sending plain text mail to: {}", to);
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            msg.setFrom("conal@gmail.com");

            mailSender.send(msg);
            log.info("Plain text email sent to {}", to);
            return true;
        } catch (MailException e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendHtmlMail(String to, String subject, String htmlBody) {
        log.info("Sending HTML mail to: {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true = multipart

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = isHtml
            helper.setFrom("conal@gmail.com");

            mailSender.send(message);
            log.info("HTML email sent to {}", to);
            return true;
        } catch (MessagingException | MailException e) {
            log.error("Failed to send HTML mail to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendMailWithCc(String to, String cc, String subject, String body) {
        log.info("Sending mail to: {} with CC: {}", to, cc);
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setCc(cc);
            msg.setSubject(subject);
            msg.setText(body);
            msg.setFrom("conal@gmail.com");

            mailSender.send(msg);
            log.info("Mail with CC sent to {} (CC: {})", to, cc);
            return true;
        } catch (MailException e) {
            log.error("Failed to send mail with CC to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendMailWithAttachment(String to, String subject, String body, File attachment) {
        log.info("Sending mail with attachment to: {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // plain text
            helper.setFrom("conal@gmail.com");

            FileSystemResource file = new FileSystemResource(attachment);
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            log.info("Email with attachment sent to {}", to);
            return true;
        } catch (MessagingException | MailException e) {
            log.error("Failed to send mail with attachment to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
}
