package com.joker.mall.service.impl;

import com.joker.mall.dao.UserMapper;
import com.joker.mall.enums.ResponseEnum;
import com.joker.mall.enums.RoleEnum;
import com.joker.mall.pojo.User;
import com.joker.mall.service.IUserService;
import com.joker.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl implements IUserService {

   @Autowired
    UserMapper userMapper;
    /**
     * 注册
      * @param user
     */
    @Override
    public ResponseVo<User> register(User user) {

        error();

        //username不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if(countByUsername > 0){
//            throw new RuntimeException("该username已注册");
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
        }
        //email不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if(countByEmail > 0){
//            throw new RuntimeException("该Email已注册");
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }
        user.setRole(RoleEnum.CUSTOMER.getCode());
        //MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if(resultCount == 0){
//            throw new RuntimeException("注册失败");
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.successByMsg();
    }

    public void error(){
        throw new RuntimeException("意外错误测试");
    }


    @Override
    public ResponseVo<User> login(String username,String password) {
        User user = userMapper.selectByUsername(username);
        if(user == null){
            //用户名不存在
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if(!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))){
            //密码错误
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }


        user.setPassword("");
        return ResponseVo.successByData(user);
    }
}
