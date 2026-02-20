/**
 * A FinTech platform must support both PostgreSQL and MongoDB as backends
 * depending on deployment environment. Every database operation —
 * connection, query builder, transaction manager — must come from the
 * same DB family. Mixing a Postgres connection with a Mongo query builder
 * would be catastrophic.
 */

// ── Step 1: Abstract Product Interfaces ───────────────────────────────────────

interface DBConnection {
    void connect();

    void disconnect();

    boolean isAlive();
}

interface QueryBuilder {
    QueryBuilder select(String... fields);

    QueryBuilder from(String table);

    QueryBuilder where(String condition);

    String build();
}

interface TransactionManager {
    void beginTransaction();

    void commit();

    void rollback();
}

// ── Step 2: Abstract Factory Interface ────────────────────────────────────────

interface DatabaseFactory {
    DBConnection createConnection();

    QueryBuilder createQueryBuilder();

    TransactionManager createTransactionManager();
}

// ── Step 3A: PostgreSQL Concrete Products ─────────────────────────────────────
class PostgresConnection implements DBConnection {
    private boolean connected = false;
    private final String url = System.getenv("POSTGRES_URL") != null ? System.getenv("POSTGRES_URL") : "jdbc:postgresql://localhost:5432/mydb";

    @Override
    public void connect() {
        connected = true;
        System.out.println("[PostgreSQL] ✅ Connected to: " + url);
    }

    @Override
    public void disconnect() {
        connected = false;
        System.out.println("[PostgreSQL] 🔌 Disconnected.");
    }

    @Override
    public boolean isAlive() {
        return connected;
    }
}

class PostgresQueryBuilder implements QueryBuilder {
    private String fields = "*";
    private String table = "";
    private String condition = "";

    @Override
    public QueryBuilder select(String... fields) {
        this.fields = String.join(", ", fields);
        return this;
    }

    @Override
    public QueryBuilder from(String table) {
        this.table = table;
        return this;
    }

    @Override
    public QueryBuilder where(String condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public String build() {
        String query = String.format("SELECT %s FROM %s", fields, table);
        if (!condition.isEmpty())
            query += " WHERE " + condition;
        System.out.println("[PostgreSQL] 📝 Query: " + query);
        return query;
    }
}

class PostgresTransactionManager implements TransactionManager {
    @Override
    public void beginTransaction() {
        System.out.println("[PostgreSQL] 🔒 BEGIN TRANSACTION");
    }

    @Override
    public void commit() {
        System.out.println("[PostgreSQL] ✅ COMMIT");
    }

    @Override
    public void rollback() {
        System.out.println("[PostgreSQL] ↩️ ROLLBACK");
    }
}

// ── Step 3B: MongoDB Concrete Products ───────────────────────────────────────

class MongoConnection implements DBConnection {
    private boolean connected = false;
    private final String uri = System.getenv("MONGO_URI") != null ? System.getenv("MONGO_URI") : "mongodb://localhost:27017";

    @Override
    public void connect() {
        connected = true;
        System.out.println("[MongoDB] ✅ Connected to: " + uri);
    }

    @Override
    public void disconnect() {
        connected = false;
        System.out.println("[MongoDB] 🔌 Disconnected.");
    }

    @Override
    public boolean isAlive() {
        return connected;
    }
}

class MongoQueryBuilder implements QueryBuilder {
    private String collection = "";
    private String filter = "{}";
    private String projection = "{}";

    @Override
    public QueryBuilder select(String... fields) {
        // MongoDB projection
        this.projection = "{ " + String.join(": 1, ", fields) + ": 1 }";
        return this;
    }

    @Override
    public QueryBuilder from(String collection) {
        this.collection = collection;
        return this;
    }

    @Override
    public QueryBuilder where(String condition) {
        this.filter = "{ " + condition + " }";
        return this;
    }

    @Override
    public String build() {
        String query = String.format(
                "db.%s.find(%s, %s)", collection, filter, projection);
        System.out.println("[MongoDB] 📝 Query: " + query);
        return query;
    }
}

class MongoTransactionManager implements TransactionManager {
    @Override
    public void beginTransaction() {
        System.out.println("[MongoDB] 🔒 startSession() + startTransaction()");
    }

    @Override
    public void commit() {
        System.out.println("[MongoDB] ✅ commitTransaction()");
    }

    @Override
    public void rollback() {
        System.out.println("[MongoDB] ↩️  abortTransaction()");
    }
}

// ── Step 4: Concrete Factories ────────────────────────────────────────────────

class PostgresDatabaseFactory implements DatabaseFactory {
    @Override
    public DBConnection createConnection() {
        return new PostgresConnection();
    }

    @Override
    public QueryBuilder createQueryBuilder() {
        return new PostgresQueryBuilder();
    }

    @Override
    public TransactionManager createTransactionManager() {
        return new PostgresTransactionManager();
    }
}

class MongoDatabaseFactory implements DatabaseFactory {
    @Override
    public DBConnection createConnection() {
        return new MongoConnection();
    }

    @Override
    public QueryBuilder createQueryBuilder() {
        return new MongoQueryBuilder();
    }

    @Override
    public TransactionManager createTransactionManager() {
        return new MongoTransactionManager();
    }
}

// ── Step 5: Factory Provider ──────────────────────────────────────────────────

enum DatabaseType {
    POSTGRES,
    MONGODB
}

public class DatabaseFactoryProvider {
    public static DatabaseFactory getFactory(DatabaseType type) {
        return switch (type) {
            case POSTGRES -> new PostgresDatabaseFactory();
            case MONGODB -> new MongoDatabaseFactory();   
        };
    }
}
