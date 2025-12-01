package bank.bank.controller.impl;

import bank.bank.controller.ICardController;
import bank.bank.dto.*;
import bank.bank.entity.TransactionHistory;
import bank.bank.repository.TransactionHistoryRepository;
import bank.bank.service.ICardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardControllerImpl implements ICardController {

    private final ICardService cardService;
    private final TransactionHistoryRepository historyRepository;

    @Override
    @PostMapping("/create/{customerId}")
    public DtoCard createCard(@PathVariable Long customerId, @RequestBody DtoCardIU dtoCardIU) {
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
    @PostMapping("/pin/reset/simple")
    public String resetPinSimple(@RequestBody DtoPinResetSimpleRequest request) {
        return cardService.resetPinSimple(request);
    }


}
