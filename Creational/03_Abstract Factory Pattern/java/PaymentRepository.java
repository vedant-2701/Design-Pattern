// ── Step 6: Client — Payment Repository ──────────────────────────────────────

class PaymentRepository {
    private final DBConnection connection;
    private final QueryBuilder queryBuilder;
    private final TransactionManager txManager;

    public PaymentRepository(DatabaseFactory factory) {
        // All components guaranteed to be from the same DB family
        this.connection = factory.createConnection();
        this.queryBuilder = factory.createQueryBuilder();
        this.txManager = factory.createTransactionManager();
        this.connection.connect();
    }

    public void processPayment(String accountId, double amount) {
        try {
            txManager.beginTransaction();

            String query = queryBuilder
                    .select("account_id", "balance")
                    .from("accounts")
                    .where("account_id = '" + accountId + "'")
                    .build();

            // In production: execute query, update balance...
            System.out.printf("[PaymentRepository] Processing ₹%.2f for account %s%n",
                    amount, accountId);

            txManager.commit();
        } catch (Exception e) {
            txManager.rollback();
            throw new RuntimeException("Payment processing failed", e);
        }
    }

    public static void main(String[] args) {
        // Swap POSTGRES → MONGODB and everything changes consistently
        DatabaseType dbType = DatabaseType.POSTGRES;

        DatabaseFactory factory = DatabaseFactoryProvider.getFactory(dbType);
        PaymentRepository repo = new PaymentRepository(factory);

        repo.processPayment("ACC-9821", 15000.00);
    }
}