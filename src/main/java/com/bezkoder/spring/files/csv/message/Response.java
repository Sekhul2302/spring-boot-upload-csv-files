package com.bezkoder.spring.files.csv.message;

public class Response {
    String status;
    String message;
    Object Data;

    public Response(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        Data = data;
    }

    public Response() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
