package wang.depp.providerapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.depp.substruction.common.ApiResponse;

/**
 * Created by DEPP WANG on 2/11/2020
 */
@RestController
public class HomeController {
    @GetMapping(value = {"/Status/Version", "/status/version"})
    public ApiResponse<String> hello() {
        return systemInfo();
    }

    private ApiResponse<String> systemInfo() {
        String data =  new StringBuilder("Provider Api").append("<br/>").append("Current time：").append(System.currentTimeMillis()).append("<br/>")
                .toString();
        return ApiResponse.success(data);
    }
}
