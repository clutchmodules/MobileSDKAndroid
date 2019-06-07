package com.clutch.mobilesdk.models;

/**
 * Represents a custom or primary demographic field definition.
 */
public class DemographicField {

  public String apiName;

  public String displayName;

  public boolean required;

  public boolean editable;

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }
}
