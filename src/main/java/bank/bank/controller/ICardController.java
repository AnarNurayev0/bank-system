package bank.bank.controller;

import bank.bank.dto.*;
import bank.bank.entity.TransactionHistory;

import java.util.List;

public interface ICardController {

    public DtoCard createCard(Long customerId, DtoCardIU dtoCardIU);

    public String transfer(DtoTransferRequest request);

    public List<TransactionHistory> getHistory(Long cardId);

    public String withdraw(DtoWithdrawRequest request);

    public String resetPinSimple(  DtoPinResetSimpleRequest request);

}
