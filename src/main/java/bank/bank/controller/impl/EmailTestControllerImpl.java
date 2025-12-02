package bank.bank.controller.impl;

import bank.bank.controller.IEmailTestController;
import bank.bank.service.impl.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class EmailTestControllerImpl implements IEmailTestController {

    private final EmailServiceImpl emailServiceImpl;

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
