package bank.bank.repository;


import bank.bank.entity.Customer;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Customer findByEmail(String email);

    boolean existsByEmail(String email);


}
