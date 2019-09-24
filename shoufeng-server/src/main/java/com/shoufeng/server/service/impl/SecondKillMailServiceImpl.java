package com.shoufeng.server.service.impl;

import com.shoufeng.message.service.MailService;
import com.shoufeng.server.common.utils.FreeMarkerUtil;
import com.shoufeng.server.service.SecondKillMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shoufeng
 */
@Service
public class SecondKillMailServiceImpl implements SecondKillMailService {

    @Autowired
    private FreeMarkerUtil freeMarkerUtil;

    @Autowired
    private MailService mailService;

    @Override
    public void sendSecondKillSuccessMail(String toMailAddress, String username, String itemName) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("itemName", itemName);
        String htmlString = freeMarkerUtil.getHtmlString("/second_kill_success_mail.ftl", map);
        mailService.sendMail(toMailAddress, "秒杀成功", htmlString, null, null);
    }
}
