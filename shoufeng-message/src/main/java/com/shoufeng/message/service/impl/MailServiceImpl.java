package com.shoufeng.message.service.impl;

import com.shoufeng.message.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件服务
 *
 * @author shoufeng
 */
@Service
public class MailServiceImpl implements MailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUserName;

    @Override
    public void sendSimpleMail(String toMailAddress, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUserName);
            message.setTo(toMailAddress);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("邮件发送异常: ", e);
        }
    }

    @Override
    public void sendMail(String toMailAddress, String subject, String text, String fileName, File file) {

        try {
            //复杂邮件
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(mailUserName);
            messageHelper.setTo(toMailAddress);
            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.addAttachment(fileName, file);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            LOGGER.error("邮件发送异常: ", e);
        }
    }
}
