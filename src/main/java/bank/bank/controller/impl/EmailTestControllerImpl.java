package bank.bank.controller.impl;

import bank.bank.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class EmailTestControllerImpl {

    @Autowired
    private EmailServiceImpl emailServiceImpl;

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
