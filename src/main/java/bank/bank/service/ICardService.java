package bank.bank.service;

import bank.bank.dto.*;

public interface ICardService {

    public DtoCard createCard(Long customerId, DtoCardIU dto);

    public String transfer(DtoTransferRequest request);

    public String withdraw(DtoWithdrawRequest request);

    public String startPinReset(DtoPinResetStartRequest request);

    String verifyPinReset(DtoPinResetVerify request);

    String confirmPinReset(DtoPinResetConfirm request);



}
