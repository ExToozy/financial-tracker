package ru.extoozy.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;
import ru.extoozy.entity.TransactionEntity;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMapper {

    public static TransactionEntity toEntity(CreateTransactionDto dto) {
        return TransactionEntity.builder()
                .category(dto.getCategory())
                .description(dto.getDescription())
                .transactionType(dto.getTransactionType())
                .amount(dto.getAmount())
                .build();
    }

    public static TransactionEntity toEntity(UpdateTransactionDto dto) {
        return TransactionEntity.builder()
                .id(dto.getId())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .amount(dto.getAmount())
                .build();
    }

    public static TransactionDto toDto(TransactionEntity entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .transactionType(entity.getTransactionType())
                .amount(entity.getAmount())
                .build();
    }

    public static List<TransactionDto> toDto(List<TransactionEntity> entities) {
        return entities.stream()
                .map(TransactionMapper::toDto)
                .toList();
    }

}
