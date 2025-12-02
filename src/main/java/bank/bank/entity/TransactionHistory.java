package bank.bank.entity;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_card_id")
    private Long ownerCardId;

    @Column(name = "from_card_number")
    private String fromCardNumber;

    @Column(name = "to_card_number")
    private String toCardNumber;

    @Column(name = "from_customer_name")
    private String fromCustomerName;

    @Column(name = "to_customer_name")
    private String toCustomerName;

    private BigDecimal amount;

    private BigDecimal convertedAmount;

    private String type;

    private LocalDateTime createdAt = LocalDateTime.now();
}
