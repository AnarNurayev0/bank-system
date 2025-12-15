package bank.bank.dto;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;

@Data
public class DtoPayRequest {

    @NotBlank
    @Pattern(regexp = "^(CASHBACK|[0-9]{16})$")
    private String fromCard;

    @NotBlank
    private String providerName;

    @jakarta.validation.constraints.NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;
}
