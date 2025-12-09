package bank.bank.repository;

import java.util.Optional;
import bank.bank.entity.RegistrationOTP;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RegistrationOTPRepository extends JpaRepository<RegistrationOTP, Long> {

    Optional<RegistrationOTP> findByEmailAndVerifiedFalse(String email);

    Optional<RegistrationOTP> findByEmailAndVerifiedTrue(String email);

    void deleteByEmail(String email);
}
