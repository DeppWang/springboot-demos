package wang.depp.consumerapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.depp.providerclient.api.UserApi;
import wang.depp.substruction.common.ApiResponse;


/**
 * Created by DEPP WANG on 2/11/2020
 */
@RestController
public class HomeController {
    @Autowired
    private UserApi userApi;

    @PostMapping("/signin")
    public ApiResponse<Void> signin() {
        return userApi.signin("admin","admin");
    }

    @GetMapping("/providerStatus")
    public ApiResponse<String> providerStatus() {
        return userApi.statusVersion();
    }

}
