package com.m2u.eyelink.collector.manage.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{eyelink_collector_properties['collector.admin.password']}")
    private String password;

    @Value("#{eyelink_collector_properties['collector.admin.api.rest.active'] ?: false}")
    private boolean isActive;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isActive) {
            throwAuthException("not activating rest api for admin.");
        }

        if (StringUtils.isEmpty(password)) {
            throwAuthException("not activating rest api for admin.");
        }

        String password = request.getParameter("password");
        if (!this.password.equals(password)) {
            throwAuthException("not matched admin password.");
        }

        return true;
    }
    
    private void throwAuthException(String messsage) throws ModelAndViewDefiningException {
        logger.warn(messsage);
        throw new ModelAndViewDefiningException(ControllerUtils.createJsonView(false, messsage));
    }

}
