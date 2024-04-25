package org.example.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Controller어노테이션을 통해서도 핸들러로 등록될 수 있또록
@Target({ElementType.TYPE}) // 클래스에 붙인다는 의미
@Retention(RetentionPolicy.RUNTIME) // 실행되는 동안
public @interface Controller {
}
