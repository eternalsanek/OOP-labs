package ru.ssau.tk.NAME.PROJECT.mapper;

import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FunctionMapper {

    public FunctionDTO toDTO(Function function) {
        return FunctionDTO.builder()
                .id(function.getId())
                .name(function.getName())
                .type(function.getType())
                .expression(function.getExpression())
                .ownerId(function.getOwner() != null ? function.getOwner().getId() : null)
                .build();
    }

    public Function toEntity(FunctionDTO dto, User owner) {
        Function function = new Function();
        function.setId(dto.getId());
        function.setName(dto.getName());
        function.setType(dto.getType());
        function.setExpression(dto.getExpression());
        function.setOwner(owner);
        return function;
    }
}
