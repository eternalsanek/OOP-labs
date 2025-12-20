package ru.ssau.tk.NAME.PROJECT.repository;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PointRepositoryTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenGeneratePoints_thenFindAll() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);
        Function function = new Function(savedUser, "TestFunc", "linear", "x");
        Function savedFunction = functionRepository.save(function);

        Point point1 = new Point(savedFunction, new BigDecimal("1.0"), new BigDecimal("2.0"));
        Point point2 = new Point(savedFunction, new BigDecimal("2.0"), new BigDecimal("3.0"));

        pointRepository.save(point1);
        pointRepository.save(point2);

        // Поиск всех
        List<Point> points = pointRepository.findAll();
        assertThat(points).hasSize(2);
    }

    @Test
    void whenGeneratePoints_thenFindByFunction() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);
        Function func1 = new Function(savedUser, "Func1", "linear", "x");
        Function func2 = new Function(savedUser, "Func2", "quadratic", "x^2");
        Function savedFunc1 = functionRepository.save(func1);
        Function savedFunc2 = functionRepository.save(func2);

        Point point1 = new Point(savedFunc1, new BigDecimal("1.0"), new BigDecimal("1.0"));
        Point point2 = new Point(savedFunc1, new BigDecimal("2.0"), new BigDecimal("2.0"));
        Point point3 = new Point(savedFunc2, new BigDecimal("1.0"), new BigDecimal("1.0"));

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);

        // Поиск по функции
        List<Point> func1Points = pointRepository.findByFunction(savedFunc1);
        assertThat(func1Points).hasSize(2);
    }

    @Test
    void whenGeneratePoints_thenFindByFunctionId() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);
        Function function = new Function(savedUser, "TestFunc", "linear", "x");
        Function savedFunction = functionRepository.save(function);

        Point point1 = new Point(savedFunction, new BigDecimal("1.0"), new BigDecimal("2.0"));
        Point point2 = new Point(savedFunction, new BigDecimal("2.0"), new BigDecimal("4.0"));

        pointRepository.save(point1);
        pointRepository.save(point2);

        // Поиск по ID функции
        List<Point> points = pointRepository.findByFunctionId(savedFunction.getId());
        assertThat(points).hasSize(2);
    }

    @Test
    void whenGeneratePoints_thenFindByFunctionAndXValBetween() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);
        Function function = new Function(savedUser, "TestFunc", "linear", "x");
        Function savedFunction = functionRepository.save(function);

        Point point1 = new Point(savedFunction, new BigDecimal("1.0"), new BigDecimal("2.0"));
        Point point2 = new Point(savedFunction, new BigDecimal("2.0"), new BigDecimal("4.0"));
        Point point3 = new Point(savedFunction, new BigDecimal("3.0"), new BigDecimal("6.0"));

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);

        // Поиск в диапазоне
        List<Point> pointsInRange = pointRepository.findByFunctionAndXValBetween(
                savedFunction, new BigDecimal("1.5"), new BigDecimal("2.5"));

        assertThat(pointsInRange).hasSize(1);
        assertThat(pointsInRange.get(0).getXVal()).isEqualTo(new BigDecimal("2.0"));
    }

    @Test
    void whenGeneratePoints_thenDeletePoint() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);
        Function function = new Function(savedUser, "TestFunc", "linear", "x");
        Function savedFunction = functionRepository.save(function);

        Point point1 = new Point(savedFunction, new BigDecimal("1.0"), new BigDecimal("2.0"));
        Point point2 = new Point(savedFunction, new BigDecimal("2.0"), new BigDecimal("4.0"));

        Point saved1 = pointRepository.save(point1);
        Point saved2 = pointRepository.save(point2);

        // Удаление
        pointRepository.delete(saved1);

        // Проверка удаления
        List<Point> remaining = pointRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getXVal()).isEqualTo(new BigDecimal("2.0"));
    }

    @Test
    void whenGeneratePoints_thenDeleteByFunction() {
        // Генерация данных
        User user = new User("owner", "hash", User.Role.USER);
        User savedUser = userRepository.save(user);
        Function func1 = new Function(savedUser, "Func1", "linear", "x");
        Function func2 = new Function(savedUser, "Func2", "quadratic", "x^2");
        Function savedFunc1 = functionRepository.save(func1);
        Function savedFunc2 = functionRepository.save(func2);

        Point point1 = new Point(savedFunc1, new BigDecimal("1.0"), new BigDecimal("1.0"));
        Point point2 = new Point(savedFunc1, new BigDecimal("2.0"), new BigDecimal("2.0"));
        Point point3 = new Point(savedFunc2, new BigDecimal("1.0"), new BigDecimal("1.0"));

        pointRepository.save(point1);
        pointRepository.save(point2);
        pointRepository.save(point3);

        // Удаление по функции
        pointRepository.deleteByFunction(savedFunc1);

        // Проверка удаления
        List<Point> remaining = pointRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getFunction().getName()).isEqualTo("Func2");
    }
}