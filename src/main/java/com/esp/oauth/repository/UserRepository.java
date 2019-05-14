package com.esp.oauth.repository;

import com.esp.oauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author hekai
 * @Date 2019/3/28 17:14
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**根据username查找user*/
    User findByUsername(String username);
}
