package ru.extoozy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileEntity {

    private Long id;

    private String firstName;

    private String lastName;

    private UserEntity user;

}
