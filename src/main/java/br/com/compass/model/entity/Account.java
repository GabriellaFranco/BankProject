package br.com.compass.model.entity;

import br.com.compass.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {

    private Long number;

    private AccountType type;

    private BigDecimal balance;

    private LocalDate openingDate;

    private String holder;

    private String holderPhone;

    private LocalDate holderBirthdate;

    private String holderCpf;

    private String password;

    private Boolean active;

    private List<Transaction> transactions = new ArrayList<>();
}
