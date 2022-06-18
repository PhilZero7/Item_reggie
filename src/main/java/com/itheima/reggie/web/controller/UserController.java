package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
            // 使用手机号作为key，避免前台用户获取验证码后更换手机号
            session.setAttribute(user.getPhone(), code);

            return R.success("验证码发送成功", null);

        }


        return R.fail("手机号有误");
    }


    /**
     * 登录
     * @param map 包含登录用的手机号和验证码
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> map) {
        log.info("前台用户登录，参数为：{}", map);

        String code = map.get("code");
        String phone = map.get("phone");

        if (phone != null && phone.length() == 11 && code != null) {


            // 判断验证码是否正确
            // 从session获取保存的验证码
            Object codeInSession = session.getAttribute(phone);

            // 获取验证码失败
            if (codeInSession==null) {
                return R.fail("请确认手机号是否正确/验证码已过期");
            }

            // 判断验证码是否相同
            if (code.equals(codeInSession.toString())) {
                // 查询手机号是否已注册
                LambdaQueryWrapper<User> wq = new LambdaQueryWrapper<>();
                wq.eq(User::getPhone, phone);
                User user = userService.getOne(wq);

                // 如果没有注册，完成注册
                if (user == null) {
                    user = new User();
                    user.setPhone(phone);
                    //user.setStatus(1);  // msyql默认填充1
                    userService.save(user);
                }

                // 保存用户登录状态
                session.setAttribute("user", user.getId());

                // 删除本次登录的验证码
                session.removeAttribute(phone);

                // 组织数据并返回
                return R.success("登录成功", user);
            }else{
                return R.fail("验证码错误");
            }
        }
        return R.fail("参数有误");
    }

}