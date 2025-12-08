package ru.ssau.tk.NAME.PROJECT.mapper;

import ru.ssau.tk.NAME.PROJECT.dto.PointDTO;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class PointMapper {

    public PointDTO toDTO(Point point) {
        if (point == null) {
            return null;
        }

        PointDTO dto = PointDTO.builder()
                .id(point.getId())
                .functionId(point.getFunction() != null ? point.getFunction().getId() : null)
                .xVal(point.getXVal())
                .yVal(point.getYVal())
                .build();

        log.debug("Mapped point entity to DTO: {} -> {}", point, dto);
        return dto;
    }

    public Point toEntity(PointDTO dto, Function function) {
        if (dto == null) {
            return null;
        }

        Point point = new Point();
        point.setId(dto.getId());
        point.setFunction(function);

        BigDecimal xVal = dto.getXVal() != null ? dto.getXVal() : BigDecimal.ZERO;
        BigDecimal yVal = dto.getYVal() != null ? dto.getYVal() : BigDecimal.ZERO;

        point.setXVal(xVal);
        point.setYVal(yVal);

        log.debug("Mapped DTO to point entity: {} -> {}", dto, point);
        return point;
    }
}
