package bank.bank.dto;

import lombok.Data;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.enums.Currency;
import bank.bank.entity.enums.CardBrand;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoCard {

    private Long id;

    private CardBrand cardBrand;

    private CardType cardType;

    private Currency currency;

    private String cardNumber;

    private String expirationDate;

    private BigDecimal balance;
}
