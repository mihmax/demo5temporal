package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.api.failure.v1.ApplicationFailureInfoOrBuilder;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;
import ua.dp.maxym.demo5.order.domain.LogRepository;

@ActivityImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
@Service
public class ShipmentActivitiesImpl implements ShipmentActivities {

    @Autowired
    private LogRepository logger;

    @Override
    public void ship(String user, String goods, Integer amount) {
        logger.log("Shipping -- do nothing for now, but return error");
        throw ApplicationFailure.newNonRetryableFailure("Not Implemented", "n/a");
    }
}
