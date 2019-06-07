package com.clutch.mobilesdk;

import com.clutch.mobilesdk.models.*;
import com.clutch.mobilesdk.network.APIClient;
import com.clutch.mobilesdk.network.ResponseHandler;
import com.clutch.mobilesdk.network.ResponseWrapper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Clutch Mobile API client for Java / Android.
 *
 */
public class ClutchClient {

  private APIClient apiClient;

  public ClutchClient(APIClient apiClient) {
    this.apiClient = apiClient;
  }

  /**
   * Place a health check, checking if the mobile API can be reached.
   * @param handler Handler for health check response
   * @return True iff the health check can be reached
   */
  public Future<ResponseWrapper<Boolean>> getHealthCheck(ResponseHandler<Boolean> handler) {
    return apiClient.placeAsyncCall("/health", null, data -> true, handler);
  }

  /**
   * List all subscription lists.
   * @param handler  Handler for all available subscription lists and their meta data
   * @return Future holding the API response
   */
  public Future<ResponseWrapper<ListSubscriptionListsResponse>> getSubscriptionLists(ResponseHandler<ListSubscriptionListsResponse> handler) {
    return apiClient.placeAsyncCall("/brand/lists", null,
            data -> new Gson().fromJson(data, ListSubscriptionListsResponse.class),
            handler);
  }

  /**
   * List all available demographics / custom fields.
   * Includes information about required and editable status of fields.
   * @param handler Handler for all available demographics / custom fields
   * @return Future holding the API response
   */
  public Future<ResponseWrapper<ListFieldsResponse>> getFields(ResponseHandler<ListFieldsResponse> handler) {
    return apiClient.placeAsyncCall("/brand/fields", null,
            data -> new Gson().fromJson(data, ListFieldsResponse.class),
            handler);
  }

  /**
   * Generate a captcha ID.
   * @param handler Handler for captcha ID
   * @return Future holding the API response
   */
  public Future<ResponseWrapper<String>> getCaptchaID(ResponseHandler<String> handler) {
    return apiClient.placeAsyncCall("/captcha/new", null,
            data -> new Gson().fromJson(data, CaptchaResponse.class).captchaId,
            handler);
  }

  /**
   * Helper method to get the URL for a captcha image.
   * NOTE: The URL can only be accessed once. If it needs to be accessed again, a new captcha ID should be requested from getCaptchaID.
   *
   * @param captchaID Captcha ID. A new Captcha ID can be obtained from getCaptchaID
   * @return Full URL to captcha image. This URL can only be opened once.
   */
  public String getCaptchaImageURL(String captchaID) {
    return apiClient.getEndpoint() + "/captcha/show/" + captchaID;
  }

  /**
   * Register for a new mobile token.
   * This method requires a unique captcha token to be entered by the user.
   * NOTE: A single captcha ID can only be used once.
   *
   * @param captchaId ID of the captcha image that was entered
   * @param captchaValue Value that the user could read from the captcha
   * @param primaryFields Demographics values for primary fields
   * @param customFields Demographics/custom values for custom fields
   * @param handler Handler for response object, including the created Clutch card number and PIN, along with the mobile access token.
   *         It is recommended to store this response and show the PIN to the user.
   * @return Future holding API response
   */
  public Future<ResponseWrapper<RegisterResponse>> registerNewCard(String captchaId, String captchaValue, Map<String, String> primaryFields, Map<String, String> customFields, ResponseHandler<RegisterResponse> handler) {
    RegisterRequest request = new RegisterRequest();
    request.captchaId = captchaId;
    request.captchaSecret = captchaValue;
    request.primaryFields = primaryFields;
    request.customFields = customFields;
    return apiClient.placeAsyncCall("/auth/token/register", new Gson().toJson(request),
            data -> new Gson().fromJson(data, RegisterResponse.class),
            handler);
  }

  /**
   * Get an app token for an existing Clutch card.
   * This method requires a unique captcha token to be entered by the user.
   * NOTE: A single captcha ID can only be used once.
   *
   * @param captchaId ID of the captcha image that was entered
   * @param captchaValue Value that the user could read from the captcha
   * @param cardNumber Clutch card number of the card that belongs to the user
   * @param pin Card PIN if the Clutch configuration specifies this as required for the app key
   * @param handler Handler for response object, being the token. It is recommended to store this token for future access to the Clutch Client
   * @return Future holding API response
   */
  public Future<ResponseWrapper<String>> getTokenExistingCard(String captchaId, String captchaValue, String cardNumber, String pin, ResponseHandler<String> handler) {
    GetTokenRequest request = new GetTokenRequest();
    request.captchaId = captchaId;
    request.captchaSecret = captchaValue;
    request.cardNumber = cardNumber;
    request.pin = pin;
    return apiClient.placeAsyncCall("/auth/token/existing", new Gson().toJson(request),
            data -> {
              GetTokenResponse response = new Gson().fromJson(data, GetTokenResponse.class);
              if(response == null || !response.success) {
                throw new APIException("Invalid input");
              }
              return response.token;
            },
            handler);
  }

  /**
   * Release a token. This will invalidate the token, preventing it from being used in future API calls.
   * @param token Mobile app token to release
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  public Future<ResponseWrapper<Boolean>> releaseToken(String token, ResponseHandler<Boolean> handler) {
    return apiClient.placeAsyncCall("/auth/token/release/" + token, null,
            data -> new Gson().fromJson(data, BasicResponse.class).success,
            handler);
  }

  /**
   * Update demographics.
   * @param token Token to access a single card
   * @param primaryFields Demographics values for primary fields, should only contain updates
   * @param customFields Demographics/custom values for custom fields, should only contain updates
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  public Future<ResponseWrapper<Boolean>> updateDemographics(String token, Map<String, String> primaryFields, Map<String, String> customFields, ResponseHandler<Boolean> handler) {
    DemographicsUpdateRequest request = new DemographicsUpdateRequest();
    request.token = token;
    request.primaryFields = primaryFields == null ? new HashMap<>() : primaryFields;
    request.customFields = customFields == null ? new HashMap<>() : customFields;
    return apiClient.placeAsyncCall("/profile/demographics", new Gson().toJson(request),
            data -> new Gson().fromJson(data, BasicResponse.class).success,
            handler);
  }

  /**
   * Update the push token for APNS (Apple).
   * This should typically not be used from an Android app.
   *
   * @param token Token to access a single card
   * @param pushToken New APNS push token for the user
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  public Future<ResponseWrapper<Boolean>> updatePushTokenAPNS(String token, String pushToken, ResponseHandler<Boolean> handler) {
    return updatePushToken(token, "apns", pushToken, handler);
  }

  /**
   * Update the push token for GCM (Android).
   * @param token Token to access a single card
   * @param pushToken New GCM push token for the user
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  public Future<ResponseWrapper<Boolean>> updatePushTokenGCM(String token, String pushToken, ResponseHandler<Boolean> handler) {
    return updatePushToken(token, "gcm", pushToken, handler);
  }

  /**
   * Register a card event.
   * @param token Token to access a single card
   * @param eventCategoryId The category ID of the event to record
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  public Future<ResponseWrapper<Boolean>> registerEvent(String token, String eventCategoryId, ResponseHandler<Boolean> handler) {
    RegisterEventRequest request = new RegisterEventRequest();
    request.token = token;
    request.categoryId = eventCategoryId;
    return apiClient.placeAsyncCall("/profile/event", new Gson().toJson(request),
            data -> new Gson().fromJson(data, BasicResponse.class).success,
            handler);
  }

  /**
   * Update the subscription list opt in status for a single subscription list.
   * @param token Token to access a single card
   * @param subscriptionListId ID of the subscription list that gets an opt in status change
   * @param globalOptIn New global opt in status, or null if this should not be changed
   * @param newOptIn New opt in status, true for opted in, false for opted out
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  public Future<ResponseWrapper<Boolean>> updateOptInStatus(String token, String subscriptionListId, Boolean globalOptIn, Boolean newOptIn, ResponseHandler<Boolean> handler) {
    SubscriptionListUpdateRequest request = new SubscriptionListUpdateRequest();
    request.token = token;
    request.subscriptionListId = subscriptionListId;
    request.newOptIn = newOptIn;
    request.globalOptIn = globalOptIn;
    return apiClient.placeAsyncCall("/profile/optInStatus", new Gson().toJson(request),
            data -> new Gson().fromJson(data, BasicResponse.class).success,
            handler);
  }

  /**
   * Get the current profile status for a user.
   * @param token Token to access a single card
   * @param handler Response handler to receive all information for the current card
   * @return Future holding API response
   */
  public Future<ResponseWrapper<ProfileViewResponse>> getProfile(String token, ResponseHandler<ProfileViewResponse> handler) {
    return apiClient.placeAsyncCall("/profile/token/" + token, null,
            data -> new Gson().fromJson(data, ProfileViewResponse.class),
            handler);
  }

  /**
   * Update push token.
   *
   * @param token Token to access a single card
   * @param pushTokenType Type, either apns or gcm
   * @param pushToken New push token value
   * @param handler Response handler, will handle a flag with the request success state
   * @return Future holding API response
   */
  private Future<ResponseWrapper<Boolean>> updatePushToken(String token, String pushTokenType, String pushToken, ResponseHandler<Boolean> handler) {
    PushTokenUpdateRequest request = new PushTokenUpdateRequest();
    request.token = token;
    request.pushTokenType = pushTokenType;
    request.pushToken = pushToken;

    return apiClient.placeAsyncCall("/profile/pushToken", new Gson().toJson(request),
            data -> new Gson().fromJson(data, BasicResponse.class).success,
            handler);
  }

}
