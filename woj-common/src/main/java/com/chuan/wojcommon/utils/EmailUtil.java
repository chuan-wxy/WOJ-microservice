package com.chuan.wojcommon.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 描述
 *
 * @Author chuan-wxy
 * @Create 2024/8/15 9:46
 */
@Component
@Slf4j
public class EmailUtil {

    @Value("${emailUtil.hostName}")
    private String hostName;
    @Value("${emailUtil.charset}")
    private String charset;
    @Value("${emailUtil.account}")
    private String account;
    @Value("${emailUtil.name}")
    private String name;
    @Value("${emailUtil.password}")
    private String password;

    private static  String staticHostName;

    private static  String staticCharset;

    private static  String staticAccount;

    private static  String staticName;

    private static  String staticPassword;
    
    private EmailUtil(){}
    
    @PostConstruct
    public void init() {
        staticHostName = hostName;
        staticCharset = charset;
        staticAccount = account;
        staticName = name;
        staticPassword = password;
        System.out.println(staticCharset);
    }

    public static boolean send(String emailAddress, String message,String content) {
        HtmlEmail email = new HtmlEmail();
        try {
            //服务器地址
            email.setHostName(staticHostName);
            //字符集
            email.setCharset(staticCharset);
            //设置端口
            email.setSmtpPort(465);
            // 开启SSL加密
            email.setSSL(true);
            //收件人邮箱
            email.addTo(emailAddress);
            //发件人
            email.setFrom(staticAccount, staticName);
            email.setAuthentication(staticAccount, staticPassword);
            //主题
            email.setSubject("AcKing验证码");
            //正文
            if(content != null){
                email.setMsg(content + message);
            }else {
                email.setMsg("您的验证码为：" + message);
            }
            log.info("EmailUtil 正在向{}发送内容为：{}的邮件",emailAddress,message);
            email.send();
            return true;
        } catch (EmailException e) {
            log.info("EmailUtil--->send---",e);
            return false;
        }
    }
}
