package com.auth.backend.app.services;

import com.auth.backend.app.dtos.UserDto;
import com.auth.backend.app.entities.Provider;
import com.auth.backend.app.entities.User;
import com.auth.backend.app.exception.ResourceNotFoundException;
import com.auth.backend.app.helpers.UserHelper;
import com.auth.backend.app.mapper.UserMapper;
import com.auth.backend.app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        //role assigns to user for authorization
        User user = userMapper.mapUserDtoToUser(userDto);
        user.setProvider(userDto.getProvider()!= null ? userDto.getProvider() : Provider.LOCAL);

        User savedUser = userRepository.save(user);
        return userMapper.mapUserToUserDto(savedUser);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given email Id"));
        return userMapper.mapUserToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userid) {
        UUID uuid = UserHelper.parseUUID(userid);
        User existingUser = userRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given Id"));
        if (userDto.getName() != null ) existingUser.setName(userDto.getName());
        if (userDto.getImage() != null ) existingUser.setImage(userDto.getImage());
        if (userDto.getProvider() != null ) existingUser.setProvider(userDto.getProvider());
        if (userDto.getPassword() != null ) existingUser.setPassword(userDto.getPassword());
        existingUser.setEnable(userDto.isEnable());
        User updatedUser = userRepository.save(existingUser);
        return userMapper.mapUserToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        UUID uuid = UserHelper.parseUUID(userId);
        User user = userRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("User not found with given user Id"));
        userRepository.delete(user);

    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(() -> new ResourceNotFoundException("User not found with given user Id"));
        return userMapper.mapUserToUserDto(user);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::mapUserToUserDto)
                .toList();
    }
}
