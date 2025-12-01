package bank.bank.service;

import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;
import org.springframework.web.bind.annotation.RequestBody;

public interface ICustomerService {

    public DtoCustomer createCustomer(@RequestBody DtoCustomerIU dtoCustomerIU);

    public DtoCustomer getCustomerById(Long id);


}
