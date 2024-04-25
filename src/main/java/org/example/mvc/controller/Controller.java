package org.example.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// RequestMappingHandlerMapping에서 사용하는 핸들러를 controller를 붙여서 만들어진다.,
public interface Controller {
    // 컨트롤러 실행메서드
    String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}