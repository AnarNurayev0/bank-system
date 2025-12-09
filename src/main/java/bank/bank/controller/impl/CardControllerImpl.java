package bank.bank.controller.impl;

import java.util.List;
import bank.bank.dto.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import bank.bank.service.ICardService;
import bank.bank.entity.TransactionHistory;
import bank.bank.controller.ICardController;
import org.springframework.web.bind.annotation.*;
import bank.bank.repository.TransactionHistoryRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardControllerImpl implements ICardController {

    private final ICardService cardService;
    private final TransactionHistoryRepository historyRepository;

    @Override
    @PostMapping("/create/{customerId}")
    public DtoCard createCard(
            @PathVariable Long customerId,
            @RequestBody DtoCardIU dtoCardIU) {
        return cardService.createCard(customerId, dtoCardIU);
    }

    @Override
    @PostMapping("/transfer")
    public String transfer(@RequestBody DtoTransferRequest request) {
        return cardService.transfer(request);
    }

    @Override
    @GetMapping("/history/{cardId}")
    public List<TransactionHistory> getHistory(@PathVariable Long cardId) {
        return historyRepository.findByOwnerCardIdOrderByCreatedAtDesc(cardId);
    }

    @Override
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody DtoWithdrawRequest request) {
        return cardService.withdraw(request);
    }

    @Override
    @PostMapping("/pin/reset/start")
    public String startPinReset(@RequestBody DtoPinResetStartRequest request) {
        return cardService.startPinReset(request);
    }

    @Override
    @PostMapping("/pin/reset/verify")
    public String verifyPinReset(@RequestBody DtoPinResetVerify request) {
        return cardService.verifyPinReset(request);
    }

    @Override
    @PostMapping("/pin/reset/confirm")
    public String confirmPinReset(@RequestBody DtoPinResetConfirm request) {
        return cardService.confirmPinReset(request);
    }

    @PostMapping("/pay")
    public String pay(@RequestBody @Valid DtoPayRequest request) {
        return cardService.pay(request);
    }

    @GetMapping("/credit/debt")
    public String getCreditDebt(@RequestParam String cardNumber) {
        return cardService.getCreditDebt(cardNumber);
    }

    @PostMapping("/credit/pay")
    public String payCreditDebt(
            @RequestParam String cardNumber,
            @RequestParam BigDecimal amount,
            @RequestParam String sourceCardNumber) {
        return cardService.payCreditDebt(cardNumber, amount, sourceCardNumber);
    }

    @Override
    @PostMapping("/history/{cardId}/export")
    public String exportHistoryToPdf(@PathVariable Long cardId) {
        return cardService.exportHistoryToPdf(cardId);
    }
}
