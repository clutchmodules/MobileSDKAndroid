package com.clutch.mobilesdk.models;

import java.util.Map;

public class RegisterRequest {

  public String captchaId;

  public String captchaSecret;

  public Map<String, String> primaryFields;

  public Map<String, String> customFields;

}
