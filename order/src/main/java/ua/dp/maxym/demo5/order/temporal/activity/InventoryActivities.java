package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.UUID;

@ActivityInterface
public interface InventoryActivities {
    @ActivityMethod
    Double getPrice(String goods);

    @ActivityMethod
    UUID collect(String goods, Integer quantity);

    @ActivityMethod
    void compensateCollect(UUID collectionId);

}
