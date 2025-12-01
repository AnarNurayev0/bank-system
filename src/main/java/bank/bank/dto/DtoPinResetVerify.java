package bank.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoPinResetVerify {
    private String cardNumber;
    private String email;
    private String code;
}
