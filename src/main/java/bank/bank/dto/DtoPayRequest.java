package bank.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import java.math.BigDecimal;

@Data
public class DtoPayRequest {

    @NotBlank
    @Pattern(regexp = "^(CASHBACK|[0-9]{16})$")
    private String fromCard;

    @NotBlank
    private String providerName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{4}$")
    private String cardPassword;

    @NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;
}
