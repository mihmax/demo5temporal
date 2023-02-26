package ua.dp.maxym.demo5.shipment;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.dp.maxym.demo5.shipment.domain.UserAddress;
import ua.dp.maxym.demo5.shipment.domain.UserAddressRepository;


@SpringBootApplication
public class Demo5ShipmentApplication {

    private final UserAddressRepository userAddressRepository;

    public Demo5ShipmentApplication(UserAddressRepository userAddressRepository) {
        this.userAddressRepository = userAddressRepository;
    }

    @PostConstruct
    public void init() {
        userAddressRepository.deleteAll();
        userAddressRepository.insert(new UserAddress("user2", "Dnipro, Somewhere Str. 11 / 16"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo5ShipmentApplication.class, args);
    }

}