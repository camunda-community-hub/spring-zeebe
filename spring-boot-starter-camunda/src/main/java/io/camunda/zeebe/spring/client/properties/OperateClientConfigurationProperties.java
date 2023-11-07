package io.camunda.zeebe.spring.client.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.invoke.MethodHandles;

@ConfigurationProperties
public class OperateClientConfigurationProperties {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    // Normal Zeebe Engine Properties
    @Value("${zeebe.client.cloud.cluster-id:#{null}}")
    private String clusterId;

    @Value("${zeebe.client.cloud.region:bru-2}")
    private String region;

    @Value("${camunda.operate.client.url:#{null}}")
    private String operateUrl;

    // TODO: This currently assumes PROD in Cloud - do we want to support DEV and INT?
    // and make it configurable? At the moment the workaround is to set the operateUrl yourself
    public static String operateCloudBaseUrl = "operate.camunda.io";

    public String getOperateUrl() {
      if (operateUrl != null) {
        LOG.debug("Connecting to Camunda Operate on URL: " + operateUrl);
        return operateUrl;
      } else if (clusterId != null) {
        String url = "https://" + region + "." + operateCloudBaseUrl + "/" + clusterId + "/";
        LOG.debug("Connecting to Camunda Operate SaaS via URL: " + url);
        return url;
      }
      throw new IllegalArgumentException(
        "In order to connect to Camunda Operate you need to specify either a SaaS clusterId or an Operate URL.");
    }
}
