package com.example.event.api.presentation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectReader;

import java.util.Set;

/**
 * readValue後にJakarta Validationを適用し、不正な値であればConstraintViolationExceptionをスローする。
 */
public class ValidatableObjectReader extends ObjectReader {

    private final Validator validator;

    protected ValidatableObjectReader(ObjectReader base, Validator validator) {
        super(base, base.getConfig());
        this.validator = validator;
    }

    @Override
    public <T> T readValue(String src) throws JacksonException {
        T bean = super.readValue(src);
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return bean;
    }
}
