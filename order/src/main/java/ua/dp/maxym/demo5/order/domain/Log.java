package ua.dp.maxym.demo5.order.domain;

import org.springframework.data.annotation.Id;

import java.util.Date;

public record Log(@Id Date timestamp, String logMessage) {

    public Log(String logMessage, Object... args) {
        this(new Date(), String.format(logMessage, args));
    }
}
