package bank.bank.entity;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.enums.Currency;
import bank.bank.entity.enums.CardBrand;
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
    @Column(name = "card_brand")
    private CardBrand cardBrand;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType cardType;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "card_password")
    private String cardPassword;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;
}
