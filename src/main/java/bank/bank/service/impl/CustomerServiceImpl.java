package bank.bank.service.impl;

import bank.bank.dto.DtoCard;
import bank.bank.dto.DtoCustomer;
import bank.bank.dto.DtoCustomerIU;
import bank.bank.entity.Customer;
import bank.bank.repository.CustomerRepository;
import bank.bank.service.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public DtoCustomer createCustomer(DtoCustomerIU dto) {

        int age = Period.between(
                dto.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                LocalDate.now()
        ).getYears();

        if (age < 18) {
            throw new RuntimeException("18 yaşdan aşağı qeydiyyata icazə verilmir");
        }

        Customer c = new Customer();
        c.setFullName(dto.getFullName());
        c.setEmail(dto.getEmail());
        c.setEmailPassword(dto.getEmailPassword());
        c.setTelephone(dto.getTelephone());
        c.setBirthDate(dto.getBirthDate());

        Customer saved = customerRepository.save(c);

        DtoCustomer out = new DtoCustomer();
        out.setFullName(saved.getFullName());
        out.setEmail(saved.getEmail());
        out.setTelephone(saved.getTelephone());
        out.setBirthDate(saved.getBirthDate());

        return out;
    }

    @Override
    public DtoCustomer getCustomerById(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer tapılmadı"));

        DtoCustomer out = new DtoCustomer();
        out.setFullName(c.getFullName());
        out.setEmail(c.getEmail());
        out.setTelephone(c.getTelephone());
        out.setBirthDate(c.getBirthDate());

        List<DtoCard> dtoCards = c.getCards().stream().map(card -> {
            DtoCard dto = new DtoCard();
            dto.setCardBrand(card.getCardBrand());
            dto.setCardType(card.getCardType());
            dto.setCurrency(card.getCurrency());
            dto.setCardNumber(card.getCardNumber());
            dto.setExpirationDate(card.getExpirationDate());
            dto.setBalance(card.getBalance());
            return dto;
        }).toList();

        out.setCards(dtoCards);

        return out;
    }
}
