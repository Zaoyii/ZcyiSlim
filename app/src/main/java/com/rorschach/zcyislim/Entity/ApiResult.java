package com.rorschach.zcyislim.Entity;

/**
 * Api统一响应实体类
 *
 * @param <T> 业务实体类
 * @author ZaoYi
 */

public class ApiResult<T> {

    public int resultCode;
    public T data;
    public String message;
    public boolean success;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "resultCode=" + resultCode +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
