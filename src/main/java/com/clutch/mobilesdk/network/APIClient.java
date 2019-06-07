package com.clutch.mobilesdk.network;

import com.clutch.mobilesdk.ShaHelper;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Abstract API client that can be extended with different implementations to actually perform the network operations.
 */
public abstract class APIClient {

  protected final String appKey;

  protected final String appSecret;

  /**
   * Endpoint, without a trailing slash.
   */
  protected String endpoint = "https://mobile-api.clutch.com";

  /**
   * Set up a new API client.
   * @param appKey App key for this implementation
   * @param appSecret App secret for this implementation
   */
  public APIClient(String appKey, String appSecret) {
    this.appKey = appKey;
    this.appSecret = appSecret;
  }

  /**
   * Update the endpoint to a custom URL.
   * @param endpoint New endpoint, e.g. https://mobile-api.clutch.com
   */
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  /**
   * Get the shared endpoint.
   * @return Endpoint, without a trailing slash
   */
  public String getEndpoint() {
    return endpoint;
  }

  /**
   * Place an async call.
   * Implementations can choose to retry failed calls multiple times, and should use the getHeaders method once per attempt.
   *
   * @param <T> Type of object to expect back
   * @param method API method to call
   * @param postData Post data or null for GET calls
   * @param parser Parser to convert the string output from the server into the desired output object
   * @param handler Handler for the expected result object, or failures. This should be invoked when the API call finishes, times out or errors out in any other way.
   * @return A future that holds the wrapped API response. The future can be cancelled to cancel the network request,
   * in which case the response handler will likely not be invoked.
   */
  public abstract <T> Future<ResponseWrapper<T>> placeAsyncCall(String method, String postData, Function<String, T> parser, ResponseHandler<T> handler);

  /**
   * Get all headers for request identification and authentication.
   *
   * @param method    Method to use, starting with a leading slash, e.g. /brand/lists
   * @param postData  Post data for the request, or null if there is none
   * @return          Map of all HTTP headers to add to the request. This will be different every time the method is invoked.
   *                  The result is a map where keys are header names and the values are the header values.
   */
  protected Map<String, String> getHeaders(String method, String postData) {
    String requestID = UUID.randomUUID().toString();
    String requestTime = Long.toString(System.currentTimeMillis());
    String contentDigest = postData == null ? "" : ShaHelper.sha512(postData);

    String hmacData = new Gson().toJson(Arrays.asList(requestID, requestTime, method, contentDigest));// Create the data string as JSON - this is used as the hmac sha512 data component
    String signature = ShaHelper.hmacSha512(hmacData, appSecret);

    Map<String, String> headers = new HashMap<>();
    headers.put("X-Application-Key", appKey);
    headers.put("X-Request-Id", requestID);
    headers.put("X-Request-Time", requestTime);
    headers.put("X-Signature", signature);

    return headers;
  }

}
