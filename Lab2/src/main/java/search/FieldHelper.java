// search/FieldHelper.java
package search;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Comparator;

@Slf4j
public class FieldHelper {

    public static <T> boolean matchesField(T item, String fieldName, String value) {
        try {
            String fieldValue = getFieldValue(item, fieldName);
            return fieldValue.toLowerCase().contains(value.toLowerCase());
        } catch (Exception e) {
            log.warn("Ошибка доступа к полю '{}': {}", fieldName, e.getMessage());
            return false;
        }
    }

    public static <T> String getFieldValue(T item, String fieldName) {
        try {
            Object fieldValue = getFieldValueObject(item, fieldName);
            return fieldValue != null ? fieldValue.toString() : "";
        } catch (Exception e) {
            log.warn("Ошибка получения значения поля '{}': {}", fieldName, e.getMessage());
            return "";
        }
    }

    public static <T> Object getFieldValueObject(T item, String fieldName) {
        try {
            Field declaredField = item.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField.get(item);
        } catch (Exception e) {
            log.warn("Ошибка доступа к полю '{}' через рефлексию: {}", fieldName, e.getMessage());
            return null;
        }
    }

    public static <T> Comparator<T> createComparator(String fieldName) {
        return (o1, o2) -> {
            try {
                String val1 = getFieldValue(o1, fieldName).toLowerCase();
                String val2 = getFieldValue(o2, fieldName).toLowerCase();
                return val1.compareTo(val2);
            } catch (Exception e) {
                log.warn("Ошибка сравнения полей '{}': {}", fieldName, e.getMessage());
                return 0;
            }
        };
    }

    public static <T> Comparable<?> getComparableFieldValue(T item, String fieldName) {
        try {
            Object fieldValue = getFieldValueObject(item, fieldName);
            return (Comparable<?>) fieldValue;
        } catch (Exception e) {
            log.warn("Ошибка приведения поля '{}' к Comparable: {}", fieldName, e.getMessage());
            return null;
        }
    }
}