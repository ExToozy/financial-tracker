package ru.extoozy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.DeleteTransactionDto;
import ru.extoozy.dto.transaction.GetTransactionsDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.util.MapperConverter;

import java.util.List;
import java.util.Map;


@Mapper(uses = MapperConverter.class)
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionEntity toEntity(CreateTransactionDto dto);

    TransactionEntity toEntity(UpdateTransactionDto dto);

    @Mapping(target = "id", source = "transaction_id")
    UpdateTransactionDto toUpdateTransactionDto(Map<String, Object> jsonMap);

    @Mapping(target = "transactionType", source = "transaction_type", qualifiedByName = "transactionTypeStrToEnum")
    CreateTransactionDto toCreateTransactionDto(Map<String, Object> jsonMap);

    @Mapping(target = "id", source = "transaction_id")
    DeleteTransactionDto toDeleteTransactionDto(Map<String, Object> jsonMap);

    @Mapping(target = "userProfileId", source = "user_profile_id")
    GetTransactionsDto toGetTransactionsDto(Map<String, Object> jsonMap);

    TransactionDto toDto(TransactionEntity entity);

    List<TransactionDto> toDto(List<TransactionEntity> entities);

}
