package bank.bank.repository;

import java.util.List;
import bank.bank.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    List<TransactionHistory> findByOwnerCardIdOrderByCreatedAtDesc(Long cardId);


}
