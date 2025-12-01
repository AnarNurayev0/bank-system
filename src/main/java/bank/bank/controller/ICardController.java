package bank.bank.controller;

import bank.bank.dto.*;
import bank.bank.entity.TransactionHistory;
import java.util.List;

public interface ICardController {

    DtoCard createCard(Long customerId, DtoCardIU dtoCardIU);

    String transfer(DtoTransferRequest request);

    List<TransactionHistory> getHistory(Long cardId);

    String withdraw(DtoWithdrawRequest request);

    String startPinReset(DtoPinResetStartRequest request);

    String verifyPinReset(DtoPinResetVerify request);

    String confirmPinReset(DtoPinResetConfirm request);
}
