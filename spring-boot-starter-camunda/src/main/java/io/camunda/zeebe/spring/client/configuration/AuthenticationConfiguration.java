package io.camunda.zeebe.spring.client.configuration;

import static java.util.Optional.*;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.DefaultNoopAuthentication;
import io.camunda.common.auth.JwtConfig;
import io.camunda.common.auth.JwtCredential;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SaaSAuthentication;
import io.camunda.common.auth.SelfManagedAuthentication;
import io.camunda.common.auth.SimpleAuthentication;
import io.camunda.common.auth.SimpleConfig;
import io.camunda.common.auth.SimpleCredential;
import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.common.auth.identity.IdentityContainer;
import io.camunda.common.json.JsonMapper;
import io.camunda.identity.sdk.Identity;
import io.camunda.identity.sdk.IdentityConfiguration;
import io.camunda.identity.sdk.IdentityConfiguration.Type;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties.ClientMode;
import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import java.net.URL;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(CamundaClientProperties.class)
@ConditionalOnProperty(prefix = "camunda.client", name = "mode")
@Import(JsonMapperConfiguration.class)
public class AuthenticationConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationConfiguration.class);
  private final CamundaClientProperties camundaClientProperties;
  private final JsonMapper jsonMapper;

  @Autowired
  public AuthenticationConfiguration(
      CamundaClientProperties camundaClientProperties, JsonMapper jsonMapper) {
    this.camundaClientProperties = camundaClientProperties;
    this.jsonMapper = jsonMapper;
  }

  @Bean
  public Authentication camundaAuthentication() {
    ClientMode clientMode = camundaClientProperties.getMode();
    // check which kind of authentication is used
    // simple: configure tasklist and operate only
    if (ClientMode.simple.equals(clientMode)) {
      SimpleConfig config = new SimpleConfig();
      for (Product p : Product.coveredProducts()) {
        simpleCredentialForProduct(config, p);
      }
      return SimpleAuthentication.builder().withSimpleConfig(config).build();
    } else
    // oidc: configure zeebe, tasklist, operate, optimize
    if (ClientMode.oidc.equals(clientMode)) {
      IdentityConfig identityConfig = new IdentityConfig();
      for (Product p : Product.coveredProducts()) {
        oidcCredentialForProduct(identityConfig, p);
      }
      return SelfManagedAuthentication.builder().withIdentityConfig(identityConfig).build();
    } else
    // saas: configure all
    if (ClientMode.saas.equals(clientMode)) {
      JwtConfig jwtConfig = new JwtConfig();
      for (Product p : Product.coveredProducts()) {
        saasCredentialForProduct(jwtConfig, p);
      }
      return SaaSAuthentication.builder()
          .withJwtConfig(jwtConfig)
          .withJsonMapper(jsonMapper)
          .build();
    } else {
      LOG.warn("Unknown client mode {}, using noop authentication", clientMode);
      return new DefaultNoopAuthentication();
    }
  }

  private void simpleCredentialForProduct(SimpleConfig config, Product product) {
    if (enabledForProduct(product)) {
      LOG.debug("{} is enabled", product);
      config.addProduct(
          product,
          new SimpleCredential(baseUrlForProduct(product).toString(), username(), password()));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private void oidcCredentialForProduct(IdentityConfig identityConfig, Product product) {
    if (enabledForProduct(product)) {
      LOG.debug("{} is enabled", product);
      String issuer = globalIssuer();
      String clientId = clientId();
      String clientSecret = clientSecret();
      String audience = audienceForProduct(product);
      IdentityConfiguration identityCfg =
          new IdentityConfiguration(
              baseUrlForProduct(Product.IDENTITY).toString(),
              issuer,
              issuer,
              clientId,
              clientSecret,
              audience,
              globalOidcType().name());
      identityConfig.addProduct(
          product, new IdentityContainer(new Identity(identityCfg), identityCfg));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private void saasCredentialForProduct(JwtConfig jwtConfig, Product product) {
    if (enabledForProduct(product)) {
      LOG.debug("{} is enabled", product);
      String issuer = globalIssuer();
      String clientId = clientId();
      String clientSecret = clientSecret();
      String audience = audienceForProduct(product);
      jwtConfig.addProduct(product, new JwtCredential(clientId, clientSecret, audience, issuer));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private String globalIssuer() {
    return getGlobalAuthProperty("issuer", AuthProperties::getIssuer);
  }

  private Type globalOidcType() {
    return getGlobalAuthProperty("oidc type", AuthProperties::getOidcType);
  }

  private <T> T getGlobalAuthProperty(String propertyName, Function<AuthProperties, T> getter) {
    return ofNullable(camundaClientProperties.getAuth())
        .map(getter)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Could not detect required auth property " + propertyName));
  }

  private Boolean enabledForProduct(Product product) {
    return getApiProperty("enabled", product, ApiProperties::getEnabled);
  }

  private URL baseUrlForProduct(Product product) {
    return getApiProperty("base url", product, ApiProperties::getBaseUrl);
  }

  private String username() {
    return getAuthProperty("username", AuthProperties::getUsername);
  }

  private String password() {
    return getAuthProperty("password", AuthProperties::getPassword);
  }

  private String clientId() {
    return getAuthProperty("client id", AuthProperties::getClientId);
  }

  private String clientSecret() {
    return getAuthProperty("client secret", AuthProperties::getClientSecret);
  }

  private String audienceForProduct(Product product) {
    return getApiProperty("audience", product, ApiProperties::getAudience);
  }

  private <T> T getApiProperty(
      String propertyName, Product product, Function<ApiProperties, T> getter) {
    return getApiProperty(product + " " + propertyName, getter, apiPropertiesForProduct(product));
  }

  private <T> T getAuthProperty(String propertyName, Function<AuthProperties, T> getter) {
    return getAuthProperty(propertyName, getter, camundaClientProperties::getAuth);
  }

  private ApiPropertiesSupplier apiPropertiesForProduct(Product product) {
    return apiPropertiesForProduct(camundaClientProperties, product);
  }

  private ApiPropertiesSupplier apiPropertiesForProduct(
      CamundaClientProperties properties, Product product) {
    switch (product) {
      case OPERATE -> {
        return properties::getOperate;
      }
      case TASKLIST -> {
        return properties::getTasklist;
      }
      case ZEEBE -> {
        return properties::getZeebe;
      }
      case OPTIMIZE -> {
        return properties::getOptimize;
      }
      case IDENTITY -> {
        return properties::getIdentity;
      }
      default -> {
        throw new IllegalStateException(
            "Could not detect auth properties supplier for product " + product);
      }
    }
  }

  private <T> T getApiProperty(
      String propertyName,
      Function<ApiProperties, T> getter,
      ApiPropertiesSupplier... alternatives) {
    for (ApiPropertiesSupplier supplier : alternatives) {
      ApiProperties properties = supplier.get();
      if (properties != null) {
        T property = getter.apply(properties);
        if (property != null) {
          LOG.debug("Detected property {}", propertyName);
          return property;
        }
      }
    }
    throw new IllegalStateException("Could not detect required property " + propertyName);
  }

  private <T> T getAuthProperty(
      String propertyName,
      Function<AuthProperties, T> getter,
      AuthPropertiesSupplier... alternatives) {
    for (AuthPropertiesSupplier supplier : alternatives) {
      AuthProperties properties = supplier.get();
      if (properties != null) {
        T property = getter.apply(properties);
        if (property != null) {
          LOG.debug("Detected property {}", propertyName);
          return property;
        }
      }
    }
    throw new IllegalStateException("Could not detect required property " + propertyName);
  }

  private interface AuthPropertiesSupplier extends Supplier<AuthProperties> {}

  private interface ApiPropertiesSupplier extends Supplier<ApiProperties> {}
}
