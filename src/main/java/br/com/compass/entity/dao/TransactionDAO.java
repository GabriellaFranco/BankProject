package br.com.compass.entity.dao;

import br.com.compass.db.Database;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;

@RequiredArgsConstructor
public class TransactionDAO {

    private final Connection conn;

    public static TransactionDAO createTransactionDao() {
        return new TransactionDAO(Database.getConnection());
    }

}
