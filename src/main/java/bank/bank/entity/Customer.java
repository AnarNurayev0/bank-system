package bank.bank.entity;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "email_password")
    private String emailPassword;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Card> cards;
}
