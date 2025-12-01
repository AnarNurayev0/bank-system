package bank.bank.service.impl;

import org.springframework.stereotype.Service;

@Service
public interface IEmailService {

    public void send(String to, String subject, String text);

}
