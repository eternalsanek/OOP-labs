package ru.ssau.tk.NAME.PROJECT.repository;

import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByName(String name);

    List<User> findByRole(User.Role role);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    boolean existsByName(String name);

    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoles(@Param("roles") List<User.Role> roles);
}
