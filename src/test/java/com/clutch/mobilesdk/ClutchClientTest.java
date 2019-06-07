package com.clutch.mobilesdk;

import com.clutch.mobilesdk.network.NativeAPIClient;
import com.clutch.mobilesdk.network.ResponseHandler;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClutchClientTest {

  /**
   * Run a health check against the real endpoint and make sure it responds properly.
   */
  @Test
  public void healthCheckTest() throws ExecutionException, InterruptedException {
    ClutchClient client = new ClutchClient(new NativeAPIClient("app-key-does-not-exist", "secret-also-does-not-exist"));
    Boolean result = client.getHealthCheck(null).get().response;
    assertTrue(result);

    AtomicBoolean callbackResult = new AtomicBoolean();
    result = client.getHealthCheck(new ResponseHandler<Boolean>() {
      @Override
      public void handleSuccess(Boolean response) {
        callbackResult.set(true);
      }

      @Override
      public void handleError(APIException ex) {
        fail("Callback error");
      }
    }).get().response;
    assertTrue(result);
    assertTrue(callbackResult.get());
  }

}
