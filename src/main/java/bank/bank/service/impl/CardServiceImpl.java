package bank.bank.service.impl;

import bank.bank.dto.*;
import java.time.Period;
import java.time.LocalDate;
import java.math.BigDecimal;
import bank.bank.entity.Card;
import bank.bank.repository.*;
import bank.bank.util.CardUtil;
import java.time.LocalDateTime;
import bank.bank.entity.Customer;
import bank.bank.entity.ResetPinCode;
import bank.bank.service.ICardService;
import lombok.RequiredArgsConstructor;
import bank.bank.service.IEmailService;
import bank.bank.util.CurrencyRateUtil;
import bank.bank.util.EmailTemplateUtil;
import bank.bank.entity.TransactionHistory;
import org.springframework.stereotype.Service;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.PayProvider;


@Service
@RequiredArgsConstructor
public class CardServiceImpl implements ICardService {

    private final PayProviderRepository payProviderRepository;
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final TransactionHistoryRepository historyRepository;
    private final ResetPinCodeRepository resetPinCodeRepository;
    private final IEmailService emailService;

    @Override
    public DtoCard createCard(Long customerId, DtoCardIU dtoCardIU) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer tapılmadı"));

        int age = Period.between(customer.getBirthDate(), LocalDate.now()).getYears();

        if (age < 18) {
            throw new RuntimeException("Customer 18 yaşından kiçikdir.");
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

        String emailContent = "Hörmətli " + customer.getFullName() + ",<br><br>" +
                "Yeni kartınız uğurla yaradıldı.<br>" +
                "---------------------------------<br>" +
                "<b>Kart brendi:</b> " + card.getCardBrand() + "<br>" +
                "<b>Kart növü:</b> " + card.getCardType() + "<br>" +
                "<b>Kart nömrəsi:</b> " + card.getCardNumber() + "<br>" +
                "<b>Son istifadə tarixi:</b> " + card.getExpirationDate() + "<br>" +
                "---------------------------------<br><br>" +
                "Bank xidmətlərimizdən istifadə etdiyiniz üçün təşəkkür edirik!";

        emailService.send(
                card.getCustomer().getEmail(),
                "Yeni Kartınız Uğurla Yaradıldı",
                EmailTemplateUtil.getFormattedEmail(emailContent)
        );

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

        Card from;

        if ("CASHBACK".equalsIgnoreCase(request.getFromCardNumber())) {
            from = cardRepository
                    .findByCardTypeAndCardPassword(CardType.CASHBACK, request.getCardPassword())
                    .orElseThrow(() -> new RuntimeException("Cashback kart tapılmadı və ya PIN yanlışdır"));
        } else {
            from = cardRepository.findByCardNumber(request.getFromCardNumber())
                    .orElseThrow(() -> new RuntimeException("Göndərən kart tapılmadı"));
        }

        Card to = cardRepository.findByCardNumber(request.getToCardNumber())
                .orElseThrow(() -> new RuntimeException("Alan kart tapılmadı"));

        if (!from.getCardPassword().equals(request.getCardPassword())) {
            throw new RuntimeException("PIN yanlışdır");
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

        String fromEmailContent = "Hörmətli " + from.getCustomer().getFullName() + ",<br><br>" +
                "Kartınızdan pul köçürülməsi həyata keçirildi.<br>" +
                "<b>Əməliyyat detalları:</b><br>" +
                "---------------------------------<br>" +
                "<b>Göndərilən məbləğ:</b> " + amount + " " + from.getCurrency() + "<br>" +
                "<b>Alan şəxs:</b> " + to.getCustomer().getFullName() + "<br>" +
                "<b>Alan kart:</b> " + to.getCardNumber() + "<br>" +
                "<b>Tarix:</b> " + LocalDateTime.now() + "<br>" +
                "---------------------------------<br>" +
                "<b>Yeni balansınız:</b> " + from.getBalance() + " " + from.getCurrency() + "<br><br>" +
                "Əgər siz bu əməliyyatı etməmisinizsə, dərhal bankla əlaqə saxlayın.";

        emailService.send(
                from.getCustomer().getEmail(),
                "Transfer Təsdiqi",
                EmailTemplateUtil.getFormattedEmail(fromEmailContent)
        );

        String toEmailContent = "Hörmətli " + to.getCustomer().getFullName() + ",<br><br>" +
                "Kartınıza yeni vəsait daxil olmuşdur.<br>" +
                "<b>Əməliyyat detalları:</b><br>" +
                "---------------------------------<br>" +
                "<b>Mənbə kart:</b> " + from.getCardNumber() + "<br>" +
                "<b>Mənbə şəxs:</b> " + from.getCustomer().getFullName() + "<br>" +
                "<b>Göndərilən məbləğ:</b> " + convertedAmount + " " + to.getCurrency() + "<br>" +
                "<b>Tarix:</b> " + LocalDateTime.now() + "<br>" +
                "---------------------------------<br>" +
                "Balansınız yenilənmişdir.<br><br>" +
                "Təşəkkür edirik!";

        emailService.send(
                to.getCustomer().getEmail(),
                "Hesabınıza Vəsait Daxil Oldu",
                EmailTemplateUtil.getFormattedEmail(toEmailContent)
        );


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

        return "Transfer uğurla tamamlandı.";
    }


    @Override
    public String withdraw(DtoWithdrawRequest request) {


        Card from = cardRepository.findByCardNumber(request.getFromCardNumber())
                .orElseThrow(() -> new RuntimeException("Kart tapılmadı"));

        if (from.getCardType() == CardType.CASHBACK) {
            throw new RuntimeException("Cashback kartdan nağd pul çıxarmaq qadağandır.");
        }

        if (!from.getCardPassword().equals(request.getCardPassword())) {
            throw new RuntimeException("PIN yanlışdır");
        }

        BigDecimal amount = request.getAmount();

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Balans kifayət etmir");
        }

        from.setBalance(from.getBalance().subtract(amount));
        cardRepository.save(from);

        String emailContent = "Hörmətli " + from.getCustomer().getFullName() + ",<br><br>" +
                "Kartınızdan pul çıxarışı uğurla həyata keçirildi.<br>" +
                "---------------------------------<br>" +
                "<b>Çıxarılan məbləğ:</b> " + amount + " " + from.getCurrency() + "<br>" +
                "<b>Tarix:</b> " + LocalDateTime.now() + "<br>" +
                "<b>Yeni balans:</b> " + from.getBalance() + " " + from.getCurrency() + "<br>" +
                "---------------------------------<br><br>" +
                "Əməliyyatı siz etməmisinizsə bankla əlaqə saxlayın.";

        emailService.send(
                from.getCustomer().getEmail(),
                "Pul Çıxarışı",
                EmailTemplateUtil.getFormattedEmail(emailContent)
        );


        TransactionHistory h = new TransactionHistory();
        h.setOwnerCardId(from.getId());
        h.setFromCardNumber(from.getCardNumber());
        h.setFromCustomerName(from.getCustomer().getFullName());
        h.setAmount(amount);
        h.setConvertedAmount(amount);
        h.setType("WITHDRAW");
        historyRepository.save(h);

        return "Withdraw uğurla tamamlandı.";
    }


    @Override
    public String startPinReset(DtoPinResetStartRequest request) {

        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Kart tapılmadı"));

        String email = card.getCustomer().getEmail();

        String code = String.valueOf((int) (Math.random() * 900000 + 100000));

        resetPinCodeRepository.findByCardIdAndEmail(card.getId(), email)
                .ifPresent(resetPinCodeRepository::delete);

        ResetPinCode reset = new ResetPinCode();
        reset.setCardId(card.getId());
        reset.setEmail(email);
        reset.setCode(code);
        reset.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        resetPinCodeRepository.save(reset);

        String emailContent = "Hörmətli " + card.getCustomer().getFullName() + ",<br><br>" +
                "PIN yeniləmək üçün təsdiq kodunuz:<br>" +
                "---------------------------------<br>" +
                "<b>" + code + "</b><br>" +
                "---------------------------------<br><br>" +
                "Bu kod 5 dəqiqə ərzində keçərlidir.";

        emailService.send(
                email,
                "PIN Yeniləmə Kodu",
                EmailTemplateUtil.getFormattedEmail(emailContent)
        );


        return "Təsdiq kodu emailə göndərildi.";
    }


    @Override
    public String verifyPinReset(DtoPinResetVerify request) {

        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Kart tapılmadı"));

        ResetPinCode reset = resetPinCodeRepository
                .findByCardIdAndEmail(card.getId(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kod tapılmadı."));

        if (reset.getExpiresAt().isBefore(LocalDateTime.now())) {
            resetPinCodeRepository.delete(reset);
            throw new RuntimeException("Kodun vaxtı bitib.");
        }

        if (!reset.getCode().equals(request.getCode())) {
            throw new RuntimeException("Kod yanlışdır.");
        }

        return "Kod təsdiqləndi.";
    }


    @Override
    public String confirmPinReset(DtoPinResetConfirm request) {

        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Kart tapılmadı"));

        ResetPinCode reset = resetPinCodeRepository
                .findByCardIdAndEmail(card.getId(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Təsdiq tapılmadı."));

        if (request.getNewPin().length() != 4) {
            throw new RuntimeException("PIN 4 rəqəm olmalıdır.");
        }

        card.setCardPassword(request.getNewPin());
        cardRepository.save(card);

        resetPinCodeRepository.delete(reset);

        TransactionHistory h = new TransactionHistory();
        h.setOwnerCardId(card.getId());
        h.setFromCardNumber(card.getCardNumber());
        h.setFromCustomerName(card.getCustomer().getFullName());
        h.setType("PIN_RESET");
        historyRepository.save(h);

        String emailContent = "Hörmətli " + card.getCustomer().getFullName() + ",<br><br>" +
                "Kartınız üçün yeni PIN kod uğurla təyin edildi.<br>" +
                "---------------------------------<br>" +
                "<b>Kart nömrəsi:</b> " + card.getCardNumber() + "<br>" +
                "<b>Tarix:</b> " + LocalDateTime.now() + "<br>" +
                "---------------------------------<br><br>" +
                "Bu əməliyyatı siz etməmisinizsə bankla əlaqə saxlayın.";

        emailService.send(
                card.getCustomer().getEmail(),
                "PIN Uğurla Yeniləndi",
                EmailTemplateUtil.getFormattedEmail(emailContent)
        );


        return "PIN uğurla yeniləndi.";
    }

    @Override
    public String pay(DtoPayRequest request) {

        Card from;

        if ("CASHBACK".equalsIgnoreCase(request.getFromCard())) {
            from = cardRepository
                    .findByCardTypeAndCardPassword(CardType.CASHBACK, request.getCardPassword())
                    .orElseThrow(() -> new RuntimeException("Cashback kart tapılmadı və ya PIN yanlışdır"));
        } else {
            from = cardRepository.findByCardNumber(request.getFromCard())
                    .orElseThrow(() -> new RuntimeException("Göndərən kart tapılmadı"));
        }

        if (from.getCardType() == CardType.CASHBACK) {
        }

        PayProvider provider = payProviderRepository
                .findByNameIgnoreCase(request.getProviderName())
                .orElseThrow(() -> new RuntimeException("Provider tapılmadı"));

        BigDecimal amount = request.getAmount();

        if (from.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Balans kifayət etmir");
        }

        from.setBalance(from.getBalance().subtract(amount));
        cardRepository.save(from);

        BigDecimal cashback = amount
                .multiply(provider.getCashbackPercent())
                .divide(BigDecimal.valueOf(100));

        Card cashbackCard = cardRepository
                .findByCardTypeAndCardPassword(CardType.CASHBACK, from.getCardPassword())
                .orElseThrow(() -> new RuntimeException("Cashback kart tapılmadı"));

        cashbackCard.setBalance(
                cashbackCard.getBalance().add(cashback)
        );

        cardRepository.save(cashbackCard);

        String emailContent =
                "Hörmətli " + from.getCustomer().getFullName() + ",<br><br>" +
                        "<b>" + provider.getName() + "</b> üçün ödəniş uğurla tamamlandı.<br>" +
                        "---------------------------------<br>" +
                        "<b>Məbləğ:</b> " + amount + " " + from.getCurrency() + "<br>" +
                        "<b>Cashback:</b> " + cashback + " " + from.getCurrency() + "<br>" +
                        "<b>Yeni balans:</b> " + from.getBalance() + "<br>" +
                        "---------------------------------<br>";

        emailService.send(
                from.getCustomer().getEmail(),
                "Ödəniş Təsdiqi",
                EmailTemplateUtil.getFormattedEmail(emailContent)
        );

        TransactionHistory h = new TransactionHistory();
        h.setOwnerCardId(from.getId());
        h.setFromCardNumber(from.getCardNumber());
        h.setToCardNumber(provider.getName());
        h.setFromCustomerName(from.getCustomer().getFullName());
        h.setToCustomerName(provider.getName());
        h.setAmount(amount);
        h.setConvertedAmount(amount);
        h.setType("PAY");
        historyRepository.save(h);

        return "Ödəniş uğurla həyata keçirildi. Cashback əlavə olundu.";
    }


}
