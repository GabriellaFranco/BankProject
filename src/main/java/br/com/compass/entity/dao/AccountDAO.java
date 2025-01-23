package br.com.compass.entity.dao;

import br.com.compass.db.Database;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;


@RequiredArgsConstructor
public class AccountDAO {

    private final Connection conn;

    public static AccountDAO createAccountDAO() {
        return new AccountDAO(Database.getConnection());
    }

}
