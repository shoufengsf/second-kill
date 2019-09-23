package com.shoufeng.server;

import com.alibaba.fastjson.JSON;
import com.shoufeng.model.entity.ItemKillSuccess;
import com.shoufeng.server.service.IItemKillSuccessService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author shoufeng
 */
public class IItemKillSuccessServiceTest extends ServerApplicationTests{

    @Autowired
    private IItemKillSuccessService itemKillSuccessService;

    @Test
    public void addTest(){
        ItemKillSuccess itemKillSuccess = new ItemKillSuccess();
        itemKillSuccess.setItemId(1);
        itemKillSuccess.setKillId(2);
        itemKillSuccess.setUserId(3);
        itemKillSuccessService.save(itemKillSuccess);
        System.out.println(JSON.toJSONString(itemKillSuccess));
    }
}
