package com.demo.springcustomizedstarterexample.entities.example;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, Integer> {

    private static final Integer ZERO = 0;
    private static final Integer ONE = 1;

    public BooleanConverter() {
    }

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (null == attribute) {
            return null;
        }
        if (Boolean.TRUE.equals(attribute)) {
            return ONE;
        } else {
            return ZERO;
        }
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        if (null == dbData) {
            return null;
        }
        return ONE.equals(dbData);
    }

}
