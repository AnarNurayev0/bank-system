package bank.bank.service;

import bank.bank.dto.*;

public interface ICardService {

    public DtoCard createCard(Long customerId, DtoCardIU dto);

    public String transfer(DtoTransferRequest request);

    public String withdraw(DtoWithdrawRequest request);

    String resetPinSimple(DtoPinResetSimpleRequest request);

}
