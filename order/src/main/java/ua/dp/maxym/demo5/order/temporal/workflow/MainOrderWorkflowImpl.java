package ua.dp.maxym.demo5.order.temporal.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;
import ua.dp.maxym.demo5.order.domain.Order;
import ua.dp.maxym.demo5.order.domain.OrderRepository;
import ua.dp.maxym.demo5.order.domain.OrderStatus;
import ua.dp.maxym.demo5.order.temporal.activity.InventoryActivities;
import ua.dp.maxym.demo5.order.temporal.activity.ShipmentActivities;

import java.time.Duration;
import java.util.UUID;

@WorkflowImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
public class MainOrderWorkflowImpl implements MainOrderWorkflow {

    private final InventoryActivities inventory =
            Workflow.newActivityStub(InventoryActivities.class,
                                     ActivityOptions.newBuilder()
                                                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                                                    .build());
    private final ShipmentActivities shipment =
            Workflow.newActivityStub(ShipmentActivities.class,
                                     ActivityOptions.newBuilder()
                                                    .setStartToCloseTimeout(Duration.ofSeconds(5))
                                                    .build());
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void order(Order oldOrder) {
        oldOrder.setStatus(OrderStatus.INITIATED);
        var order = orderRepository.save(oldOrder);

        Saga saga = new Saga(new Saga.Options.Builder().build());
        try {
            Double price = inventory.getPrice(order.getGoods());
            Double amount = price * order.getQuantity();

            PaymentPay pay = Workflow
                    .newChildWorkflowStub(PaymentPay.class,
                                          ChildWorkflowOptions
                                                  .newBuilder().setParentClosePolicy(
                                                          ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)
                                                  .build());
            UUID paymentId = pay.pay(order.getUser(), amount);
            PaymentCompensatePay payCompensation = Workflow
                    .newChildWorkflowStub(PaymentCompensatePay.class,
                                          ChildWorkflowOptions
                                                  .newBuilder()
                                                  .setParentClosePolicy(
                                                          ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)
                                                  .build());
            saga.addCompensation(payCompensation::compensate, paymentId);

            InventoryCollect collectGoods = Workflow
                    .newChildWorkflowStub(InventoryCollect.class,
                                          ChildWorkflowOptions
                                                  .newBuilder()
                                                  .setParentClosePolicy(
                                                          ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)
                                                  .build());
            UUID collectionId = collectGoods.collect(order.getGoods(), order.getQuantity());
            InventoryCompensateCollect collectGoodsCompensation = Workflow.newChildWorkflowStub(
                    InventoryCompensateCollect.class,
                    ChildWorkflowOptions
                            .newBuilder()
                            .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)
                            .build());
            saga.addCompensation(collectGoodsCompensation::compensate, collectionId);

            shipment.ship(order.getUser(), order.getGoods(), order.getQuantity());
            order.setStatus(OrderStatus.SUCCEEDED);
            orderRepository.save(order);
        } catch (Exception e) {
            order.setStatus(OrderStatus.REVERT_INITIATED);
            var order2 = orderRepository.save(order);
            saga.compensate();
            order2.setStatus(OrderStatus.FAILED);
            order2.setStatusReason(e.getMessage());
            orderRepository.save(order2);
        }
    }
}
