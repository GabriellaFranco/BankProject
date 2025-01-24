package br.com.compass;

import br.com.compass.model.dao.AccountDAO;
import br.com.compass.model.dao.TransactionDAO;
import br.com.compass.model.entity.Account;
import br.com.compass.model.entity.Transaction;
import br.com.compass.model.enums.AccountType;
import br.com.compass.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;

public class App {

    private static AccountDAO accountDAO = AccountDAO.createAccountDAO();
    private static TransactionDAO transactionDAO = TransactionDAO.createTransactionDao();
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);

        scanner.close();
        System.out.println("Application closed");
    }

    public static void mainMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("========= Main Menu =========");
            System.out.println("|| 1. Login                ||");
            System.out.println("|| 2. Account Opening      ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    var loginAccount = loginScreen(scanner);
                    if (loginAccount != null) {
                        System.out.println("Login successful!");
                        bankMenu(scanner, loginAccount);
                    } else {
                        System.out.println("Login failed. Returning to main menu.");
                    }
                    break;

                case 2:
                    var account = getAccountInfo();
                    accountDAO.createAccount(account);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public static void bankMenu(Scanner scanner, Account loginAccount) {
        boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    deposit(loginAccount, scanner);
                    break;
                case 2:
                    withdraw(loginAccount, scanner);
                    break;
                case 3:
                    // ToDo...
                    System.out.println("Check Balance.");
                    break;
                case 4:
                    // ToDo...
                    System.out.println("Transfer.");
                    break;
                case 5:
                    // ToDo...
                    System.out.println("Bank Statement.");
                    break;
                case 0:
                    // ToDo...
                    System.out.println("Exiting...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static Account getAccountInfo() {
        Scanner sc = new Scanner(System.in);
        Account account = new Account();

        var holderName = askValueUntilValid("What's your full name?", name -> {
            if (!name.matches("[a-zA-Z' ]+")) {
                System.out.println("Please enter a valid name!");
                return false;
            }
            return true;
        }, sc);

        var holderBirthdate = askValueUntilValid("What's your birthdate? (dd/mm/yyyy):", birthdate -> {
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
            ;
            return true;
        }, sc);

        var holderPhone = askValueUntilValid("Inform a good phone number (XX XXXXXXXXX):", phone -> {
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

        var password = askValueUntilValid("Choose a password for your account:", pass -> {
            if (pass.length() < 6) {
                System.out.println("The password must have at least 6 characters");
                return false;
            }
            return true;
        }, sc);

        var accountType = askValueUntilValid("At last, please choose the type of account you want to open (CHECKING, SAVINGS, SALARY" +
                "BUSINESS, STUDENT, INVESTMENT)", type -> {

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

    private static String askValueUntilValid(String msg, Predicate<String> validator, Scanner scanner) {
        System.out.print(msg);
        var value = scanner.nextLine();
        while (!validator.test(value)) {
            System.out.println(msg);
            value = scanner.nextLine();
        }
        return value;
    }

    private static Account loginScreen(Scanner scanner) {
        System.out.print("Account number: ");
        var acc = scanner.next();
        System.out.print("Password: ");
        var password = scanner.next();

        return accountDAO.loginAccount(acc, password);

    }

    private static void deposit(Account account, Scanner scanner) {
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

    public static void withdraw(Account account, Scanner scanner) {
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
    };

}
