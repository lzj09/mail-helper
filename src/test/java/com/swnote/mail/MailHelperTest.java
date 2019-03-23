package com.swnote.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.swnote.mail.config.SenderConfig;
import com.swnote.mail.helper.MailHelper;

/**
 * 测试类
 * 
 * @author lzj
 * @date [2019-03-23]
 */
public class MailHelperTest {

    @Test
    public void send() throws Exception {
        // 配置邮件发送信息
        SenderConfig sender = new SenderConfig();
        sender.setNickname("架构与我");
        // 改成自己的邮件帐号
        sender.setUsername("xxx@163.com");
        // 改成自己的邮件密码
        sender.setPassword("xxx");
        sender.setSmtpHost("smtp.163.com");
        sender.setSmtpPort("25");
        sender.setSsl(false);
        
        // 获取邮件模板
        File tpl = new File("src/test/resources/test.html");
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tpl), "utf-8"));) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }

        // 测试数据
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "架构与我");
        data.put("description", "专注大数据、微服务架构、高并发高吞吐量大型网站、移动开发。");
        
        // 测试发送邮件
        // 改成自己的接收邮件地址
        MailHelper.send(sender, "xxx@qq.com", "来自架构与我的邮件", builder.toString(), data);
    }
}