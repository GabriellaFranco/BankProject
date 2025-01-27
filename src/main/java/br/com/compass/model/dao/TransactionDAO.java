package br.com.compass.model.dao;

import br.com.compass.db.Database;
import br.com.compass.db.exception.DbException;
import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import br.com.compass.model.enums.AccountType;
import br.com.compass.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TransactionDAO {

    private final Connection conn;

    public static TransactionDAO createTransactionDao() {
        return new TransactionDAO(Database.getConnection());
    }

    public void makeTransaction(Transaction transaction) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("INSERT INTO tb_transaction(type, value," +
                    "transaction_date, transfer_account, origin_account) VALUES(?::transaction_type, ?, ?, ?, ?)");

            statement.setString(1, transaction.getType().name());
            statement.setBigDecimal(2, transaction.getValue());
            statement.setObject(3, transaction.getTransactionDate());
            if (transaction.getTransferAccount() == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setLong(4, transaction.getTransferAccount().getNumber());
            }
            statement.setLong(5, transaction.getOriginAccount().getNumber());

            statement.executeUpdate();
            System.out.println("Transaction successful!\n");
        }
        catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);
        }
        finally {
            Database.closeStatement(statement);
        }

    }

    public boolean targetAccountExistsAndActive(Long accountNumber) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("SELECT 1 FROM tb_account WHERE number=? AND active=true");

            statement.setLong(1, accountNumber);

            ResultSet rs = statement.executeQuery();

            return rs.next();
        }
        catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);
        }
        finally {
            Database.closeStatement(statement);
        }
    };

    public List<Transaction> bankStatement(Long accountNumber) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("SELECT * FROM tb_transaction WHERE " +
                    "origin_account=? OR transfer_account=? " +
                    "ORDER BY id");

            statement.setLong(1, accountNumber);
            statement.setLong(2, accountNumber);

            ResultSet rs = statement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();

            while (rs.next()) {
                var transaction = Transaction.builder()
                        .type(TransactionType.valueOf(rs.getString("type")))
                        .value(rs.getBigDecimal("value"))
                        .transactionDate(rs.getDate("transaction_date").toLocalDate())
                        .transferAccount(AccountDAO.createAccountDAO()
                                .getAccount(rs.getLong("transfer_account")))
                        .originAccount(AccountDAO.createAccountDAO()
                                .getAccount(rs.getLong("origin_account")))
                        .build();
                transactions.add(transaction);
            }

            return transactions;
        }
        catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);
        }
        finally {
            Database.closeStatement(statement);
        }
    }
}
