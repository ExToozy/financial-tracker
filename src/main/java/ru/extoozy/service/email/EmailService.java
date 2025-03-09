package ru.extoozy.service.email;

import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.EmailType;

public interface EmailService {
    void sendEmail(EmailType type, UserProfileEntity user);
}
