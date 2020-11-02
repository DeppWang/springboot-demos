package wang.depp.providerclient;

import com.google.common.base.Preconditions;
import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wang.depp.providerclient.api.UserApi;
import wang.depp.providerclient.support.AccessTraceHttpClient;

import java.util.concurrent.TimeUnit;

//@Slf4j
@Configuration
public class ProviderClientBuilder {
    @Value("${provider.domain}")
    public String providerDomain;
    @Value("${provider.socketTimeout:60000}")
    public int socketTimeout;
    @Value("${provider.connectTimeout:10000}")
    public int connectTimeout;

//    @Resource
//    private PrometheusMeterRegistry prometheusMeterRegistry;

    @Bean
    public UserApi buildProviderApi() {
        Preconditions.checkArgument(StringUtils.isNotBlank(providerDomain));
        return Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new FormEncoder())
                .logger(new Slf4jLogger(UserApi.class))
                .retryer(Retryer.NEVER_RETRY)
                .logLevel(Logger.Level.FULL)
                .client(new AccessTraceHttpClient(httpClient(), requestConfig()))
//                .addCapability(prometheusMeterRegistry != null ? new MicrometerCapability(prometheusMeterRegistry) : new MicrometerCapability())
                .target(UserApi.class, providerDomain);

//        return Feign.builder()
//                .encoder(new FormEncoder())
//                .decoder(new GsonDecoder())
//                .target(UserApi.class, providerService);
    }

    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(httpClientConnectionManager());
        return builder;
    }

    public HttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 设置整个连接池最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(500);
        // 路由是对 maxTotal 的细分
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(100);
        // 闲置连接超时回收
        poolingHttpClientConnectionManager.closeIdleConnections(5, TimeUnit.SECONDS);
        poolingHttpClientConnectionManager.setValidateAfterInactivity(1000);
        return poolingHttpClientConnectionManager;
    }

    public HttpClient httpClient(HttpClientBuilder builder) {
        return builder.build();
    }

    public CloseableHttpClient httpClient() {
        return httpClientBuilder().build();
    }

    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                // 服务器返回数据(response)的时间，超过该时间抛出 read timeout
                .setSocketTimeout(socketTimeout)
                // 连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectTimeout(connectTimeout)
                // 从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(1000)
                .build();
    }

}
