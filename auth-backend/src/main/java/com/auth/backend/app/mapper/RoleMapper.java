package com.auth.backend.app.mapper;

import com.auth.backend.app.auth.payload.RoleDto;
import com.auth.backend.app.auth.entities.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface RoleMapper {
    RoleDto mapRoleToRoleDto(Role role);
    Role mapRoleDtoToRole(RoleDto roleDto);
}
