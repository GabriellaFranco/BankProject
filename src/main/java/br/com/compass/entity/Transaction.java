package br.com.compass.entity;

import br.com.compass.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {

    private Long id;

    private AccountType type;

    private BigDecimal value;

    private LocalDate transactionDate;

    private Long transferAccount;

    private Account originAccount;

}
