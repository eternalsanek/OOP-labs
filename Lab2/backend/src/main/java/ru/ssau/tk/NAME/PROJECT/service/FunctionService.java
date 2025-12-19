package ru.ssau.tk.NAME.PROJECT.service;

import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.mapper.FunctionMapper;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import ru.ssau.tk.NAME.PROJECT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FunctionService {

    private final FunctionRepository functionRepository;
    private final UserRepository userRepository;
    private final FunctionMapper functionMapper;

    public List<FunctionDTO> getAllFunctions() {
        return functionRepository.findAll().stream()
                .map(functionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<FunctionDTO> getFunctionById(UUID id) {
        return functionRepository.findById(id)
                .map(functionMapper::toDTO);
    }

    public List<FunctionDTO> getFunctionsByOwnerId(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + ownerId));
        return functionRepository.findByOwner(owner).stream()
                .map(functionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FunctionDTO createFunction(FunctionDTO functionDTO) {
        if (functionDTO.getName() == null || functionDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Function name is required");
        }

        if (functionDTO.getOwnerId() == null) {
            throw new IllegalArgumentException("Owner ID is required");
        }

        User owner = userRepository.findById(functionDTO.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + functionDTO.getOwnerId()));

        Function function = functionMapper.toEntity(functionDTO, owner);
        function = functionRepository.save(function);
        return functionMapper.toDTO(function);
    }

    @Transactional
    public Optional<FunctionDTO> updateFunction(UUID id, FunctionDTO functionDTO) {
        return functionRepository.findById(id)
                .map(existingFunction -> {
                    // Обновляем поля
                    if (functionDTO.getName() != null) {
                        existingFunction.setName(functionDTO.getName());
                    }

                    if (functionDTO.getType() != null) {
                        existingFunction.setType(functionDTO.getType());
                    }

                    if (functionDTO.getExpression() != null) {
                        existingFunction.setExpression(functionDTO.getExpression());
                    }

                    // Если нужно обновить owner
                    if (functionDTO.getOwnerId() != null) {
                        User newOwner = userRepository.findById(functionDTO.getOwnerId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "User not found with ID: " + functionDTO.getOwnerId()));
                        existingFunction.setOwner(newOwner);
                    }

                    return functionRepository.save(existingFunction);
                })
                .map(functionMapper::toDTO);
    }

    @Transactional
    public boolean deleteFunction(UUID id) {
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
