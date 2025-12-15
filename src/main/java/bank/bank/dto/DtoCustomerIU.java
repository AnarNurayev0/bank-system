package bank.bank.dto;

import lombok.Data;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCustomerIU {

    @NotBlank(message = "Full name boş ola bilməz")
    private String fullName;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Email düzgün formatda olmalıdır")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@gmail\\.com$",
            message = "Yalnız @gmail.com emailinə icazə verilir")
    private String email;

    @NotBlank(message = "Email üçün şifrə boş ola bilməz")
    private String emailPassword;

    @NotBlank(message = "Telefon boş ola bilməz")
    @Pattern(
            regexp = "^\\+994(50|51|55|77|99|10)\\d{7}$",
            message = "Telefon formatı düzgün deyil. Format: +99450xxxxxxx"
    )
    private String telephone;

    @NotNull(message = "Doğum tarixi boş ola bilməz")
    private LocalDate birthDate;
}
