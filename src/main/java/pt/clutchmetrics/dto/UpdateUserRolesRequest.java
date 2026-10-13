package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotEmpty;
import pt.clutchmetrics.entity.RoleName;

import java.util.Set;

public class UpdateUserRolesRequest {

    @NotEmpty
    private Set<RoleName> roles;

    public Set<RoleName> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleName> roles) {
        this.roles = roles;
    }
}
