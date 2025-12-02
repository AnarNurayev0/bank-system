package bank.bank.controller.impl;

import jakarta.validation.Valid;
import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;
import bank.bank.service.ICustomerService;
import bank.bank.controller.ICustomerController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/customer")
public class CustomerControllerImpl implements ICustomerController {

    @Autowired
    private ICustomerService customerService;

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
