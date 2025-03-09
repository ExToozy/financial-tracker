package ru.extoozy.service.profile.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserProfileMapper;
import ru.extoozy.repository.profile.UserProfileRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Создание профиля пользователя")
    void create_whenValidDto_thenUserProfileIsCreated() {
        CreateUserProfileDto dto = new CreateUserProfileDto();
        UserProfileEntity userProfileEntity = new UserProfileEntity();

        try (MockedStatic<UserProfileMapper> mocked = mockStatic(UserProfileMapper.class)) {
            mocked.when(() -> UserProfileMapper.toEntity(dto)).thenReturn(userProfileEntity);
            userProfileService.create(dto);
        }

        verify(userProfileRepository, times(1)).save(any());
        assertThat(UserContext.getUser().getUserProfile()).isEqualTo(userProfileEntity);
    }

    @Test
    @DisplayName("Обновление профиля пользователя")
    void update_whenValidDto_thenUserProfileIsUpdated() {
        UpdateUserProfileDto dto = new UpdateUserProfileDto();
        UserProfileEntity userProfileEntity = new UserProfileEntity();

        try (MockedStatic<UserProfileMapper> mocked = mockStatic(UserProfileMapper.class)) {
            mocked.when(() -> UserProfileMapper.toEntity(dto)).thenReturn(userProfileEntity);
            userProfileService.update(dto);
        }


        verify(userProfileRepository, times(1)).update(userProfileEntity);
    }

    @Test
    @DisplayName("Получение профиля пользователя по ID")
    void get_whenProfileExists_thenReturnUserProfileDto() {
        Long profileId = 1L;
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileRepository.findById(profileId)).thenReturn(userProfileEntity);

        try (MockedStatic<UserProfileMapper> mocked = mockStatic(UserProfileMapper.class)) {
            mocked.when(() -> UserProfileMapper.toDto(userProfileEntity)).thenReturn(userProfileDto);
            UserProfileDto result = userProfileService.get(profileId);
            assertThat(result).isEqualTo(userProfileDto);
        }
    }

    @Test
    @DisplayName("Получение профиля пользователя по User ID")
    void getByUserId_whenProfileExists_thenReturnUserProfileDto() {
        Long userId = 1L;
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileRepository.findByUserId(userId)).thenReturn(userProfileEntity);

        try (MockedStatic<UserProfileMapper> mocked = mockStatic(UserProfileMapper.class)) {
            mocked.when(() -> UserProfileMapper.toDto(userProfileEntity)).thenReturn(userProfileDto);
            UserProfileDto result = userProfileService.getByUserId(userId);
            assertThat(result).isEqualTo(userProfileDto);
        }
    }

    @Test
    @DisplayName("Получение всех профилей пользователей")
    void getAll_whenProfilesExist_thenReturnListOfUserProfileDtos() {
        UserProfileEntity userProfileEntity1 = new UserProfileEntity();
        UserProfileEntity userProfileEntity2 = new UserProfileEntity();
        List<UserProfileEntity> userProfiles = Arrays.asList(userProfileEntity1, userProfileEntity2);
        List<UserProfileDto> userProfileDtos = Arrays.asList(new UserProfileDto(), new UserProfileDto());

        when(userProfileRepository.findAll()).thenReturn(userProfiles);
        try (MockedStatic<UserProfileMapper> mocked = mockStatic(UserProfileMapper.class)) {
            mocked.when(() -> UserProfileMapper.toDto(userProfiles)).thenReturn(userProfileDtos);
            List<UserProfileDto> result = userProfileService.getAll();
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(userProfileDtos);
        }
    }

    @Test
    @DisplayName("Удаление профиля пользователя по ID")
    void delete_whenProfileExists_thenProfileIsDeleted() {
        Long profileId = 1L;

        when(userProfileRepository.delete(profileId)).thenReturn(true);

        userProfileService.delete(profileId);

        verify(userProfileRepository, times(1)).delete(profileId);
    }

    @Test
    @DisplayName("Удаление профиля пользователя по ID, когда профиль не существует")
    void delete_whenProfileDoesNotExist_thenThrowResourceNotFoundException() {
        Long profileId = 1L;

        when(userProfileRepository.delete(profileId)).thenReturn(false);

        assertThatThrownBy(() -> userProfileService.delete(profileId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User profile with id=1 not found");
    }

}
