package com.shoufeng.server.common.utils;

import com.shoufeng.server.common.pojo.Result;
import com.shoufeng.server.common.pojo.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 网络请求工具类
 *
 * @author shoufeng
 */
@Component
public class HttpUtil {

    private final RestTemplate restTemplate;

    @Autowired
    public HttpUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> Result get(String url, Class<T> clazz, Map<String, Object> uriMap) {
        ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, clazz, uriMap);
        return generateResult(responseEntity);
    }

    public <T> Result post(String url, Class<T> clazz, Object data, Map<String, Object> uriMap) {
        ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, data, clazz, uriMap);
        return generateResult(responseEntity);
    }

    public <T> Result put(String url, Class<T> clazz, Object data, Map<String, Object> uriMap) {
        ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, data, clazz, uriMap);
        return generateResult(responseEntity);
    }

    public void delete(String url, Map<String, Object> uriMap) {
        restTemplate.delete(url, uriMap);
    }

    private <T> Result generateResult(ResponseEntity<T> responseEntity) {
        HttpStatus httpStatus = responseEntity.getStatusCode();
        T body = responseEntity.getBody();
        if (httpStatus.is2xxSuccessful()) {
            return Result.builder().code(ResultCode.SUCCESS.code()).message("SUCCESS").data(body).build();
        } else {
            return Result.builder().code(ResultCode.FAIL.code()).message("FAIL").data(body).build();
        }
    }
}
