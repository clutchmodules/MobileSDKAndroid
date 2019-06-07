package com.clutch.mobilesdk.models;

public class PushTokenUpdateRequest {

  public String token;

  /**
   * Token type, allowed: 'apns' or 'gcm'.
   */
  public String pushTokenType;

  public String pushToken;

}
