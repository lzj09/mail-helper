package com.swnote.mail.config;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 权限配置
 * 
 * @author lzj
 * @date [2019-03-23]
 */
public class AuthConfig extends Authenticator {

    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;

    public AuthConfig(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}