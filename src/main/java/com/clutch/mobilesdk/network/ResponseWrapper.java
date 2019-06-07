package com.clutch.mobilesdk.network;

public class ResponseWrapper <T> {

  public boolean success;

  public T response;

  public ResponseWrapper() {
    this.success = false;
  }

  public ResponseWrapper(T response) {
    this.success = true;
    this.response = response;
  }

}
