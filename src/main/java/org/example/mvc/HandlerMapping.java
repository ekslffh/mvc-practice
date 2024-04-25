package org.example.mvc;

// 여러개의 핸들러 매핑을 만들 수 있게 인터페이스로 선언 (직접작성, 어노테이션 등 다양한 핸들러 매핑이 존재한다.)
public interface HandlerMapping {

    // 핸들러 매핑은 여러개의 핸들러를 가지고 있고, 알맞은 핸들러를 꺼내주는 기능이 필요하다.
    Object findHandler(HandlerKey handlerKey);
}