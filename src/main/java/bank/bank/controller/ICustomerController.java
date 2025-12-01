package bank.bank.controller;

import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ICustomerController {

    public DtoCustomer createCustomer(@RequestBody DtoCustomerIU dtoCustomerIU);

    public DtoCustomer getCustomerById(@PathVariable Long id);



}
