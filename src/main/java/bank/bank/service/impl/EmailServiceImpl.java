package bank.bank.service.impl;

import lombok.RequiredArgsConstructor;
import bank.bank.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("An error occurred while sending the email", e);
        }
    }

    @Override
    public void send(String to, String subject, String text, byte[] attachmentData, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            if (attachmentData != null && attachmentName != null) {
                helper.addAttachment(attachmentName, new org.springframework.core.io.ByteArrayResource(attachmentData));
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("An error occurred while sending the email (with attachment)", e);
        }
    }
}
