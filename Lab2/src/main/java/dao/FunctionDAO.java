package dao;

import modelDB.Function;

import java.util.*;

public interface FunctionDAO {
    void createFunction(UUID ownerId, String name, String type, String expression);
    Optional<Function> getFunctionById(UUID id);
    List<Function> getFunctionsByOwner(UUID ownerId);
    void updateFunction(UUID id, String name, String type, String expression);
    void deleteFunction(UUID id);
}