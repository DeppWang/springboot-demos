package wang.depp.providerclient.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import wang.depp.substruction.common.ApiResponse;

/**
 * @author: bcl
 * @create: 2020-05-24
 **/
@Headers("Content-Type: application/x-www-form-urlencoded")
public interface ProviderApi {
    @RequestLine("POST /apply/submit")
    public ApiResponse<Void> submit(@Param("content") String content, @Param("submitter") String submitter);
}
