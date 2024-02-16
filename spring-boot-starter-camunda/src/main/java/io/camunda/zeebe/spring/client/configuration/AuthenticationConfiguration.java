package io.camunda.zeebe.spring.client.configuration;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.JwtConfig;
import io.camunda.common.auth.JwtCredential;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SelfManagedAuthentication;
import io.camunda.common.auth.SimpleAuthentication;
import io.camunda.common.auth.SimpleConfig;
import io.camunda.common.auth.SimpleCredential;
import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.common.auth.identity.IdentityContainer;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties.AuthMode;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CamundaClientProperties.class)
public class AuthenticationConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(AuthenticationConfiguration.class);
  private final CamundaClientProperties camundaClientProperties;

  @Autowired
  public AuthenticationConfiguration(CamundaClientProperties camundaClientProperties) {
    this.camundaClientProperties = camundaClientProperties;
  }

  @Bean
  public Authentication camundaAuthentication() {
    // check which kind of authentication is used
    AuthMode authMode = detectAuthMode();
    // simple: configure tasklist and operate only
    if (AuthMode.SIMPLE.equals(authMode)) {
      SimpleConfig config = new SimpleConfig();
      simpleCredentialForProduct(config, Product.OPERATE);
      simpleCredentialForProduct(config, Product.TASKLIST);
      return SimpleAuthentication.builder().withSimpleConfig(config).build();
    } else
    // oidc: configure zeebe, tasklist, operate, optimize
    if (AuthMode.OIDC.equals(authMode)) {
      IdentityConfig identityConfig = new IdentityConfig();
      identityContainerForProduct(identityConfig,Product.OPERATE);
      JwtConfig config = new JwtConfig();
      jwtCredentialForProduct(config,Product.OPERATE);
      jwtCredentialForProduct(config,Product.TASKLIST);
      jwtCredentialForProduct(config,Product.ZEEBE);
      jwtCredentialForProduct(config,Product.OPTIMIZE);
      return SelfManagedAuthentication.builder().withJwtConfig(config).withIdentityConfig()
    } else
    // saas: configure all
    if (AuthMode.SAAS.equals(authMode)){

    }
  }

  private void identityContainerForProduct(IdentityConfig config, Product product) {
    if(enabledForProduct(product)){
      LOG.debug("{} is enabled", product);
      config.addProduct(product,new IdentityContainer());
    }
  }

  private AuthMode detectAuthMode() {
    // TODO implement this
    // check if auth mode is given somewhere
    // if not, check if properties allow for an explicit detection
    // username, password -> simple
    // client id, client secret, cluster id -> saas
    // auth url, audience, client id, client secret -> oidc
  }

  private void simpleCredentialForProduct(SimpleConfig config, Product product) {
    if (enabledForProduct(product)) {
      LOG.debug("{} is enabled", product);
      config.addProduct(
          product,
          new SimpleCredential(
              baseUrlForProduct(product),
              usernameForProduct(product),
              passwordForProduct(product)));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private void jwtCredentialForProduct(JwtConfig config, Product product) {
    if (enabledForProduct(product)) {
      LOG.debug("{} is enabled", product);
      config.addProduct(
          product,
          new JwtCredential(
              clientIdForProduct(product),
              clientSecretForProduct(product),
              audienceForProduct(product),
              authUrlForProduct(product)));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private Boolean enabledForProduct(Product product) {
    return getApiProperty("enabled", product, ApiProperties::getEnabled);
  }

  private String baseUrlForProduct(Product product) {
    return getApiProperty("base url", product, ApiProperties::getBaseUrl);
  }

  private String usernameForProduct(Product product) {
    return getAuthProperty("username", product, AuthProperties::getUsername);
  }

  private String passwordForProduct(Product product) {
    return getAuthProperty("password", product, AuthProperties::getPassword);
  }

  private String authUrlForProduct(Product product) {
    return getAuthProperty("auth url", product, AuthProperties::getAuthUrl);
  }

  private String clientIdForProduct(Product product) {
    return getAuthProperty("client id", product, AuthProperties::getClientId);
  }

  private String clientSecretForProduct(Product product) {
    return getAuthProperty("client secret", product, AuthProperties::getClientSecret);
  }

  private String audienceForProduct(Product product) {
    return getAuthProperty("audience", product, AuthProperties::getAudience);
  }

  private <T> T getApiProperty(
      String propertyName, Product product, Function<ApiProperties, T> getter) {
    return getApiProperty(product + " " + propertyName, getter, apiPropertiesForProduct(product));
  }

  private <T> T getAuthProperty(
      String propertyName, Product product, Function<AuthProperties, T> getter) {
    return getAuthProperty(
        product + " " + propertyName,
        getter,
        authPropertiesForProduct(product),
        camundaClientProperties::getAuth);
  }

  private ApiPropertiesSupplier apiPropertiesForProduct(Product product) {
    switch (product) {
      case OPERATE -> {
        return camundaClientProperties::getOperate;
      }
      case TASKLIST -> {
        return camundaClientProperties::getTasklist;
      }
      default -> {
        throw new IllegalStateException(
            "Could not detect auth properties supplier for product " + product);
      }
    }
  }

  private AuthPropertiesSupplier authPropertiesForProduct(Product product) {
    switch (product) {
      case OPERATE -> {
        return camundaClientProperties::getOperate;
      }
      case TASKLIST -> {
        return camundaClientProperties::getTasklist;
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
