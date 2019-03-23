package com.swnote.mail.helper;

import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.swnote.mail.config.AuthConfig;
import com.swnote.mail.config.SenderConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

/**
 * 邮件发送器工具类
 * 
 * @author lzj
 * @date [2019-03-23]
 */
public class MailHelper {
    private static Logger logger = Logger.getLogger(MailHelper.class);
    
    /**
     * 私有化构造函数
     */
    private MailHelper() {
    }

    /**
     * 发送邮件方法
     * 
     * @param sender 发送邮件配置
     * @param receiver 接收邮件的email地址
     * @param subject 邮件主题
     * @param template 邮件模板（支持freemarker语法）
     * @param data 数据
     */
    public static void send(SenderConfig sender, String receiver, String subject, String template, Map<String, Object> data) throws Exception {
       try {
           // 将SenderConfig配置信息转换成Properties
           Properties prop = wrapProperties(sender);
           
           // 获取权限配置
           AuthConfig auth = getAuthConfig(sender);

           // 构建邮件会话
           Session mailSession = Session.getDefaultInstance(prop, auth);  
           mailSession.setDebug(false); 

           // 构建邮件消息
           Message mailMessage = new MimeMessage(mailSession);
           
           // 设置昵称
           String nick = MimeUtility.encodeText(getNickname(sender));
           Address from = new InternetAddress(nick + " <" + sender.getUsername() + ">"); 
           mailMessage.setFrom(from);

           // 设置邮件接收者
           Address to = new InternetAddress(receiver);
           mailMessage.setRecipient(Message.RecipientType.TO, to);  
           
           // 设置邮件主题
           mailMessage.setSubject(subject);

           // 设置发送时间
           mailMessage.setSentDate(new Date());

           // 设置邮件内容
           Multipart mainPart = new MimeMultipart();
           // 内容是可以包含html
           BodyPart html = new MimeBodyPart();
           html.setContent(getContent(template, data), "text/html; charset=utf-8");
           mainPart.addBodyPart(html);
           mailMessage.setContent(mainPart);

           // 发送邮件  
           Transport.send(mailMessage);
       } catch (Exception e) {
           logger.error("发送邮件方法错误", e);
           throw e;
       }
    }

    /**
     * 获取权限配置信息
     * 
     * @param sender
     * @return
     */
    private static AuthConfig getAuthConfig(SenderConfig sender) {
        return new AuthConfig(sender.getUsername(), sender.getPassword());
    }

    /**
     * 获取邮件内容
     * 
     * @param template
     * @param data
     * @return
     */
    private static String getContent(String template, Map<String, Object> data) throws Exception {
        if (data == null || data.isEmpty()) {
            return template;
        }
        
        Template tpl = new Template("tpl", template, new Configuration(new Version(2, 3, 23)));
        StringWriter result = new StringWriter();
        tpl.process(data, result);
        return result.toString();
    }

    /**
     * 获取昵称
     * 
     * @param sender
     * @return
     */
    private static String getNickname(SenderConfig sender) {
        // 如果昵称为空，则用用户名
        return sender.getNickname() == null ? sender.getUsername() : sender.getNickname();
    }
    
    /**
     * 将SenderConfig配置信息包装成Properties
     * 
     * @param sender
     * @return
     */
    private static Properties wrapProperties(SenderConfig sender) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", sender.getSmtpHost());  
        prop.put("mail.smtp.port", sender.getSmtpPort());  
        prop.put("mail.smtp.auth", "true");
        
        // 开启ssl
        if (sender.isSsl()) {
            prop.put("mail.smtp.socketFactory.port", sender.getSmtpPort());  
            prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
        }
        return prop;
    }
}