package ua.dp.maxym.demo5.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.dp.maxym.demo5.order.domain.LogRepository;
import ua.dp.maxym.demo5.order.domain.Order;
import ua.dp.maxym.demo5.order.domain.OrderRepository;
import ua.dp.maxym.demo5.order.service.OrderService;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    private final LogRepository logRepository;
    private final OrderRepository orderRepository;

    private final OrderService orderService;

    public OrderController(LogRepository logRepository, OrderRepository orderRepository, OrderService orderService) {
        this.logRepository = logRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public String list() {
        return String.format("""
                                     Order list:
                                     <br/><br/>
                                     %1$s
                                     <br/><br/>
                                     See also <a href="/log">Log</a>
                                     """, orderRepository.findAll().stream().map(Object::toString)
                                                         .collect(Collectors.joining("<br/>")));
    }

    @GetMapping("/log")
    public String log() {
        return String.format("""
                                     Log messages:
                                     <br/><br/>
                                     %1$s
                                     <br/><br/>
                                     See also <a href="/list">Order List</a>
                                     """, logRepository.findAll().stream().map(Object::toString)
                                                       .collect(Collectors.joining("<br/>")));
    }

    @GetMapping("/order")
    public String order(String user, String goods, Integer quantity) {
        UUID orderId = orderService.order(user, goods, quantity);
        return String.format("""
                                     Initiating order for
                                     <ul>
                                         <li>User %s</li>
                                         <li>Goods %s</li>
                                         <li>Quantity %s</li>
                                     </ul>
                                     <br>
                                     To check status, query
                                     <a href="/status?id=%4$s" target="_blank">Order %4$s status</a>
                                     """, user, goods, quantity, orderId);
    }

    @GetMapping("/status")
    public String status(String id) {
        UUID orderId = UUID.fromString(id);
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return switch (order.getStatus()) {
                case NOT_STARTED, INITIATED, SUCCEEDED -> order.getStatus().toString();
                case REVERT_INITIATED, FAILED ->
                        String.format("%s with reason %s", order.getStatus(), order.getStatusReason());
            };
        } else {
            return """
                    Order with id %s not found.
                    <br/><br/>
                    See <a href="/">Order List</a> and <a href="/log">Application Log</a>
                    """;
        }
    }
}
