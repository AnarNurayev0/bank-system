package bank.bank.service;

public interface IEmailService {

    void send(String to, String subject, String text);

    void send(String to, String subject, String text, byte[] attachmentData, String attachmentName);

}
