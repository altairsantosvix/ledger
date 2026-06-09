
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEvent(
        String transactionId,
        String sourceAccount,
        String targetAccount,
        BigDecimal amount,
        Instant timestamp
) {}
