package com.deppwang.demo.center.web.controller;

import com.deppwang.demo.core.model.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class IndexController {
    @GetMapping({"/", "/index"})
    public String index(Map<String, Object> map) {
        Subject currentUser = SecurityUtils.getSubject();
        String username2 = (String) currentUser.getSession().getAttribute("username");
        UserInfo user = (UserInfo) currentUser.getPrincipal();
        String username = user.getName() == null ? "Guest" : user.getName();
        map.put("username", username2);
        return "index";
    }
}
