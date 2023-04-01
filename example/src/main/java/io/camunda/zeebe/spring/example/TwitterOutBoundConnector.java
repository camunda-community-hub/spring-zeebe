package io.camunda.zeebe.spring.example;


import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;import java.lang.invoke.MethodHandles;



@OutboundConnector(
  name = "Twitter",
  inputVariables = {""},
  type = "io.berndruecker.example.TwitterConnector:1"
)
public class TwitterOutBoundConnector implements OutboundConnectorFunction{
  private static final Logger LOGGER = LoggerFactory.getLogger(TwitterOutBoundConnector.class);




@Override public Object execute(OutboundConnectorContext context)throws Exception {
  LOGGER.info("start twitter");
    return null;
    }
}
