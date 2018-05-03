package com.common.util;

import com.common.exception.WJException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chengwei
 * @date 2018/4/27 14:49
 */
public class AbsResponse<T> implements Serializable {
    private static final long serialVersionUID = 5014379068811962022L;

    /**
     * 错误代码：如果为0则表示成功，其他均为失败，值自定义
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;

    @JsonIgnore
    private String body; // 结果信息
    private T data; // 返回结果

    /**
     * 参数列表
     */
    @JsonIgnore
    private Map<String, String> params;


    public AbsResponse(WJException ex) {
        this.code = ex.getErrCode();
        this.msg = ex.getErrMsg();
    }

    public AbsResponse() {
        this(0, null);
    }

    public AbsResponse(int code, String msg) {
        this(code, msg, null);
    }

    public AbsResponse(int code, String msg, String body) {
        this(code, msg, body, null);
    }

    public AbsResponse(int code, String msg, String body, T data) {
        this.code = code;
        this.msg = msg;
        this.body = body;
        this.data = data;
    }

    public AbsResponse<T> setResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public AbsResponse<T> setResult(int code, String msg,
                                    String body) {
        this.code = code;
        this.msg = msg;
        this.body = body;
        return this;
    }

    public AbsResponse<T> setResult(int code, String msg,
                                    String body, T data) {
        this.code = code;
        this.msg = msg;
        this.body = body;
        this.data = data;
        return this;
    }


    /**
     * 由另一个对象赋值，可能泛型不一致
     * @param ano
     * @return
     */
    public AbsResponse<T> setResult(AbsResponse<?> ano) {
        return setResult(ano.getCode(), ano.getMsg(), ano.getBody());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public T getData() {
        return data;
    }

    public AbsResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "AbsResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", params=" + params +
                '}';
    }
}
