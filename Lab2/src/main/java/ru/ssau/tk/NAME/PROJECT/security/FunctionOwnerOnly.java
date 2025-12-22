package ru.ssau.tk.NAME.PROJECT.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Или ElementType.PARAMETER, если нужно повесить на параметр
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionOwnerOnly {
}
