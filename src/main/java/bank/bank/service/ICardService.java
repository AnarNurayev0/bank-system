package bank.bank.service;

import bank.bank.dto.*;
import java.math.BigDecimal;

public interface ICardService {

    DtoCard createCard(Long customerId, DtoCardIU dto);

    String transfer(DtoTransferRequest request);

    String withdraw(DtoWithdrawRequest request);

    String startPinReset(DtoPinResetStartRequest request);

    String verifyPinReset(DtoPinResetVerify request);

    String confirmPinReset(DtoPinResetConfirm request);

    String pay(DtoPayRequest request);

    String payCreditDebt(String cardNumber, BigDecimal amount, String sourceCardNumber);

    String getCreditDebt(String cardNumber);

    String exportHistoryToPdf(Long cardId);
}
