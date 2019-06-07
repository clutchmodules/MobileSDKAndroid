# Clutch Mobile SDK for Android

Library for access to the Clutch Mobile API from Android applications.

## Configuration

Obtain an app key and secret for the Mobile API from [Clutch](https://www.clutch.com).
This app key and secret pair will be generated specifically for the Mobile API and can't be used with the Clutch JSON API.

In your Android app, create an instance of `ClutchClient`, which requires an inner API client.
You can either use the built-in native API client, or replace this with your own API client extension of `com.clutch.mobilesdk.network.APIClient`.

Setting up the `ClutchClient` with the default `NativeAPIClient`

```java
import com.clutch.mobilesdk.network.NativeAPIClient;
import com.clutch.mobilesdk.ClutchClient;

NativeAPIClient apiClient = new NativeAPIClient("YOUR-APP-KEY", "YOUR-APP-SECRET");
ClutchClient client = new ClutchClient(apiClient);

// Store a single reference to this ClutchClient instance in your application

// On shutdown:
apiClient.shutdown();
```

## Usage

With the `ClutchClient` instance, you can immediately request brand-level data. To list all subscription lists for your brand:

```java
import java.util.concurrent.Future;

import com.clutch.mobilesdk.models.ListSubscriptionListsResponse;
import com.clutch.mobilesdk.network.ResponseHandler;
import com.clutch.mobilesdk.network.ResponseWrapper;
import com.clutch.mobilesdk.APIException;

Future<ResponseWrapper<ListSubscriptionListsResponse>> future = client.getSubscriptionLists(new ResponseHandler<ListSubscriptionListsResponse>() {
  @Override
  public void handleSuccess(ListSubscriptionListsResponse response) {
    // Handle the successful response here
  }

  @Override
  public void handleError(APIException ex) {
    // A non-recoverable error happened, try again at a later time
  }
});

// To cancel the call before the ResponseHandler is invoked:
future.cancel(true);
```

This approach will asynchronously invoke the `ResponseHandler` callback in a thread provided by the `APIClient`.

You can also access the response synchronously in the same thread:

```java
ListSubscriptionListsResponse response = client.getSubscriptionLists(null).get().response;
```

The API call will take some time due to the underlying network operation, which is not allowed to execute in the UI thread of an Android application. To prevent blocking the UI, asynchronous calls are recommended in most cases.

## Tokens

All user-level actions require a token. A token can be obtained by either registering for a new Clutch card or entering an existing Clutch card number and card PIN.

### Captchas

The first step when obtaining a token is to complete a captcha. Request a new captcha ID:

```java
client.getCaptchaID(new ResponseHandler<String>() {
  @Override
  public void handleSuccess(String captchaID) {
    String imageURL = client.getCaptchaImageURL(captchaID);
    // Store the captchaID somewhere and show the image to the user
  }

  @Override
  public void handleError(APIException ex) {

  }
});
```

The captcha image will be 400x200px and contains a randomly generated 'captcha secret' that is not case sensitive. The user should interpret the captcha image and enter this captcha secret.

With a captcha ID and secret, you can place a single API call to register a new Clutch card or get a Token for an existing Clutch card. If this API calls fails, a new captcha ID should be requested and interpreted by the user.

If a user is having trouble interpreting the code in the image, it's recommended to provide a refresh/reload button that requests a new captcha ID.

### Registering for a new card

To register a new Clutch card, the user should not only provide the captcha ID, but also fill out all required demographic fields. The Clutch brand mobile configurationcan specify which fields are required for mobile sign up.

```java
Map<String, String> primaryFields = new HashMap<>();
primaryFields.put("firstName", "John");

Map<String, String> customFields = new HashMap();

client.registerNewCard(captchaId, captchaSecret, primaryFields, customFields, new ResponseHandler<RegisterResponse>() {
  @Override
  public void handleSuccess(RegisterResponse response) {
    // The response will contain the card number and PIN that were generated
    // Show the PIN to the user, as they won't be able to obtain this later on anymore
    
    // The response will also contain a token, which should be stored to keep the user 'logged in'
  }

  @Override
  public void handleError(APIException ex) {
    // The registration failed, the user should enter a new captcha for a new attempt
  }
});
```

### Using an existing card

If the user already has a Clutch card, they should enter the card number and PIN, along with the captcha secret. To get a Token with this information:

```java
client.getTokenExistingCard(captchaId, captchaSecret, cardNumber, pin, new ResponseHandler<String>() {
  @Override
  public void handleSuccess(String token) {
    // Store this token to keep the user 'logged in' in the app
  }

  @Override
  public void handleError(APIException ex) {
    // The login failed, the user should enter a new captcha for a new attempt
  }
});
```

### Logging out / release a token

If a user wants to log out from the app, their Token can be released again. This will invalidate the Token for future usage and require the user to log in again before they can use the Mobile API again.

```java
client.releaseToken(token, new ResponseHandler<Boolean>() {
  @Override
  public void handleSuccess(Boolean success) {
    // If success is true, the logout was successful
  }

  @Override
  public void handleError(APIException ex) {
    // Logout failed
  }
});
```

### User actions

Once you have obtained a user token, you can perform the following actions:

 - Get the user profile, returning balances (loyalty, gift and custom), demographics, email opt in status, segment membership
 - Update demographics (only fields specified as 'editable' in your Clutch brand configuration)
 - Change email opt-in status, both global and per individual subscription list
 - Register events
 - Provide APNS token for push notification integration with Clutch campaigns
 
 ## Author

Clutch, asksupport@clutch.com

## License

The Clutch Mobile SDK for Android is available under the MIT license. See the LICENSE file for more info.

