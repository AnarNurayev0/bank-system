package bank.bank.dto;

import lombok.Data;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoTransferRequest {

    @NotBlank(message = "Göndərən kart nömrəsi boş ola bilməz")
    @Pattern(regexp = "^(CASHBACK|[0-9]{16})$", message = "Kart nömrəsi 16 rəqəm və ya CASHBACK olmalıdır")
    private String fromCardNumber;

    @NotBlank(message = "Alan kart nömrəsi boş ola bilməz")
    @Pattern(regexp = "^[0-9]{16}$", message = "Kart nömrəsi yalnız 16 rəqəmdən ibarət olmalıdır")
    private String toCardNumber;

    @NotNull(message = "Məbləğ boş ola bilməz")
    @DecimalMin(value = "1.00", message = "Minimim göndərilə bilən məbləğ 1 AZN/USD/EUR olmalıdır")
    private BigDecimal amount;
}
