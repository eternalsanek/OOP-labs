// PointServiceTest.java
package service;

import dao.PointDAO;
import dto.PointCreateDTO;
import dto.PointDTO;
import modelDB.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointDAO pointDAO;

    @InjectMocks
    private PointService pointService;

    @Test
    void shouldCreatePointSuccessfully() {
        log.info("Тест: успешное создание точки");

        // Given
        UUID functionId = UUID.randomUUID();
        PointCreateDTO createDTO = new PointCreateDTO(functionId, 1.5, 3.2);

        Point savedPoint = new Point();
        savedPoint.setId(UUID.randomUUID());
        savedPoint.setFunctionId(functionId);
        savedPoint.setXVal(1.5);
        savedPoint.setYVal(3.2);

        // When - Используем thenReturn с несколькими возвращаемыми значениями
        when(pointDAO.getPointsByFunction(functionId))
                .thenReturn(Collections.emptyList())  // Первый вызов - пустой список
                .thenReturn(Arrays.asList(savedPoint)); // Второй вызов - список с точкой

        PointDTO result = pointService.createPoint(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(1.5, result.getXVal());
        assertEquals(3.2, result.getYVal());
        assertEquals(functionId, result.getFunctionId());

        verify(pointDAO).createPoint(eq(functionId), eq(1.5), eq(3.2));
        log.info("Тест создания точки завершен успешно");
    }

    @Test
    void shouldCreatePointSuccessfullyWithThenAnswer() {
        log.info("Тест: успешное создание точки (с thenAnswer)");

        // Given
        UUID functionId = UUID.randomUUID();
        PointCreateDTO createDTO = new PointCreateDTO(functionId, 2.0, 4.0);

        Point savedPoint = new Point();
        savedPoint.setId(UUID.randomUUID());
        savedPoint.setFunctionId(functionId);
        savedPoint.setXVal(2.0);
        savedPoint.setYVal(4.0);

        // When - Используем thenAnswer для полного контроля
        final boolean[] firstCall = {true};
        when(pointDAO.getPointsByFunction(functionId))
                .thenAnswer(invocation -> {
                    if (firstCall[0]) {
                        firstCall[0] = false;
                        return Collections.emptyList(); // Первый вызов - пустой список
                    } else {
                        return Arrays.asList(savedPoint); // Второй вызов - с точкой
                    }
                });

        PointDTO result = pointService.createPoint(createDTO);

        // Then
        assertNotNull(result);
        assertEquals(2.0, result.getXVal());
        assertEquals(4.0, result.getYVal());

        verify(pointDAO).createPoint(eq(functionId), eq(2.0), eq(4.0));
        log.info("Тест создания точки с thenAnswer завершен успешно");
    }

    @Test
    void shouldThrowExceptionWhenPointWithSameXExists() {
        log.info("Тест: ошибка при создании точки с существующим X");

        // Given
        UUID functionId = UUID.randomUUID();
        PointCreateDTO createDTO = new PointCreateDTO(functionId, 1.5, 3.2);

        Point existingPoint = new Point();
        existingPoint.setId(UUID.randomUUID());
        existingPoint.setFunctionId(functionId);
        existingPoint.setXVal(1.5); // Та же координата X!
        existingPoint.setYVal(2.0);

        // When - настраиваем мок так, чтобы возвращал существующую точку
        when(pointDAO.getPointsByFunction(functionId))
                .thenReturn(Arrays.asList(existingPoint));

        // Then - ожидаем исключение
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> pointService.createPoint(createDTO));

        assertTrue(exception.getMessage().contains("Точка с таким X уже существует"));

        // Проверяем, что createPoint НЕ вызывался
        verify(pointDAO, never()).createPoint(any(), anyDouble(), anyDouble());
        log.info("Тест проверки уникальности X завершен успешно");
    }

    @Test
    void shouldGetPointsByFunction() {
        log.info("Тест: получение точек функции");

        // Given
        UUID functionId = UUID.randomUUID();
        Point point1 = new Point();
        point1.setId(UUID.randomUUID());
        point1.setFunctionId(functionId);
        point1.setXVal(1.0);
        point1.setYVal(2.0);

        Point point2 = new Point();
        point2.setId(UUID.randomUUID());
        point2.setFunctionId(functionId);
        point2.setXVal(2.0);
        point2.setYVal(4.0);

        // When
        when(pointDAO.getPointsByFunction(functionId))
                .thenReturn(Arrays.asList(point1, point2));

        List<PointDTO> result = pointService.getPointsByFunction(functionId);

        // Then
        assertEquals(2, result.size());
        assertEquals(1.0, result.get(0).getXVal());
        assertEquals(2.0, result.get(1).getXVal());

        verify(pointDAO).getPointsByFunction(functionId);
        log.info("Тест получения точек завершен успешно");
    }

    @Test
    void shouldGetPointById() {
        log.info("Тест: получение точки по ID");

        // Given
        UUID pointId = UUID.randomUUID();
        UUID functionId = UUID.randomUUID();

        Point point = new Point();
        point.setId(pointId);
        point.setFunctionId(functionId);
        point.setXVal(1.5);
        point.setYVal(3.2);

        // When
        when(pointDAO.getPointById(pointId))
                .thenReturn(Optional.of(point));

        PointDTO result = pointService.getPointById(pointId);

        // Then
        assertNotNull(result);
        assertEquals(1.5, result.getXVal());
        assertEquals(3.2, result.getYVal());
        assertEquals(pointId, result.getId());

        log.info("Тест получения точки по ID завершен успешно");
    }

    @Test
    void shouldUpdatePointSuccessfully() {
        log.info("Тест: успешное обновление точки");

        // Given
        UUID pointId = UUID.randomUUID();
        UUID functionId = UUID.randomUUID();
        PointCreateDTO updateDTO = new PointCreateDTO(functionId, 3.0, 6.0);

        Point updatedPoint = new Point();
        updatedPoint.setId(pointId);
        updatedPoint.setFunctionId(functionId);
        updatedPoint.setXVal(3.0);
        updatedPoint.setYVal(6.0);

        // When
        when(pointDAO.getPointById(pointId))
                .thenReturn(Optional.of(updatedPoint));

        PointDTO result = pointService.updatePoint(pointId, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(3.0, result.getXVal());
        assertEquals(6.0, result.getYVal());

        verify(pointDAO).updatePoint(eq(pointId), eq(3.0), eq(6.0));
        log.info("Тест обновления точки завершен успешно");
    }

    @Test
    void shouldDeletePointSuccessfully() {
        log.info("Тест: успешное удаление точки");

        // Given
        UUID pointId = UUID.randomUUID();

        Point point = new Point();
        point.setId(pointId);
        point.setFunctionId(UUID.randomUUID());
        point.setXVal(1.0);
        point.setYVal(2.0);

        // When
        when(pointDAO.getPointById(pointId))
                .thenReturn(Optional.of(point));

        pointService.deletePoint(pointId);

        // Then
        verify(pointDAO).deletePoint(pointId);
        log.info("Тест удаления точки завершен успешно");
    }

    @Test
    void shouldDeleteAllPointsByFunction() {
        log.info("Тест: удаление всех точек функции");

        // Given
        UUID functionId = UUID.randomUUID();

        Point point1 = new Point();
        point1.setId(UUID.randomUUID());
        point1.setFunctionId(functionId);
        point1.setXVal(1.0);
        point1.setYVal(2.0);

        Point point2 = new Point();
        point2.setId(UUID.randomUUID());
        point2.setFunctionId(functionId);
        point2.setXVal(2.0);
        point2.setYVal(4.0);

        // When
        when(pointDAO.getPointsByFunction(functionId))
                .thenReturn(Arrays.asList(point1, point2));

        pointService.deleteAllPointsByFunction(functionId);

        // Then
        verify(pointDAO, times(2)).deletePoint(any(UUID.class));
        log.info("Тест удаления всех точек завершен успешно");
    }
}