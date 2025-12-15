package bank.bank.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class DtoVerifyOTP {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;
}
