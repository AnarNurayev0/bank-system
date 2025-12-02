package bank.bank.controller.impl;

import bank.bank.controller.ICustomerController;
import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;
import bank.bank.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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


}
