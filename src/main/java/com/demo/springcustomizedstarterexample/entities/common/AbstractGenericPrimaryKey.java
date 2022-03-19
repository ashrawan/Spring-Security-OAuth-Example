package com.demo.springcustomizedstarterexample.entities.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractGenericPrimaryKey<PK> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    @Column(name = "id", nullable = false)
    private PK id;

}
