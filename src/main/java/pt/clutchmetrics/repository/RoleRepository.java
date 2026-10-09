package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Role;
import pt.clutchmetrics.entity.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}