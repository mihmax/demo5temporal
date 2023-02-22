package ua.dp.maxym.demo4.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"ua.dp.maxym.demo4"})
public class Demo4InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(Demo4InventoryApplication.class, args);
    }

}