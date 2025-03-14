package ru.extoozy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.extoozy.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEntity {

    private Long id;

    private LocalDateTime createdAt;

    private String category;

    private String description;

    private TransactionType transactionType;

    private BigDecimal amount;

    private UserProfileEntity userProfile;

}
