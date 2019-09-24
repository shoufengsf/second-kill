package com.shoufeng.server.common.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker工具类
 *
 * @author shoufeng
 */
@Component
public class FreeMarkerUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FreeMarkerUtil.class);

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public String getHtmlString(String templateName, Map<String, Object> data) {
        try {
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            StringWriter out = new StringWriter();
            template.process(data, out);
            out.flush();
            out.close();
            return out.getBuffer().toString();
        } catch (IOException | TemplateException e) {
            LOGGER.error("获取ftl失败: ", e);
        }
        return null;
    }
}
