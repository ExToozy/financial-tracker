package ru.extoozy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.extoozy.enums.UserRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    private Long id;

    private String email;

    private String password;

    private UserRole role;

    private UserProfileEntity userProfile;

    private boolean blocked;
}
