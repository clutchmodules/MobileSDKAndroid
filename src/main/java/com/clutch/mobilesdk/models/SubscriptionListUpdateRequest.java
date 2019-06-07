package com.clutch.mobilesdk.models;

public class SubscriptionListUpdateRequest {

  public String token;

  /**
   * Global opt in status.
   * If this is specified as true, the global email opt in status will be set to opted in.
   * If this is specified as false, the global email opt in status will be set to opted out.
   * If this is not specified, the global email opt in status will not be changed.
   */
  public Boolean globalOptIn;

  /**
   * Subscription list to opt in/out of. Set this to null to only update the globalOptIn status.
   */
  public String subscriptionListId;

  /**
   * True to change to opted in, false to change to opted out. This will only apply to the specified subscriptionList.
   *
   * NOTE: This will only be processed if subscriptionListId was specified.
   *
   */
  public Boolean newOptIn;

}
