package ru.extoozy.repository.profile;

import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.repository.CrudRepository;

public interface UserProfileRepository extends CrudRepository<UserProfileEntity, Long> {
    UserProfileEntity findByUserId(Long id);
}
