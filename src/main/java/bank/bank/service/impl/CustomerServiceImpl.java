package bank.bank.service.impl;

import java.util.List;
import java.time.Period;
import java.time.LocalDate;
import java.util.Date; // Import Date
import java.util.Calendar; // Import Calendar
import java.math.BigDecimal; // Import BigDecimal
import bank.bank.dto.DtoCard;
import bank.bank.dto.DtoCustomer;
import bank.bank.entity.Card; // Import Card
import bank.bank.entity.Customer;
import bank.bank.dto.DtoCustomerIU;
import lombok.RequiredArgsConstructor;
import bank.bank.service.IEmailService;
import bank.bank.util.EmailTemplateUtil;
import bank.bank.service.ICustomerService;
import org.springframework.stereotype.Service;
import bank.bank.repository.CardRepository; // Import CardRepository
import bank.bank.repository.CustomerRepository;
import bank.bank.entity.enums.CardType; // Import CardType
import bank.bank.entity.enums.Currency; // Import Currency
import bank.bank.entity.enums.CardBrand; // Import CardBrand
import java.util.Random; // Import Random

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final IEmailService emailService;
    private final CardRepository cardRepository; // Inject CardRepository

    @Override
    public DtoCustomer createCustomer(DtoCustomerIU dto) {

        int age = Period.between(dto.getBirthDate(), LocalDate.now()).getYears();

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

        Card cashbackCard = new Card();
        cashbackCard.setCustomer(saved);
        cashbackCard.setCardNumber(generateCardNumber());
        cashbackCard.setCvv(generateCvv());
        cashbackCard.setExpirationDate(generateExpirationDate());

        cashbackCard.setCardBrand(CardBrand.VISA);
        cashbackCard.setCardType(CardType.CASHBACK);
        cashbackCard.setCurrency(Currency.AZN);
        cashbackCard.setBalance(BigDecimal.ZERO);
        cashbackCard.setCardPassword("0000");

        cardRepository.save(cashbackCard);

        DtoCustomer out = new DtoCustomer();
        out.setFullName(saved.getFullName());
        out.setEmail(saved.getEmail());
        out.setTelephone(saved.getTelephone());
        out.setBirthDate(saved.getBirthDate());

        String emailContent =
                "Hörmətli " + saved.getFullName() + ",<br><br>" +
                        "Bankımızda uğurla qeydiyyatdan keçdiniz.<br><br>" +
                        "Sizin üçün avtomatik <b>Cashback kart</b> yaradıldı.<br>" +
                        "---------------------------------<br>" +
                        "<b>Kart nömrəsi:</b> " + cashbackCard.getCardNumber() + "<br>" +
                        "<b>Balans:</b> 0 AZN<br>" +
                        "<b>Kart növü:</b> Cashback<br>" +
                        "---------------------------------<br><br>" +
                        "Hörmətlə,<br>Bank";

        emailService.send(
                saved.getEmail(),
                "Qeydiyyat və Cashback kartınız hazırdır",
                EmailTemplateUtil.getFormattedEmail(emailContent)
        );

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

    // Helper method to generate a 16-digit card number
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // Helper method to generate a 3-digit CVV
    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    // Helper method to generate an expiration date (5 years from now)
    private Date generateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        return calendar.getTime();
    }
}
