package ru.extoozy.service.email;

import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.EmailType;

/**
 * Интерфейс для сервиса отправки электронных писем.
 * Содержит метод для отправки писем в зависимости от типа и профиля пользователя.
 */
public interface EmailService {

    /**
     * Отправляет электронное письмо пользователю в зависимости от типа письма.
     *
     * @param type тип письма, которое нужно отправить
     * @param user профиль пользователя, которому будет отправлено письмо
     */
    void sendEmail(EmailType type, UserProfileEntity user);
}
