package ru.ssau.tk.NAME.PROJECT.mapper;

import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.dto.PointDTO;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FunctionMapper {

    public FunctionDTO toDTO(Function function) {
        return FunctionDTO.builder()
                .id(function.getId())
                .name(function.getName())
                .type(function.getType())
                .expression(function.getExpression())
                .ownerId(function.getOwner() != null ? function.getOwner().getId() : null)
                // Маппим точки из Entity в DTO
                .points(function.getPoints().stream()
                        .map(this::toPointDTO) // Используем вспомогательный метод
                        .collect(Collectors.toList()))
                .build();
    }

    public Function toEntity(FunctionDTO dto, User owner) {
        Function function = new Function();
        function.setId(dto.getId());
        function.setName(dto.getName());
        function.setType(dto.getType());
        function.setExpression(dto.getExpression());
        function.setOwner(owner);

        // --- НОВЫЙ КОД ---
        // Маппим точки из DTO в Entity и устанавливаем связь
        if (dto.getPoints() != null && !dto.getPoints().isEmpty()) {
            for (PointDTO pointDTO : dto.getPoints()) {
                Point point = toPointEntity(pointDTO); // Используем вспомогательный метод
                point.setFunction(function); // Устанавливаем связь с функцией
                function.addPoint(point); // Используем метод addPoint, чтобы корректно добавить в коллекцию и установить связь в обе стороны
            }
        }
        // --- /НОВЫЙ КОД ---

        return function;
    }

    // Вспомогательный метод для маппинга PointDTO -> PointEntity
    private Point toPointEntity(PointDTO pointDTO) {
        // Предполагаем, что PointDTO имеет поля x и y (как в FunctionForm.jsx)
        return new Point(
                null, // Функция будет установлена позже
                pointDTO.getXVal(), // Используем BigDecimal напрямую
                pointDTO.getYVal()  // Используем BigDecimal напрямую
        );
    }

    // Вспомогательный метод для маппинга PointEntity -> PointDTO
    private PointDTO toPointDTO(Point pointEntity) {
        return PointDTO.builder()
                .id(pointEntity.getId())
                .xVal(pointEntity.getXVal()) // или BigDecimal, если DTO использует BigDecimal
                .yVal(pointEntity.getYVal()) // или BigDecimal, если DTO использует BigDecimal
                .build();
    }
}
