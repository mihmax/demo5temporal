package ua.dp.maxym.demo5.order.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.UUID;

@WorkflowInterface
public interface InventoryCollect {
    /**
     *
     * @param goods What to collect
     * @param quantity How many
     * @return Collection Id for reference (and possibility to cancel later)
     */
    @WorkflowMethod
    UUID collect(String goods, Integer quantity);
}
