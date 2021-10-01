package com.joker.mall.service;

import com.joker.mall.pojo.User;
import com.joker.mall.vo.ResponseVo;

public interface IUserService {

    /**
     * 注册
     */
    ResponseVo<User> register(User user);
    /**
     * 登录
     */
    ResponseVo<User> login(String username,String password);
}
