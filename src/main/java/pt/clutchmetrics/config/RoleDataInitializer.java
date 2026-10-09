package pt.clutchmetrics.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.clutchmetrics.entity.Role;
import pt.clutchmetrics.entity.RoleName;
import pt.clutchmetrics.repository.RoleRepository;

@Configuration
public class RoleDataInitializer {

    @Bean
    CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {

            if (roleRepository.findByName(RoleName.USER).isEmpty()) {
                roleRepository.save(new Role(RoleName.USER));
            }

            if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
                roleRepository.save(new Role(RoleName.ADMIN));
            }
        };
    }
}
