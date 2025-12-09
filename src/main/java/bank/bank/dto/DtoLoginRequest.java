package bank.bank.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DtoLoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
