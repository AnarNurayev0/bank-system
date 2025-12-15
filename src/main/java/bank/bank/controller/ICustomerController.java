package bank.bank.controller;

import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;

public interface ICustomerController {

    public DtoCustomer createCustomer(DtoCustomerIU dtoCustomerIU);

    public DtoCustomer getCustomerById(Long id);

    public DtoCustomer login(bank.bank.dto.DtoLoginRequest request);

    public String sendRegistrationOTP(bank.bank.dto.DtoSendOTP request);

    public String verifyRegistrationOTP(bank.bank.dto.DtoVerifyOTP request);

}
