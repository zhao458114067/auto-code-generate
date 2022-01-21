package com.zxcode.generatecode.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author: zhaoxu
 */
@ControllerAdvice
public class BaseResponseBody implements ResponseBodyAdvice {
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter arg1,
                                  MediaType arg2, Class arg3, ServerHttpRequest arg4,
                                  ServerHttpResponse arg5) {
        JSONObject map = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("data", body);
        map.put("result", data);
        map.put("code", 0);
        map.put("msg", "success");
        if (body instanceof String) {
            return map.toJSONString();
        }
        return map;
    }

    @Override
    public boolean supports(MethodParameter arg0, Class arg1) {
        return true;
    }
}
