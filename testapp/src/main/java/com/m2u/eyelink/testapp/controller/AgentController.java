package com.m2u.eyelink.testapp.controller;

import java.util.Map;
import java.util.HashMap;

import com.m2u.eyelink.testapp.service.remote.RemoteService;
import com.m2u.eyelink.testapp.util.Description;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/agent")
public class AgentController{

    @Autowired
    @Qualifier("httpRemoteService")
    RemoteService remoteService;

    @RequestMapping("/req")
    @ResponseBody
    @Description("request to other agent")
    public Map<String, Object> req(@RequestParam("host") String host) throws Exception {
        return remoteService.get(String.format("http://%s/agent/res.eyelink", host), Map.class);
    }

    @RequestMapping("/res")
    @ResponseBody
    @Description("/response fron other agent")
    public Map<String, Object> req() throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("result", "ok");
        result.put("code", 200);
        return result;
    }

}