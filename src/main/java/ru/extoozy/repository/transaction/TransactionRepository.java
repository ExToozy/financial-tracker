package ru.extoozy.repository.transaction;

import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByUserProfileId(Long userProfileId);

}
