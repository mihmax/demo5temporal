package ua.dp.maxym.demo5.payment;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.dp.maxym.demo5.payment.domain.UserCredit;
import ua.dp.maxym.demo5.payment.domain.UserCreditRepository;


@SpringBootApplication
public class Demo5PaymentApplication {

    private final UserCreditRepository creditRepository;

    public Demo5PaymentApplication(UserCreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    @PostConstruct
    public void init() {
        creditRepository.deleteAll();
        creditRepository.save(new UserCredit("user1", 1000.0));
        creditRepository.save(new UserCredit("user2", 500.0));
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo5PaymentApplication.class, args);
    }
}