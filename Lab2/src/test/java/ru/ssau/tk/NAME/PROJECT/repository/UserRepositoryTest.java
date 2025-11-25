package ru.ssau.tk.NAME.PROJECT.repository;

import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenGenerateUsers_thenFindAll() {
        // Генерация данных
        User user1 = new User("user1", "hash1", User.Role.USER);
        User user2 = new User("user2", "hash2", User.Role.ADMIN);
        User user3 = new User("user3", "hash3", User.Role.MODERATOR);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Поиск всех
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(3);
    }

    @Test
    void whenGenerateUsers_thenFindByName() {
        // Генерация данных
        User user1 = new User("john", "hash1", User.Role.USER);
        User user2 = new User("jane", "hash2", User.Role.USER);

        userRepository.save(user1);
        userRepository.save(user2);

        // Поиск по имени
        Optional<User> found = userRepository.findByName("john");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("john");
    }

    @Test
    void whenGenerateUsers_thenFindByRole() {
        // Генерация данных
        User user1 = new User("user1", "hash1", User.Role.USER);
        User user2 = new User("admin1", "hash2", User.Role.ADMIN);
        User user3 = new User("user2", "hash3", User.Role.USER);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Поиск по роли
        List<User> users = userRepository.findByRole(User.Role.USER);
        assertThat(users).hasSize(2);
    }

    @Test
    void whenGenerateUsers_thenFindByNameContaining() {
        // Генерация данных
        User user1 = new User("john_doe", "hash1", User.Role.USER);
        User user2 = new User("john_smith", "hash2", User.Role.USER);
        User user3 = new User("jane_doe", "hash3", User.Role.USER);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Поиск по части имени
        List<User> johnUsers = userRepository.findByNameContaining("john");
        assertThat(johnUsers).hasSize(2);
    }

    @Test
    void whenGenerateUsers_thenDeleteUser() {
        // Генерация данных
        User user1 = new User("user1", "hash1", User.Role.USER);
        User user2 = new User("user2", "hash2", User.Role.USER);

        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        // Удаление
        userRepository.delete(saved1);

        // Проверка удаления
        List<User> remaining = userRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getName()).isEqualTo("user2");
    }

    @Test
    void whenGenerateUsers_thenDeleteAll() {
        // Генерация данных
        User user1 = new User("user1", "hash1", User.Role.USER);
        User user2 = new User("user2", "hash2", User.Role.USER);

        userRepository.save(user1);
        userRepository.save(user2);

        // Удаление всех
        userRepository.deleteAll();

        // Проверка удаления
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }
}