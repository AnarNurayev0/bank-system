package bank.bank.controller.impl;

import bank.bank.service.impl.EmailServiceImpl;
import bank.bank.controller.IEmailTestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/test")
public class EmailTestControllerImpl implements IEmailTestController {

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Override
    @GetMapping("/email")
    public String testEmail() {
        emailServiceImpl.send(
                "anarnurayev32@gmail.com",
                "Test Email",
                "Bu test məqsədli göndərilmiş emaildir!"
        );
        return "OK – Email göndərildi!";
    }
}
