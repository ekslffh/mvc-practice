package org.example.mvc.annotation;

import org.example.mvc.controller.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) // 클래스와 메소드에 붙을 수 있다.
@Retention(RetentionPolicy.RUNTIME) // 실행되는 동안
public @interface RequestMapping {
    String value() default ""; // uriPath 정보담기

    RequestMethod[] method() default {}; // method정보 담기
}