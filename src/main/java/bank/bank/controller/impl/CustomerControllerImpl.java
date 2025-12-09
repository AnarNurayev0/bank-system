package bank.bank.controller.impl;

import jakarta.validation.Valid;
import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;
import lombok.RequiredArgsConstructor;
import bank.bank.service.ICustomerService;
import bank.bank.controller.ICustomerController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerControllerImpl implements ICustomerController {

    private final ICustomerService customerService;

    @Override
    @PostMapping("/create")
    public DtoCustomer createCustomer(@Valid @RequestBody DtoCustomerIU dtoCustomerIU) {
        return customerService.createCustomer(dtoCustomerIU);
    }

    @Override
    @GetMapping("/{id}")
    public DtoCustomer getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @Override
    @PostMapping("/login")
    public DtoCustomer login(@Valid @RequestBody bank.bank.dto.DtoLoginRequest request) {
        return customerService.login(request);
    }

    @Override
    @PostMapping("/register/send-otp")
    public String sendRegistrationOTP(@Valid @RequestBody bank.bank.dto.DtoSendOTP request) {
        return customerService.sendRegistrationOTP(request.getEmail());
    }

    @Override
    @PostMapping("/register/verify-otp")
    public String verifyRegistrationOTP(@Valid @RequestBody bank.bank.dto.DtoVerifyOTP request) {
        return customerService.verifyRegistrationOTP(request.getEmail(), request.getCode());
    }

}
