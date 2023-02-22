package ua.dp.maxym.demo5.order.temporal.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;
import ua.dp.maxym.demo5.order.temporal.activity.InventoryActivities;

import java.time.Duration;
import java.util.UUID;

@WorkflowImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
public class InventoryCompensateCollectImpl implements InventoryCompensateCollect {

    private final InventoryActivities inventory = Workflow.newActivityStub(
            InventoryActivities.class,
            ActivityOptions.newBuilder().setStartToCloseTimeout(
                    Duration.ofSeconds(5)).build());

    @Override
    public void compensate(UUID collectionId) {
        inventory.compensateCollect(collectionId);
    }
}
