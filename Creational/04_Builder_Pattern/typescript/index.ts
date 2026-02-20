// ── Step 4: Client Code ───────────────────────────────────────────────────────

import { CampaignDirector, EmailCampaignBuilder } from "./EmailCampaignBuilder";

// Simple — use Director for standard recipe
const transactional = CampaignDirector.buildTransactional(
    new EmailCampaignBuilder(),
    "user@example.com",
    "Your invoice is ready",
    "<p>Please find your invoice attached.</p>",
);
transactional.summarize();

// Complex — chain builder steps directly for full custom control
const custom = new EmailCampaignBuilder()
    .setName("Black Friday Flash Sale")
    .setSubject("⚡ 24 Hours Only — 60% Off Everything")
    .setHtmlBody("<h1>Black Friday is here!</h1>")
    .addRecipients("a@example.com", "b@example.com", "c@example.com")
    .addTag("black-friday", "flash-sale")
    .setTracking({ utmSource: "email-bf-2026" })
    .setRetryPolicy({ maxAttempts: 2, intervalMinutes: 60 })
    .build();
custom.summarize();

