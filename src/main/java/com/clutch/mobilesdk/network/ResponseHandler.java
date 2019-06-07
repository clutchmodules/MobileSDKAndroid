package com.clutch.mobilesdk.network;

import com.clutch.mobilesdk.APIException;

/**
 * Handler for API responses.
 * @param <T>
 */
public interface ResponseHandler<T> {

  /**
   * Handle a successful API response. This might not guarantee that the request parameters were also successful.
   * In case the response parameter is an object with a success flag, use that instead to determine if the API call was actually successful.
   * @param response Response object
   */
  void handleSuccess(T response);

  /**
   * Handle an API call error.
   * @param ex The API exception showing by the API call failed
   */
  void handleError(APIException ex);

}
