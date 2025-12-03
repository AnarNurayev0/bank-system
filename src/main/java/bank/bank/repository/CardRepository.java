package bank.bank.repository;

import bank.bank.entity.Card;
import bank.bank.entity.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByCardTypeAndCardPassword(CardType cardType, String cardPassword);
}
