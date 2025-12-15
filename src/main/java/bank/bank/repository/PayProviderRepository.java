package bank.bank.repository;

import java.util.Optional;
import bank.bank.entity.PayProvider;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PayProviderRepository extends JpaRepository<PayProvider, Long> {
    Optional<PayProvider> findByNameIgnoreCase(String name);
}
