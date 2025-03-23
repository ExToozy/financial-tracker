package ru.extoozy.service.user.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.InvalidEmailException;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserMapper;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.security.PasswordHelper;
import ru.extoozy.service.user.UserService;
import ru.extoozy.util.RegexUtil;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public void create(AuthUserDto dto) {
        UserEntity user = UserMapper.INSTANCE.toEntity(dto);
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    @Override
    public void update(UpdateUserDto dto) {
        UserEntity user = UserMapper.INSTANCE.toEntity(dto);
        if (userDoesNotHaveAccessToUser(UserContext.getUser(), user)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user with id=%s".formatted(
                            UserContext.getUser().getId(),
                            user.getId()
                    )
            );
        }
        if (RegexUtil.isInvalidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email=%s is invalid".formatted(dto.getEmail()));
        }
        if (dto.getPassword() != null) {
            user.setPassword(PasswordHelper.getPasswordHash(dto.getPassword()));
        }
        userRepository.update(user);
    }

    @Override
    public UserDto get(Long id) {
        UserEntity user = userRepository.findById(id);
        if (userDoesNotHaveAccessToUser(UserContext.getUser(), user)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user with id=%s".formatted(
                            UserContext.getUser().getId(),
                            user.getId()
                    )
            );
        }
        return UserMapper.INSTANCE.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        if (UserContext.getUser().getRole().equals(UserRole.ADMIN)) {
            List<UserEntity> users = userRepository.findAll();
            return UserMapper.INSTANCE.toDto(users);
        }
        return new ArrayList<>();
    }

    @Override
    public void delete(Long id) {
        UserEntity user = userRepository.findById(id);
        if (userDoesNotHaveAccessToUser(UserContext.getUser(), user)) {
            throw new ResourceNotFoundException(
                    "The user with id=%s does not have access to user with id=%s".formatted(
                            UserContext.getUser().getId(),
                            user.getId()
                    )
            );
        }
        boolean deleted = userRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("User with id=%s not found".formatted(id));
        }
    }

    @Override
    public boolean containsByEmail(String email) {
        try {
            userRepository.getByEmail(email);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    @Override
    public void changeBlockStatus(Long id) {
        if (UserContext.getUser().getRole().equals(UserRole.ADMIN)) {
            userRepository.changeBlockStatus(id);
        }
    }

    private boolean userDoesNotHaveAccessToUser(UserEntity user, UserEntity accessingUser) {
        return !user.getRole().equals(UserRole.ADMIN) && !user.getId().equals(accessingUser.getId());
    }
}
