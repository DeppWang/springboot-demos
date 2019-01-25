package com.deppwang.demo;

import org.apache.shiro.SecurityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
		Boolean isPermitted = SecurityUtils.getSubject().isPermitted("admin");//是否有什么权限
	}

}

