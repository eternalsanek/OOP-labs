package ru.ssau.tk.NAME.PROJECT.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateTimeKeyDeserializer;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointDTO {
    private UUID id;
    private UUID functionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal xVal;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal yVal;
    public String getXValAsString() {
        return xVal != null ? xVal.toString() : null;
    }
    public String getYValAsString() {
        return yVal != null ? yVal.toString() : null;
    }
    public void setXValFromString(String value) {
        this.xVal = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }
    public void setYValFromString(String value) {
        this.yVal = value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }
}
