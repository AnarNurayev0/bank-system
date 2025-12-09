package bank.bank.dto;

import lombok.Data;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoWithdrawRequest {

    @NotBlank(message = "Göndərən kart nömrəsi boş ola bilməz")
    @Pattern(regexp = "^[0-9]{16}$", message = "Kart nömrəsi yalnız 16 rəqəmdən ibarət olmalıdır")
    private String fromCardNumber;

    @NotNull(message = "Məbləğ boş ola bilməz")
    @DecimalMin(value = "1.00", message = "Minimim göndərilə bilən məbləğ 1 AZN/USD/EUR olmalıdır")
    private BigDecimal amount;

}
