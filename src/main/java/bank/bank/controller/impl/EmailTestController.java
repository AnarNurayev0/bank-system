package bank.bank.controller.impl;

import bank.bank.service.impl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/email")
    public String testEmail() {
        emailService.send(
                "BURAYA_SENIN_EMAILIN@gmail.com",
                "Test Email",
                "Bu test məqsədli göndərilmiş emaildir!"
        );
        return "OK – Email göndərildi!";
    }
}
