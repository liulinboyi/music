package com.podcasts.jpa.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Msg {
    int code;   //错误码
    String Message; //消息提示
    Map<String, Object> data = new HashMap<String, Object>();   //数据
    Boolean success;

    //无权访问
    public static Msg denyAccess(String message) {
        Msg result = new Msg();
        result.setCode(300);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    //操作成功
    public static Msg success(String message) {
        Msg result = new Msg();
        result.setCode(200);
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    //客户端操作失败
    public static Msg fail(String message) {
        Msg result = new Msg();
        result.setCode(400);
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public Msg add(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
