package dao;

import modelDB.User;

import java.util.*;

public interface UserDAO {
    void createUser(String name, String passwordHash, String role);
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByName(String name);
    List<User> getAllUsers();
    void updateUser(UUID id, String newName, String newPasswordHash, String role);
    void deleteUser(UUID id);
    void close();
}