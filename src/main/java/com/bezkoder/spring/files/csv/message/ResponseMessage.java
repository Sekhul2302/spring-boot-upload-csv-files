package com.bezkoder.spring.files.csv.message;

import java.util.List;

public class ResponseMessage {
  private String message;
  private List data;

  public ResponseMessage(String message) {
    this.message = message;
  }

  public ResponseMessage(String message, List data) {
    this.message = message;
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List getData() {
    return data;
  }

  public void setData(List data) {
    this.data = data;
  }
}
