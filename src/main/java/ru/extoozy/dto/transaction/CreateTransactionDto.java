package ru.extoozy.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.extoozy.enums.TransactionType;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransactionDto {
    private String category;

    private String description;

    private TransactionType transactionType;

    private BigDecimal amount;
}
