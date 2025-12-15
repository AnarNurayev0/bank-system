package bank.bank.entity;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import bank.bank.entity.enums.*;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "card")
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;

    @Column(name = "card_number")
    private String cardNumber;

    private String cvv;

    @Column(name = "card_password")
    private String cardPassword;

    private java.time.LocalDate expirationDate;

    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @Column(nullable = false)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal usedLimit = BigDecimal.ZERO;

}
