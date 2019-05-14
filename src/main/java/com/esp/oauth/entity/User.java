package com.esp.oauth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author hekai
 * @Date 2019/3/28 16:59
 */
@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "password")
    private String password;
}
