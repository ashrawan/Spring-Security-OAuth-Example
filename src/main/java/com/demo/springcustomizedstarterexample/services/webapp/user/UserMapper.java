package com.demo.springcustomizedstarterexample.services.webapp.user;

import com.demo.springcustomizedstarterexample.entities.UserEntity;
import com.demo.springcustomizedstarterexample.services.common.GenericMapper;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<UserDTO, UserEntity> {

    @Override
    UserEntity toEntity(UserDTO dto);

    @Override
    UserDTO toDto(UserEntity entity);

    @Override
    List<UserEntity> toEntityList(List<UserDTO> list);

    @Override
    List<UserDTO> toDtoList(List<UserEntity> list);

}