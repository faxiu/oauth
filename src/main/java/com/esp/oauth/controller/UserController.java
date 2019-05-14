package com.esp.oauth.controller;

import com.esp.oauth.entity.User;
import com.esp.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author hekai
 * @Date 2019/3/28 18:53
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository repository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public User create(String username, String password){
        User user = new User();
        user.setId(2);
        user.setUsername(username);
        String password1 = passwordEncoder.encode(password);
        user.setPassword(password1);
        repository.save(user);
        return user;
    }

    @GetMapping("/test")
    public String find(){
        return "Success!";
    }

    @GetMapping("query")
    public User get(@RequestParam(value = "username") String username){
        User user = repository.findByUsername(username);
        return user;
    }

}
