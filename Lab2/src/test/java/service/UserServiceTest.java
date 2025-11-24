// UserServiceTest.java
package service;

import dao.UserDAO;
import dto.UserCreateDTO;
import dto.UserDTO;
import modelDB.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        log.info("Тест: успешное создание пользователя");

        UserCreateDTO createDTO = new UserCreateDTO("testUser", "password123", "USER");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName("testUser");
        savedUser.setRole("USER");

        when(userDAO.getUserByName("testUser"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(savedUser));

        UserDTO result = userService.createUser(createDTO);

        assertNotNull(result);
        assertEquals("testUser", result.getName());
        assertEquals("USER", result.getRole());

        verify(userDAO).createUser(eq("testUser"), anyString(), eq("USER"));
        log.info("Тест создания пользователя завершен успешно");
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        log.info("Тест: ошибка при создании существующего пользователя");

        UserCreateDTO createDTO = new UserCreateDTO("existingUser", "password123", "USER");

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setName("existingUser");
        existingUser.setRole("USER");

        when(userDAO.getUserByName("existingUser"))
                .thenReturn(Optional.of(existingUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(createDTO));

        assertTrue(exception.getMessage().contains("Пользователь с таким именем уже существует"));

        verify(userDAO, never()).createUser(anyString(), anyString(), anyString());
        log.info("Тест проверки уникальности пользователя завершен успешно");
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        log.info("Тест: успешное получение пользователя по ID");

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("testUser");
        user.setRole("USER");

        when(userDAO.getUserById(userId))
                .thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals("testUser", result.getName());
        assertEquals(userId, result.getId());

        log.info("Тест получения пользователя по ID завершен успешно");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        log.info("Тест: пользователь не найден");

        UUID userId = UUID.randomUUID();

        when(userDAO.getUserById(userId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        log.info("Тест исключения завершен успешно");
    }

    @Test
    void shouldGetUserByNameSuccessfully() {
        log.info("Тест: успешное получение пользователя по имени");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("testUser");
        user.setRole("USER");

        when(userDAO.getUserByName("testUser"))
                .thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.getUserByName("testUser");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getName());

        log.info("Тест получения пользователя по имени завершен успешно");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        log.info("Тест: успешное обновление пользователя");

        UUID userId = UUID.randomUUID();
        UserCreateDTO updateDTO = new UserCreateDTO("updatedUser", "newPassword123", "ADMIN");

        userService.updateUser(userId, updateDTO);

        verify(userDAO).updateUser(eq(userId), eq("updatedUser"), anyString(), eq("ADMIN"));
        log.info("Тест обновления пользователя завершен успешно");
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        log.info("Тест: успешное удаление пользователя");

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setName("testUser");
        user.setRole("USER");

        when(userDAO.getUserById(userId))
                .thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userDAO).deleteUser(userId);
        log.info("Тест удаления пользователя завершен успешно");
    }
}