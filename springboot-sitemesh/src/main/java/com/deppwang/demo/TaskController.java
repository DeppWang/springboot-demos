package com.deppwang.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 描述：
 *
 * @author WangXQ
 * @date 2019/1/9 11:05
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    @RequestMapping("/decorator")
    public String decorator() {
        return "decorator";
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }
}
