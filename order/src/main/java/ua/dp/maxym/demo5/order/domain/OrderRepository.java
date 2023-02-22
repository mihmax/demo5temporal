package ua.dp.maxym.demo5.order.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface OrderRepository extends MongoRepository<Order, UUID> {

}
