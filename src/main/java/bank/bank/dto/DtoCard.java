package bank.bank.dto;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.enums.Currency;
import bank.bank.entity.enums.CardBrand;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCard {

    private CardBrand cardBrand;

    private CardType cardType;

    private Currency currency;

    private String cardNumber;

    private Date expirationDate;

    private BigDecimal balance;
}
