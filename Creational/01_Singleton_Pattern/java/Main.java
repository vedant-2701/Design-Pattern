public class Main {
    public static void main(String[] args) {
        System.out.println("=== Singleton Pattern Test ===\n");
        
        // Test 1: Verify Single Instance
        System.out.println("Test 1: Verifying Singleton behavior...");
        testSingletonInstance();
        
        // Test 2: Thread-Safety Test
        System.out.println("\nTest 2: Testing thread-safety...");
        testThreadSafety();
        
        System.out.println("\n=== All Tests Completed ===");
    }
    
    /**
     * Test 1: Verify that only one instance is created
     */
    private static void testSingletonInstance() {
        try {
            ConnectionPoolManager instance1 = ConnectionPoolManager.getInstance();
            ConnectionPoolManager instance2 = ConnectionPoolManager.getInstance();
            ConnectionPoolManager instance3 = ConnectionPoolManager.getInstance();
            
            // Check if all references point to the same object
            if (instance1 == instance2 && instance2 == instance3) {
                System.out.println("✅ PASS: All instances are the same object");
                System.out.println("   Instance 1 hashCode: " + System.identityHashCode(instance1));
                System.out.println("   Instance 2 hashCode: " + System.identityHashCode(instance2));
                System.out.println("   Instance 3 hashCode: " + System.identityHashCode(instance3));
            } else {
                System.out.println("❌ FAIL: Multiple instances created!");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Test skipped: " + e.getMessage());
            System.out.println("   (Database connection not available - this is expected for demo)");
        }
    }
    
    /**
     * Test 2: Verify thread-safety of Singleton instantiation
     */
    private static void testThreadSafety() {
        final int THREAD_COUNT = 10;
        final ConnectionPoolManager[] instances = new ConnectionPoolManager[THREAD_COUNT];
        Thread[] threads = new Thread[THREAD_COUNT];
        
        // Create multiple threads that try to get instance simultaneously
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    instances[index] = ConnectionPoolManager.getInstance();
                } catch (Exception e) {
                    // Ignore - database might not be available
                }
            });
        }
        
        // Start all threads
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
            ConnectionPoolManager firstInstance = instances[0];
            
            if (firstInstance != null) {
                for (int i = 1; i < THREAD_COUNT; i++) {
                    if (instances[i] != firstInstance) {
                        allSame = false;
                        break;
                    }
                }
                
                if (allSame) {
                    System.out.println("✅ PASS: Thread-safe - all " + THREAD_COUNT 
                        + " threads got the same instance");
                } else {
                    System.out.println("❌ FAIL: Multiple instances created in multi-threaded environment!");
                }
            } else {
                System.out.println("⚠️ Test skipped: Database connection not available");
            }
        } catch (InterruptedException e) {
            System.out.println("❌ Thread test interrupted: " + e.getMessage());
        }
    }
}
