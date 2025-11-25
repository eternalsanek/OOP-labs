package ru.ssau.tk.NAME.PROJECT.repository;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FunctionRepositoryTest {

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenGenerateFunctions_thenFindAll() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);

        Function func1 = new Function(savedUser, "Linear", "linear", "x + 1");
        Function func2 = new Function(savedUser, "Quadratic", "quadratic", "x^2");

        functionRepository.save(func1);
        functionRepository.save(func2);

        // Поиск всех
        List<Function> functions = functionRepository.findAll();
        assertThat(functions).hasSize(2);
    }

    @Test
    void whenGenerateFunctions_thenFindByOwner() {
        // Генерация данных
        User user1 = new User("owner1", "hash1", User.Role.USER);
        User user2 = new User("owner2", "hash2", User.Role.USER);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Function func1 = new Function(savedUser1, "Func1", "linear", "x");
        Function func2 = new Function(savedUser1, "Func2", "quadratic", "x^2");
        Function func3 = new Function(savedUser2, "Func3", "cubic", "x^3");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        // Поиск по владельцу
        List<Function> user1Functions = functionRepository.findByOwner(savedUser1);
        assertThat(user1Functions).hasSize(2);
    }

    @Test
    void whenGenerateFunctions_thenFindByType() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);

        Function func1 = new Function(savedUser, "Linear1", "linear", "x + 1");
        Function func2 = new Function(savedUser, "Linear2", "linear", "2*x");
        Function func3 = new Function(savedUser, "Quadratic", "quadratic", "x^2");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        // Поиск по типу
        List<Function> linearFunctions = functionRepository.findByType("linear");
        assertThat(linearFunctions).hasSize(2);
    }

    @Test
    void whenGenerateFunctions_thenFindByOwnerAndType() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);

        Function func1 = new Function(savedUser, "Sin", "trigonometric", "sin(x)");
        Function func2 = new Function(savedUser, "Cos", "trigonometric", "cos(x)");
        Function func3 = new Function(savedUser, "Linear", "linear", "x");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        // Поиск по владельцу и типу
        List<Function> trigFunctions = functionRepository.findByOwnerAndType(savedUser, "trigonometric");
        assertThat(trigFunctions).hasSize(2);
    }

    @Test
    void whenGenerateFunctions_thenDeleteFunction() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);

        Function func1 = new Function(savedUser, "ToDelete", "linear", "x");
        Function func2 = new Function(savedUser, "ToKeep", "quadratic", "x^2");

        Function saved1 = functionRepository.save(func1);
        Function saved2 = functionRepository.save(func2);

        // Удаление
        functionRepository.delete(saved1);

        // Проверка удаления
        List<Function> remaining = functionRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getName()).isEqualTo("ToKeep");
    }

    @Test
    void whenGenerateFunctions_thenDeleteByOwner() {
        // Генерация данных
        User user1 = new User("owner1", "hash1", User.Role.USER);
        User user2 = new User("owner2", "hash2", User.Role.USER);
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        Function func1 = new Function(savedUser1, "Func1", "linear", "x");
        Function func2 = new Function(savedUser1, "Func2", "quadratic", "x^2");
        Function func3 = new Function(savedUser2, "Func3", "cubic", "x^3");

        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        // Удаление по владельцу
        List<Function> user1Functions = functionRepository.findByOwner(savedUser1);
        functionRepository.deleteAll(user1Functions);

        // Проверка удаления
        List<Function> remaining = functionRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getOwner().getName()).isEqualTo("owner2");
    }
}