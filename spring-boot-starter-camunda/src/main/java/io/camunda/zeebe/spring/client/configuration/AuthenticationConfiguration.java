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
import io.camunda.zeebe.spring.client.properties.common.ApiProperties;
import io.camunda.zeebe.spring.client.properties.common.AuthProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties;
import io.camunda.zeebe.spring.client.properties.common.GlobalAuthProperties.AuthMode;
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
  private final JsonMapper jsonMapper;

  @Autowired
  public AuthenticationConfiguration(
      CamundaClientProperties camundaClientProperties, JsonMapper jsonMapper) {
    this.camundaClientProperties = camundaClientProperties;
    this.jsonMapper = jsonMapper;
  }

  @Bean
  public Authentication camundaAuthentication(AuthMode authMode) {
    // check which kind of authentication is used
    // simple: configure tasklist and operate only
    if (AuthMode.simple.equals(authMode)) {
      SimpleConfig config = new SimpleConfig();
      for (Product p : Product.values()) {
        simpleCredentialForProduct(config, p);
      }
      return SimpleAuthentication.builder().withSimpleConfig(config).build();
    } else
    // oidc: configure zeebe, tasklist, operate, optimize
    if (AuthMode.oidc.equals(authMode)) {
      IdentityConfig identityConfig = new IdentityConfig();
      JwtConfig jwtConfig = new JwtConfig();
      for (Product p : Product.values()) {
        oidcCredentialForProduct(identityConfig, jwtConfig, p);
      }
      return SelfManagedAuthentication.builder()
          .withJwtConfig(jwtConfig)
          .withIdentityConfig(identityConfig)
          .build();
    } else
    // saas: configure all
    if (AuthMode.saas.equals(authMode)) {
      JwtConfig jwtConfig = new JwtConfig();
      // TODO add all applications
      return SaaSAuthentication.builder()
          .withJwtConfig(jwtConfig)
          .withJsonMapper(jsonMapper)
          .build();
    } else {
      return new DefaultNoopAuthentication();
    }
  }

  @Bean
  public AuthMode camundaAuthMode() {
    // check if auth mode is given
    if (hasAuthModeSet()) {
      return camundaClientProperties.getAuth().getMode();
    }
    // if not, check if properties allow for an explicit detection
    // client id, client secret, cluster id -> saas
    if (hasClusterIdSet()) {
      return AuthMode.saas;
    } else
    // issuer, audience, client id, client secret -> oidc
    if (hasOidcCredentialSet()) {
      return AuthMode.oidc;
    } else
    // username, password -> simple
    if (hasSimpleCredentialSet()) {
      return AuthMode.simple;
    } else {
      throw new IllegalStateException("Could not detect auth mode");
    }
  }

  private boolean hasAuthModeSet() {
    return ofNullable(camundaClientProperties.getAuth())
        .map(GlobalAuthProperties::getMode)
        .isPresent();
  }

  private boolean hasClusterIdSet() {
    return ofNullable(camundaClientProperties.getAuth())
        .map(GlobalAuthProperties::getClusterId)
        .isPresent();
  }

  private boolean hasOidcCredentialSet() {
    for (Product p : Product.values()) {
      if (enabledForProduct(p, AuthMode.oidc)) {
        boolean clientCredentialsPresent =
            ofNullable(authPropertiesForProduct(p).get())
                    .map(AuthProperties::getClientId)
                    .isPresent()
                && ofNullable(authPropertiesForProduct(p).get())
                    .map(AuthProperties::getClientSecret)
                    .isPresent();
        if (clientCredentialsPresent) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasSimpleCredentialSet() {
    for (Product p : Product.values()) {
      if (enabledForProduct(p, AuthMode.simple)) {
        boolean clientCredentialsPresent =
            ofNullable(authPropertiesForProduct(p).get())
                    .map(AuthProperties::getUsername)
                    .isPresent()
                && ofNullable(authPropertiesForProduct(p).get())
                    .map(AuthProperties::getPassword)
                    .isPresent();
        if (clientCredentialsPresent) {
          return true;
        }
      }
    }
    return false;
  }

  private void simpleCredentialForProduct(SimpleConfig config, Product product) {
    if (enabledForProduct(product, AuthMode.simple)) {
      LOG.debug("{} is enabled", product);
      config.addProduct(
          product,
          new SimpleCredential(
              baseUrlForProduct(product,AuthMode.simple),
              usernameForProduct(product),
              passwordForProduct(product)));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private void oidcCredentialForProduct(
      IdentityConfig identityConfig, JwtConfig jwtConfig, Product product) {
    if (enabledForProduct(product, AuthMode.oidc)) {
      LOG.debug("{} is enabled", product);
      String issuer = globalIssuer();
      String issuerBackendUrl = globalIssuerBackendUrl();
      String clientId = clientIdForProduct(product, AuthMode.oidc);
      String clientSecret = clientSecretForProduct(product, AuthMode.oidc);
      String audience = audienceForProduct(product, AuthMode.oidc);
      jwtConfig.addProduct(product, new JwtCredential(clientId, clientSecret, audience, issuer));
      IdentityConfiguration identityCfg =
          new IdentityConfiguration(
              baseUrlForProduct(Product.IDENTITY, AuthMode.oidc),
              issuer,
              issuerBackendUrl,
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
    if (enabledForProduct(product, AuthMode.saas)) {
      LOG.debug("{} is enabled", product);
      jwtConfig.addProduct(
          product,
          new JwtCredential(
              clientIdForProduct(product, AuthMode.saas),
              clientSecretForProduct(product, AuthMode.saas),
              audienceForProduct(product, AuthMode.saas),
              globalIssuer()));
    } else {
      LOG.debug("{} is disabled", product);
    }
  }

  private String globalIssuer() {
    return getGlobalAuthProperty("issuer", GlobalAuthProperties::getIssuer);
  }

  private String globalIssuerBackendUrl() {
    return getGlobalAuthProperty("issuer backend url", GlobalAuthProperties::getIssuerBackendUrl);
  }

  private Type globalOidcType() {
    return getGlobalAuthProperty("oidc type", GlobalAuthProperties::getOidcType);
  }

  private <T> T getGlobalAuthProperty(
      String propertyName, Function<GlobalAuthProperties, T> getter) {
    return ofNullable(camundaClientProperties.getAuth())
        .map(getter)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Could not detect required auth property " + propertyName));
  }

  private Boolean enabledForProduct(Product product, AuthMode authMode) {
    return getApiProperty("enabled", product, ApiProperties::getEnabled, authMode);
  }

  private String baseUrlForProduct(Product product, AuthMode authMode) {
    return getApiProperty("base url", product, ApiProperties::getBaseUrl, authMode);
  }

  private String usernameForProduct(Product product) {
    return getAuthProperty("username", product, AuthProperties::getUsername, AuthMode.simple);
  }

  private String passwordForProduct(Product product) {
    return getAuthProperty("password", product, AuthProperties::getPassword, AuthMode.simple);
  }

  private String clientIdForProduct(Product product, AuthMode authMode) {
    return getAuthProperty("client id", product, AuthProperties::getClientId, authMode);
  }

  private String clientSecretForProduct(Product product, AuthMode authMode) {
    return getAuthProperty("client secret", product, AuthProperties::getClientSecret, authMode);
  }

  private String audienceForProduct(Product product, AuthMode authMode) {
    return getApiProperty("audience", product, ApiProperties::getAudience, authMode);
  }

  private <T> T getApiProperty(
      String propertyName, Product product, Function<ApiProperties, T> getter, AuthMode authMode) {
    return getApiProperty(
        product + " " + propertyName,
        getter,
        apiPropertiesForProduct(product),
        defaultApiPropertiesForProductAndMode(product,authMode));
  }

  private ApiPropertiesSupplier defaultApiPropertiesForProductAndMode(Product product, AuthMode authMode) {
    return apiPropertiesForProduct(CamundaClientProperties.DEFAULT_CLIENT_PROPERTIES.get(authMode),product);
  }

  private <T> T getAuthProperty(
      String propertyName, Product product, Function<AuthProperties, T> getter, AuthMode authMode) {
    return getAuthProperty(
        product + " " + propertyName,
        getter,
        authPropertiesForProduct(product),
        camundaClientProperties::getAuth,
        defaultAuthPropertiesForProductAndMode(product,authMode));
  }

  private AuthPropertiesSupplier defaultAuthPropertiesForProductAndMode(Product product,AuthMode authMode) {
    return authPropertiesForProduct(CamundaClientProperties.DEFAULT_CLIENT_PROPERTIES.get(authMode),product);
  }

  private ApiPropertiesSupplier apiPropertiesForProduct(Product product) {
    return apiPropertiesForProduct(camundaClientProperties,product);
  }

  private ApiPropertiesSupplier apiPropertiesForProduct(CamundaClientProperties properties, Product product){
    switch (product) {
    case OPERATE -> {
      return properties::getOperate;
    }
    case TASKLIST -> {
      return properties::getTasklist;
    }
    default -> {
      throw new IllegalStateException(
        "Could not detect auth properties supplier for product " + product);
    }
    }
  }

  private AuthPropertiesSupplier authPropertiesForProduct(Product product) {
    return authPropertiesForProduct(camundaClientProperties,product);
  }

  private AuthPropertiesSupplier authPropertiesForProduct(CamundaClientProperties properties,Product product) {
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
