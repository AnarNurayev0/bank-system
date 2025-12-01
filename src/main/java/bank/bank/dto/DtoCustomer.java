package bank.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCustomer {

    private String fullName;

    private String email;

    private String telephone;

    private Date birthDate;

    private List<DtoCard> cards;
}
