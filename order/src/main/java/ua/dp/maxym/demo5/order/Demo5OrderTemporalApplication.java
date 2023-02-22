package ua.dp.maxym.demo5.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import ua.dp.maxym.demo5.order.domain.LogRepository;
import ua.dp.maxym.demo5.order.domain.OrderRepository;


@SpringBootApplication
public class Demo5OrderTemporalApplication {

    public static final String DEMO5_ORDER_TASK_QUEUE = "Demo5OrderTaskQueue";

    public Demo5OrderTemporalApplication(LogRepository logger, OrderRepository orderRepository) {
        logger.deleteAll();
        orderRepository.deleteAll();
        logger.log("Application started");
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo5OrderTemporalApplication.class, args);
    }


}