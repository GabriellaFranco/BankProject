package br.com.compass;

import br.com.compass.db.exception.DbException;
import br.com.compass.model.dao.AccountDAO;
import br.com.compass.model.entity.Account;
import br.com.compass.model.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;

public class App {

    private static AccountDAO accountDAO = AccountDAO.createAccountDAO();
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
                    bankMenu(scanner);
                    return;
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

    public static void bankMenu(Scanner scanner) {
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

            switch (option) {
                case 1:
                    // ToDo...
                    System.out.println("Deposit.");
                    break;
                case 2:
                    // ToDo...
                    System.out.println("Withdraw.");
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
            }
            catch (DateTimeParseException exc) {
                System.out.println("Please enter a valid format of date!");
                return false;
            }
            return true;
        }, sc);

        var holderCpf = askValueUntilValid("Inform your CPF: ", cpf ->  {
            if (!cpf.matches("[0-9]+")) {
                System.out.println("Please enter a valid CPF!");
                return false;
            }
            if (cpf.length() != 11) {
                System.out.println("The CPF must have 11 digits!");
                return false;
            };
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

        account.setBalance(BigDecimal.ZERO);
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
        System.out.println(msg);
        var value = scanner.nextLine();
        while (!validator.test(value)) {
            System.out.println(msg);
            value = scanner.nextLine();
        }
        return value;
    }

}
