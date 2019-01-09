package com.deppwang.demo.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

/**
 * 描述：
 *
 * @author WangXQ
 * @date 2019/1/9 15:17
 */
public class WebSiteMeshFilter extends ConfigurableSiteMeshFilter {

    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/task/index", "/WEB-INF/views/decorator.jsp");
//        也可使用Controller请求映射
//        builder.addDecoratorPath("/task/index", "/task/decorator").addExcludedPath("/task/decorator");
    }
}