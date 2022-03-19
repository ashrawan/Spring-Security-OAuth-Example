package com.demo.springcustomizedstarterexample.entities.example;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class ExampleEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "ID")
    private String id;

    @Column(name = "is_flying_bird", columnDefinition = "NUMBER(1,0)")
    @Convert(converter = BooleanConverter.class)
    private boolean isFlyingBird;

}
