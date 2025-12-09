package bank.bank.service.impl;

import java.util.List;
import java.time.Period;
import java.util.Random;
import java.time.LocalDate;
import java.math.BigDecimal;
import bank.bank.dto.DtoCard;
import bank.bank.entity.Card;
import bank.bank.dto.DtoCustomer;
import bank.bank.entity.Customer;
import bank.bank.dto.DtoCustomerIU;
import lombok.RequiredArgsConstructor;
import bank.bank.entity.enums.Currency;
import bank.bank.service.IEmailService;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.RegistrationOTP;
import bank.bank.entity.enums.CardBrand;
import bank.bank.util.EmailTemplateUtil;
import bank.bank.service.ICustomerService;
import java.time.format.DateTimeFormatter;
import bank.bank.repository.CardRepository;
import org.springframework.stereotype.Service;
import bank.bank.repository.CustomerRepository;
import bank.bank.repository.RegistrationOTPRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final IEmailService emailService;
    private final CardRepository cardRepository;
    private final RegistrationOTPRepository registrationOTPRepository;

    @Override
    public DtoCustomer createCustomer(DtoCustomerIU dto) {

        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already in use by another customer!");
        }

        registrationOTPRepository.findByEmailAndVerifiedTrue(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not verified. Please verify OTP first."));

        int age = Period.between(dto.getBirthDate(), LocalDate.now()).getYears();

        if (age < 18) {
            throw new RuntimeException("Registration not allowed for under 18.");
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
        cashbackCard.setExpirationDate(null);

        cashbackCard.setCardBrand(CardBrand.VISA);
        cashbackCard.setCardType(CardType.CASHBACK);
        cashbackCard.setCurrency(Currency.AZN);
        cashbackCard.setBalance(BigDecimal.ZERO);
        cashbackCard.setCardPassword("0000");

        cardRepository.save(cashbackCard);

        DtoCustomer out = new DtoCustomer();
        out.setId(saved.getId());
        out.setFullName(saved.getFullName());
        out.setEmail(saved.getEmail());
        out.setTelephone(saved.getTelephone());
        out.setBirthDate(saved.getBirthDate());

        String emailContent = "Dear " + saved.getFullName() + ",<br><br>" +
                "You have successfully registered with our bank.<br><br>" +
                "Cashback system has been automatically activated for you.<br>" +
                "Cashback is calculated automatically during purchases.<br><br>" +
                "Sincerely,<br>" +
                "NexusBank Service";

        emailService.send(
                saved.getEmail(),
                "Registration and Cashback System Activated",
                EmailTemplateUtil.getFormattedEmail(emailContent));

        return out;
    }

    @Override
    public DtoCustomer getCustomerById(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        DtoCustomer out = new DtoCustomer();
        out.setId(c.getId());
        out.setFullName(c.getFullName());
        out.setEmail(c.getEmail());
        out.setTelephone(c.getTelephone());
        out.setBirthDate(c.getBirthDate());

        List<DtoCard> dtoCards = c.getCards().stream().map(card -> {
            DtoCard dto = new DtoCard();
            dto.setId(card.getId());
            dto.setCardType(card.getCardType());
            dto.setCurrency(card.getCurrency());

            if (card.getCardType() == CardType.CREDIT) {
                BigDecimal availableLimit = card.getCreditLimit().subtract(card.getUsedLimit());
                dto.setBalance(availableLimit);
            } else {
                dto.setBalance(card.getBalance());
            }

            if (card.getCardType() == CardType.CASHBACK) {
                dto.setCardBrand(null);
                dto.setCardNumber(card.getCardNumber());
                dto.setExpirationDate(null);
            } else {
                dto.setCardBrand(card.getCardBrand());
                dto.setCardNumber(card.getCardNumber());
                dto.setExpirationDate(formatExpirationDate(card.getExpirationDate()));
            }

            return dto;
        }).toList();

        out.setCards(dtoCards);

        return out;
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private String formatExpirationDate(LocalDate date) {
        if (date == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return date.format(formatter);
    }

    @Override
    public DtoCustomer login(bank.bank.dto.DtoLoginRequest request) {
        Customer c = customerRepository.findByEmail(request.getEmail());
        if (c == null) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!c.getEmailPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return getCustomerById(c.getId());
    }

    @Override
    public String sendRegistrationOTP(String email) {
        if (customerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }

        registrationOTPRepository.deleteByEmail(email);

        String code = String.valueOf((int) (Math.random() * 900000 + 100000));

        RegistrationOTP otp = new RegistrationOTP();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(5));
        otp.setVerified(false);
        registrationOTPRepository.save(otp);

        String emailContent = "Your registration verification code is: <b>" + code + "</b><br><br>" +
                "This code is valid for 5 minutes.";

        emailService.send(email, "Registration Verification Code", EmailTemplateUtil.getFormattedEmail(emailContent));

        return "Verification code sent to email";
    }

    @Override
    public String verifyRegistrationOTP(String email, String code) {
        RegistrationOTP otp = registrationOTPRepository.findByEmailAndVerifiedFalse(email)
                .orElseThrow(() -> new RuntimeException("Verification code not found"));

        if (otp.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            registrationOTPRepository.delete(otp);
            throw new RuntimeException("Code expired");
        }

        if (!otp.getCode().equals(code)) {
            throw new RuntimeException("Invalid code");
        }

        otp.setVerified(true);
        registrationOTPRepository.save(otp);

        return "Email verified";
    }
}
