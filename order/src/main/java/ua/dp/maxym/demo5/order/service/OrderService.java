package ua.dp.maxym.demo5.order.service;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;
import ua.dp.maxym.demo5.order.domain.LogRepository;
import ua.dp.maxym.demo5.order.domain.Order;
import ua.dp.maxym.demo5.order.domain.OrderRepository;
import ua.dp.maxym.demo5.order.domain.OrderStatus;
import ua.dp.maxym.demo5.order.temporal.workflow.*;

import java.time.Duration;
import java.util.UUID;

@Service
public class OrderService {

    /*
    @Autowired
    private CheckoutProducer checkoutProducer;

    @Autowired
    private PaymentProducer paymentProducer;

    @Autowired
    private InventoryProducer inventoryProducer;

    @Autowired
    private ShipmentProducer shipmentProducer;

    @Autowired
    private OrderRepository orderRepository;

    public void startCheckout(CheckOut checkOutCommand) {
        logger.log("\n\n<br/>\n\n");
        logger.log("New checkout initiated %s", checkOutCommand);

        UUID id = checkOutCommand.checkoutId();
        var order = new Order(id, INITIATED, NOT_STARTED, NOT_STARTED, NOT_STARTED,
                checkOutCommand,
                null, null,
                null, null,
                null);
        orderRepository.insert(order);
        logger.log("Created state for order %s", order);

        logger.log("1. Payment");
        var payCommand = new Pay(id, checkOutCommand.user(), checkOutCommand.amount());
        order.setPaymentStatus(INITIATED);
        order.setPayCommand(payCommand);
        orderRepository.save(order);
        logger.log("Updated state for order %s", order);
        paymentProducer.perform(payCommand);
        logger.log("Performing payment %s", payCommand);
    }

    public void paymentSucceeded(PaymentSucceeded paymentSucceeded) {
        logger.log("Payment succeeded %s", paymentSucceeded);

        var id = paymentSucceeded.paymentId();
        var order = orderRepository.findById(id).get();

        if (order.paymentStatus() == INITIATED) {
            logger.log("Payment that was initiated now succeeded");
            order.setPaymentStatus(SUCCEEDED);
            orderRepository.save(order);
            logger.log("Updated state for order %s", order);

            logger.log("2. Inventory");
            var checkOutCommand = order.checkOutCommand();
            var collectGoodsCommand = new CollectGoods(id, checkOutCommand.goods(), checkOutCommand.quantity());
            order.setInventoryStatus(INITIATED);
            order.setCollectGoodsCommand(collectGoodsCommand);
            orderRepository.save(order);
            logger.log("Updated state for order %s", order);
            inventoryProducer.perform(collectGoodsCommand);
            logger.log("Getting goods from inventory %s", collectGoodsCommand);
        } else if (order.paymentStatus() == REVERT_INITIATED) {
            logger.log("Reverting payment succeeded");
            order.setPaymentStatus(FAILED);
            order.setOverallStatus(FAILED);
            orderRepository.save(order);
            logger.log("FINAL Updated state for order %s", order);

            checkoutProducer.failed(new CheckoutFailed(id, "Payment and/or Inventory failed"));
        } else {
            logger.log("ERROR! Invalid payment state %s", order.paymentStatus());
            throw new RuntimeException(String.format("ERROR! Invalid payment state %s", order.paymentStatus()));
        }
    }

    public void paymentFailed(PaymentFailed paymentFailed) {
        logger.log("Payment failed %s", paymentFailed);

        var id = paymentFailed.paymentId();
        var order = orderRepository.findById(id).get();

        if (order.paymentStatus() == INITIATED) {
            logger.log("Payment was initiated, but failed, no need to compensate for payment");
            order.setPaymentStatus(FAILED);
            order.setOverallStatus(FAILED);
            orderRepository.save(order);
            logger.log("FINAL Updated state for order %s", order);

            checkoutProducer.failed(new CheckoutFailed(id, "Payment and/or Inventory failed"));
        } else {
            logger.log("ERROR! Invalid payment state %s", order.paymentStatus());
            throw new RuntimeException(String.format("ERROR! Invalid payment state %s", order.paymentStatus()));
        }
    }

    public void inventorySucceeded(CollectingGoodsSucceeded collectingGoodsSucceeded) {
        logger.log("Getting goods from inventory succeeded %s", collectingGoodsSucceeded);

        var id = collectingGoodsSucceeded.requestId();
        var order = orderRepository.findById(id).get();

        if (order.inventoryStatus() == INITIATED) {
            logger.log("Getting from Inventory was initiated, now succeeded");
            logger.log("3. Shipment");
            var checkOut = order.checkOutCommand();
            var shipGoods = new ShipGoods(id, checkOut.user(), checkOut.goods(), checkOut.quantity());
            order.setInventoryStatus(SUCCEEDED);
            order.setShipmentStatus(INITIATED);
            order.setShipGoodsCommand(shipGoods);
            orderRepository.save(order);
            logger.log("Updated state for order %s", order);

            shipmentProducer.perform(shipGoods);
            logger.log("Shipping our order %s", shipGoods);
        } else if (order.inventoryStatus() == REVERT_INITIATED) {
            logger.log("Inventory revert was initiated, now reverted");
            order.setInventoryStatus(FAILED);
            logger.log("Need to revert payment");
            order.setPaymentStatus(REVERT_INITIATED);
            var compensatePayCommand = new Pay(id, order.payCommand().user(), -order.payCommand().amount());
            order.setCompensatePayCommand(compensatePayCommand);
            orderRepository.save(order);
            logger.log("Updated state for order %s", order);

            paymentProducer.perform(compensatePayCommand);
            logger.log("Compensating payment %s", compensatePayCommand);
        } else {
            logger.log("ERROR! Invalid inventory state %s", order.inventoryStatus());
            throw new RuntimeException(String.format("ERROR! Invalid inventory state %s", order.inventoryStatus()));
        }
    }

    public void inventoryFailed(CollectingGoodsFailed collectingGoodsFailed) {
        logger.log("Getting goods from inventory failed %s", collectingGoodsFailed);

        var id = collectingGoodsFailed.requestId();
        var order = orderRepository.findById(id).get();

        if (order.inventoryStatus() == INITIATED) {
            logger.log("Getting from Inventory was initiated, but failed");
            order.setInventoryStatus(FAILED);

            logger.log("Need to revert payment");
            order.setPaymentStatus(REVERT_INITIATED);
            var compensatePayCommand = new Pay(id, order.payCommand().user(), -order.payCommand().amount());
            order.setCompensatePayCommand(compensatePayCommand);
            orderRepository.save(order);
            logger.log("Updated state for order %s", order);

            paymentProducer.perform(compensatePayCommand);
            logger.log("Compensating payment %s", compensatePayCommand);
        } else {
            logger.log("ERROR! Invalid inventory state %s", order.inventoryStatus());
            throw new RuntimeException(String.format("ERROR! Invalid inventory state %s", order.inventoryStatus()));
        }
    }

    public void shipmentSucceeded(GoodsShipmentSucceeded shipmentSucceeded) {
        logger.log("Shipment succeeded %s", shipmentSucceeded);

        var id = shipmentSucceeded.requestId();
        var order = orderRepository.findById(id).get();

        if (order.shipmentStatus() == INITIATED) {
            logger.log("Shipment was initiated, and now succeeded. Overall order succeeded.");
            order.setShipmentStatus(SUCCEEDED);
            order.setOverallStatus(SUCCEEDED);
            orderRepository.save(order);
            logger.log("FINAL Updated state for order %s", order);

            var checkoutSucceeded = new CheckoutSucceeded(id);
            checkoutProducer.succeded(checkoutSucceeded);
            logger.log("Informing checkout service %s", checkoutSucceeded);
        } else {
            logger.log("ERROR! Invalid shipment status %s", order.shipmentStatus());
            throw new RuntimeException(String.format("ERROR! Invalid shipment status %s", order.shipmentStatus()));
        }
    }

    public void shipmentFailed(GoodsShipmentFailed shipmentFailed) {
        logger.log("Shipment failed %s", shipmentFailed);

        var id = shipmentFailed.requestId();
        var order = orderRepository.findById(id).get();

        if (order.shipmentStatus() == INITIATED) {
            logger.log("Shipment was initiated, and now failed. Need to roll back inventory.");
            order.setShipmentStatus(FAILED);
            order.setInventoryStatus(REVERT_INITIATED);
            var compensateInventoryCommand = new CollectGoods(id, order.collectGoodsCommand().goods(), -order
            .collectGoodsCommand().quantity());
            order.setCompensateCollectGoodsCommand(compensateInventoryCommand);
            orderRepository.save(order);
            logger.log("Updated state for order %s", order);

            inventoryProducer.perform(compensateInventoryCommand);
            logger.log("Sending compensation inventory %s", compensateInventoryCommand);
        } else {
            logger.log("ERROR! Invalid shipment status %s", order.shipmentStatus());
            throw new RuntimeException(String.format("ERROR! Invalid shipment status %s", order.shipmentStatus()));
        }
    }
     */
    @Autowired
    private LogRepository logger;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WorkflowClient workflowClient;

    @Autowired
    private WorkerFactory factory;

    public UUID order(String user, String goods, Integer quantity) {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, OrderStatus.NOT_STARTED, null, user, goods, quantity, 0.0);
        orderRepository.save(order);
        logger.log("Start order %s", order);

        factory.start();
        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                                                         .setWorkflowId(orderId.toString())
                                                         .setWorkflowRunTimeout(Duration.ofMinutes(1))
                                                         .setTaskQueue(
                                                                 Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
                                                         .build();
        MainOrderWorkflow workflow = workflowClient.newWorkflowStub(MainOrderWorkflow.class, workflowOptions);

        logger.log("Temporal initialized, starting workflow...");
        var workflowExecution = WorkflowClient.start(workflow::order, order);
        logger.log("Started workflow asynchronously %s", workflowExecution);

        return orderId;
    }
}
