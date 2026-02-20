
import java.time.Duration;
import java.util.UUID;

// ── Step 4: Client Code ───────────────────────────────────────────────────────

public class ApiGatewayService {

    public static void main(String[] args) {

        // Simple — Director handles the standard recipe
        HttpRequest healthCheck = GatewayRequestDirector.buildHealthCheck(
            "https://payments-service.internal"
        );
        healthCheck.summarize();

        // Complex — full custom chain for a specific downstream call
        HttpRequest paymentRequest = HttpRequest.builder()
            .url("https://payments-service.internal/api/v2/charge")
            .method(HttpRequest.Method.POST)
            .contentType("application/json")
            .accept("application/json")
            .header("X-Correlation-ID", UUID.randomUUID().toString())
            .header("X-Gateway-Version", "2.1.0")
            .queryParam("idempotency_key", "order-98721")
            .body("""
                {
                  "amount": 15000,
                  "currency": "INR",
                  "account_id": "ACC-9821"
                }
            """)
            .bearerToken(System.getenv("PAYMENTS_SERVICE_TOKEN"))
            .timeout(Duration.ofSeconds(15))
            .maxRetries(2)
            .followRedirects(false)
            .build();

        paymentRequest.summarize();
    }
}