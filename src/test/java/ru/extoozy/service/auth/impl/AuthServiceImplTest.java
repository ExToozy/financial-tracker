package ru.extoozy.service.auth.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.exception.InvalidEmailException;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.exception.UserAlreadyExistsException;
import ru.extoozy.exception.UserIsBlockedException;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.security.PasswordHelper;
import ru.extoozy.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Регистрация пользователя - если email уже существует, выбрасывает исключение")
    void testRegister_whenEmailExists_thenThrowException() {
        AuthUserDto dto = new AuthUserDto("test@example.com", "password");
        when(userService.containsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User with email=test@example.com already exists");
    }

    @Test
    @DisplayName("Регистрация пользователя - если email невалидный, выбрасывает исключение")
    void testRegister_whenEmailInvalid_thenThrowException() {
        AuthUserDto dto = new AuthUserDto("invalidEmail", "password");
        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(InvalidEmailException.class)
                .hasMessageContaining("Email=invalidEmail is invalid");
    }

    @Test
    @DisplayName("Регистрация пользователя - успешная регистрация")
    void testRegister_whenValid_thenSuccess() {
        AuthUserDto dto = new AuthUserDto("test@example.com", "password");
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(PasswordHelper.getPasswordHash(dto.getPassword()));

        when(userService.containsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.getByEmail(dto.getEmail())).thenReturn(user);

        authService.register(dto);

        assertThat(UserContext.getUser()).isEqualTo(user);
        verify(userService).create(dto);
    }

    @Test
    @DisplayName("Аутентификация пользователя - если email не найден, выбрасывает исключение")
    void testAuthenticate_whenEmailNotFound_thenThrowException() {
        AuthUserDto dto = new AuthUserDto("test@example.com", "password");
        when(userRepository.getByEmail(dto.getEmail())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThatThrownBy(() -> authService.authenticate(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("Аутентификация пользователя - если пароль неверный, выбрасывает исключение")
    void testAuthenticate_whenPasswordIncorrect_thenThrowException() {
        AuthUserDto dto = new AuthUserDto("test@example.com", "wrong_password");
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(PasswordHelper.getPasswordHash("correct_password"));

        when(userRepository.getByEmail(dto.getEmail())).thenReturn(user);

        assertThatThrownBy(() -> authService.authenticate(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Wrong password");
    }

    @Test
    @DisplayName("Аутентификация пользователя - если пользователь заблокирован, выбрасывает исключение")
    void testAuthenticate_whenUserBlocked_thenThrowException() {
        AuthUserDto dto = new AuthUserDto("test@example.com", "password");
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(PasswordHelper.getPasswordHash(dto.getPassword()));
        user.setBlocked(true);

        when(userRepository.getByEmail(dto.getEmail())).thenReturn(user);

        assertThatThrownBy(() -> authService.authenticate(dto))
                .isInstanceOf(UserIsBlockedException.class)
                .hasMessageContaining("User blocked");
    }

    @Test
    @DisplayName("Аутентификация пользователя - успешная аутентификация")
    void testAuthenticate_whenValid_thenSuccess() {
        AuthUserDto dto = new AuthUserDto("test@example.com", "password");
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(PasswordHelper.getPasswordHash(dto.getPassword()));
        user.setBlocked(false);

        when(userRepository.getByEmail(dto.getEmail())).thenReturn(user);

        authService.authenticate(dto);

        assertThat(UserContext.getUser()).isEqualTo(user);
    }
}