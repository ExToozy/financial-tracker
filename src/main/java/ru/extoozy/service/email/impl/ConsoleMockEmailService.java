package ru.extoozy.service.email.impl;

import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.EmailType;
import ru.extoozy.service.email.EmailService;

public class ConsoleMockEmailService implements EmailService {
    @Override
    public void sendEmail(EmailType type, UserProfileEntity userProfile) {
        if (type == EmailType.OVER_BUDGET) {
            System.out.println("-".repeat(20));
            System.out.printf("%s Ваш бюджет был превышен%n", userProfile.getFirstName());
            System.out.println("-".repeat(20));
        }
    }
}
