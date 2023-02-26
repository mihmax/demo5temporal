package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@ActivityInterface
public interface InventoryActivities {
    @ActivityMethod
    double getPrice(String goods) throws IOException, InterruptedException, URISyntaxException;

    @ActivityMethod
    UUID collect(String goods, Integer quantity);

    @ActivityMethod
    void compensateCollect(UUID collectionId);

}
