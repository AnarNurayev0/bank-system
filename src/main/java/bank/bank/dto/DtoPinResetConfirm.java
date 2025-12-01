package bank.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPinResetConfirm {
    private String cardNumber;
    private String email;
    private String newPin;
}
