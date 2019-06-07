package com.clutch.mobilesdk.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ProfileViewResponse {

  public boolean success;

  /**
   * Map of balance key to balance amount.
   * Balance key will be either 'Points', 'Punches', 'Currency.USD' or 'Custom.ABC',
   * Currency and Custom codes can vary.
   */
  public Map<String, BigDecimal> balances;

  /**
   * Primary demographics that are visible to the mobile app.
   */
  public Map<String, String> primaryDemographics;

  /**
   * Custom demographics that are visible to the mobile app.
   */
  public Map<String, String> customDemographics;

  /**
   * Global email opt in. If this is false, no email subscription lists will be active, i.e. effectively opted out of all lists.
   * If set to true, the opt in status per subscription list will be present in emailSubscriptionLists.
   */
  public boolean emailOptIn;

  /**
   * Map of email subscription list ID to a boolean that indicates if the user is opted in to this email subscription list.
   * NOTE: If emailOptIn is set to false, this map represents the opt in settings that would apply if global email opt in was allowed,
   * but in this case the user will effectively be opted out of all lists.
   */
  public Map<String, Boolean> emailSubscriptionLists;

  /**
   * List of segment information for all segments that the user is a member of.
   */
  public List<Segment> segments;

  public ProfileViewResponse() {}

  public ProfileViewResponse(boolean success) {
    this.success = success;
  }

}
