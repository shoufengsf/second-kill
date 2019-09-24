package com.shoufeng.message.service;

import java.io.File;

/**
 * @author shoufeng
 */
public interface MailService {

    /**
     * 发送简单邮件
     *
     * @param toMailAddress 接受人邮箱地址
     * @param subject       主题
     * @param text          内容
     */
    void sendSimpleMail(String toMailAddress, String subject, String text);

    /**
     * 发送复杂邮件
     *
     * @param toMailAddress 接受人邮箱地址
     * @param subject       主题
     * @param text          内容
     * @param fileName      附件名
     * @param file          附件
     */
    void sendMail(String toMailAddress, String subject, String text, String fileName, File file);
}
