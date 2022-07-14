package com.helper.spring.boot.aop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 同步,全局异常处理
 * @author jaydon
 */
@RestControllerAdvice
@Slf4j
@ResponseBody
public class SyncExceptionHandle implements ResponseBodyAdvice<Object> {

    @ExceptionHandler(value = Exception.class)
    public String processException(Exception ex) {
        log.error(" 统一异常处理："+ex.getMessage(), ex);
        return "SyncExceptionHandle";
    }

 /*   @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return null;
    }*/

    /** 此处如果返回false , 则不执行当前Advice的业务 */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

      //  return methodParameter.hasParameterAnnotation(RequestBody.class);

        // response是 String 类型，或者注释了 NotControllerResponseAdvice都不进行包装
        return !methodParameter.getParameterType().isAssignableFrom(String.class);
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
        // String类型不能直接包装
        if (returnType.getGenericParameterType().equals(String.class)) {
            //ObjectMapper objectMapper = new ObjectMapper();

                // 将数据包装在ResultVo里后转换为json串进行返回
             //   return objectMapper.writeValueAsString(new ResultVo(data));

              //  throw new APIException(ResultCode.RESPONSE_PACK_ERROR, e.getMessage());

        }
        // 否则直接包装成ResultVo返回
       // return new ResultVo(data);
        return null;
    }
}
