package ru.extoozy.service.profile.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
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

    @Mock
    private UserProfileMapper mapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserContext.setUser(
                UserEntity.builder()
                        .id(1L)
                        .role(UserRole.ADMIN)
                        .userProfile(UserProfileEntity.builder()
                                .id(1L)
                                .build())
                        .build()
        );
    }

    @Test
    @DisplayName("Создание профиля пользователя")
    void create_whenValidDto_thenUserProfileIsCreated() {
        CreateUserProfileDto dto = new CreateUserProfileDto();
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        when(mapper.toEntity(dto)).thenReturn(userProfileEntity);
        userProfileService.create(dto);
        verify(userProfileRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Обновление профиля пользователя")
    void update_whenValidDto_thenUserProfileIsUpdated() {
        UpdateUserProfileDto dto = new UpdateUserProfileDto();
        dto.setId(1L);
        dto.setFirstName("first");
        dto.setLastName("last");
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        when(mapper.toEntity(dto)).thenReturn(userProfileEntity);
        userProfileService.update(dto);

        verify(userProfileRepository, times(1)).update(any());
    }

    @Test
    @DisplayName("Получение профиля пользователя по ID")
    void get_whenProfileExists_thenReturnUserProfileDto() {
        Long profileId = 1L;
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setId(1L);
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileRepository.findById(profileId)).thenReturn(userProfileEntity);
        when(mapper.toDto(userProfileEntity)).thenReturn(userProfileDto);
        UserProfileDto result = userProfileService.get(profileId);
        System.out.println(result);
        assertThat(result.getId()).isEqualTo(profileId);
    }

    @Test
    @DisplayName("Получение профиля пользователя по User ID")
    void getByUserId_whenProfileExists_thenReturnUserProfileDto() {
        Long userId = 1L;
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setId(1L);
        UserProfileDto userProfileDto = new UserProfileDto();

        when(userProfileRepository.findByUserId(userId)).thenReturn(userProfileEntity);
        when(mapper.toDto(userProfileEntity)).thenReturn(userProfileDto);
        UserProfileDto result = userProfileService.getByUserId(userId);
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Получение всех профилей пользователей")
    void getAll_whenProfilesExist_thenReturnListOfUserProfileDtos() {
        UserProfileEntity userProfileEntity1 = new UserProfileEntity();
        userProfileEntity1.setId(1L);
        UserProfileEntity userProfileEntity2 = new UserProfileEntity();
        userProfileEntity2.setId(2L);
        UserProfileDto userProfileDto1 = new UserProfileDto();
        userProfileDto1.setId(1L);
        UserProfileDto userProfileDto2 = new UserProfileDto();
        userProfileDto2.setId(2L);
        List<UserProfileEntity> userProfiles = Arrays.asList(userProfileEntity1, userProfileEntity2);
        List<UserProfileDto> userProfileDtos = Arrays.asList(userProfileDto1, userProfileDto2);

        when(userProfileRepository.findAll()).thenReturn(userProfiles);
        when(mapper.toDto(userProfiles)).thenReturn(userProfileDtos);
        List<UserProfileDto> result = userProfileService.getAll();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Удаление профиля пользователя по ID")
    void delete_whenProfileExists_thenProfileIsDeleted() {
        Long profileId = 1L;

        when(userProfileRepository.findById(profileId)).thenReturn(
                UserProfileEntity.builder()
                        .id(profileId)
                        .user(UserEntity.builder()
                                .id(1L)
                                .build())
                        .build()
        );
        when(userProfileRepository.delete(profileId)).thenReturn(true);
        userProfileService.delete(profileId);

        verify(userProfileRepository, times(1)).delete(profileId);
    }

    @Test
    @DisplayName("Удаление профиля пользователя по ID, когда профиль не существует")
    void delete_whenProfileDoesNotExist_thenThrowResourceNotFoundException() {
        Long profileId = 1L;
        when(userProfileRepository.findById(profileId)).thenThrow(
                new ResourceNotFoundException("User profile with id=1 not found")
        );
        when(userProfileRepository.delete(profileId)).thenReturn(false);

        assertThatThrownBy(() -> userProfileService.delete(profileId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User profile with id=1 not found");
    }

}
