package service;

import dao.FunctionDAO;
import dto.FunctionCreateDTO;
import dto.FunctionDTO;
import modelDB.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class FunctionServiceTest {

    @Mock
    private FunctionDAO functionDAO;

    @InjectMocks
    private FunctionService functionService;

    @Test
    void shouldCreateFunctionSuccessfully() {
        log.info("Тест: успешное создание функции");

        UUID ownerId = UUID.randomUUID();
        FunctionCreateDTO createDTO = new FunctionCreateDTO(ownerId, "testFunc", "LINEAR", "x + 2");

        Function savedFunction = new Function();
        savedFunction.setId(UUID.randomUUID());
        savedFunction.setOwnerId(ownerId);
        savedFunction.setName("testFunc");
        savedFunction.setType("LINEAR");
        savedFunction.setExpression("x + 2");

        when(functionDAO.getFunctionsByOwner(ownerId)).thenReturn(Arrays.asList(savedFunction));

        FunctionDTO result = functionService.createFunction(createDTO);

        assertNotNull(result);
        assertEquals("testFunc", result.getName());
        assertEquals("LINEAR", result.getType());
        assertEquals("x + 2", result.getExpression());

        verify(functionDAO).createFunction(any(), any(), any(), any());
        log.info("Тест создания функции завершен успешно");
    }

    @Test
    void shouldGetFunctionsByOwner() {
        log.info("Тест: получение функций пользователя");

        UUID ownerId = UUID.randomUUID();
        Function func1 = new Function();
        func1.setId(UUID.randomUUID());
        func1.setOwnerId(ownerId);
        func1.setName("func1");
        func1.setType("LINEAR");

        Function func2 = new Function();
        func2.setId(UUID.randomUUID());
        func2.setOwnerId(ownerId);
        func2.setName("func2");
        func2.setType("QUADRATIC");

        when(functionDAO.getFunctionsByOwner(ownerId)).thenReturn(Arrays.asList(func1, func2));

        List<FunctionDTO> result = functionService.getFunctionsByOwner(ownerId);

        assertEquals(2, result.size());
        assertEquals("func1", result.get(0).getName());
        assertEquals("func2", result.get(1).getName());

        log.info("Тест получения функций завершен успешно");
    }
}