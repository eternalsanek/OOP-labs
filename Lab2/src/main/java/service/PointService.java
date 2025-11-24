package service;

import dao.DAOFactory;
import dao.PointDAO;
import dto.PointDTO;
import dto.PointCreateDTO;
import modelDB.Point;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class PointService {

    private final PointDAO pointDAO;

    public PointService() {
        this.pointDAO = DAOFactory.getPointDAO();
        log.info("Создан PointService с PointDAO");
    }

    public PointService(PointDAO pointDAO) {
        this.pointDAO = pointDAO;
        log.debug("Создан PointService с переданным PointDAO (для тестов)");
    }

    public PointDTO createPoint(PointCreateDTO pointDTO) {
        log.info("Создание точки для функции ID: {}", pointDTO.getFunctionId());
        try {
            log.debug("Данные точки: x={}, y={}", pointDTO.getXVal(), pointDTO.getYVal());
            List<Point> existingPoints = pointDAO.getPointsByFunction(pointDTO.getFunctionId());
            boolean pointExists = existingPoints.stream()
                    .anyMatch(p -> p.getXVal() == pointDTO.getXVal());
            if (pointExists) {
                log.warn("Точка с x={} уже существует для функции ID={}",
                        pointDTO.getXVal(), pointDTO.getFunctionId());
                throw new RuntimeException("Точка с таким X уже существует для этой функции");
            }
            pointDAO.createPoint(
                    pointDTO.getFunctionId(),
                    pointDTO.getXVal(),
                    pointDTO.getYVal()
            );
            List<Point> points = pointDAO.getPointsByFunction(pointDTO.getFunctionId());
            Optional<Point> createdPoint = points.stream()
                    .filter(p -> p.getXVal() == pointDTO.getXVal() && p.getYVal() == pointDTO.getYVal())
                    .findFirst();
            PointDTO result = createdPoint.map(this::toDTO)
                    .orElseThrow(() -> {
                        log.error("Не удалось найти созданную точку: функция={}, x={}, y={}",
                                pointDTO.getFunctionId(), pointDTO.getXVal(), pointDTO.getYVal());
                        return new RuntimeException("Точка не найдена после создания");
                    });
            log.info("Точка успешно создана: ID={}, функция={}, координаты=({}, {})",
                    result.getId(), result.getFunctionId(), result.getXVal(), result.getYVal());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при создании точки: {}", e.getMessage(), e);
            throw e;
        }
    }

    public PointDTO getPointById(UUID id) {
        log.debug("Получение точки по ID: {}", id);
        Optional<Point> point = pointDAO.getPointById(id);
        PointDTO result = point.map(this::toDTO)
                .orElseThrow(() -> {
                    log.warn("Точка с ID {} не найдена", id);
                    return new RuntimeException("Точка не найдена");
                });
        log.debug("Точка найдена: ID={}, координаты=({}, {})",
                result.getId(), result.getXVal(), result.getYVal());
        return result;
    }

    public List<PointDTO> getPointsByFunction(UUID functionId) {
        log.debug("Получение всех точек функции: ID={}", functionId);
        List<Point> points = pointDAO.getPointsByFunction(functionId);
        List<PointDTO> result = points.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        log.debug("Найдено {} точек для функции ID={}", result.size(), functionId);
        return result;
    }

    public PointDTO updatePoint(UUID id, PointCreateDTO pointDTO) {
        log.info("Обновление точки ID: {}", id);
        try {
            log.debug("Новые координаты точки: x={}, y={}", pointDTO.getXVal(), pointDTO.getYVal());
            pointDAO.updatePoint(id, pointDTO.getXVal(), pointDTO.getYVal());
            Optional<Point> updatedPoint = pointDAO.getPointById(id);
            PointDTO result = updatedPoint.map(this::toDTO)
                    .orElseThrow(() -> {
                        log.error("Точка не найдена после обновления: ID={}", id);
                        return new RuntimeException("Точка не найдена после обновления");
                    });
            log.info("Точка успешно обновлена: ID={}, новые координаты=({}, {})",
                    id, result.getXVal(), result.getYVal());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при обновлении точки ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deletePoint(UUID id) {
        log.info("Удаление точки ID: {}", id);
        try {
            Optional<Point> point = pointDAO.getPointById(id);
            point.ifPresent(p ->
                    log.debug("Удаление точки: координаты=({}, {}), функция={}",
                            p.getXVal(), p.getYVal(), p.getFunctionId())
            );
            pointDAO.deletePoint(id);
            log.info("Точка успешно удалена: ID={}", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении точки ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteAllPointsByFunction(UUID functionId) {
        log.info("Удаление всех точек функции: ID={}", functionId);
        try {
            List<Point> points = pointDAO.getPointsByFunction(functionId);
            log.debug("Будет удалено {} точек функции ID={}", points.size(), functionId);
            for (Point point : points) {
                pointDAO.deletePoint(point.getId());
            }
            log.info("Все точки функции успешно удалены: ID={}, удалено {} точек",
                    functionId, points.size());
        } catch (Exception e) {
            log.error("Ошибка при удалении точек функции ID {}: {}", functionId, e.getMessage(), e);
            throw e;
        }
    }

    private PointDTO toDTO(Point point) {
        return new PointDTO(
                point.getId(),
                point.getFunctionId(),
                point.getXVal(),
                point.getYVal()
        );
    }
}
