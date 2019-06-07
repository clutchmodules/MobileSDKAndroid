package com.clutch.mobilesdk.models;

public class RegisterResponse {

  public boolean success;

  public String token;

  public String cardNumber;

  public String pin;

  public RegisterResponse() {}

  public RegisterResponse(boolean success) {
    this.success = success;
  }

}
