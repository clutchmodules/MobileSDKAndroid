package com.clutch.mobilesdk.models;

/**
 * Request to get a new token for an existing card.
 */
public class GetTokenRequest {

  public String cardNumber;

  public String pin;

  public String captchaId;

  public String captchaSecret;

}
