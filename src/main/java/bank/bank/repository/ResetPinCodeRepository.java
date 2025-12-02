package bank.bank.repository;

import java.util.Optional;
import bank.bank.entity.ResetPinCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPinCodeRepository extends JpaRepository<ResetPinCode, Long> {
    Optional<ResetPinCode> findByCardIdAndEmail(Long cardId, String email);
}
