// Test file for ConfigurationManager Singleton Pattern

import { ConfigurationManager } from "./ConfigurationManager";

console.log("=== Singleton Pattern Test (TypeScript) ===\n");

// Test 1: Verify Single Instance
console.log("Test 1: Verifying Singleton behavior...");
testSingletonInstance();

// Test 2: Verify Configuration Loading
console.log("\nTest 2: Testing configuration access...");
testConfigurationAccess();

// Test 3: Verify Immutability
console.log("\nTest 3: Testing immutability...");
testImmutability();

// Test 4: Verify Serialization Protection
console.log("\nTest 4: Testing serialization protection...");
testSerializationProtection();

console.log("\n=== All Tests Completed ===");

/**
 * Test 1: Verify that only one instance is created
 */
function testSingletonInstance(): void {
    const instance1 = ConfigurationManager.getInstance();
    const instance2 = ConfigurationManager.getInstance();
    const instance3 = ConfigurationManager.getInstance();

    if (instance1 === instance2 && instance2 === instance3) {
        console.log("✅ PASS: All instances are the same object");
        console.log("   Instance 1 === Instance 2:", instance1 === instance2);
        console.log("   Instance 2 === Instance 3:", instance2 === instance3);
    } else {
        console.log("❌ FAIL: Multiple instances created!");
    }
}

/**
 * Test 2: Verify configuration values are accessible
 */
function testConfigurationAccess(): void {
    const config = ConfigurationManager.getInstance();

    try {
        const dbUrl = config.get("dbUrl");
        const port = config.get("port");
        const jwtSecret = config.get("jwtSecret");
        const env = config.get("environment");

        console.log("✅ PASS: Configuration loaded successfully");
        console.log("   Database URL:", dbUrl);
        console.log("   Port:", port);
        console.log("   JWT Secret:", jwtSecret ? "***" + jwtSecret.slice(-4) : "not set");
        console.log("   Environment:", env);

        // Test feature flags
        const hasNewCheckout = config.isFeatureEnabled("newCheckout");
        const hasDarkMode = config.isFeatureEnabled("darkMode");

        console.log("\n   Feature Flags:");
        console.log("   - New Checkout:", hasNewCheckout);
        console.log("   - Dark Mode:", hasDarkMode);

        if (typeof dbUrl === "string" && typeof port === "number") {
            console.log("\n✅ PASS: Type safety maintained");
        } else {
            console.log("\n❌ FAIL: Type safety violated");
        }
    } catch (error) {
        console.log("❌ FAIL: Configuration access failed:", error);
    }
}

/**
 * Test 3: Verify configuration is immutable
 */
function testImmutability(): void {
    const config = ConfigurationManager.getInstance();
    const allConfig = config.getAll();

    try {
        // Attempt to modify the config (should fail silently or throw in strict mode)
        (allConfig as any).dbUrl = "hacked://malicious.com";

        // Check if modification actually happened
        const currentDbUrl = config.get("dbUrl");

        if (currentDbUrl !== "hacked://malicious.com") {
            console.log("✅ PASS: Configuration is immutable");
            console.log("   Original value preserved:", currentDbUrl);
        } else {
            console.log("❌ FAIL: Configuration was modified!");
        }
    } catch (error) {
        console.log("✅ PASS: Configuration is immutable (threw error on modification)");
    }
}

/**
 * Test 4: Verify serialization is prevented
 */
function testSerializationProtection(): void {
    const config = ConfigurationManager.getInstance();

    try {
        JSON.stringify(config);
        console.log("❌ FAIL: Serialization should be prevented");
    } catch (error) {
        if (error instanceof Error && error.message.includes("cannot be serialized")) {
            console.log("✅ PASS: Serialization is prevented");
            console.log("   Error message:", error.message);
        } else {
            console.log("⚠️  PARTIAL: Serialization failed with unexpected error");
        }
    }
}

// Additional demonstration: Usage across simulated modules
console.log("\n=== Simulating Cross-Module Usage ===");

// Simulating different modules accessing the same instance
class DatabaseService {
    private dbUrl: string;

    constructor() {
        this.dbUrl = ConfigurationManager.getInstance().get("dbUrl");
    }

    connect() {
        console.log(`[DatabaseService] Connecting to: ${this.dbUrl}`);
    }
}

class AuthService {
    private jwtSecret: string;

    constructor() {
        this.jwtSecret = ConfigurationManager.getInstance().get("jwtSecret");
    }

    generateToken() {
        console.log(`[AuthService] Using JWT secret: ***${this.jwtSecret.slice(-4)}`);
    }
}

class FeatureGuard {
    isNewCheckoutEnabled(): boolean {
        return ConfigurationManager.getInstance().isFeatureEnabled("newCheckout");
    }

    checkFeature() {
        console.log(`[FeatureGuard] New checkout enabled: ${this.isNewCheckoutEnabled()}`);
    }
}

// Each service gets the same configuration instance
const dbService = new DatabaseService();
const authService = new AuthService();
const featureGuard = new FeatureGuard();

dbService.connect();
authService.generateToken();
featureGuard.checkFeature();

console.log("\n✅ All services share the same configuration instance");
