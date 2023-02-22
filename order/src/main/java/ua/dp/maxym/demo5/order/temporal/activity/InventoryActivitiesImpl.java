package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Service;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;

import java.util.UUID;

@ActivityImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
@Service
public class InventoryActivitiesImpl implements InventoryActivities {
    @Override
    public Double getPrice(String goods) {
        return 10.0;
    }

    @Override
    public UUID collect(String goods, Integer quantity) {
        return UUID.randomUUID();
    }

    @Override
    public void compensateCollect(UUID collectionId) {

    }
}
