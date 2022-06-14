package com.itheima.reggie.demo;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;

import java.util.Random;

/**
 * @Author Vsunks.v
 * @Date 2022/6/14 14:59
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description:
 */
public class SMSDemo {

    public static void main(String[] args) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tPTsGvXEgjvnhdKsyYJ",
                "mQ6mJfM4yCSLOcHApyKGuPhONMjSB4");

        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers("13291840116");//接收短信的手机号码
        request.setSignName("日拱一卒");//短信签名名称
        request.setTemplateCode("SMS_243190674");//短信模板CODE

        // {"code":"验证码"}
        request.setTemplateParam("{\"code\":\"1234\"}");//短信模板变量对应的实际值

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }

    }
}