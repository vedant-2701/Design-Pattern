// Mock version of ConnectionPoolManager for testing without database
// Simulates connection pool behavior without actual DB connections

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class MockConnectionPoolManager {
    // ── Singleton mechanics ──────────────────────────────────────────────────

    private static volatile MockConnectionPoolManager instance = null;

    private MockConnectionPoolManager() {
        initializePool();
    }

    /**
     * Thread-safe Singleton via Double-Checked Locking (DCL).
     */
    public static MockConnectionPoolManager getInstance() {
        // First check (without locking) for performance
        if (instance == null) {
            synchronized (MockConnectionPoolManager.class) {
                // Second check (with locking) to ensure only one instance is created
                if (instance == null) {
                    instance = new MockConnectionPoolManager();
                }
            }
        }
        return instance;
    }

    // ── Mock Connection Pool Logic ───────────────────────────────────────────

    private static final int POOL_SIZE = 10;
    private static final int ACQUIRE_TIMEOUT = 5; // seconds

    private BlockingQueue<MockConnection> availableConnections;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicInteger totalConnectionsCreated = new AtomicInteger(0);

    private void initializePool() {
        availableConnections = new ArrayBlockingQueue<>(POOL_SIZE);
        System.out.println("[MockConnectionPoolManager] Warming up " + POOL_SIZE + " mock connections...");

        for (int i = 0; i < POOL_SIZE; i++) {
            MockConnection conn = new MockConnection("CONN-" + (i + 1));
            availableConnections.offer(conn);
            totalConnectionsCreated.incrementAndGet();
            System.out.println("[MockConnectionPoolManager] Mock connection " + (i + 1) + " created and added to pool.");
        }
        System.out.println("[MockConnectionPoolManager] Pool ready with " + POOL_SIZE + " connections.\n");
    }

    /**
     * Borrow a connection. Blocks up to ACQUIRE_TIMEOUT seconds if pool is full.
     */
    public MockConnection acquireConnection() throws InterruptedException {
        MockConnection conn = availableConnections.poll(ACQUIRE_TIMEOUT, TimeUnit.SECONDS);

        if (conn == null) {
            throw new RuntimeException(
                    "Connection pool exhausted. No connection available within "
                            + ACQUIRE_TIMEOUT + "s. Active connections: " + activeConnections.get());
        }

        activeConnections.incrementAndGet();
        System.out.println("   [Pool] Connection acquired: " + conn.getId() + 
                         " (Active: " + activeConnections.get() + 
                         ", Available: " + availableConnections.size() + ")");
        return conn;
    }

    /**
     * Return a connection to the pool after use. Always call in a finally block.
     */
    public void releaseConnection(MockConnection conn) {
        if (conn != null) {
            availableConnections.offer(conn);
            activeConnections.decrementAndGet();
            System.out.println("   [Pool] Connection released: " + conn.getId() + 
                             " (Active: " + activeConnections.get() + 
                             ", Available: " + availableConnections.size() + ")");
        }
    }

    public int getAvailableCount() {
        return availableConnections.size();
    }

    public int getActiveCount() {
        return activeConnections.get();
    }

    public int getTotalConnectionsCreated() {
        return totalConnectionsCreated.get();
    }

    // Prevent second instance via Java serialization
    private Object readResolve() {
        return instance;
    }

    // Reset for testing purposes (normally you wouldn't have this in production)
    public static void resetInstance() {
        instance = null;
    }
}

/**
 * Mock Connection class to simulate database connections
 */
class MockConnection {
    private final String id;
    private boolean autoCommit = true;
    private boolean closed = false;

    public MockConnection(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
        System.out.println("      [" + id + "] Auto-commit set to: " + autoCommit);
    }

    public void commit() {
        if (closed) {
            throw new RuntimeException("Connection is closed");
        }
        System.out.println("      [" + id + "] Transaction COMMITTED");
    }

    public void rollback() {
        if (closed) {
            throw new RuntimeException("Connection is closed");
        }
        System.out.println("      [" + id + "] Transaction ROLLED BACK");
    }

    public MockPreparedStatement prepareStatement(String sql) {
        return new MockPreparedStatement(this, sql);
    }

    public void close() {
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}

/**
 * Mock PreparedStatement class
 */
class MockPreparedStatement {
    private final MockConnection connection;
    private final String sql;
    private Object[] parameters = new Object[10];

    public MockPreparedStatement(MockConnection connection, String sql) {
        this.connection = connection;
        this.sql = sql;
    }

    public void setDouble(int index, double value) {
        parameters[index] = value;
    }

    public void setString(int index, String value) {
        parameters[index] = value;
    }

    public int executeUpdate() {
        System.out.println("      [" + connection.getId() + "] Executing: " + sql);
        return 1; // Simulate 1 row affected
    }
}
