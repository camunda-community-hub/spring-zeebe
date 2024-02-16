package io.camunda.operate.search;

public enum DateFilterRange {
  YEAR("y"),
  MONTH("M"),
  WEEK("w"),
  DAY("d"),
  HOUR("h"),
  MINUTE("m"),
  SECOND("s");

  private String value;

  DateFilterRange(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
