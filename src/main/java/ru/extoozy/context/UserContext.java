package ru.extoozy.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.entity.UserEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserContext {

    private static final ThreadLocal<UserEntity> USER_HOLDER = new ThreadLocal<>();

    public static UserEntity getUser() {
        return USER_HOLDER.get();
    }

    public static void setUser(UserEntity user) {
        USER_HOLDER.set(user);
    }

    public static void clear() {
        USER_HOLDER.remove();
    }


}
