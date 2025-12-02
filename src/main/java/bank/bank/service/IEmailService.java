package bank.bank.service;

public interface IEmailService {

    void send(String to, String subject, String text);

}
