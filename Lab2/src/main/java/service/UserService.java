package service;

import dao.DAOFactory;
import dao.UserDAO;
import dto.UserDTO;
import dto.UserCreateDTO;
import modelDB.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = DAOFactory.getUserDAO();
        log.info("Создан UserService с UserDAO");
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        log.debug("Создан UserService с переданным UserDAO (для тестов)");
    }

    public UserDTO createUser(UserCreateDTO userDTO) {
        log.info("Начало создания пользователя с именем: {}", userDTO.getName());
        try {
            Optional<User> existingUser = userDAO.getUserByName(userDTO.getName());
            if (existingUser.isPresent()) {
                log.warn("Попытка создания пользователя с существующим именем: {}", userDTO.getName());
                throw new RuntimeException("Пользователь с таким именем уже существует");
            }
            String passwordHash = hashPassword(userDTO.getPassword());

            log.debug("Создание пользователя в БД: имя={}, роль={}",
                    userDTO.getName(), userDTO.getRole());
            userDAO.createUser(userDTO.getName(), passwordHash, userDTO.getRole());

            Optional<User> user = userDAO.getUserByName(userDTO.getName());
            UserDTO result = user.map(this::toDTO)
                    .orElseThrow(() -> {
                        log.error("Не удалось найти созданного пользователя: {}", userDTO.getName());
                        return new RuntimeException("Пользователь не найден после создания");
                    });
            log.info("Пользователь успешно создан: ID={}, имя={}",
                    result.getId(), result.getName());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    public UserDTO getUserById(UUID id) {
        log.debug("Получение пользователя по ID: {}", id);
        Optional<User> user = userDAO.getUserById(id);
        UserDTO result = user.map(this::toDTO)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new RuntimeException("Пользователь не найден");
                });
        log.debug("Пользователь найден: ID={}, имя={}", result.getId(), result.getName());
        return result;
    }

    public Optional<UserDTO> getUserByName(String name) {
        log.debug("Поиск пользователя по имени: {}", name);
        Optional<User> user = userDAO.getUserByName(name);
        if (user.isPresent()) {
            log.debug("Пользователь найден по имени: {}", name);
        } else {
            log.debug("Пользователь не найден по имени: {}", name);
        }
        return user.map(this::toDTO);
    }

    public void updateUser(UUID id, UserCreateDTO userDTO) {
        log.info("Обновление пользователя ID: {}", id);
        try {
            String passwordHash = hashPassword(userDTO.getPassword());
            userDAO.updateUser(id, userDTO.getName(), passwordHash, userDTO.getRole());

            log.info("Пользователь успешно обновлен: ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteUser(UUID id) {
        log.info("Удаление пользователя ID: {}", id);
        try {
            Optional<User> user = userDAO.getUserById(id);
            user.ifPresent(u ->
                    log.debug("Удаление пользователя: имя={}, роль={}", u.getName(), u.getRole())
            );
            userDAO.deleteUser(id);
            log.info("Пользователь успешно удален: ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getRole());
    }

    private String hashPassword(String password) {
        log.debug("Хеширование пароля");
        return Integer.toString(password.hashCode());
    }
}
