package com.auth.backend.app.mapper;

import com.auth.backend.app.dtos.UserDto;
import com.auth.backend.app.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
   UserDto mapUserToUserDto(User user);
   User mapUserDtoToUser(UserDto userDto);
}
