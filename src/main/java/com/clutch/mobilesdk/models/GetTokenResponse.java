package com.clutch.mobilesdk.models;

public class GetTokenResponse {

  public boolean success;

  public String token;

  public GetTokenResponse() {}

  public GetTokenResponse(boolean success, String token) {
    this.success = success;
    this.token = token;
  }

}
