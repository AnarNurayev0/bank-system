package bank.bank.service.impl;

import java.util.List;
import bank.bank.dto.*;
import java.time.Period;
import bank.bank.entity.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import bank.bank.repository.*;
import bank.bank.util.CardUtil;
import bank.bank.util.DateTimeUtil;
import com.itextpdf.layout.Document;
import java.io.ByteArrayOutputStream;
import bank.bank.service.ICardService;
import lombok.RequiredArgsConstructor;
import bank.bank.util.CurrencyRateUtil;
import bank.bank.entity.enums.CardType;
import bank.bank.service.IEmailService;
import bank.bank.util.EmailTemplateUtil;
import bank.bank.entity.enums.CardStatus;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.time.format.DateTimeFormatter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements ICardService {

    private final PayProviderRepository payProviderRepository;
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final TransactionHistoryRepository historyRepository;
    private final ResetPinCodeRepository resetPinCodeRepository;
    private final IEmailService emailService;

    private String formatExpirationDate(LocalDate date) {
        if (date == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return date.format(formatter);
    }

    @Override
    public DtoCard createCard(Long customerId, DtoCardIU dtoCardIU) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        boolean hasCreditDebt = customer.getCards().stream()
                .anyMatch(c -> c.getCardType() == CardType.CREDIT
                        && c.getUsedLimit().compareTo(BigDecimal.ZERO) > 0);

        if (hasCreditDebt) {
            throw new RuntimeException("Cannot create new card due to existing credit debt.");
        }

        int age = Period.between(customer.getBirthDate(), LocalDate.now()).getYears();
        if (age < 18) {
            throw new RuntimeException("Customer is under 18.");
        }

        Card card = new Card();
        card.setCustomer(customer);
        card.setCardNumber(CardUtil.generateCardNumber());
        card.setCvv(CardUtil.generateCVV());

        if (dtoCardIU.getCardType() == CardType.CASHBACK) {
            card.setExpirationDate(null);
        } else {
            card.setExpirationDate(CardUtil.generateExpirationDate());
        }

        card.setCardBrand(dtoCardIU.getCardBrand());
        card.setCardType(dtoCardIU.getCardType());
        card.setCardPassword(dtoCardIU.getCardPassword());
        card.setCurrency(dtoCardIU.getCurrency());

        if (dtoCardIU.getCardType() == CardType.CREDIT) {
            card.setBalance(BigDecimal.ZERO);
            card.setCreditLimit(BigDecimal.valueOf(5000));
            card.setUsedLimit(BigDecimal.ZERO);
        } else {
            card.setBalance(BigDecimal.ZERO);
            card.setCreditLimit(BigDecimal.ZERO);
            card.setUsedLimit(BigDecimal.ZERO);
        }

        cardRepository.save(card);

        String amountInfo = "";
        if (card.getCardType() == CardType.CREDIT) {
            amountInfo = "<b>Credit Limit:</b> " + card.getCreditLimit();
        } else {
            amountInfo = "<b>Balance:</b> " + card.getBalance();
        }

        String emailContent = "Dear " + customer.getFullName() + ",<br><br>" +
                "Your new card has been successfully created.<br>" +
                "<b>Card Type:</b> " + card.getCardType() + "<br>" +
                amountInfo + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(
                card.getCustomer().getEmail(),
                "New Card",
                EmailTemplateUtil.getFormattedEmail(emailContent));

        return new DtoCard(
                card.getId(),
                card.getCardBrand(),
                card.getCardType(),
                card.getCurrency(),
                card.getCardType() == CardType.CASHBACK ? card.getCardNumber() : card.getCardNumber(),
                card.getCardType() == CardType.CASHBACK ? null : formatExpirationDate(card.getExpirationDate()),
                card.getBalance());
    }

    @Override
    public String getCreditDebt(String cardNumber) {

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getCardType() != CardType.CREDIT)
            throw new RuntimeException("This is not a credit card");

        return "Current Credit Debt: " + card.getUsedLimit();
    }

    @Override
    public String payCreditDebt(String cardNumber, BigDecimal amount, String sourceCardNumber) {

        Card creditCard = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Credit card not found"));

        if (creditCard.getCardType() != CardType.CREDIT)
            throw new RuntimeException("This card is not credit");

        Card sourceCard = cardRepository.findByCardNumber(sourceCardNumber)
                .orElseThrow(() -> new RuntimeException("Source card not found"));

        if (creditCard.getUsedLimit().compareTo(amount) < 0)
            throw new RuntimeException("Payment amount exceeds debt");

        if (sourceCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for payment");
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        cardRepository.save(sourceCard);

        creditCard.setUsedLimit(creditCard.getUsedLimit().subtract(amount));
        cardRepository.save(creditCard);

        TransactionHistory hSource = new TransactionHistory();
        hSource.setOwnerCardId(sourceCard.getId());
        hSource.setFromCardNumber(sourceCard.getCardNumber());
        hSource.setToCardNumber(creditCard.getCardNumber());
        hSource.setAmount(amount);
        hSource.setConvertedAmount(amount);
        hSource.setFromCustomerName(sourceCard.getCustomer().getFullName());
        hSource.setToCustomerName("Credit Debt Payment");
        hSource.setType("PAY_DEBT");
        historyRepository.save(hSource);

        TransactionHistory hCredit = new TransactionHistory();
        hCredit.setOwnerCardId(creditCard.getId());
        hCredit.setFromCardNumber(sourceCard.getCardNumber());
        hCredit.setToCardNumber(creditCard.getCardNumber());
        hCredit.setAmount(amount);
        hCredit.setConvertedAmount(amount);
        hCredit.setFromCustomerName(sourceCard.getCustomer().getFullName());
        hCredit.setToCustomerName("Credit Debt Payment");
        hCredit.setType("DEBT_PAID");
        historyRepository.save(hCredit);

        String mail = "Dear " + creditCard.getCustomer().getFullName() + ",<br><br>" +
                "Payment made to your credit debt.<br>" +
                "<b>Paid:</b> " + amount + "<br>" +
                "<b>Remaining Debt:</b> " + creditCard.getUsedLimit() + "<br>" +
                "<b>Source Card:</b> " + sourceCard.getCardNumber() + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(creditCard.getCustomer().getEmail(),
                "Credit Payment", EmailTemplateUtil.getFormattedEmail(mail));

        return "Credit debt paid";
    }

    @Override
    public String pay(DtoPayRequest request) {

        Card from = cardRepository
                .findByCardNumber(request.getFromCard())
                .orElseThrow(() -> new RuntimeException("Kart tapılmadı"));

        BigDecimal amount = request.getAmount();

        if (from.getCardType() == CardType.CREDIT) {

            BigDecimal available = from.getCreditLimit().subtract(from.getUsedLimit());

            if (available.compareTo(amount) < 0)
                throw new RuntimeException("Insufficient credit limit");

            from.setUsedLimit(from.getUsedLimit().add(amount));
        } else {

            if (from.getBalance().compareTo(amount) < 0)
                throw new RuntimeException("Insufficient balance");

            from.setBalance(from.getBalance().subtract(amount));
        }

        cardRepository.save(from);

        PayProvider provider = payProviderRepository
                .findByNameIgnoreCase(request.getProviderName())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        BigDecimal cashback = amount
                .multiply(provider.getCashbackPercent())
                .divide(BigDecimal.valueOf(100));

        Card cashbackCard = cardRepository
                .findByCustomerAndStatusAndCardTypeNot(
                        from.getCustomer(),
                        CardStatus.ACTIVE,
                        CardType.DEBIT)
                .stream()
                .filter(c -> c.getCardType() == CardType.CASHBACK)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cashback card not found"));

        cashbackCard.setBalance(cashbackCard.getBalance().add(cashback));
        cardRepository.save(cashbackCard);

        TransactionHistory h = new TransactionHistory();
        h.setOwnerCardId(from.getId());
        h.setFromCardNumber(from.getCardNumber());
        h.setAmount(amount);
        h.setConvertedAmount(amount);
        h.setFromCustomerName(from.getCustomer().getFullName());
        h.setToCustomerName(provider.getName());
        h.setType("PAY");
        historyRepository.save(h);

        String balanceInfo = "";
        if (from.getCardType() == CardType.CREDIT) {
            balanceInfo = "<b>Used Limit:</b> " + from.getUsedLimit();
        } else {
            balanceInfo = "<b>Balance:</b> " + from.getBalance();
        }

        String mail = "Dear " + from.getCustomer().getFullName() + ",<br><br>" +
                "Payment to " + provider.getName() + " successfully completed.<br>" +
                "<b>Amount:</b> " + amount + "<br>" +
                "<b>Cashback Earned:</b> " + cashback + "<br>" +
                balanceInfo + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(
                from.getCustomer().getEmail(),
                "Payment Confirmation",
                EmailTemplateUtil.getFormattedEmail(mail));

        return "Payment successfully completed. Cashback added.";
    }

    @Override
    public String transfer(DtoTransferRequest request) {

        Card from = cardRepository.findByCardNumber(request.getFromCardNumber())
                .orElseThrow(() -> new RuntimeException("Sender card not found"));

        Card to = cardRepository.findByCardNumber(request.getToCardNumber())
                .orElseThrow(() -> new RuntimeException("Receiver card not found"));

        BigDecimal amount = request.getAmount();
        BigDecimal convertedAmount = amount;

        if (!from.getCurrency().equals(to.getCurrency())) {
            BigDecimal rate = CurrencyRateUtil.getRate(
                    from.getCurrency().name(),
                    to.getCurrency().name());
            convertedAmount = amount.multiply(rate);
        }

        if (from.getCardType() == CardType.CREDIT) {
            BigDecimal availableLimit = from.getCreditLimit().subtract(from.getUsedLimit());
            if (availableLimit.compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient credit limit");
            }
            from.setUsedLimit(from.getUsedLimit().add(amount));
        } else {
            if (from.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            from.setBalance(from.getBalance().subtract(amount));
        }

        to.setBalance(to.getBalance().add(convertedAmount));

        cardRepository.save(from);
        cardRepository.save(to);

        TransactionHistory hFrom = new TransactionHistory();
        hFrom.setOwnerCardId(from.getId());
        hFrom.setFromCardNumber(from.getCardNumber());
        hFrom.setToCardNumber(to.getCardNumber());
        hFrom.setAmount(amount);
        hFrom.setConvertedAmount(amount);
        hFrom.setFromCustomerName(from.getCustomer().getFullName());
        hFrom.setToCustomerName(to.getCustomer().getFullName());
        hFrom.setType("TRANSFER_OUT");
        historyRepository.save(hFrom);

        TransactionHistory hTo = new TransactionHistory();
        hTo.setOwnerCardId(to.getId());
        hTo.setFromCardNumber(from.getCardNumber());
        hTo.setToCardNumber(to.getCardNumber());
        hTo.setAmount(convertedAmount);
        hTo.setConvertedAmount(convertedAmount);
        hTo.setFromCustomerName(from.getCustomer().getFullName());
        hTo.setToCustomerName(to.getCustomer().getFullName());
        hTo.setType("TRANSFER_IN");
        historyRepository.save(hTo);

        String senderMail = "Dear " + from.getCustomer().getFullName() + ",<br><br>" +
                "Money transfer sent from your account.<br>" +
                "<b>Amount:</b> " + amount + " " + from.getCurrency() + "<br>" +
                "<b>Receiver:</b> " + to.getCustomer().getFullName() + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(from.getCustomer().getEmail(), "Money Transfer",
                EmailTemplateUtil.getFormattedEmail(senderMail));

        String receiverMail = "Dear " + to.getCustomer().getFullName() + ",<br><br>" +
                "Money received in your account.<br>" +
                "<b>Amount:</b> " + convertedAmount + " " + to.getCurrency() + "<br>" +
                "<b>Sender:</b> " + from.getCustomer().getFullName() + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(to.getCustomer().getEmail(), "Money Received",
                EmailTemplateUtil.getFormattedEmail(receiverMail));

        return "Transfer successfully completed.";
    }

    @Override
    public String withdraw(DtoWithdrawRequest request) {

        Card from = cardRepository.findByCardNumber(request.getFromCardNumber())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        BigDecimal amount = request.getAmount();

        if (from.getCardType() == CardType.CREDIT) {

            BigDecimal available = from.getCreditLimit().subtract(from.getUsedLimit());

            if (available.compareTo(amount) < 0)
                throw new RuntimeException("Insufficient credit limit");

            from.setUsedLimit(from.getUsedLimit().add(amount));
        } else {

            if (from.getBalance().compareTo(amount) < 0)
                throw new RuntimeException("Insufficient balance");

            from.setBalance(from.getBalance().subtract(amount));
        }

        cardRepository.save(from);

        TransactionHistory h = new TransactionHistory();
        h.setOwnerCardId(from.getId());
        h.setFromCardNumber(from.getCardNumber());
        h.setAmount(amount);
        h.setConvertedAmount(amount);
        h.setFromCustomerName(from.getCustomer().getFullName());
        h.setToCustomerName("ATM / Bank");
        h.setType("WITHDRAW");
        historyRepository.save(h);

        String mail;

        if(from.getCardType().equals(CardType.CASHBACK)){
            mail = "Dear " + from.getCustomer().getFullName() + ",<br><br>" +
                    "Cash withdrawal from your Cashback card.<br>" +
                    "<b>Amount:</b> " + amount + " " + from.getCurrency() + "<br>" +
                    "<b>Date:</b> " + DateTimeUtil.nowFormatted();
        }else{
            mail = "Dear " + from.getCustomer().getFullName() + ",<br><br>" +
                    "Cash withdrawal from your "+from.getCardNumber()+" card."+"<br>" +
                    "<b>Amount:</b> " + amount + " " + from.getCurrency() + "<br>" +
                    "<b>Date:</b> " + DateTimeUtil.nowFormatted();
        }


        emailService.send(from.getCustomer().getEmail(), "Withdrawal", EmailTemplateUtil.getFormattedEmail(mail));

        return "Withdraw successfully completed.";
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
        reset.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(5));
        resetPinCodeRepository.save(reset);

        String mail = "Dear " + card.getCustomer().getFullName() + ",<br><br>" +
                "Your PIN reset request has been registered.<br>" +
                "<b>Verification Code:</b> " + code + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(email, "PIN Reset", EmailTemplateUtil.getFormattedEmail(mail));

        return "Verification code sent to email.";
    }

    @Override
    public String verifyPinReset(DtoPinResetVerify request) {

        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        ResetPinCode reset = resetPinCodeRepository
                .findByCardIdAndEmail(card.getId(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Code not found."));

        if (reset.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            resetPinCodeRepository.delete(reset);
            throw new RuntimeException("Code expired.");
        }

        if (!reset.getCode().equals(request.getCode())) {
            throw new RuntimeException("Invalid code.");
        }

        return "Code verified.";
    }

    @Override
    public String confirmPinReset(DtoPinResetConfirm request) {

        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        ResetPinCode reset = resetPinCodeRepository
                .findByCardIdAndEmail(card.getId(), request.getEmail())
                .orElseThrow(() -> new RuntimeException("Verification record not found."));

        if (request.getNewPin().length() != 4) {
            throw new RuntimeException("PIN must be 4 digits.");
        }

        card.setCardPassword(request.getNewPin());
        cardRepository.save(card);
        resetPinCodeRepository.delete(reset);

        String mail = "Dear " + card.getCustomer().getFullName() + ",<br><br>" +
                "Your card PIN has been successfully updated.<br>" +
                "<b>Card:</b> " + card.getCardNumber() + "<br>" +
                "<b>Date:</b> " + DateTimeUtil.nowFormatted();

        emailService.send(card.getCustomer().getEmail(), "PIN Successfully Updated",
                EmailTemplateUtil.getFormattedEmail(mail));

        return "PIN successfully updated.";
    }

    @Override
    public String exportHistoryToPdf(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        List<TransactionHistory> history = historyRepository.findByOwnerCardIdOrderByCreatedAtDesc(cardId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String displayCardNumber = card.getCardType() == CardType.CASHBACK ? "CASHBACK" : card.getCardNumber();

            document.add(
                    new Paragraph("Transaction History for Card: " + displayCardNumber).setBold().setFontSize(14));
            document.add(new Paragraph("Owner: " + card.getCustomer().getFullName()));
            document.add(new Paragraph("Date: " + DateTimeUtil.nowFormatted()));
            document.add(new Paragraph("\n"));

            float[] columnWidths = { 3, 3, 3, 3, 4 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell("Type");
            table.addHeaderCell("Amount");
            table.addHeaderCell("From");
            table.addHeaderCell("To");
            table.addHeaderCell("Date");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (TransactionHistory h : history) {
                table.addCell(h.getType() != null ? h.getType() : "");
                table.addCell(h.getAmount() + " " + card.getCurrency());
                table.addCell(h.getFromCustomerName() != null ? h.getFromCustomerName() : "-");
                table.addCell(h.getToCustomerName() != null ? h.getToCustomerName() : "-");
                table.addCell(h.getCreatedAt() != null ? h.getCreatedAt().format(formatter) : "");
            }

            document.add(table);
            document.close();

            String mail = "Dear " + card.getCustomer().getFullName() + ",<br><br>" +
                    "The account history you requested is attached as PDF.";

            emailService.send(
                    card.getCustomer().getEmail(),
                    "Account History - " + displayCardNumber,
                    EmailTemplateUtil.getFormattedEmail(mail),
                    out.toByteArray(),
                    "History_" + displayCardNumber + ".pdf");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating PDF: " + e.getMessage());
        }

        return "History sent to your email address.";
    }
}
