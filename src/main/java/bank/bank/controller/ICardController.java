package bank.bank.controller;

import bank.bank.dto.*;
import java.util.List;
import java.math.BigDecimal;
import bank.bank.entity.TransactionHistory;

public interface ICardController {

    DtoCard createCard(Long customerId, DtoCardIU dtoCardIU);

    String transfer(DtoTransferRequest request);

    List<TransactionHistory> getHistory(Long cardId);

    String withdraw(DtoWithdrawRequest request);

    String startPinReset(DtoPinResetStartRequest request);

    String verifyPinReset(DtoPinResetVerify request);

    String confirmPinReset(DtoPinResetConfirm request);

    String payCreditDebt(String cardNumber, BigDecimal amount, String sourceCardNumber);

    String getCreditDebt(String cardNumber);

    String exportHistoryToPdf(Long cardId);
}
