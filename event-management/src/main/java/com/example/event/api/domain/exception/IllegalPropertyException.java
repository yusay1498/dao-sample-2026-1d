package com.example.event.api.domain.exception;

/**
 * リクエストされたプロパティの値が不正な場合にスローする例外。
 */
public class IllegalPropertyException extends IllegalArgumentException {

    private final String name;
    private final Object value;

    public IllegalPropertyException(String message, String name, Object value) {
        super(message);
        this.name = name;
        this.value = value;
    }

    public IllegalPropertyException(String message, Throwable cause, String name, Object value) {
        super(message, cause);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
