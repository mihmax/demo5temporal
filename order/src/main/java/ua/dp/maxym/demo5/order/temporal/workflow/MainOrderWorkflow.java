package ua.dp.maxym.demo5.order.temporal.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import ua.dp.maxym.demo5.order.domain.Order;

@WorkflowInterface
public interface MainOrderWorkflow {
    @WorkflowMethod
    void order(Order order);
}
