package com.stmartinviersen.tuetenverkauf.user.mapper;

import com.stmartinviersen.tuetenverkauf.user.model.RegisterRequest;
import com.stmartinviersen.tuetenverkauf.user.model.User;
import com.stmartinviersen.tuetenverkauf.user.model.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest registerRequest);
    UserDTO toUserDTO(User user);
}