package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@ActivityInterface
public interface PaymentActivities {

    /**
     *
     * @param user User to deduct funds from
     * @param amount How much is the fish
     * @return Payment Id created
     */
    @ActivityMethod
    UUID pay(String user, Double amount) throws IOException, InterruptedException, URISyntaxException;

    @ActivityMethod
    void compensatePay(UUID paymentId) throws URISyntaxException, IOException, InterruptedException;

}
