package br.com.compass;

import br.com.compass.model.dao.AccountDAO;
import br.com.compass.model.entity.Account;

import java.util.Scanner;

public class App {

    private static final AccountDAO accountDAO = AccountDAO.createAccountDAO();
    private static final Bank bank = new Bank();

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
                    var loginAccount = bank.loginScreen(scanner);
                    if (loginAccount != null) {
                        System.out.println("Login successful!\n");
                        if (!loginAccount.getActive()) {
                            loginAccount.setActive(true);
                            accountDAO.updateAccount(loginAccount);
                        }
                        bankMenu(scanner, loginAccount);
                    } else {
                        System.out.println("Login failed. Returning to main menu.\n");
                    }
                    break;

                case 2:
                    var account = bank.getAccountInfo();
                    accountDAO.createAccount(account);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.\n");
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
                    bank.deposit(loginAccount, scanner);
                    break;
                case 2:
                    bank.withdraw(loginAccount, scanner);
                    break;
                case 3:
                    bank.checkBalance(loginAccount);
                    break;
                case 4:
                    bank.transfer(loginAccount, scanner);
                    break;
                case 5:
                    bank.bankStatement(loginAccount.getNumber());
                    break;
                case 0:
                    System.out.println("Logging out...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.\n");
            }
        }
    }
}
