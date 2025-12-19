package ru.ssau.tk.NAME.PROJECT.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String fieldName;
    private Object value;
    private List<Object> values;
    private SearchOperation operation;

    public SearchCriteria(String fieldName, SearchOperation operation, Object value) {
        this.fieldName = fieldName;
        this.operation = operation;
        this.value = value;
    }
}
