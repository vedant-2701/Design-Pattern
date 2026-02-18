// Scenario: Notification Service — SaaS App
// Client asks for a notifier by channel. Factory builds the right one.

/**
 * A SaaS platform needs to send notifications via different channels 
 * — Email, SMS, and Push. The rest of the application just asks for a notifier 
 * by channel type and calls .send() — it never knows which 
 * third-party SDK is being used underneath.
 */

// ── Step 1: Common Interface ──────────────────────────────────────────────────
interface CustomNotification {
    readonly recipient: string;
    readonly message: string;
    readonly metadata?: Record<string, unknown>;
}

interface NotificationChannel {
    send(notification: CustomNotification): Promise<void>;
}

// ── Step 2: Concrete Implementations ─────────────────────────────────────────

class EmailNotifier implements NotificationChannel {
    async send(notification: CustomNotification): Promise<void> {
        // In production: integrate SendGrid / AWS SES here
        console.log(`[EMAIL] → To: ${notification.recipient}`);
        console.log(`          Message: ${notification.message}`);
    }
}

class SMSNotifier implements NotificationChannel {
    async send(notification: CustomNotification): Promise<void> {
        // In production: integrate Twilio / AWS SNS here
        console.log(`[SMS] → To: ${notification.recipient}`);
        console.log(`        Message: ${notification.message}`);
    }
}

class PushNotifier implements NotificationChannel {
    async send(notification: CustomNotification): Promise<void> {
        // In production: integrate Firebase FCM here
        console.log(`[PUSH] → Device: ${notification.recipient}`);
        console.log(`         Message: ${notification.message}`);
        console.log(`         Metadata: ${JSON.stringify(notification.metadata)}`);
    }
}

// ── Step 3: Channel Type Enum ─────────────────────────────────────────────────

enum ChannelType {
    EMAIL = "EMAIL",
    SMS = "SMS",
    PUSH = "PUSH",
}

// ── Step 4: The Factory ───────────────────────────────────────────────────────
class NotificationFactory {
    private static readonly registry: Record<
        ChannelType,
        () => NotificationChannel
    > = {
        [ChannelType.EMAIL]: () => new EmailNotifier(),
        [ChannelType.SMS]: () => new SMSNotifier(),
        [ChannelType.PUSH]: () => new PushNotifier(),
    };

    /**
     * Returns the correct NotificationChannel for the given type.
     * Adding a new channel = add one line to the registry. Nothing else changes.
     */
    public static create(channel: ChannelType): NotificationChannel {
        const factory = NotificationFactory.registry[channel];

        if (!factory) {
            throw new Error(
                `[NotificationFactory] Unknown channel type: "${channel}"`,
            );
        }

        return factory();
    }
}

export { NotificationFactory, ChannelType, NotificationChannel, CustomNotification };