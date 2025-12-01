package bank.bank.service.impl;

import bank.bank.dto.*;
import bank.bank.entity.Card;
import bank.bank.entity.Customer;
import bank.bank.entity.TransactionHistory;
import bank.bank.repository.CardRepository;
import bank.bank.repository.CustomerRepository;
import bank.bank.repository.TransactionHistoryRepository;
import bank.bank.service.ICardService;
import bank.bank.util.CardUtil;
import bank.bank.util.CurrencyRateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardService {

    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final TransactionHistoryRepository historyRepository;
    private final EmailService emailService;

    @Override
    public DtoCard createCard(Long customerId, DtoCardIU dtoCardIU) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer tapılmadı"));

        int age = Period.between(
                customer.getBirthDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(),
                LocalDate.now()
        ).getYears();

        if (age < 18) {
            throw new RuntimeException("Customer 18 yaşından kiçikdir, kart açıla bilməz");
        }

        Card card = new Card();
        card.setCustomer(customer);

        card.setCardNumber(CardUtil.generateCardNumber());
        card.setCvv(CardUtil.generateCVV());
        card.setExpirationDate(CardUtil.generateExpirationDate());

        card.setCardBrand(dtoCardIU.getCardBrand());
        card.setCardType(dtoCardIU.getCardType());
        card.setCardPassword(dtoCardIU.getCardPassword());
        card.setCurrency(dtoCardIU.getCurrency());
        card.setBalance(BigDecimal.ZERO);

        cardRepository.save(card);

        return new DtoCard(
                card.getCardBrand(),
                card.getCardType(),
                card.getCurrency(),
                card.getCardNumber(),
                card.getExpirationDate(),
                card.getBalance()
        );
    }

    @Override
    public String transfer(DtoTransferRequest request) {

        Card from = cardRepository.findByCardNumber(request.getFromCardNumber())
                .orElseThrow(() -> new RuntimeException("Göndərən kart tapılmadı"));

        Card to = cardRepository.findByCardNumber(request.getToCardNumber())
                .orElseThrow(() -> new RuntimeException("Alan kart tapılmadı"));

        if (!from.getCardPassword().equals(request.getCardPassword())) {
            throw new RuntimeException("Kart şifrəsi yanlışdır");
        }

        BigDecimal amount = request.getAmount();

        BigDecimal convertedAmount = amount;

        if (!from.getCurrency().equals(to.getCurrency())) {
            BigDecimal rate = CurrencyRateUtil.getRate(
                    from.getCurrency().name(),
                    to.getCurrency().name()
            );
            convertedAmount = amount.multiply(rate);
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Balans kifayət etmir");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(convertedAmount));

        cardRepository.save(from);
        cardRepository.save(to);


        TransactionHistory out = new TransactionHistory();
        out.setOwnerCardId(from.getId());
        out.setFromCardNumber(from.getCardNumber());
        out.setToCardNumber(to.getCardNumber());
        out.setFromCustomerName(from.getCustomer().getFullName());
        out.setToCustomerName(to.getCustomer().getFullName());
        out.setAmount(amount);
        out.setConvertedAmount(convertedAmount);
        out.setType("TRANSFER_OUT");
        historyRepository.save(out);

        TransactionHistory in = new TransactionHistory();
        in.setOwnerCardId(to.getId());
        in.setFromCardNumber(from.getCardNumber());
        in.setToCardNumber(to.getCardNumber());
        in.setFromCustomerName(from.getCustomer().getFullName());
        in.setToCustomerName(to.getCustomer().getFullName());
        in.setAmount(amount);
        in.setConvertedAmount(convertedAmount);
        in.setType("TRANSFER_IN");
        historyRepository.save(in);

        return "Transfer uğurla tamamlandı";
    }

    @Override
    public String withdraw(DtoWithdrawRequest request) {

        Card from = cardRepository.findByCardNumber(request.getFromCardNumber())
                .orElseThrow(() -> new RuntimeException("Göndərən kart tapılmadı"));

        if (!from.getCardPassword().equals(request.getCardPassword())) {
            throw new RuntimeException("Kart şifrəsi yanlışdır");
        }

        BigDecimal amount = request.getAmount();

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Balans kifayət etmir");
        }

        from.setBalance(from.getBalance().subtract(amount));
        cardRepository.save(from);

        emailService.send(
                from.getCustomer().getEmail(),
                "Pul Çıxarışı",
                "Hörmətli " + from.getCustomer().getFullName() +
                        ",\n\nKartınızdan " + amount + " AZN məbləğində pul çıxarılmışdır.\n" +
                        "Yeni balans: " + from.getBalance() + " AZN\n\n" +
                        "Bank Sistem"
        );

        return "Withdraw uğurludur.";
    }


    @Override
    public String resetPinSimple(DtoPinResetSimpleRequest request) {
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Kart tapılmadı"));

        Customer customer = card.getCustomer();

        if (!customer.getEmail().equals(request.getEmail())) {
            throw new RuntimeException("Email kart sahibinə məxsus deyil");
        }

        if (!customer.getEmailPassword().equals(request.getEmailPassword())) {
            throw new RuntimeException("Email şifrəsi yanlışdır");
        }

        if (request.getNewPin().length() != 4) {
            throw new RuntimeException("PIN 4 rəqəmdən ibarət olmalıdır");
        }

        card.setCardPassword(request.getNewPin());
        cardRepository.save(card);

        TransactionHistory h = new TransactionHistory();
        h.setOwnerCardId(card.getId());
        h.setFromCardNumber(card.getCardNumber());
        h.setFromCustomerName(customer.getFullName());
        h.setType("PIN_RESET_SIMPLE");
        historyRepository.save(h);

        return "PIN uğurla yeniləndi";

    }
}
