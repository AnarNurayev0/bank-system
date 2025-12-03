package bank.bank.service;

import bank.bank.dto.*;

public interface ICardService {

    DtoCard createCard(Long customerId, DtoCardIU dto);

    String transfer(DtoTransferRequest request);

    String withdraw(DtoWithdrawRequest request);

    String startPinReset(DtoPinResetStartRequest request);

    String verifyPinReset(DtoPinResetVerify request);

    String confirmPinReset(DtoPinResetConfirm request);

    String pay(DtoPayRequest request);

}
