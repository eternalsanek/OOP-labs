package service;

import dao.DAOFactory;
import dao.FunctionDAO;
import dto.FunctionDTO;
import dto.FunctionCreateDTO;
import modelDB.Function;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class FunctionService {

    private final FunctionDAO functionDAO;

    public FunctionService() {
        this.functionDAO = DAOFactory.getFunctionDAO();
        log.info("Создан FunctionService с FunctionDAO");
    }

    public FunctionService(FunctionDAO functionDAO) {
        this.functionDAO = functionDAO;
        log.debug("Создан FunctionService с переданным FunctionDAO (для тестов)");
    }

    public FunctionDTO createFunction(FunctionCreateDTO functionDTO) {
        log.info("Начало создания функции: имя={}, тип={}",
                functionDTO.getName(), functionDTO.getType());
        try {
            log.debug("Создание функции в БД: владелец={}, имя={}, выражение={}",
                    functionDTO.getOwnerId(), functionDTO.getName(), functionDTO.getExpression());

            functionDAO.createFunction(
                    functionDTO.getOwnerId(),
                    functionDTO.getName(),
                    functionDTO.getType(),
                    functionDTO.getExpression()
            );
            List<Function> functions = functionDAO.getFunctionsByOwner(functionDTO.getOwnerId());
            Optional<Function> createdFunction = functions.stream()
                    .filter(f -> f.getName().equals(functionDTO.getName()))
                    .findFirst();
            FunctionDTO result = createdFunction.map(this::toDTO)
                    .orElseThrow(() -> {
                        log.error("Не удалось найти созданную функцию: имя={}, владелец={}",
                                functionDTO.getName(), functionDTO.getOwnerId());
                        return new RuntimeException("Функция не найдена после создания");
                    });
            log.info("Функция успешно создана: ID={}, имя={}, тип={}",
                    result.getId(), result.getName(), result.getType());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при создании функции: {}", e.getMessage(), e);
            throw e;
        }
    }

    public FunctionDTO getFunctionById(UUID id) {
        log.debug("Получение функции по ID: {}", id);
        Optional<Function> function = functionDAO.getFunctionById(id);
        FunctionDTO result = function.map(this::toDTO)
                .orElseThrow(() -> {
                    log.warn("Функция с ID {} не найдена", id);
                    return new RuntimeException("Функция не найдена");
                });
        log.debug("Функция найдена: ID={}, имя={}, тип={}",
                result.getId(), result.getName(), result.getType());
        return result;
    }

    public List<FunctionDTO> getFunctionsByOwner(UUID ownerId) {
        log.debug("Получение всех функций пользователя: ID={}", ownerId);
        List<Function> functions = functionDAO.getFunctionsByOwner(ownerId);
        List<FunctionDTO> result = functions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        log.debug("Найдено {} функций для пользователя ID={}", result.size(), ownerId);
        return result;
    }

    public FunctionDTO updateFunction(UUID id, FunctionCreateDTO functionDTO) {
        log.info("Обновление функции ID: {}", id);
        try {
            log.debug("Обновление данных функции: имя={}, тип={}, выражение={}",
                    functionDTO.getName(), functionDTO.getType(), functionDTO.getExpression());

            functionDAO.updateFunction(
                    id,
                    functionDTO.getName(),
                    functionDTO.getType(),
                    functionDTO.getExpression()
            );
            Optional<Function> updatedFunction = functionDAO.getFunctionById(id);
            FunctionDTO result = updatedFunction.map(this::toDTO)
                    .orElseThrow(() -> {
                        log.error("Функция не найдена после обновления: ID={}", id);
                        return new RuntimeException("Функция не найдена после обновления");
                    });
            log.info("Функция успешно обновлена: ID={}, имя={}", id, result.getName());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при обновлении функции ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteFunction(UUID id) {
        log.info("Удаление функции ID: {}", id);
        try {
            Optional<Function> function = functionDAO.getFunctionById(id);
            function.ifPresent(f ->
                    log.debug("Удаление функции: имя={}, тип={}", f.getName(), f.getType())
            );
            functionDAO.deleteFunction(id);
            log.info("Функция успешно удалена: ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении функции ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    private FunctionDTO toDTO(Function function) {
        return new FunctionDTO(
                function.getId(),
                function.getOwnerId(),
                function.getName(),
                function.getType(),
                function.getExpression()
        );
    }
}
