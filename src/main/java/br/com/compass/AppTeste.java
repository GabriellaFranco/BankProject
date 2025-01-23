package br.com.compass;

import br.com.compass.model.entity.Account;
import br.com.compass.model.dao.AccountDAO;
import br.com.compass.model.enums.AccountType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppTeste {

    public static void main(String[] args) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        AccountDAO accountDAO = AccountDAO.createAccountDAO();

        System.out.println("Criando Account: ");

        Account account = Account.builder()
                .type(AccountType.CHECKING)
                .holder("Gabriella Franco")
                .holderPhone("47986541278")
                .holderCpf("12309869013")
                .holderBirthdate(LocalDate.parse("31/10/1996", dtf))
                .password("dev123")
                .build();

        accountDAO.createAccount(account);
        System.out.println("Account criada!");
        System.out.println(account);
    }
}
