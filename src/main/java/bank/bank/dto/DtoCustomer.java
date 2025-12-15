package bank.bank.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCustomer {

    private Long id;

    private String fullName;

    private String email;

    private String telephone;

    private LocalDate birthDate;

    private List<DtoCard> cards;
}
