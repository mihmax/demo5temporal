package ua.dp.maxym.demo5.order.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface InventoryCompensateCollect {
    @WorkflowMethod
    void compensate(UUID collectionId);
}
