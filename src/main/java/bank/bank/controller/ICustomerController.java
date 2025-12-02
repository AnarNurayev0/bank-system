package bank.bank.controller;

import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;

public interface ICustomerController {

    public DtoCustomer createCustomer(DtoCustomerIU dtoCustomerIU);

    public DtoCustomer getCustomerById(Long id);

}
