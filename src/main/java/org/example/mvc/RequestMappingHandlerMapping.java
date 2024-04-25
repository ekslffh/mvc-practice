package org.example.mvc;

import org.example.mvc.controller.*;

import java.util.HashMap;
import java.util.Map;

// 직접 작성 방식의 핸들러 매핑정보
public class RequestMappingHandlerMapping implements HandlerMapping {

    // [key] /users [value] UserController
    private Map<HandlerKey, Controller> mappings = new HashMap<>();

    // 처음 초기화 할 때, 핸들러들 등록해주기 (method, uri) 기준으로 등록하기
    void init() {
//        mappings.put(new HandlerKey(RequestMethod.GET, "/"), new HomeController());
        mappings.put(new HandlerKey(RequestMethod.GET, "/users"), new UserListController());
        mappings.put(new HandlerKey(RequestMethod.POST, "/users"), new UserCreateController());
        mappings.put(new HandlerKey(RequestMethod.GET, "/user/form"), new ForwardController("/user/form"));
    }

    // key(method, uri) 기준으로 핸들러 내주기 (등록되지 않았따면 Null)
    public Controller findHandler(HandlerKey handlerKey) {
        return mappings.get(handlerKey);
    }
}
