package br.com.compass.model.dao;

import br.com.compass.db.Database;
import br.com.compass.db.exception.DbException;
import br.com.compass.model.entity.Account;
import br.com.compass.model.enums.AccountType;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;


@RequiredArgsConstructor
public class AccountDAO {

    private final Connection conn;

    public static AccountDAO createAccountDAO() {
        return new AccountDAO(Database.getConnection());
    }

    public void createAccount(Account account) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("INSERT INTO tb_account " +
                    "(type, balance, opening_date, holder, holder_phone, holder_birthdate, " +
                    "holder_cpf, password, active) VALUES (?::account_type, ?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, account.getType().name());
            statement.setBigDecimal(2, account.getBalance());
            statement.setObject(3, account.getOpeningDate());
            statement.setString(4, account.getHolder());
            statement.setString(5, account.getHolderPhone());
            statement.setObject(6, account.getHolderBirthdate());
            statement.setString(7, account.getHolderCpf());
            statement.setString(8, account.getPassword());
            statement.setBoolean(9, account.getActive());

            statement.executeUpdate();
            System.out.println("Account successfully created. Please login to activate transactions.");
        } catch (SQLException exc) {
            throw new DbException(exc.getMessage());
        } finally {
            Database.closeStatement(statement);
        }

    }

    public boolean existsAccountTypeForCpf(AccountType accountType, String cpf) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("SELECT 1 FROM tb_account WHERE holder_cpf=? AND type=?::account_type");

            statement.setString(1, cpf);
            statement.setObject(2, accountType.name());

            ResultSet rs = statement.executeQuery();

            return rs.next();

        } catch (SQLException exception) {
            throw new DbException(exception.getMessage());

        } finally {
            Database.closeStatement(statement);
        }
    }

}
