package com.itheima.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {
    // 签名
    private static final String SIGN_NAME = "日拱一卒";

    // 短信模板
    private static final String TEMPLATE_CODE = "SMS_243190674";
    private static final String ACCESS_KEY = "LTAI5tPTsGvXEgjvnhdKsyYJ";
    private static final String ACCESS_KEY_SECRET = "mQ6mJfM4yCSLOcHApyKGuPhONMjSB4";
    private static final String REGION_ID = "cn-hangzhou";


    /**
     * 发送短信
     *
     * @param signName     签名
     * @param templateCode 模板
     * @param phoneNumber 手机号
     * @param code        要发送的内容
     */
    public static void sendMessage(String signName, String templateCode, String phoneNumber,
                                   String code) {
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, ACCESS_KEY,
                ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId(REGION_ID);
        request.setPhoneNumbers(phoneNumber);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            if ("ok".equalsIgnoreCase(response.getCode())) {
                System.out.println("短信发送成功");
                return;
            }
            System.out.println("发送失败，原因如下：" + response.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送短信
     *
     * @param phoneNumber 手机号
     * @param code        要发送的内容
     */
    public static void sendMessage(String phoneNumber, String code) {
        DefaultProfile profile = DefaultProfile.getProfile(REGION_ID, ACCESS_KEY,
                ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setSysRegionId(REGION_ID);
        request.setPhoneNumbers(phoneNumber);
        request.setSignName(SIGN_NAME);
        request.setTemplateCode(TEMPLATE_CODE);
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            if (!"ok".equalsIgnoreCase(response.getCode())) {
                System.out.println("发送失败，原因如下：" + response.getMessage());
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}