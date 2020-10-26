package wang.depp.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wang.depp.session.session.DeppSessionPostHandler;
import wang.depp.session.session.WrapperSessionFilter;
import wang.depp.session.session.store.RedisSessionCacheContainer;

@SpringBootApplication
public class SessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionApplication.class, args);
	}

	@Value("${session.forbidden.url.suffixes}")
	private String forbiddenUrlSuffixes;

	// 使用 @Bean 注入，或使用 @Component
//	@Bean
//	public WrapperSessionFilter wrapperSessionFilter() {
//		WrapperSessionFilter filter = new WrapperSessionFilter();
//		// 设置
//		filter.setForbiddenUrlSuffixes(forbiddenUrlSuffixes.split(","));
//		filter.setSessionCacheContainer(redisSessionCacheContainer());
//		filter.setSessionPostHandler(deppSessionPostHandler());
//		filter.setSiteTag("p");
//		return filter;
//	}

	@Bean
	public RedisSessionCacheContainer redisSessionCacheContainer() {
		return new RedisSessionCacheContainer();
	}

	@Bean
	public DeppSessionPostHandler deppSessionPostHandler() {
		return new DeppSessionPostHandler();
	}

	@Bean
	WebMvcConfigurer createWebMvcConfigurer(@Autowired HandlerInterceptor[] interceptors) {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
			}
		};
	}

}
