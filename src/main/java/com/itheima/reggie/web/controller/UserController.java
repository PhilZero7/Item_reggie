package com.itheima.reggie.web.controller;

import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author Vsunks.v
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description: 前台用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    HttpSession session;


    @PostMapping("/sendMsg")
    public R semdSMS(@RequestBody User user) {
        if (user.getPhone() != null && user.getPhone().length() == 11) {


            // 生成验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            System.out.println("code = " + code);

            // 发送短信
            //SMSUtils.sendMessage(user.getPhone(), code + "");

            // 保存验证码到session
            session.setAttribute("code", code);

            return R.success("验证码发送成功",null);

        }


        return R.fail("手机号有误");
    }


}