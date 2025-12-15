package bank.bank.service.impl;

import java.util.List;
import bank.bank.entity.PayProvider;
import bank.bank.dto.DtoPayProviderIU;
import lombok.RequiredArgsConstructor;
import bank.bank.service.IPayProviderService;
import bank.bank.repository.PayProviderRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PayProviderServiceImpl implements IPayProviderService {

    private final PayProviderRepository payProviderRepository;

    @Override
    public PayProvider createProvider(DtoPayProviderIU dto) {
        if (payProviderRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new RuntimeException("Provider with this name already exists");
        }
        PayProvider provider = new PayProvider();
        provider.setName(dto.getName());
        provider.setDescription(dto.getDescription());
        provider.setCashbackPercent(dto.getCashbackPercent());
        provider.setActive(true); // Default active
        return payProviderRepository.save(provider);
    }

    @Override
    public PayProvider updateProvider(Long id, DtoPayProviderIU dto) {
        PayProvider provider = getProviderById(id);

        // Check name uniqueness if changed
        payProviderRepository.findByNameIgnoreCase(dto.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new RuntimeException("Provider with phone name already exists");
                    }
                });

        provider.setName(dto.getName());
        provider.setDescription(dto.getDescription());
        provider.setCashbackPercent(dto.getCashbackPercent());
        return payProviderRepository.save(provider);
    }

    @Override
    public void deleteProvider(Long id) {
        if (!payProviderRepository.existsById(id)) {
            throw new EntityNotFoundException("Provider not found");
        }
        payProviderRepository.deleteById(id);
    }

    @Override
    public PayProvider toggleActive(Long id) {
        PayProvider provider = getProviderById(id);
        provider.setActive(!Boolean.TRUE.equals(provider.getActive()));
        return payProviderRepository.save(provider);
    }

    @Override
    public List<PayProvider> getAllProviders() {
        return payProviderRepository.findAll();
    }

    @Override
    public PayProvider getProviderById(Long id) {
        return payProviderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PayProvider not found with id: " + id));
    }
}
