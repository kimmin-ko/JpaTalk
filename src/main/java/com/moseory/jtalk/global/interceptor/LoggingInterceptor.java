package com.moseory.jtalk.global.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        log.info(String.format("START URL ==> %-35s TIME ==> %-25s IP ==> %s", request.getRequestURI(), now().format(ofPattern("yyyy-MM-dd HH:mm:ss")), ip));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String ip = request.getRemoteAddr();
        log.info(String.format("END URL   ==> %-35s TIME ==> %-25s IP ==> %s", request.getRequestURI(), now().format(ofPattern("yyyy-MM-dd HH:mm:ss")), ip));
    }

}
