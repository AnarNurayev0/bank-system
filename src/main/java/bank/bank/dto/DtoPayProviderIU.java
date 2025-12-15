package bank.bank.dto;

import lombok.Data;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPayProviderIU {

    @NotBlank(message = "Provider name is required")
    private String name;

    private String description;

    @NotNull(message = "Cashback percent is required")
    private BigDecimal cashbackPercent;
}
