package com.shoufeng.server;

import com.alibaba.fastjson.JSON;
import com.shoufeng.model.entity.ItemKillSuccess;
import com.shoufeng.server.service.IItemKillSuccessService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author shoufeng
 */
public class IItemKillSuccessServiceTest extends ServerApplicationTests {

    @Autowired
    private IItemKillSuccessService itemKillSuccessService;

    @Test
    public void addTest() {
        ItemKillSuccess itemKillSuccess = new ItemKillSuccess();
        itemKillSuccess.setItemId(1L);
        itemKillSuccess.setKillId(2L);
        itemKillSuccess.setUserId(3L);
        itemKillSuccessService.save(itemKillSuccess);
        System.out.println(JSON.toJSONString(itemKillSuccess));
    }
}
