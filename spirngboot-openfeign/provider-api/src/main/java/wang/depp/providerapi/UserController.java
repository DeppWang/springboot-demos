package wang.depp.providerapi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by DEPP WANG on 2/11/2020
 */
@Controller
public class HomeController {
    @GetMapping(value = {"/Status/Version", "/status/version"})
    public String hello() {
        return systemInfo();
    }

    private String systemInfo() {
        return new StringBuilder("Current time：").append(System.currentTimeMillis()).append("<br/>")
                .toString();
    }
}
