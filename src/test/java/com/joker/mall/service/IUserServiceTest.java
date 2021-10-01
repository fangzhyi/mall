package com.joker.mall.service;

import com.joker.mall.MallApplicationTests;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.enums.RoleEnum;
import com.joker.mall.pojo.User;
import com.joker.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class IUserServiceTest extends MallApplicationTests {

    public static final String USERNAME = "joker";
    public static final String PASSWORD = "1234";
    @Autowired
    private IUserService userService;
    @Before
    public void register() {

        User user = new User("joker","1234","joker@qq.com", RoleEnum.CUSTOMER.getCode());

        userService.register(user);
    }

    @Test
    public void login(){
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }
}