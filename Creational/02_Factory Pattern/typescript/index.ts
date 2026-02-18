// ── Step 5: Client Code ───────────────────────────────────────────────────────
// The client never imports EmailNotifier, SMSNotifier, or PushNotifier directly.
// It only knows about the interface and the factory.

import { ChannelType, NotificationChannel, NotificationFactory } from "./NotificationFactory";

async function alertUser(channelType: ChannelType): Promise<void> {
    const notifier: NotificationChannel =
        NotificationFactory.create(channelType);

    await notifier.send({
        recipient:
            channelType === ChannelType.EMAIL
                ? "user@example.com"
                : "+91-9876543210",
        message: "Your order #4521 has been shipped!",
        metadata: { orderId: "4521", eta: "2 days" },
    });
}

// Usage
alertUser(ChannelType.EMAIL);
alertUser(ChannelType.SMS);
alertUser(ChannelType.PUSH);