package com.deppwang.demo.core.repository;

import com.deppwang.demo.core.model.UserInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<UserInfo, Long>, JpaSpecificationExecutor<UserInfo> {
    UserInfo findByUsername(String username);
}
