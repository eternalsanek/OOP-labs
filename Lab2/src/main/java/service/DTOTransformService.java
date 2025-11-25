// service/DTOTransformService.java
package service;

import dao.PointDAOImpl;
import modelDB.User;
import modelDB.Function;
import modelDB.Point;
import dao.UserDAO;
import dao.FunctionDAO;
import dao.PointDAO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DTOTransformService {
    private final UserDAO userDAO;
    private final FunctionDAO functionDAO;
    private final PointDAO pointDAO;

    public DTOTransformService(UserDAO userDAO, FunctionDAO functionDAO, PointDAO pointDAO) {
        this.userDAO = userDAO;
        this.functionDAO = functionDAO;
        this.pointDAO = pointDAO;
        log.debug("Создан DTOTransformService");
    }

    // User операции
    public Optional<User> getUserById(UUID id) {
        log.debug("Получение пользователя по ID: {}", id);
        return userDAO.getUserById(id);
    }

    public Optional<User> getUserByName(String name) {
        log.debug("Получение пользователя по имени: {}", name);
        return userDAO.getUserByName(name);
    }

    public List<User> getAllUsers() {
        log.debug("Получение всех пользователей");
        return userDAO.getAllUsers();
    }

    public User createUser(String name, String passwordHash, String role) {
        log.debug("Создание пользователя: имя={}, роль={}", name, role);
        validateRole(role);
        userDAO.createUser(name, passwordHash, role);

        // Получаем созданного пользователя
        Optional<User> createdUser = userDAO.getUserByName(name);
        if (createdUser.isPresent()) {
            log.info("Пользователь успешно создан: {}", createdUser.get());
            return createdUser.get();
        } else {
            log.error("Пользователь не найден после создания: {}", name);
            throw new RuntimeException("Ошибка при создании пользователя");
        }
    }

    public User updateUser(UUID id, String newName, String newPasswordHash, String role) {
        log.debug("Обновление пользователя: ID={}, новое имя={}", id, newName);
        validateRole(role);
        userDAO.updateUser(id, newName, newPasswordHash, role);

        Optional<User> updatedUser = userDAO.getUserById(id);
        if (updatedUser.isPresent()) {
            log.info("Пользователь успешно обновлен: {}", updatedUser.get());
            return updatedUser.get();
        } else {
            log.error("Пользователь не найден после обновления: {}", id);
            throw new RuntimeException("Ошибка при обновлении пользователя");
        }
    }

    public void deleteUser(UUID id) {
        log.debug("Удаление пользователя: ID={}", id);

        // Сначала удаляем все функции пользователя (и их точки)
        List<Function> userFunctions = functionDAO.getFunctionsByOwner(id);
        for (Function function : userFunctions) {
            deleteFunction(function.getId());
        }

        userDAO.deleteUser(id);
        log.info("Пользователь успешно удален: ID={}", id);
    }

    // Function операции
    public Optional<Function> getFunctionById(UUID id) {
        log.debug("Получение функции по ID: {}", id);
        return functionDAO.getFunctionById(id);
    }

    public List<Function> getFunctionsByOwner(UUID ownerId) {
        log.debug("Получение функций пользователя: ID={}", ownerId);
        return functionDAO.getFunctionsByOwner(ownerId);
    }

    public Function createFunction(UUID ownerId, String name, String type, String expression) {
        log.debug("Создание функции: имя={}, тип={}, владелец={}", name, type, ownerId);
        validateFunctionType(type);
        functionDAO.createFunction(ownerId, name, type, expression);

        // Получаем созданную функцию по имени и владельцу
        List<Function> userFunctions = functionDAO.getFunctionsByOwner(ownerId);
        Optional<Function> createdFunction = userFunctions.stream()
                .filter(f -> f.getName().equals(name) && f.getType().equals(type))
                .findFirst();

        if (createdFunction.isPresent()) {
            log.info("Функция успешно создана: {}", createdFunction.get());
            return createdFunction.get();
        } else {
            log.error("Функция не найдена после создания: {}", name);
            throw new RuntimeException("Ошибка при создании функции");
        }
    }

    public Function updateFunction(UUID id, String name, String type, String expression) {
        log.debug("Обновление функции: ID={}, новое имя={}", id, name);
        validateFunctionType(type);
        functionDAO.updateFunction(id, name, type, expression);

        Optional<Function> updatedFunction = functionDAO.getFunctionById(id);
        if (updatedFunction.isPresent()) {
            log.info("Функция успешно обновлена: {}", updatedFunction.get());
            return updatedFunction.get();
        } else {
            log.error("Функция не найдена после обновления: {}", id);
            throw new RuntimeException("Ошибка при обновлении функции");
        }
    }

    public void deleteFunction(UUID id) {
        log.debug("Удаление функции: ID={}", id);

        // Сначала удаляем все точки функции
        if (pointDAO instanceof PointDAOImpl) {
            ((PointDAOImpl) pointDAO).deletePointsByFunction(id);
        } else {
            List<Point> functionPoints = pointDAO.getPointsByFunction(id);
            for (Point point : functionPoints) {
                pointDAO.deletePoint(point.getId());
            }
        }

        functionDAO.deleteFunction(id);
        log.info("Функция успешно удалена: ID={}", id);
    }

    // Point операции
    public Optional<Point> getPointById(UUID id) {
        log.debug("Получение точки по ID: {}", id);
        return pointDAO.getPointById(id);
    }

    public List<Point> getPointsByFunction(UUID funcId) {
        log.debug("Получение точек функции: ID={}", funcId);
        return pointDAO.getPointsByFunction(funcId);
    }

    public Point createPoint(UUID funcId, double x, double y) {
        log.debug("Создание точки: функция={}, координаты=({}, {})", funcId, x, y);
        pointDAO.createPoint(funcId, x, y);

        // Получаем созданную точку по координатам и функции
        List<Point> functionPoints = pointDAO.getPointsByFunction(funcId);
        Optional<Point> createdPoint = functionPoints.stream()
                .filter(p -> p.getXVal() == x && p.getYVal() == y)
                .findFirst();

        if (createdPoint.isPresent()) {
            log.info("Точка успешно создана: {}", createdPoint.get());
            return createdPoint.get();
        } else {
            log.error("Точка не найдена после создания: x={}, y={}", x, y);
            throw new RuntimeException("Ошибка при создании точки");
        }
    }

    public Point updatePoint(UUID id, double x, double y) {
        log.debug("Обновление точки: ID={}, новые координаты=({}, {})", id, x, y);
        pointDAO.updatePoint(id, x, y);

        Optional<Point> updatedPoint = pointDAO.getPointById(id);
        if (updatedPoint.isPresent()) {
            log.info("Точка успешно обновлена: {}", updatedPoint.get());
            return updatedPoint.get();
        } else {
            log.error("Точка не найдена после обновления: {}", id);
            throw new RuntimeException("Ошибка при обновлении точки");
        }
    }

    public void deletePoint(UUID id) {
        log.debug("Удаление точки: ID={}", id);
        pointDAO.deletePoint(id);
        log.info("Точка успешно удалена: ID={}", id);
    }

    // Валидации
    private void validateRole(String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            log.error("Недопустимая роль: {}", role);
            throw new IllegalArgumentException("Роль должна быть 'USER' или 'ADMIN'");
        }
    }

    private void validateFunctionType(String type) {
        if (!"MathFunction".equals(type) && !"TabulatedFunction".equals(type)) {
            log.error("Недопустимый тип функции: {}", type);
            throw new IllegalArgumentException("Тип функции должен быть 'MathFunction' или 'TabulatedFunction'");
        }
    }

    public void close() {
        log.debug("Закрытие сервиса трансформации DTO");
        userDAO.close();
        functionDAO.close();
        pointDAO.close();
    }
}