package com.joker.mall.controller;

import com.joker.mall.consts.MallConst;
import com.joker.mall.form.UserLoginForm;
import com.joker.mall.form.UserRegisterForm;
import com.joker.mall.pojo.User;
import com.joker.mall.service.IUserService;
import com.joker.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo register(@Valid @RequestBody UserRegisterForm userRegisterForm){

        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return userService.register(user);

    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession session){

        log.info("/login seessionId={}",session.getId());

        ResponseVo<User> userResponeVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        //设置Session
        session.setAttribute(MallConst.CURRENT_USER,userResponeVo.getData());
        return userResponeVo;

    }

    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session){
        log.info("/user seessionId={}",session.getId());
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);

        return ResponseVo.successByData(user);

    }
    /**
     * {@link TomcatServletWebServerFactory} getSessionTimeoutInMinutes
     * session的默认失效时间为30分钟，最低失效时间为1分钟
     */
    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session){
        log.info("/logout seessionId={}",session.getId());
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.successByMsg("退出成功");
    }

}
