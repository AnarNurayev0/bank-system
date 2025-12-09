package bank.bank.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import bank.bank.entity.Card;
import bank.bank.entity.Customer;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    Optional<Card> findByCardTypeAndCardPassword(CardType cardType, String cardPassword);

    List<Card> findByExpirationDateBeforeAndStatus(Date date, CardStatus status);

    List<Card> findByCustomerAndStatusAndCardTypeNot(
            Customer customer,
            CardStatus status,
            CardType cardType);
}
