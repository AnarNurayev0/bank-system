package bank.bank.service;

import java.util.List;
import bank.bank.entity.PayProvider;
import bank.bank.dto.DtoPayProviderIU;

public interface IPayProviderService {
    PayProvider createProvider(DtoPayProviderIU dto);

    PayProvider updateProvider(Long id, DtoPayProviderIU dto);

    void deleteProvider(Long id);

    PayProvider toggleActive(Long id);

    List<PayProvider> getAllProviders();

    PayProvider getProviderById(Long id);
}
