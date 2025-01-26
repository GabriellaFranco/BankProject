package br.com.compass.model.entity;

import br.com.compass.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {

    private Long id;

    private TransactionType type;

    private BigDecimal value;

    private LocalDate transactionDate;

    private Account transferAccount;

    private Account originAccount;

}
