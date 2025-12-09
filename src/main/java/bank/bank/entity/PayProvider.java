package bank.bank.entity;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "pay_provider")
@Data
public class PayProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private BigDecimal cashbackPercent;

    private Boolean active = true;
}
