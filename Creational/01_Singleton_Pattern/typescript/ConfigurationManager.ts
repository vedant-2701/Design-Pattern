// Scenario: Application Configuration Manager — SaaS App
// Loads environment variables and feature flags exactly once at startup.
// Shared safely across all modules without reloading or duplicating state.

/**
 * A large SaaS application needs a single ConfigurationManager that 
 * loads environment variables and feature flags once at startup, 
 * and is safely shared across hundreds of modules without reloading 
 * or duplicating state.
 */

/**
 * Node's module system already caches require()/import results, 
 * so you could also export a plain object instance. 
 * The class-based approach below is explicit, testable, and framework-agnostic.
 */

interface AppConfig {
    readonly dbUrl: string;
    readonly port: number;
    readonly jwtSecret: string;
    readonly featureFlags: Record<string, boolean>;
    readonly environment: "development" | "staging" | "production";
}

class ConfigurationManager {
    // Step 1: Static private instance holder
    private static instance: ConfigurationManager | null = null;

    // Step 2: The actual config payload
    private readonly config: AppConfig;

    // Step 3: Private constructor — no external code can call `new ConfigurationManager()`
    private constructor() {
        console.log(`[ConfigurationManager] Initializing Configuration`);
        this.config = this.loadConfiguration();
        Object.freeze(this.config); // Ensure immutability
    }

    // Step 4: The single global access point
    public static getInstance(): ConfigurationManager {
        if (!ConfigurationManager.instance) {
            ConfigurationManager.instance = new ConfigurationManager();
        }
        return ConfigurationManager.instance;
    }
    
    // Step 5: Private loader — runs exactly once
    private loadConfiguration(): AppConfig {
        // const env = process.env;

        const env = {
            DB_URL: "mongodb://localhost:27017/myapp",
            PORT: "3000",
            JWT_SECRET: "supersecretkey",
            NODE_ENV: "development",
            FF_NEW_CHECKOUT: "true",
            FF_EXPERIMENTAL_SEARCH: "true",
            FF_DARK_MODE: "true",
        };

        if (!env.DB_URL) throw new Error("DATABASE_URL is required");
        if (!env.JWT_SECRET) throw new Error("JWT_SECRET is required");

        return {
            dbUrl: env.DB_URL,
            port: parseInt(env.PORT ?? 3000, 10),
            jwtSecret: env.JWT_SECRET,
            featureFlags: {
                newCheckout: env.FF_NEW_CHECKOUT === "true",
                experimentalSearch: env.FF_EXPERIMENTAL_SEARCH === "true",
                darkMode: env.FF_DARK_MODE === "true",
            },
            environment: env.NODE_ENV as AppConfig["environment"],
        };
    }


    // Public accessors: type-safe getters
    public get<K extends keyof AppConfig>(key: K): AppConfig[K] {
        return this.config[key];
    }

    public isFeatureEnabled(flag: string): boolean {
        return this.config.featureFlags[flag] ?? false;
    }

    public getAll(): Readonly<AppConfig> {
        return this.config;
    }

    // Prevent cloning via JSON tricks or Object.assign
    public toJSON(): string {
        throw new Error("ConfigurationManager cannot be serialized.");
    }
}

// ─── Usage across different modules ───────────────────────────────────────────

// In database.service.ts
const dbUrl = ConfigurationManager.getInstance().get("dbUrl");
console.log(dbUrl);

// In auth.service.ts
const secret = ConfigurationManager.getInstance().get("jwtSecret");
console.log(secret);

// In feature.guard.ts
const canUseNewCheckout = ConfigurationManager
    .getInstance()
    .isFeatureEnabled("newCheckoutFlow");
console.log(canUseNewCheckout);

// Proof of Singleton — both references point to the exact same object
const instanceA = ConfigurationManager.getInstance();
const instanceB = ConfigurationManager.getInstance();
console.log(instanceA === instanceB); // ✅ true — same reference in memory

export { ConfigurationManager };