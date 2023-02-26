package ua.dp.maxym.demo5.order;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.dp.maxym.demo5.order.domain.LogRepository;
import ua.dp.maxym.demo5.order.domain.OrderRepository;


@SpringBootApplication
public class Demo5OrderTemporalApplication {

    public static final String DEMO5_ORDER_TASK_QUEUE = "Demo5OrderTaskQueue";

    private final LogRepository logger;
    private final OrderRepository orderRepository;

    public Demo5OrderTemporalApplication(LogRepository logger, OrderRepository orderRepository) {
        this.logger = logger;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        logger.deleteAll();
        orderRepository.deleteAll();
        logger.log("Application started");
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo5OrderTemporalApplication.class, args);
    }


}