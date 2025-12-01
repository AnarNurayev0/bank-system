package bank.bank.repository;

import bank.bank.entity.ResetPinCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPinCodeRepository extends JpaRepository<ResetPinCode, Long> {
    Optional<ResetPinCode> findByCardIdAndEmail(Long cardId, String email);
}
