package ru.extoozy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoalEntity {

    private Long id;

    private String name;

    private BigDecimal goalAmount;

    private BigDecimal currentAmount;

    private UserProfileEntity userProfile;

}
