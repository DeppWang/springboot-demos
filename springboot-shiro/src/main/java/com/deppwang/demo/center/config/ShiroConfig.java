package com.deppwang.demo.center.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 描述：
 *
 * @author WangXQ
 * @date 2019/1/17 9:48
 */
@Configuration
public class ShiroConfig {
    /**
     * 将MyShiroRealm的配置注入到IoC容器，
     *
     * @return
     */
    @Bean
    public MyShiroRealm myShiroRealm() {
        System.out.println("ShiroConfig.myShiroRealm()");
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        //设置凭证(密码)验证器，用于SimpleAuthorizationInfo验证token中密码是否和数据库密码是否匹配
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    /**
     * 配置hashedCredentialsMatcher(凭证匹配器)，让SimpleAuthorizationInfo知道如何验证密码
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }

    @Bean
    public SecurityManager securityManager() {
        System.out.println("ShiroConfig.securityManager()");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        System.out.println("ShiroConfig.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //当shiroFilter(SecurityManager securityManager)时，此处为securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 以下过滤器按顺序判断
        // 配置不会被拦截的链接，一般是排除前端文件（anon:指定的url可以匿名访问）
//        filterChainDefinitionMap.put("/static/**", "anon");

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/logout", "logout");
        //authc:所有url都必须认证通过才可以访问;
        filterChainDefinitionMap.put("/**", "authc");

//        filterChainDefinitionMap.put("/login", "authc");
//        //user:需要已登录或“记住我”的用户才能访问;
//        filterChainDefinitionMap.put("/**", "user");


        //当项目访问其他没有通过认证的URL时，会默认跳转到/login，如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        //登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //当用户访问没有权限的URL时，跳转到未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    /**
     * 为shiro权限控制开启aop注解支持，这样才能使用@RequiresPermissions等注解
     * 使用代理方式，所以需要开启代码支持
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        System.out.println("开启Shrio注解");
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 当没有访问权限时，会抛出异常，需要自定义异常处理，将没有权限的异常重定向到403页面
     *
     * @return
     */
    @Bean
    public SimpleMappingExceptionResolver
    createSimpleMappingExceptionResolver() {
        System.out.println("自定义异常处理");
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        mappings.setProperty("UnauthorizedException", "403");//授权异常处理
        resolver.setExceptionMappings(mappings);  // None by default
        resolver.setDefaultErrorView("error");    // No default
        resolver.setExceptionAttribute("ex");     // Default is "exception"
        return resolver;
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}
