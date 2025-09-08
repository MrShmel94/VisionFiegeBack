package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserRest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", ignore = true)
    UserRest toUserRest(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    UserDto toUserDto(UserRest userRest);

    UserDto toUserDto(UserDetailsRequestModel userDetails);

    @Mapping(target = "encryptedPassword", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserDto toUserDto(UserEntity userEntity);

    UserEntity toUserEntity(UserDto userDto);
}
