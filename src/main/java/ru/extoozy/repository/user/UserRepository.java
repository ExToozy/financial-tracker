package ru.extoozy.repository.user;

import ru.extoozy.entity.UserEntity;
import ru.extoozy.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    UserEntity getByEmail(String email);

    void changeBlockStatus(Long id);
}
