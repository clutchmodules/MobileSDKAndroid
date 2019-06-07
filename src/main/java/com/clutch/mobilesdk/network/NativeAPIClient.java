package com.clutch.mobilesdk.network;

import com.clutch.mobilesdk.APIException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Implementation of the APIClient that uses the NativeAPIClient.
 */
public class NativeAPIClient extends APIClient {

  /**
   * Executor service responsible for handling network traffic.
   */
  private final ExecutorService executor;

  private final CompletionService<ResponseWrapper> completionService;

  /**
   * Set up a new native API client.
   * @param appKey App key for your implementation
   * @param appSecret App secret for your implementation
   * @param maxThreads Maximum amount of threads to use for concurrent calls. It is recommended to use just 1 or 2 here.
   */
  public NativeAPIClient(String appKey, String appSecret, int maxThreads) {
    super(appKey, appSecret);
    executor = Executors.newFixedThreadPool(maxThreads);
    completionService = new ExecutorCompletionService<ResponseWrapper>(executor);
  }

  /**
   * Set up a new native API client with the default thread count of 2.
   * @param appKey App key for your implementation
   * @param appSecret App secret for your implementation
   */
  public NativeAPIClient(String appKey, String appSecret) {
    this(appKey, appSecret, 2);
  }

  @Override
  public <T> Future<ResponseWrapper<T>> placeAsyncCall(String method, String postData, Function<String, T> parser, ResponseHandler<T> handler) {

    Callable<ResponseWrapper> callable = () -> {
      try {
        String response = placeCall(method, postData, 3);
        T parsedResponse = parser.apply(response);
        if(handler != null) {
          handler.handleSuccess(parsedResponse);
        }
        return new ResponseWrapper(parsedResponse);
      } catch(APIException ex) {
        if(handler != null) {
          handler.handleError(ex);
        }
        return new ResponseWrapper();
      } catch(Exception ex) {
        if(handler != null) {
          handler.handleError(new APIException("Unexpected API problem", ex));
        }
        return new ResponseWrapper();
      }
    };

    // Wrap the standard future, to allow casting of the returned results
    Future<ResponseWrapper> future = completionService.submit(callable);
    return new Future<ResponseWrapper<T>>() {

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
        return future.isCancelled();
      }

      @Override
      public boolean isDone() {
        return future.isDone();
      }

      @Override
      public ResponseWrapper<T> get() throws InterruptedException, ExecutionException {
        return future.get();
      }

      @Override
      public ResponseWrapper<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
      }
    };
  }

  /**
   * Place an API call to the Clutch Mobile API.
   * @param method Method to use, starting with a leading slash, e.g. /brand/lists
   * @param postData Post data or null to place a GET call instead
   * @param attempts Maximum amount of attempts
   * @return Return data as a string
   */
  protected String placeCall(String method, String postData, int attempts) {
    OutputStream outputStream = null;
    InputStream inputStream = null;
    HttpURLConnection conn = null;

    try {
      URL url = new URL(endpoint + method);
      conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(10000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      getHeaders(method, postData).forEach(conn::setRequestProperty);

      if(postData != null) {
        byte[] bytes = postData.getBytes(StandardCharsets.UTF_8);

        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(bytes.length));

        outputStream = conn.getOutputStream();
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
      }

      int statusCode = conn.getResponseCode();
      if(statusCode != 200) {
        throw new APIException("Could not reach Clutch mobile API, HTTP status code: " + statusCode);
      }

      inputStream = conn.getInputStream();
      InputStreamReader inputReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
      StringBuilder builder = new StringBuilder();
      char[] charBuffer = new char[4096];
      int n;
      while((n = inputReader.read(charBuffer)) != -1) {
        builder.append(charBuffer, 0, n);
      }

      return builder.toString();
    } catch(MalformedURLException ex) {
      throw new APIException("Unexpected error in endpoint URL", ex);
    } catch(Exception ex) {
      if(attempts > 1) {
        return placeCall(method, postData, attempts - 1);
      } else {
        throw new APIException("Ran out of retries", ex);
      }
    } finally {
      tryClose(outputStream);
      tryClose(inputStream);
      tryDisconnect(conn);
    }
  }

  /**
   * Attempt to close a resource.
   * @param resource Resource to close
   */
  private void tryClose(Closeable resource) {
    try {
      if(resource != null) {
        resource.close();
      }
    } catch(IOException e) {
      // Ignore
    }
  }

  /**
   * Attempt to disconnect a connection, unless it's null.
   * @param conn Connection - allowed to be null
   */
  private void tryDisconnect(HttpURLConnection conn) {
    if(conn != null) {
      conn.disconnect();
    }
  }

  /**
   * Shutdown the internal connection pool.
   * @param timeoutMillis Timeout in milliseconds, wait this many milliseconds at most for completion of running tasks
   * @throws InterruptedException
   */
  public void shutdown(long timeoutMillis) throws InterruptedException {
    executor.shutdown();
    if(!executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
      executor.shutdownNow();
    }
  }

  /**
   * Shutdown the internal connection pool, waiting at most 2 seconds.
   * @throws InterruptedException
   */
  public void shutdown() throws InterruptedException {
    shutdown(2000);
  }

}
