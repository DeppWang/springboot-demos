package wang.depp.providerclient.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import wang.depp.substruction.common.ApiResponse;

@Headers("Content-Type: application/x-www-form-urlencoded") // 请求的编码格式为 application/x-www-form-urlencoded
public interface UserApi {
    @RequestLine("POST /signin")
    public ApiResponse<Void> signin(@Param("username") String username, @Param("password") String password);

    @RequestLine("GET /status/version")
    public ApiResponse<String> statusVersion();
}
