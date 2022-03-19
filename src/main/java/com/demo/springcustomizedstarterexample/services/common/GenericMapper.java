package com.demo.springcustomizedstarterexample.services.common;

import java.util.List;
import java.util.stream.Collectors;

public interface GenericMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    default List<E> toEntityList(final List<D> dtos) {
        if (dtos != null) {
            return dtos.stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList());
        }
        return null;
    }

    default List<D> toDtoList(final List<E> entitys) {
        if (entitys != null) {
            return entitys.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }
        return null;
    }

}
