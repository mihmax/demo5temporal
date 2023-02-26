package ua.dp.maxym.demo5.order.temporal.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;

@ActivityImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
@Service
public class InventoryActivitiesImpl implements InventoryActivities {
    @Override
    public double getPrice(String goods) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(new URI("http://localhost:8084/inventory/" + goods))
                                         .header("Accept", "application/json")
                                         .timeout(Duration.of(10, SECONDS))
                                         .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        switch (HttpStatus.valueOf(response.statusCode())) {
            case BAD_REQUEST, NOT_FOUND, FORBIDDEN ->
                // Expected (business) errors
                    throw new RuntimeException(
                            String.format("Business Validation error in Inventory service %s", response.body()));
            case OK -> {
                // Success, getting price
                var map = new ObjectMapper().readValue(response.body(), HashMap.class);
                return Double.parseDouble(map.get("pricePerItem").toString());
            }
            default ->
                // Unexpected error
                    throw new RuntimeException(
                            String.format("Unexpected error in Inventory service. Code %s, Message %s",
                                          response.statusCode(),
                                          response.body()));
        }
    }

    @Override
    public UUID collect(String goods, Integer quantity) {
        return UUID.randomUUID();
    }

    @Override
    public void compensateCollect(UUID collectionId) {

    }
}
