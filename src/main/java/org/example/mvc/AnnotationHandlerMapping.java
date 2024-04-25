package org.example.mvc;

import org.example.mvc.annotation.Controller;
import org.example.mvc.annotation.RequestMapping;
import org.example.mvc.controller.RequestMethod;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationHandlerMapping implements HandlerMapping {

    private final Object[] basePackage;
    private Map<HandlerKey, AnnotationHandler> handlers = new HashMap<>();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public void initialize() {
        // 현재 "org.exmpale"가 기준 패키지
        Reflections reflections = new Reflections(basePackage);

        // HomeController
        // Controller가 붙어있는 클래스 찾아서 담기
        Set<Class<?>> clazzesWithControllerAnnotation = reflections.getTypesAnnotatedWith(Controller.class);

        // 각 클래스를 돌면서
        clazzesWithControllerAnnotation.forEach(clazz ->
                // 메서드들도 돌면서
                Arrays.stream(clazz.getDeclaredMethods()).forEach(declaredMethod -> {
                    // 각 메서드에 붙어있는 RequestMapping정보 가져오기
                    RequestMapping requestMapping = declaredMethod.getDeclaredAnnotation(RequestMapping.class);

                    // 메서드는 여러개 붙을 수 있따. (GET, POST, ....)
                    Arrays.stream(getRequestMethods(requestMapping))
                            // 각각 돌면서, 핸들러등록
                            .forEach(requestMethod -> handlers.put(
                                    // GET, uriPath를 키로, AnnotationHandler로 등록해주기 (즉, 메서드 이름은 딱히 중요하지 않다.) 유연한 메서드 선언 방식
                                    new HandlerKey(requestMethod, requestMapping.value()), new AnnotationHandler(clazz, declaredMethod)
                            ));
                })
        );
    }

    private RequestMethod[] getRequestMethods(RequestMapping requestMapping) {
        return requestMapping.method();
    }

    @Override
    public Object findHandler(HandlerKey handlerKey) {
        return handlers.get(handlerKey);
    }
}
