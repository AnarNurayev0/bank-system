package bank.bank.repository;

import bank.bank.entity.PayProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PayProviderRepository extends JpaRepository<PayProvider, Long> {
    Optional<PayProvider> findByNameIgnoreCase(String name);
}
