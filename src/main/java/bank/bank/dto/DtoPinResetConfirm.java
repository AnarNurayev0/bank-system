package bank.bank.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPinResetConfirm {

    private String cardNumber;

    private String email;

    private String newPin;
}
