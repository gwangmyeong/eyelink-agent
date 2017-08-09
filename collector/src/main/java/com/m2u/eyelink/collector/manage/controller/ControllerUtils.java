package com.m2u.eyelink.collector.manage.controller;

import org.springframework.web.servlet.ModelAndView;

public final class ControllerUtils {

    public static ModelAndView createJsonView() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("jsonView");
        
        return mv;
    }
    
    public static ModelAndView createJsonView(boolean success) {
        return createJsonView(success, null);
    }
    
    public static ModelAndView createJsonView(boolean success, Object message) {
        ModelAndView mv = createJsonView();

        if (success) {
            mv.addObject("result", "success");
        } else {
            mv.addObject("result", "fail");
        }

        if (message != null) {
            mv.addObject("message", message);
        }

        return mv;
    }

}
