package wang.depp.providerapi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wang.depp.substruction.common.ApiResponse;
/**
 * Created by DEPP WANG on 2/11/2020
 */
@RestController
public class UserController {

    @PostMapping("/signin")
    public ApiResponse<Void> doSignin(@RequestParam("username") String username, @RequestParam("password") String password) {
        if (username.equals("") && password.equals("")) {
            return ApiResponse.fail(500);
        }
        return ApiResponse.success();
    }
}
