package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ShipmentActivities {
    @ActivityMethod
    void ship(String user, String goods, Integer amount);

}
