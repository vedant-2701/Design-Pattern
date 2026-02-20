// Scenario: Database Connection Pool Manager — Banking System
// Manages a fixed pool of expensive DB connections shared across
// all threads handling concurrent banking transactions.

/**
 * A high-traffic Banking System needs a single ConnectionPoolManager 
 * that manages a fixed pool of expensive database connections. 
 * Multiple threads (handling thousands of concurrent transactions) 
 * must all share the same pool — creating a new pool per thread 
 * would instantly exhaust DB resources.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConnectionPoolManager {
    // ── Singleton mechanics ──────────────────────────────────────────────────

    // Volatile ensures the instance reference is visible across all threads
    // after it is written — prevents CPU cache inconsistency
    private static volatile ConnectionPoolManager instance = null;

    private ConnectionPoolManager() {
        initializePool();
    }

    /**
     * Thread-safe Singleton via Double-Checked Locking (DCL).
     *
     * Outer null-check → skips expensive synchronization on every call after init
     * synchronized block → only one thread enters during first-time creation
     * Inner null-check → prevents two threads that both passed outer check from
     * each creating their own instance
     */
    public static ConnectionPoolManager getInstance() {
        // First check (without locking) for performance
        if (instance == null) {
            synchronized (ConnectionPoolManager.class) {
                // Second check (with locking) to ensure only one instance is created
                if (instance == null) {
                    instance = new ConnectionPoolManager();
                }
            }
        }
        return instance;
    }

    // ── Connection Pool Logic ────────────────────────────────────────────────

    private static final int POOL_SIZE = 10;
    private static final int ACQUIRE_TIMEOUT = 5; // seconds
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "bank_user";
    private static final String DB_PASS = "bank_password";
    /* When you have env file */
    // private static final String DB_URL = System.getenv("BANK_DB_URL");
    // private static final String DB_USER = System.getenv("BANK_DB_USER");
    // private static final String DB_PASS = System.getenv("BANK_DB_PASS");

    private BlockingQueue<Connection> availableConnections;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    private void initializePool() {
        availableConnections = new ArrayBlockingQueue<>(POOL_SIZE);
        System.out.println("[ConnectionPoolManager] Warming up " + POOL_SIZE + " connections...");

        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                availableConnections.offer(conn);
                System.out.println("[ConnectionPoolManager] Connection " + (i + 1) + " established and added to pool.");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize DB connection pool", e);
            }
        }
        System.out.println("[ConnectionPoolManager] Pool ready.");
    }

    /**
     * Borrow a connection. Blocks up to ACQUIRE_TIMEOUT seconds if pool is full.
     */
    public Connection acquireConnection() throws InterruptedException, SQLException {
        Connection conn = availableConnections.poll(ACQUIRE_TIMEOUT, TimeUnit.SECONDS);

        if (conn == null) {
            throw new SQLException(
                    "Connection pool exhausted. No connection available within "
                            + ACQUIRE_TIMEOUT + "s. Active connections: " + activeConnections.get());
        }

        activeConnections.incrementAndGet();
        return conn;
    }

    /**
     * Return a connection to the pool after use. Always call in a finally block.
     */
    public void releaseConnection(Connection conn) {
        if (conn != null) {
            availableConnections.offer(conn);
            activeConnections.decrementAndGet();
        }
    }

    public int getAvailableCount() {
        return availableConnections.size();
    }

    public int getActiveCount() {
        return activeConnections.get();
    }

    // Prevent second instance via Java serialization
    private Object readResolve() {
        return instance;
    }
}
