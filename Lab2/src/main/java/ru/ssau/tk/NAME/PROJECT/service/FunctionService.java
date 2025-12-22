package ru.ssau.tk.NAME.PROJECT.service;

import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.dto.PointDTO;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.mapper.FunctionMapper;
import ru.ssau.tk.NAME.PROJECT.mapper.PointMapper;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import ru.ssau.tk.NAME.PROJECT.repository.PointRepository;
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
public class FunctionService {

    private final FunctionRepository functionRepository;
    private final UserRepository userRepository;
    private final FunctionMapper functionMapper;
    private final PointRepository pointRepository;
    private final PointMapper pointMapper;

    public List<FunctionDTO> getAllFunctions() {
        return functionRepository.findAll().stream()
                .map(functionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<FunctionDTO> getFunctionById(UUID id) {
        return functionRepository.findById(id)
                .map(functionMapper::toDTO);
    }

    public List<FunctionDTO> getFunctionsByOwnerId(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + ownerId));
        return functionRepository.findByOwner(owner).stream()
                .map(functionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FunctionDTO> getFunctionsByOwner(String username) {
        Optional<User> ownerOpt = userRepository.findByName(username);
        if (!ownerOpt.isPresent()) {
            return List.of(); // Или выбросить исключение
        }
        User owner = ownerOpt.get();
        List<Function> functions = functionRepository.findByOwner(owner);
        return functions.stream()
                .map(functionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FunctionDTO createFunction(FunctionDTO functionDTO) {
        if (functionDTO.getName() == null || functionDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Function name is required");
        }

        if (functionDTO.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }

        User owner = userRepository.findById(functionDTO.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + functionDTO.getOwnerId()));

        Function function = functionMapper.toEntity(functionDTO, owner);
        function = functionRepository.save(function);
        return functionMapper.toDTO(function);
    }

    @Transactional
    public Optional<FunctionDTO> updateFunction(UUID id, FunctionDTO functionDTO) {
        return functionRepository.findById(id)
                .map(existingFunction -> {
                    // Обновляем поля
                    if (functionDTO.getName() != null) {
                        existingFunction.setName(functionDTO.getName());
                    }

                    if (functionDTO.getType() != null) {
                        existingFunction.setType(functionDTO.getType());
                    }

                    if (functionDTO.getExpression() != null) {
                        existingFunction.setExpression(functionDTO.getExpression());
                    }
                    return functionRepository.save(existingFunction);
                })
                .map(functionMapper::toDTO);
    }

    @Transactional
    public PointDTO addPointToFunction(UUID functionId, PointDTO pointDTO) {
        if (pointDTO == null) {
            throw new IllegalArgumentException("Point data is required");
        }

        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found with ID: " + functionId));

        // --- ИСПРАВЛЕНО ---
        // Создаём новую точку из DTO, передавая связь с функцией
        Point point = pointMapper.toEntity(pointDTO, function); // Вызываем через поле pointMapper
        // point.setFunction(function); // Уже установлено в toEntity, если там реализовано
        // ---
        // Сохраняем точку
        point = pointRepository.save(point); // Вызываем через поле pointRepository
        // ---
        // Возвращаем DTO созданной точки
        return pointMapper.toDTO(point); // Вызываем через поле pointMapper
        // --- /ИСПРАВЛЕНО ---
    }

    @Transactional
    public boolean deletePointFromFunction(UUID functionId, UUID pointId) {
        // Найдём функцию
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found with ID: " + functionId));

        // Найдём точку
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found with ID: " + pointId));

        // Проверим, принадлежит ли точка этой функции
        if (!point.getFunction().getId().equals(functionId)) {
            throw new IllegalArgumentException("Point does not belong to the specified function");
        }

        // Удалим точку (каскадное удаление может сработать, если настроено в Entity)
        pointRepository.deleteById(pointId);

        // Опционально: удалим точку из коллекции функции и сохраним функцию (не всегда нужно при каскаде)
        // function.removePoint(point); // Если используете метод removePoint
        // functionRepository.save(function);

        return true; // Успешно удалено
    }


    @Transactional
    public boolean deleteFunction(UUID id) {
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
