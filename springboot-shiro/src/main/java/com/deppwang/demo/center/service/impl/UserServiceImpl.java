package com.deppwang.demo.center.service.impl;

import com.deppwang.demo.center.service.UserService;
import com.deppwang.demo.core.model.UserInfo;
import com.deppwang.demo.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *
 * @author WangXQ
 * @date 2019/1/22 14:33
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    public UserInfo findByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }
}
