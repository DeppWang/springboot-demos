package com.deppwang.demo.center.service;

import com.deppwang.demo.core.model.UserInfo;
import com.deppwang.demo.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *
 * @author WangXQ
 * @date 2019/1/16 20:39
 */

public interface UserService {
    UserInfo findByUsername(String userName);
}
