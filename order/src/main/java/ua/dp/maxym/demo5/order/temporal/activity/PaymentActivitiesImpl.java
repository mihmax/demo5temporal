package ua.dp.maxym.demo5.order.temporal.activity;

import io.temporal.spring.boot.ActivityImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import ua.dp.maxym.demo5.order.Demo5OrderTemporalApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;

@ActivityImpl(taskQueues = Demo5OrderTemporalApplication.DEMO5_ORDER_TASK_QUEUE)
@Service
public class PaymentActivitiesImpl implements PaymentActivities {
    @Override
    public UUID pay(String user, Double amount) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(new URI("http://localhost:8083/pay"))
                                         .POST(HttpRequest.BodyPublishers.ofString(
                                                 String.format("user=%s&amount=%s", user, amount)))
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .timeout(Duration.of(10, SECONDS))
                                         .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        switch (response.statusCode()) {
            case HttpServletResponse.SC_BAD_REQUEST,
                    HttpServletResponse.SC_NOT_FOUND,
                    HttpServletResponse.SC_FORBIDDEN:
                // Expected (business) errors
                throw new RuntimeException(
                        String.format("Business Validation error in Payment service %s", response.body()));
            case HttpServletResponse.SC_OK:
                // Success, getting UUID
                var idString = response.body();
                return UUID.fromString(idString);
            default:
                // Unexpected error
                throw new RuntimeException(
                        String.format("Unexpected error in Payment service. Code %s, Message %s",
                                      response.statusCode(),
                                      response.body()));
        }
    }

    @Override
    public void compensatePay(UUID paymentId) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(new URI(String.format("http://localhost:8083/cancelPayment?paymentId=%s",
                                                                    paymentId)))
                                         .DELETE()
                                         .timeout(Duration.of(10, SECONDS))
                                         .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        switch (response.statusCode()) {
            case HttpServletResponse.SC_BAD_REQUEST,
                    HttpServletResponse.SC_NOT_FOUND:
                // Expected (business) errors
                throw new RuntimeException(
                        String.format("Business Validation error during payment cancellation %s",
                                      response.body()));
            case HttpServletResponse.SC_OK:
                // Success, nothing to do
                break;
            default:
                // Unexpected error
                throw new RuntimeException(
                        String.format("Unexpected error during payment cancellation. Code %s, Message %s",
                                      response.statusCode(),
                                      response.body()));
        }

    }
}
