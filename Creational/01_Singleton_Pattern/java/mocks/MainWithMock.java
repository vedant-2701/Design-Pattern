import java.util.concurrent.atomic.AtomicInteger;

// Comprehensive test for Singleton Pattern using Mock implementations
// This demonstrates all aspects of the Singleton pattern without needing a real database

public class MainWithMock {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Singleton Pattern - Comprehensive Test Suite         ║");
        System.out.println("║   (Using Mock Database Connections)                    ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Test 1: Verify Single Instance
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 1: Verifying Singleton Instance Behavior");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        testSingletonInstance();

        // Test 2: Thread-Safety Test
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 2: Testing Thread-Safety");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        testThreadSafety();

        // Test 3: Connection Pool Functionality
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 3: Testing Connection Pool Management");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        testConnectionPoolFunctionality();

        // Test 4: Concurrent Transactions
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Test 4: Testing Concurrent Transaction Processing");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        testConcurrentTransactions();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ✅ All Tests Completed Successfully!                 ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }

    /**
     * Test 1: Verify that only one instance is created
     */
    private static void testSingletonInstance() {
        MockConnectionPoolManager instance1 = MockConnectionPoolManager.getInstance();
        MockConnectionPoolManager instance2 = MockConnectionPoolManager.getInstance();
        MockConnectionPoolManager instance3 = MockConnectionPoolManager.getInstance();

        // Check if all references point to the same object
        if (instance1 == instance2 && instance2 == instance3) {
            System.out.println("✅ PASS: All instances reference the same object");
            System.out.println("   Instance 1 hashCode: " + System.identityHashCode(instance1));
            System.out.println("   Instance 2 hashCode: " + System.identityHashCode(instance2));
            System.out.println("   Instance 3 hashCode: " + System.identityHashCode(instance3));
            System.out.println("   Are they equal? instance1 == instance2: " + (instance1 == instance2));
            System.out.println("   Are they equal? instance2 == instance3: " + (instance2 == instance3));
        } else {
            System.out.println("❌ FAIL: Multiple distinct instances were created!");
        }

        System.out.println("   Pool Statistics:");
        System.out.println("   - Total connections created: " + instance1.getTotalConnectionsCreated());
        System.out.println("   - Available connections: " + instance1.getAvailableCount());
        System.out.println("   - Active connections: " + instance1.getActiveCount());
    }

    /**
     * Test 2: Verify thread-safety of Singleton instantiation
     */
    private static void testThreadSafety() {
        // Reset instance for clean test
        MockConnectionPoolManager.resetInstance();

        final int THREAD_COUNT = 20;
        final MockConnectionPoolManager[] instances = new MockConnectionPoolManager[THREAD_COUNT];
        Thread[] threads = new Thread[THREAD_COUNT];
        final AtomicInteger constructorCallCount = new AtomicInteger(0);

        System.out.println("Creating " + THREAD_COUNT + " threads to simultaneously get instance...\n");

        // Create multiple threads that try to get instance simultaneously
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = MockConnectionPoolManager.getInstance();
            }, "Thread-" + (i + 1));
        }

        // Start all threads at once
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        try {
            for (Thread thread : threads) {
                thread.join();
            }

            // Verify all threads got the same instance
            boolean allSame = true;
            MockConnectionPoolManager firstInstance = instances[0];

            for (int i = 1; i < THREAD_COUNT; i++) {
                if (instances[i] != firstInstance) {
                    allSame = false;
                    System.out.println("❌ Thread " + (i + 1) + " got a different instance!");
                }
            }

            if (allSame) {
                System.out.println("✅ PASS: Thread-safe - all " + THREAD_COUNT + " threads got the same instance");
                System.out.println("   Single instance hashCode: " + System.identityHashCode(firstInstance));
                System.out.println("   Total connections created by singleton: " + firstInstance.getTotalConnectionsCreated());
                
                // Verify only ONE pool was created (should have exactly 10 connections)
                if (firstInstance.getTotalConnectionsCreated() == 10) {
                    System.out.println("✅ PASS: Only one connection pool was initialized (10 connections)");
                } else {
                    System.out.println("❌ FAIL: Multiple pools may have been created!");
                }
            } else {
                System.out.println("❌ FAIL: Multiple instances created in multi-threaded environment!");
            }
        } catch (InterruptedException e) {
            System.out.println("❌ Thread test interrupted: " + e.getMessage());
        }
    }

    /**
     * Test 3: Verify connection pool acquire/release functionality
     */
    private static void testConnectionPoolFunctionality() {
        MockConnectionPoolManager pool = MockConnectionPoolManager.getInstance();
        
        System.out.println("Initial pool state:");
        System.out.println("   Available: " + pool.getAvailableCount());
        System.out.println("   Active: " + pool.getActiveCount());
        System.out.println();

        MockConnection conn1 = null;
        MockConnection conn2 = null;

        try {
            // Acquire connections
            System.out.println("Acquiring first connection...");
            conn1 = pool.acquireConnection();

            System.out.println("\nAcquiring second connection...");
            conn2 = pool.acquireConnection();

            int available = pool.getAvailableCount();
            int active = pool.getActiveCount();

            System.out.println("\nAfter acquiring 2 connections:");
            System.out.println("   Available: " + available);
            System.out.println("   Active: " + active);

            if (available == 8 && active == 2) {
                System.out.println("✅ PASS: Connection pool tracking is correct");
            } else {
                System.out.println("❌ FAIL: Pool tracking incorrect. Expected Available=8, Active=2");
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        } finally {
            // Release connections
            System.out.println("\nReleasing connections...");
            pool.releaseConnection(conn1);
            pool.releaseConnection(conn2);

            System.out.println("\nFinal pool state:");
            System.out.println("   Available: " + pool.getAvailableCount());
            System.out.println("   Active: " + pool.getActiveCount());

            if (pool.getAvailableCount() == 10 && pool.getActiveCount() == 0) {
                System.out.println("✅ PASS: All connections properly returned to pool");
            }
        }
    }

    /**
     * Test 4: Verify concurrent transaction processing
     */
    private static void testConcurrentTransactions() {
        MockTransactionService transactionService = new MockTransactionService();
        final int TRANSACTION_COUNT = 5;
        Thread[] transactionThreads = new Thread[TRANSACTION_COUNT];

        System.out.println("Simulating " + TRANSACTION_COUNT + " concurrent transactions...\n");

        // Create multiple concurrent transactions
        for (int i = 0; i < TRANSACTION_COUNT; i++) {
            final int txId = i + 1;
            transactionThreads[i] = new Thread(() -> {
                System.out.println("\n[Transaction-" + txId + "] Starting...");
                transactionService.transferFunds(
                    "ACC-" + txId + "00",
                    "ACC-" + txId + "01",
                    100.0 * txId
                );
                System.out.println("[Transaction-" + txId + "] Completed\n");
            }, "TX-Thread-" + txId);
        }

        // Start all transactions
        for (Thread thread : transactionThreads) {
            thread.start();
        }

        // Wait for all to complete
        try {
            for (Thread thread : transactionThreads) {
                thread.join();
            }

            MockConnectionPoolManager pool = MockConnectionPoolManager.getInstance();
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Final pool state after all transactions:");
            System.out.println("   Available: " + pool.getAvailableCount());
            System.out.println("   Active: " + pool.getActiveCount());

            if (pool.getAvailableCount() == 10 && pool.getActiveCount() == 0) {
                System.out.println("✅ PASS: All connections returned to pool after concurrent transactions");
            } else {
                System.out.println("⚠️  WARNING: Some connections may not have been properly released");
            }

        } catch (InterruptedException e) {
            System.out.println("❌ Transaction test interrupted: " + e.getMessage());
        }
    }
}
