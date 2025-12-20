package ru.ssau.tk.NAME.PROJECT.repository;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FunctionRepository extends JpaRepository<Function, UUID> {

    List<Function> findByOwner(User owner);

    List<Function> findByType(String type);

    List<Function> findByOwnerAndType(User owner, String type);

    List<Function> findByTypeAndOwner(String type, User owner);

    @Query("SELECT f FROM Function f WHERE f.owner.id = :ownerId")
    List<Function> findByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT f FROM Function f WHERE f.name LIKE %:name%")
    List<Function> findByNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(f) FROM Function f WHERE f.owner = :owner")
    long countByOwner(@Param("owner") User owner);

    boolean existsByNameAndOwner(String name, User owner);
}
