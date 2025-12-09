package bank.bank.service;

import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;

public interface ICustomerService {

    DtoCustomer createCustomer(DtoCustomerIU dtoCustomerIU);

    DtoCustomer getCustomerById(Long id);

    bank.bank.dto.DtoCustomer login(bank.bank.dto.DtoLoginRequest request);

    String sendRegistrationOTP(String email);

    String verifyRegistrationOTP(String email, String code);

}
