package ru.ssau.tk.NAME.PROJECT.service;

import ru.ssau.tk.NAME.PROJECT.dto.PointDTO;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.mapper.PointMapper;
import ru.ssau.tk.NAME.PROJECT.repository.PointRepository;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final FunctionRepository functionRepository;
    private final PointMapper pointMapper;

    public List<PointDTO> getAllPoints() {
        try {
            List<Point> points = pointRepository.findAll();
            log.debug("Found {} points in database", points.size());
            return points.stream()
                    .map(pointMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting all points: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting all points", e);
        }
    }

    public Optional<PointDTO> getPointById(UUID id) {
        try {
            log.debug("Looking for point with id: {}", id);
            return pointRepository.findById(id)
                    .map(point -> {
                        log.debug("Found point: {}", point);
                        return pointMapper.toDTO(point);
                    });
        } catch (Exception e) {
            log.error("Error getting point by ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Transactional
    public PointDTO createPoint(PointDTO pointDTO) {
        try {
            log.info("Creating point: functionId={}, xVal={}, yVal={}",
                    pointDTO.getFunctionId(), pointDTO.getXVal(), pointDTO.getYVal());

            if (pointDTO.getFunctionId() == null) {
                throw new IllegalArgumentException("Function ID is required");
            }

            Function function = functionRepository.findById(pointDTO.getFunctionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Function not found with ID: " + pointDTO.getFunctionId()));

            log.debug("Found function: {}", function);

            Point point = pointMapper.toEntity(pointDTO, function);
            log.debug("Mapped point entity: {}", point);

            point = pointRepository.save(point);
            log.info("Point saved with id: {}", point.getId());

            return pointMapper.toDTO(point);

        } catch (Exception e) {
            log.error("Error creating point: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating point: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Optional<PointDTO> updatePoint(UUID id, PointDTO pointDTO) {
        try {
            log.info("Updating point {} with data: {}", id, pointDTO);

            return pointRepository.findById(id)
                    .map(existingPoint -> {
                        if (pointDTO.getXVal() != null) {
                            existingPoint.setXVal(pointDTO.getXVal());
                        }
                        if (pointDTO.getYVal() != null) {
                            existingPoint.setYVal(pointDTO.getYVal());
                        }

                        if (pointDTO.getFunctionId() != null) {
                            Function newFunction = functionRepository.findById(pointDTO.getFunctionId())
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Function not found with ID: " + pointDTO.getFunctionId()));
                            existingPoint.setFunction(newFunction);
                        }

                        Point savedPoint = pointRepository.save(existingPoint);
                        log.info("Point {} updated successfully", id);
                        return pointMapper.toDTO(savedPoint);
                    });

        } catch (Exception e) {
            log.error("Error updating point {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Transactional
    public boolean deletePoint(UUID id) {
        try {
            log.info("Deleting point with id: {}", id);

            if (pointRepository.existsById(id)) {
                pointRepository.deleteById(id);
                log.info("Point {} deleted successfully", id);
                return true;
            }
            log.warn("Point {} not found for deletion", id);
            return false;
        } catch (Exception e) {
            log.error("Error deleting point {}: {}", id, e.getMessage(), e);
            return false;
        }
    }
}
