package com.common.exception;

import com.common.util.StringUtils;

public class WJException extends Exception {
    private static final long serialVersionUID = -238091758285157331L;
    private int errCode = 100;  //默认错误码
    private String errMsg;

    public WJException() {
        super();
    }

    public WJException(String message, Throwable cause) {
        super(message, cause);
        this.errMsg  = message;
    }

    public WJException(String message) {
        super(message);
        this.errMsg = message;
    }

    public WJException(Throwable cause) {
        super(cause);
    }

    public WJException(int errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    
    public WJException(int errCode, String errMsg, Throwable cause) {
    	super(errCode + ":" + errMsg, cause);
    	this.errCode = errCode;
        this.errMsg = errMsg;
    }

//    public WJException(IscsErrorCode apiErrorCode, String errorMsg) {
//        StringBuilder strBuilder = new StringBuilder();
//        strBuilder.append(apiErrorCode.getErrorText());
//        if (StringUtils.isNotEmpty(errorMsg)) {
//            strBuilder.append(":").append(errorMsg);
//        }
//        this.errCode = apiErrorCode.getErrorCode();
//        this.errMsg = strBuilder.toString();
//    }

    public int getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
