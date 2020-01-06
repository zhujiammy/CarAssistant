package com.example.carassistant.retrofit;

public class ApiResults<T> {

    private int code;

    private String msg;

    private T result;

    public ApiResults() {

    }

    public ApiResults(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.result = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code=" + code +
                ", message='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}

