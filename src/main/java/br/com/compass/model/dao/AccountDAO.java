package br.com.compass.model.dao;

import br.com.compass.db.Database;
import br.com.compass.db.exception.DbException;
import br.com.compass.model.entity.Account;
import br.com.compass.model.enums.AccountType;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.InputMismatchException;


@RequiredArgsConstructor
public class AccountDAO {

    private final Connection conn;

    public static AccountDAO createAccountDAO() {
        return new AccountDAO(Database.getConnection());
    }

    public Account getAccount(Long accountNumber) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("SELECT * FROM tb_account WHERE number=?");

            statement.setLong(1, accountNumber);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return Account.builder()
                        .number(rs.getLong("number"))
                        .balance(rs.getBigDecimal("balance"))
                        .type(AccountType.valueOf(rs.getString("type")))
                        .openingDate(rs.getDate("opening_date").toLocalDate())
                        .holder(rs.getString("holder"))
                        .holderPhone(rs.getString("holder_phone"))
                        .holderBirthdate(rs.getDate("holder_birthdate").toLocalDate())
                        .holderCpf(rs.getString("holder_cpf"))
                        .password(rs.getString("password"))
                        .active(rs.getBoolean("active"))
                        .build();
            }
        }
        catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);
        }
        finally {
            Database.closeStatement(statement);
        }
        return null;
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
            throw new DbException(exc.getMessage(), exc);
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

        } catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);

        } finally {
            Database.closeStatement(statement);
        }
    }

    public Account loginAccount(String acc, String password) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("SELECT * FROM tb_account WHERE " +
                    "number=CAST(? AS INTEGER) AND password=?");

            statement.setString(1, acc);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return Account.builder()
                        .number(rs.getLong("number"))
                        .balance(rs.getBigDecimal("balance"))
                        .type(AccountType.valueOf(rs.getString("type")))
                        .openingDate(rs.getDate("opening_date").toLocalDate())
                        .holder(rs.getString("holder"))
                        .holderPhone(rs.getString("holder_phone"))
                        .holderBirthdate(rs.getDate("holder_birthdate").toLocalDate())
                        .holderCpf(rs.getString("holder_cpf"))
                        .password(rs.getString("password"))
                        .active(rs.getBoolean("active"))
                        .build();
            }
            else {
                return null;
            }
        }
        catch (SQLException exception) {
            System.out.println("Incorrect account number or/and password!");
            return null;
        }
        finally {
            Database.closeStatement(statement);
        }
    }

    public void updateAccount(Account account) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement("UPDATE tb_account SET " +
                    "type=?::account_type, balance=?, opening_date=?, holder=?, holder_phone=?, holder_birthdate=?, " +
                    "holder_cpf=?, password=?, active=? WHERE number=?");

            statement.setString(1, account.getType().name());
            statement.setBigDecimal(2, account.getBalance());
            statement.setObject(3, account.getOpeningDate());
            statement.setString(4, account.getHolder());
            statement.setString(5, account.getHolderPhone());
            statement.setObject(6, account.getHolderBirthdate());
            statement.setString(7, account.getHolderCpf());
            statement.setString(8, account.getPassword());
            statement.setBoolean(9, account.getActive());
            statement.setLong(10, account.getNumber());

            statement.executeUpdate();

        } catch (SQLException exc) {
            throw new DbException(exc.getMessage(), exc);
        } finally {
            Database.closeStatement(statement);
        }
    }
}
