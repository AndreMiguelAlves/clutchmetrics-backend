package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN PlayerProfile p ON p.user = u
        WHERE u.enabled = true
        AND (
            LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(p.displayName) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        ORDER BY u.username ASC
        """)
    List<User> searchPlayers(
            @Param("search") String search,
            Pageable pageable
    );

} 