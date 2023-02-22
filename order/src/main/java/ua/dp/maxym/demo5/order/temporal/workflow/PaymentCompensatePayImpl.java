package ua.dp.maxym.demo5.order.temporal.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;
import ua.dp.maxym.demo5.order.temporal.activity.PaymentActivities;

import java.time.Duration;
import java.util.UUID;

@WorkflowImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
public class PaymentCompensatePayImpl implements PaymentCompensatePay {

    private final PaymentActivities payment = Workflow.newActivityStub(
            PaymentActivities.class,
            ActivityOptions.newBuilder()
                           .setStartToCloseTimeout(
                                   Duration.ofSeconds(5))
                           .build());

    @Override
    public void compensate(UUID paymentId) {
        try {
            payment.compensatePay(paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Compensation failed", e);
        }
    }
}
