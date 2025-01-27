package br.com.compass;

import br.com.compass.model.dao.AccountDAO;
import br.com.compass.model.dao.TransactionDAO;
import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import br.com.compass.model.enums.AccountType;
import br.com.compass.model.enums.TransactionType;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;

@NoArgsConstructor
public class Bank {

    private final AccountDAO accountDAO = AccountDAO.createAccountDAO();
    private final TransactionDAO transactionDAO = TransactionDAO.createTransactionDao();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Account getAccountInfo() {
        Scanner sc = new Scanner(System.in);
        Account account = new Account();

        var holderName = askValueUntilValid("What's your full name? ", name -> {
            if (!name.matches("[a-zA-Z' ]+")) {
                System.out.println("Please enter a valid name!");
                return false;
            }
            return true;
        }, sc);

        var holderBirthdate = askValueUntilValid("What's your birthdate? (dd/mm/yyyy): ", birthdate -> {
            try {
                LocalDate date = LocalDate.parse(birthdate, dtf);
                if (date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.of(1900, 1, 1))) {
                    System.out.println("The date you provided is not valid!");
                    return false;
                }
            } catch (DateTimeParseException exc) {
                System.out.println("Please enter a valid format of date!");
                return false;
            }
            return true;
        }, sc);

        var holderCpf = askValueUntilValid("Inform your CPF: ", cpf -> {
            if (!cpf.matches("[0-9]+")) {
                System.out.println("Please enter a valid CPF!");
                return false;
            }
            if (cpf.length() != 11) {
                System.out.println("The CPF must have 11 digits!");
                return false;
            }
            return true;
        }, sc);

        var holderPhone = askValueUntilValid("Inform a good phone number (XX XXXXXXXXX): ", phone -> {
            if (!phone.matches("[0-9]+")) {
                System.out.println("Please enter a valid phone number!");
                return false;
            }
            if (phone.length() != 11) {
                System.out.println("The telephone must have 11 digits (don't forget the area code!)");
                return false;
            }
            return true;
        }, sc);

        var password = askValueUntilValid("Choose a password for your account: ", pass -> {
            if (pass.length() < 6) {
                System.out.println("The password must have at least 6 characters");
                return false;
            }
            return true;
        }, sc);

        var accountType = askValueUntilValid("At last, please choose the type of account you want to open (CHECKING, SAVINGS, SALARY" +
                "BUSINESS, STUDENT, INVESTMENT): ", type -> {

            var typeValidation = Arrays.stream(AccountType.values())
                    .anyMatch(x -> x.toString().equals(type.toUpperCase()));

            if (!typeValidation) {
                System.out.println("Account type invalid!");
                return false;
            }
            if (accountDAO.existsAccountTypeForCpf(AccountType.valueOf(type.toUpperCase()), holderCpf)) {
                System.out.println("You already have an account of this type!");
                return false;
            }
            return true;

        }, sc);

        account.setBalance(new BigDecimal(0));
        account.setActive(false);
        account.setOpeningDate(LocalDate.now());
        account.setHolderBirthdate(LocalDate.parse(holderBirthdate, dtf));
        account.setHolder(holderName);
        account.setHolderPhone(holderPhone);
        account.setHolderCpf(holderCpf);
        account.setPassword(password);
        account.setType(AccountType.valueOf(accountType.toUpperCase()));

        return account;
    }

    public String askValueUntilValid(String msg, Predicate<String> validator, Scanner scanner) {
        System.out.print(msg);
        var value = scanner.nextLine();
        while (!validator.test(value)) {
            System.out.print(msg);
            value = scanner.nextLine();
        }
        return value;
    }

    public Account loginScreen(Scanner scanner) {
        System.out.print("Account number: ");
        var acc = scanner.next();
        System.out.print("Password: ");
        var password = scanner.next();

        return accountDAO.loginAccount(acc, password);

    }

    public void deposit(Account account, Scanner scanner) {
        var valueDepositStr = askValueUntilValid("Value: ", valueDeposit -> {
            try {
                var value = new BigDecimal(valueDeposit);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("The value must be positive!");
                    return false;
                }
            } catch (NumberFormatException exc) {
                System.out.println("Invalid value!");
                return false;
            }
            return true;
        }, scanner);

        var valueDeposit = new BigDecimal(valueDepositStr);
        account.setBalance(account.getBalance().add(valueDeposit));
        accountDAO.updateAccount(account);
        transactionDAO.makeTransaction(Transaction.builder()
                .type(TransactionType.DEPOSIT)
                .value(valueDeposit)
                .transactionDate(LocalDate.now())
                .transferAccount(null)
                .originAccount(account)
                .build());
    }

    public void withdraw(Account account, Scanner scanner) {
        var withdrawalValueStr = askValueUntilValid("Value: ", value -> {

            try {
                var valueWithdrawal = new BigDecimal(value);
                if (valueWithdrawal.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("The value must be positive!");
                    return false;
                }
                if (valueWithdrawal.compareTo(account.getBalance()) > 0) {
                    System.out.print("The value is higher than the account balance!\n");
                    return false;
                }
            }
            catch(NumberFormatException exc){
                System.out.println("Invalid value!");
                return false;
            }
            return true;
        }, scanner);

        var withdrawalValue = new BigDecimal(withdrawalValueStr);
        account.setBalance(account.getBalance().subtract(withdrawalValue));
        accountDAO.updateAccount(account);
        transactionDAO.makeTransaction(Transaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .value(withdrawalValue)
                .transactionDate(LocalDate.now())
                .transferAccount(null)
                .originAccount(account)
                .build());
    }

    public void checkBalance(Account account) {
        System.out.println("Your current balance is R$" + account.getBalance() + "\n");
    }

    public void transfer(Account origin, Scanner scanner) {

        System.out.print("Enter the target account number: ");
        Long targetAccountNumber;
        try {
            targetAccountNumber = Long.parseLong(scanner.nextLine());
        }
        catch (NumberFormatException exc) {
            System.out.println("Invalid number, please try again!");
            return;
        }

        if (!transactionDAO.targetAccountExistsAndActive(targetAccountNumber)) {
            System.out.println("The target account is inactive or doesn't exist!");
            return;
        }
        if (origin.getNumber().equals(targetAccountNumber)) {
            System.out.println("The target account can't be the same as the origin account!");
            return;
        }

        var transferValueStr = askValueUntilValid("Value: ", valueT -> {
            try {
                var valueTransfer = new BigDecimal(valueT);
                if (valueTransfer.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("The value must be positive!");
                    return false;
                }
                if (valueTransfer.compareTo(origin.getBalance()) > 0) {
                    System.out.println("The value is higher than your account balance!");
                    return false;
                }

            }
            catch (NumberFormatException exc) {
                System.out.println("Invalid value, please try again!");
                return false;
            }

            return true;
        }, scanner);

        var targetAccount = accountDAO.getAccount(targetAccountNumber);

        var transferedValue = new BigDecimal(transferValueStr);
        origin.setBalance(origin.getBalance().subtract(transferedValue));
        targetAccount.setBalance(targetAccount.getBalance().add(transferedValue));
        accountDAO.updateAccount(origin);
        accountDAO.updateAccount(targetAccount);
        transactionDAO.makeTransaction(Transaction.builder()
                .type(TransactionType.TRANSFER)
                .originAccount(origin)
                .value(transferedValue)
                .transferAccount(targetAccount)
                .transactionDate(LocalDate.now())
                .build());
    }

    public void bankStatement(Long accountNumber) {
        System.out.println("\n=========================================");
        System.out.println("||           Bank Statement            ||");
        System.out.println("=========================================");

        List<Transaction> transactions = transactionDAO.bankStatement(accountNumber);
        transactions.forEach(transaction -> {
            String targetAccount = "";
            String signStr = "";
            switch (transaction.getType()) {
                case DEPOSIT -> signStr = "[+]";
                case WITHDRAWAL -> signStr = "[-]";
                case TRANSFER -> {
                    if (transaction.getTransferAccount() != null) {
                        signStr = "[-]";
                        targetAccount =  " TO: " + transaction.getTransferAccount().getNumber();
                        if (Objects.equals(transaction.getTransferAccount().getNumber(), accountNumber)) {
                            signStr = "[+]";
                            targetAccount =  " FROM: " + transaction.getOriginAccount().getNumber();
                        }
                    }

                }
            }

            System.out.println(signStr + " "  + transaction.getType() + targetAccount + " R$" + transaction.getValue()
                    + " (" + transaction.getTransactionDate() + ")");
        });
        System.out.println("=========================================\n");
    }
}
