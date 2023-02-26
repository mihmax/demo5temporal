package ua.dp.maxym.demo4.inventory.domain;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

public record Inventory(@Id String goods, Integer quantity, BigDecimal pricePerItem) {
}
