package io.camunda.operate.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;

@JsonSerialize(using = DateFilterSerializer.class)
public class DateFilter {

  private Date date;

  private DateFilterRange range;

  public DateFilter(Date date, DateFilterRange range) {
    super();
    this.date = date;
    this.range = range;
  }

  public DateFilter(Date date) {
    this(date, DateFilterRange.SECOND);
  }

  public DateFilter() {
    this(new Date());
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public DateFilterRange getRange() {
    return range;
  }

  public void setRange(DateFilterRange range) {
    this.range = range;
  }
}
