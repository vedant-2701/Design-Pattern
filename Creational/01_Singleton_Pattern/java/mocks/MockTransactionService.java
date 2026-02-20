// Mock Transaction Service using the Mock Connection Pool

public class MockTransactionService {
    public void transferFunds(String fromAccount, String toAccount, double amount) {
        MockConnectionPoolManager poolManager = MockConnectionPoolManager.getInstance();
        MockConnection conn = null;

        try {
            conn = poolManager.acquireConnection();

            // Begin transaction
            conn.setAutoCommit(false);

            MockPreparedStatement debit = conn.prepareStatement(
                "UPDATE accounts SET balance = balance - ? WHERE account_id = ?"
            );
            debit.setDouble(1, amount);
            debit.setString(2, fromAccount);
            debit.executeUpdate();

            MockPreparedStatement credit = conn.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE account_id = ?"
            );
            credit.setDouble(1, amount);
            credit.setString(2, toAccount);
            credit.executeUpdate();

            conn.commit();

            System.out.printf("   ✅ Transfer of $%.2f from %s to %s completed successfully.%n",
                    amount, fromAccount, toAccount);
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception ignored) {}
            throw new RuntimeException("Transaction failed — rolled back.", e);
        } finally {
            // Always return to pool
            poolManager.releaseConnection(conn);
        }
    }
}
