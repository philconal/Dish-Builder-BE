package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    @Override
    public boolean sendMail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        msg.setFrom("conal@gmail.com");
        mailSender.send(msg);
        return true;
    }
}
