package io.camunda.operate.search;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class DateFilterSerializer extends StdSerializer<DateFilter> {

  public static SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

  /** serial version uid */
  private static final long serialVersionUID = -8106244922585465120L;

  public DateFilterSerializer() {
    super(DateFilter.class);
  }

  @Override
  public void serialize(DateFilter value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {

    jgen.writeString(isoFormat.format(value.getDate()) + "||/" + value.getRange().getValue());
  }
}
