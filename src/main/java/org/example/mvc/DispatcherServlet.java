package org.example.mvc;

import org.example.mvc.controller.RequestMethod;
import org.example.mvc.view.JspViewResolver;
import org.example.mvc.view.ModelAndView;
import org.example.mvc.view.View;
import org.example.mvc.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

// 모든 요청은 해당 디스패쳐 서블릿에서 받는다. (Front Controller Pattern)
@WebServlet("/")
public class DispatcherServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    // 적절한 핸들러 찾아주는 놈
    private List<HandlerMapping> handlerMappings;

    // 위에서 찾은 핸들러 실행시키고, 뷰네임 받아오는 놈
    private List<HandlerAdapter> handlerAdapters;

    // 적절한 뷰를 내려주는 놈
    private List<ViewResolver> viewResolvers;

    // 서버 시작 후, 처음 요청시에, 해당 서블릿은 초기화된다.
    @Override
    public void init() throws ServletException {
        // 직접 매핑하는 핸들러 들어있는 핸들러매핑
        RequestMappingHandlerMapping rmhm = new RequestMappingHandlerMapping();
        // 다음과 같은 작업 수행 : mappings.put(new HandlerKey(RequestMethod.GET, "/users"), new UserListController());
        rmhm.init();

        // 어노테이션 붙인 핸들러 가지고 있는 핸들러매핑
        AnnotationHandlerMapping ahm = new AnnotationHandlerMapping("org.example");
        // 어노테이션 붙어있는 클래스가져와 메서드들 매핑해주는 작업 (reflection 활용)
        ahm.initialize();

        // 핸들러 매핑은 2개 지정
        handlerMappings = List.of(rmhm, ahm);
        // 핸들러 어댑터도 핸들러 매핑과 맞게 두개 지정
        handlerAdapters = List.of(new SimpleControllerHandlerAdapter(), new AnnotationHandlerAdapter());
        // 뷰리졸버들도 여러개 받을 수 있게 지정
        viewResolvers = Collections.singletonList(new JspViewResolver());
    }

    // 이제 요청시에, service메서드가 수행된다.
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("[DispatcherServlet] service started");
        // 요청uri, method 정보 request에서 추출하기
        String requestURI = request.getRequestURI();
        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());

        try {
            // 여러 핸들러 매핑들 중에서 handlerkey에 해당하는 핸들러가 있는지 봐서 가져오기
            Object handler = handlerMappings.stream()
                    .filter(hm -> hm.findHandler(new HandlerKey(requestMethod, requestURI)) != null)
                    .map(hm -> hm.findHandler(new HandlerKey(requestMethod, requestURI)))
                    .findFirst()
                    .orElseThrow(() -> new ServletException("No handler for [" + requestMethod + ", " + requestURI + "]"));

            // 해당 핸들러를 실행할 수 있는 핸들러어댑터를 support()를 통해서 가져온다.
            HandlerAdapter handlerAdapter = handlerAdapters.stream()
                    .filter(ha -> ha.supports(handler))
                    .findFirst()
                    .orElseThrow(() -> new ServletException("No adapter for handler [" + handler + "]"));

            // 알맞은 어댑터를 통해서 핸들러를 실행하고 view정보 리턴
            ModelAndView modelAndView = handlerAdapter.handle(request, response, handler);

            // view정보를 가지고 알맞은 viewresolver를 통해서 view를 렌터한다.
            for (ViewResolver viewResolver : viewResolvers) {
                View view = viewResolver.resolveView(modelAndView.getViewName());
                // 여기서 model에 담은 정보들도 request에 setAttribute 해줌.
                view.render(modelAndView.getModel(), request, response);
            }
        } catch (Exception e) {
            log.error("exception occurred : [{}]", e.getMessage(), e);
            throw new ServletException(e);
        }
    }
}