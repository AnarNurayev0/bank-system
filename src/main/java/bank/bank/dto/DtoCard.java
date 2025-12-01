package bank.bank.dto;

import bank.bank.entity.enums.CardBrand;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

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
