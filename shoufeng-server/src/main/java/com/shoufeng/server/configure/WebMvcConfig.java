package com.shoufeng.server.configure;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.shoufeng.server.common.exception.ServiceException;
import com.shoufeng.server.common.pojo.Result;
import com.shoufeng.server.common.pojo.ResultCode;
import com.shoufeng.server.common.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletException;
import java.util.List;

//import org.apache.shiro.authz.UnauthenticatedException;
//import org.apache.shiro.authz.UnauthorizedException;

/**
 * mvc配置
 *
 * @author shoufeng
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfig.class);

    /**
     * 统一异常处理
     *
     * @param resolvers 异常处理器
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add((request, response, handler, e) -> {
            // TODO: 2019/9/26 有些异常可能不需要打印日志，比如自定义的服务异常，针对不同异常可能需要不同的日志级别划分，不然看起来有点乱
            Result result;
            LOGGER.error("服务异常: ", e);
            if (e instanceof ServiceException) {
                //业务失败的异常，如“账号或密码错误”
                result = Result.builder().code(ResultCode.FAIL.code()).message(e.getMessage()).build();
            } else if (e instanceof NoHandlerFoundException) {
                result = Result.builder().code(ResultCode.NOT_FOUND.code()).message("接口 [" + request.getRequestURI() + "] 不存在").build();
            } else if (e instanceof ServletException) {
                result = Result.builder().code(ResultCode.FAIL.code()).message(e.getMessage()).build();
            } else if (e instanceof TokenExpiredException) {
                result = Result.builder().code(ResultCode.FAIL.code()).message(e.getMessage()).build();
//            } else if (e instanceof UnauthenticatedException) {
//                result = Result.builder().code(ResultCode.FAIL.code()).message(e.getMessage()).build();
//            } else if (e instanceof UnauthorizedException) {
                result = Result.builder().code(ResultCode.FAIL.code()).message(e.getMessage()).build();
            } else {
                result = Result.builder().code(ResultCode.INTERNAL_SERVER_ERROR.code()).message("接口 [" + request.getRequestURI() + "] 内部错误，请联系管理员").build();
                String message;
                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
                            request.getRequestURI(),
                            handlerMethod.getBean().getClass().getName(),
                            handlerMethod.getMethod().getName(),
                            e.getMessage());
                } else {
                    message = e.getMessage();
                }
            }
            ResponseUtil.responseResult(response, result);
            return new ModelAndView();
        });
    }

}
