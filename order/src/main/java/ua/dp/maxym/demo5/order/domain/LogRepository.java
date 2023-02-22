package ua.dp.maxym.demo5.order.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<Log, String> {

    default void log(String logMessage, Object... args) {
        insert(new Log(logMessage, args));
    }

}
