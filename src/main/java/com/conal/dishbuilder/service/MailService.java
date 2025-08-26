package com.conal.dishbuilder.service;

public interface MailService {
    boolean sendMail(String to, String subject, String body);
}
