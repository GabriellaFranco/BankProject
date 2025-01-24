package br.com.compass.model.dao;

import br.com.compass.db.Database;
import br.com.compass.db.exception.DbException;
import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import br.com.compass.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

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
            System.out.println("Transaction successful!");
        }
        catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);
        }
        finally {
            Database.closeStatement(statement);
        }

    }

}
