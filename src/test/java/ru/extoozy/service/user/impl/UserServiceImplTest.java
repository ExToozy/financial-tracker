package ru.extoozy.service.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Создание пользователя, когда данные корректны")
    void testCreate_whenValidDto_thenUserIsCreated() {
        AuthUserDto dto = new AuthUserDto();
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        userService.create(dto);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Обновление пользователя, когда данные корректны")
    void testUpdate_whenValidDto_thenUserIsUpdated() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(1L);
        dto.setEmail("updated@example.com");

        userService.update(dto);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).update(userCaptor.capture());

        UserEntity updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Получение пользователя по ID, когда пользователь существует")
    void testGet_whenUserExists_thenReturnUserDto() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(userEntity);

        UserDto userDto = userService.get(1L);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Получение пользователя по ID, когда пользователь не существует")
    void testGet_whenUserNotFound_thenThrowResourceNotFoundException() {
        when(userRepository.findById(1L)).thenThrow(new ResourceNotFoundException("User  not found"));

        assertThatThrownBy(() -> userService.get(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User  not found");
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void testGetAll_whenCalled_thenReturnUserDtos() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> users = userService.getAll();

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("user1@example.com");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    @DisplayName("Удаление пользователя, когда пользователь существует")
    void testDelete_whenUserExists_thenUserIsDeleted() {
        when(userRepository.delete(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).delete(1L);
    }

    @Test
    @DisplayName("Удаление пользователя, когда пользователь не существует")
    void testDelete_whenUserNotFound_thenThrowResourceNotFoundException() {


        when(userRepository.delete(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id=1 not found");
    }

    @Test
    @DisplayName("Проверка наличия пользователя по email, когда пользователь существует")
    void testContainsByEmail_whenUserExists_thenReturnTrue() {
        String email = "test@example.com";
        when(userRepository.getByEmail(email)).thenReturn(new UserEntity());

        boolean exists = userService.containsByEmail(email);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Проверка наличия пользователя по email, когда пользователь не существует")
    void testContainsByEmail_whenUserNotFound_thenReturnFalse() {
        String email = "notfound@example.com";
        when(userRepository.getByEmail(email)).thenThrow(new ResourceNotFoundException("User  not found"));

        boolean exists = userService.containsByEmail(email);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Изменение статуса блокировки пользователя")
    void testChangeBlockStatus_whenCalled_thenChangeStatus() {
        Long userId = 1L;

        userService.changeBlockStatus(userId);

        verify(userRepository).changeBlockStatus(userId);
    }
}

