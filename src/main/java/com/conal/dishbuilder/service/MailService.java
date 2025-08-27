package com.conal.dishbuilder.service;

import java.io.File;

public interface MailService {
    boolean sendMail(String to, String subject, String body);

    boolean sendHtmlMail(String to, String subject, String htmlBody);

    boolean sendMailWithCc(String to, String cc, String subject, String body);

    boolean sendMailWithAttachment(String to, String subject, String body, File attachment);
}
