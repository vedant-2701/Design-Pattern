/**
 * A marketing SaaS platform needs to construct complex email campaigns
 * with many optional fields — subject, body, recipients, scheduling,
 * A/B test variants, tracking options, and retry policies. Some campaigns
 * are simple, some are fully configured. The same builder handles both.
 */

// Scenario: Email Campaign Builder — Marketing SaaS Platform
// Builds complex email campaign objects step by step.
// Simple campaigns use a few steps. Complex ones chain all steps.

// ── Step 1: Product ───────────────────────────────────────────────────────────

interface TrackingOptions {
    readonly openTracking: boolean;
    readonly clickTracking: boolean;
    readonly utmSource?: string;
}

interface ABTestVariant {
    readonly variantName: string;
    readonly subjectLine: string;
    readonly percentage: number; // % of recipients who get this variant
}

interface RetryPolicy {
    readonly maxAttempts: number;
    readonly intervalMinutes: number;
}

class EmailCampaign {
    readonly id: string;
    readonly name: string;
    readonly subject: string;
    readonly htmlBody: string;
    readonly recipients: string[];
    readonly scheduledAt?: Date;
    readonly tracking: TrackingOptions;
    readonly abVariants: ABTestVariant[];
    readonly retryPolicy: RetryPolicy;
    readonly tags: string[];
    readonly replyTo?: string;

    constructor(builder: EmailCampaignBuilder) {
        if (!builder["_name"])      throw new Error("Campaign name is required");
        if (!builder["_subject"])   throw new Error("Subject is required");
        if (!builder["_htmlBody"])  throw new Error("HTML body is required");
        if (builder["_recipients"].length === 0) throw new Error("At least one recipient required");

        this.id          = `CAMP-${Date.now()}`;
        this.name        = builder["_name"];
        this.subject     = builder["_subject"];
        this.htmlBody    = builder["_htmlBody"];
        this.recipients  = builder["_recipients"];
        this.scheduledAt = builder["_scheduledAt"];
        this.tracking    = builder["_tracking"];
        this.abVariants  = builder["_abVariants"];
        this.retryPolicy = builder["_retryPolicy"];
        this.tags        = builder["_tags"];
        this.replyTo     = builder["_replyTo"];
    }

    summarize(): void {
        console.log("── Email Campaign ────────────────────────────────");
        console.log(`ID:           ${this.id}`);
        console.log(`Name:         ${this.name}`);
        console.log(`Subject:      ${this.subject}`);
        console.log(`Recipients:   ${this.recipients.length} addresses`);
        console.log(`Scheduled:    ${this.scheduledAt?.toISOString() ?? "Immediate"}`);
        console.log(`A/B Variants: ${this.abVariants.length}`);
        console.log(`Tracking:     Opens=${this.tracking.openTracking} Clicks=${this.tracking.clickTracking}`);
        console.log(`Retry:        ${this.retryPolicy.maxAttempts}x every ${this.retryPolicy.intervalMinutes}min`);
        console.log(`Tags:         ${this.tags.join(", ") || "none"}`);
        console.log("──────────────────────────────────────────────────");
    }
}

// ── Step 2: Builder ───────────────────────────────────────────────────────────
class EmailCampaignBuilder {
    // Private fields with sensible defaults
    private _name: string = "";
    private _subject: string = "";
    private _htmlBody: string = "";
    private _recipients: string[] = [];
    private _scheduledAt?: Date;
    private _replyTo?: string;
    private _tags: string[] = [];
    private _abVariants: ABTestVariant[] = [];

    private _tracking: TrackingOptions = {
        openTracking: true,
        clickTracking: true,
    };

    private _retryPolicy: RetryPolicy = {
        maxAttempts: 3,
        intervalMinutes: 30,
    };

    // ── Fluent setter methods (each returns `this` for chaining) ───────────────

    setName(name: string): this {
        this._name = name;
        return this;
    }

    setSubject(subject: string): this {
        this._subject = subject;
        return this;
    }

    setHtmlBody(html: string): this {
        this._htmlBody = html;
        return this;
    }

    addRecipients(...emails: string[]): this {
        this._recipients.push(...emails);
        return this;
    }

    scheduleAt(date: Date): this {
        this._scheduledAt = date;
        return this;
    }

    setReplyTo(email: string): this {
        this._replyTo = email;
        return this;
    }

    addTag(...tags: string[]): this {
        this._tags.push(...tags);
        return this;
    }

    setTracking(options: Partial<TrackingOptions>): this {
        this._tracking = { ...this._tracking, ...options };
        return this;
    }

    addABVariant(variant: ABTestVariant): this {
        const totalPercentage = this._abVariants.reduce(
            (sum, v) => sum + v.percentage,
            0,
        );
        if (totalPercentage + variant.percentage > 100) {
            throw new Error("A/B variant percentages cannot exceed 100%");
        }
        this._abVariants.push(variant);
        return this;
    }

    setRetryPolicy(policy: Partial<RetryPolicy>): this {
        this._retryPolicy = { ...this._retryPolicy, ...policy };
        return this;
    }

    // ── Terminal method — assembles and returns the final product ──────────────
    build(): EmailCampaign {
        return new EmailCampaign(this);
    }
}


// ── Step 3: Director — knows recipes for common campaign types ────────────────

class CampaignDirector {

    // Recipe: minimal transactional email
    static buildTransactional(
        builder: EmailCampaignBuilder,
        recipient: string,
        subject: string,
        body: string
    ): EmailCampaign {
        return builder
            .setName("Transactional Email")
            .setSubject(subject)
            .setHtmlBody(body)
            .addRecipients(recipient)
            .setTracking({ openTracking: false, clickTracking: false })
            .setRetryPolicy({ maxAttempts: 5, intervalMinutes: 10 })
            .build();
    }

    // Recipe: full-featured A/B marketing campaign
    static buildABMarketingCampaign(
        builder: EmailCampaignBuilder,
        recipients: string[]
    ): EmailCampaign {
        return builder
            .setName("Q1 Product Launch Campaign")
            .setSubject("Default Subject — overridden by A/B variants")
            .setHtmlBody("<h1>Our biggest launch yet</h1><p>Check out what's new...</p>")
            .addRecipients(...recipients)
            .addABVariant({ variantName: "Variant A", subjectLine: "🚀 Something big is here",  percentage: 50 })
            .addABVariant({ variantName: "Variant B", subjectLine: "You won't want to miss this", percentage: 50 })
            .setTracking({ openTracking: true, clickTracking: true, utmSource: "email-q1" })
            .scheduleAt(new Date("2026-03-01T09:00:00Z"))
            .addTag("product-launch", "q1-2026", "marketing")
            .setReplyTo("campaigns@company.com")
            .build();
    }
}

export { EmailCampaignBuilder, CampaignDirector, EmailCampaign };