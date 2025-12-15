package bank.bank.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import bank.bank.entity.enums.CardType;
import bank.bank.entity.enums.Currency;
import bank.bank.entity.enums.CardBrand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCardIU {

    @NotBlank(message = "Kart şifrəsi boş ola bilməz")
    @Pattern(regexp = "^[0-9]{4}$", message = "Kart şifrəsi yalnız 4 rəqəmdən ibarət olmalıdır")
    private String cardPassword;

    @NotNull(message = "Kart brendi seçilməlidir")
    private CardBrand cardBrand;

    @NotNull(message = "Kart tipi seçilməlidir")
    private CardType cardType;

    @NotNull(message = "Kart valyutası seçilməlidir")
    private Currency currency;
}
