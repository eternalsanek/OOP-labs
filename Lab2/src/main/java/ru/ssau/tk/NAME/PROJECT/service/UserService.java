package ru.ssau.tk.NAME.PROJECT.service;

import ru.ssau.tk.NAME.PROJECT.dto.UserDTO;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.mapper.UserMapper;
import ru.ssau.tk.NAME.PROJECT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    public Optional<UserDTO> getUserByName(String name) {
        return userRepository.findByName(name)
                .map(userMapper::toDTO);
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }
        if (userDTO.getPasswordHash() == null || userDTO.getPasswordHash().isEmpty()) {
            userDTO.setPasswordHash("default_password_" + UUID.randomUUID().toString().substring(0, 8));
        }
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Transactional
    public Optional<UserDTO> updateUser(UUID id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (userDTO.getName() != null) {
                        existingUser.setName(userDTO.getName());
                    }
                    if (userDTO.getRole() != null) {
                        try {
                            existingUser.setRole(User.Role.valueOf(userDTO.getRole()));
                        } catch (IllegalArgumentException e) {
                            existingUser.setRole(User.Role.USER);
                        }
                    }
                    if (userDTO.getPasswordHash() != null && !userDTO.getPasswordHash().isEmpty()) {
                        existingUser.setPasswordHash(userDTO.getPasswordHash());
                    }
                    return userRepository.save(existingUser);
                })
                .map(userMapper::toDTO);
    }

    @Transactional
    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
