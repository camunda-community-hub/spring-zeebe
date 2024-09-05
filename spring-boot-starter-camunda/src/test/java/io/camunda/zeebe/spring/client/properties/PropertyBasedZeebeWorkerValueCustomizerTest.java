package io.camunda.zeebe.spring.client.properties;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.bean.ClassInfo;
import io.camunda.zeebe.spring.client.bean.MethodInfo;
import io.camunda.zeebe.spring.client.properties.common.ZeebeClientProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PropertyBasedZeebeWorkerValueCustomizerTest {

  private static MethodInfo methodInfo(Object bean, String beanName, String methodName) {
    try {
      return MethodInfo.builder()
          .classInfo(ClassInfo.builder().beanName(beanName).bean(bean).build())
          .method(
              Arrays.stream(PropertyBasedZeebeWorkerValueCustomizerTest.class.getDeclaredMethods())
                  .filter(m -> m.getName().equals(methodName))
                  .findFirst()
                  .orElseThrow(
                      () -> new IllegalStateException("No method present with name " + methodName)))
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Error while constructing methodInfo for method " + methodName, e);
    }
  }

  private static ZeebeClientConfigurationProperties legacyProperties() {
    ZeebeClientConfigurationProperties properties = new ZeebeClientConfigurationProperties(null);
    properties.applyOverrides();
    return properties;
  }

  private static CamundaClientProperties properties() {
    return new CamundaClientProperties();
  }

  @JobWorker
  void sampleWorker(@Variable String var1, @VariablesAsType ComplexProcessVariable var2) {}

  @JobWorker
  void sampleWorkerWithJsonProperty(@VariablesAsType PropertyAnnotatedClass var2) {}

  @JobWorker
  void activatedJobWorker(@Variable String var1, ActivatedJob activatedJob) {}

  @Test
  void shouldNotAdjustVariableFilterVariablesAsActivatedJobIsInjected_legacy() {
    // given
    ZeebeClientConfigurationProperties properties = legacyProperties();
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(properties, properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setFetchVariables(Arrays.asList("a", "var1", "b"));
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "activatedJobWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).containsExactly("a", "var1", "b");
  }

  @Test
  void shouldNotAdjustVariableFilterVariablesAsActivatedJobIsInjected() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setFetchVariables(Arrays.asList("a", "var1", "b"));
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "activatedJobWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).containsExactly("a", "var1", "b");
  }

  @Test
  void shouldSetDefaultName_legacy() throws NoSuchMethodException {
    // given
    ZeebeClientConfigurationProperties properties = legacyProperties();
    properties.getWorker().setDefaultName("defaultName");
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(properties, properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getName()).isEqualTo("defaultName");
  }

  @Test
  void shouldSetDefaultName() throws NoSuchMethodException {
    // given
    CamundaClientProperties properties = properties();
    ZeebeClientProperties zeebeClientProperties = new ZeebeClientProperties();
    zeebeClientProperties.setDefaults(new ZeebeWorkerValue());
    properties.setZeebe(zeebeClientProperties);
    properties.getZeebe().getDefaults().setName("defaultName");
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties);
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getName()).isEqualTo("defaultName");
  }

  @Test
  void shouldSetGeneratedName_legacy() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getName()).isEqualTo("testBean#sampleWorker");
  }

  @Test
  void shouldSetGeneratedName() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getName()).isEqualTo("testBean#sampleWorker");
  }

  @Test
  void shouldSetDefaultType_legacy() {
    // given
    ZeebeClientConfigurationProperties properties = legacyProperties();
    properties.getWorker().setDefaultType("defaultType");
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(properties, properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getType()).isEqualTo("defaultType");
  }

  @Test
  void shouldSetDefaultType() {
    // given
    CamundaClientProperties properties = properties();
    ZeebeClientProperties zeebeClientProperties = new ZeebeClientProperties();
    zeebeClientProperties.setDefaults(new ZeebeWorkerValue());
    properties.setZeebe(zeebeClientProperties);
    properties.getZeebe().getDefaults().setType("defaultType");
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties);
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getType()).isEqualTo("defaultType");
  }

  @Test
  void shouldSetGeneratedType_legacy() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getType()).isEqualTo("sampleWorker");
  }

  @Test
  void shouldSetGeneratedType() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getType()).isEqualTo("sampleWorker");
  }

  @Test
  void shouldSetVariablesFromVariableAnnotation_legacy() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).contains("var1");
  }

  @Test
  void shouldSetVariablesFromVariableAnnotation() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).contains("var1");
  }

  @Test
  void shouldSetVariablesFromVariablesAsTypeAnnotation_legacy() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).contains("var3", "var4");
  }

  @Test
  void shouldSetVariablesFromVariablesAsTypeAnnotation() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).contains("var3", "var4");
  }

  @Test
  void shouldNotSetNameOfVariablesAsTypeAnnotatedField_legacy() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).doesNotContain("var2");
  }

  @Test
  void shouldNotSetNameOfVariablesAsTypeAnnotatedField() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).doesNotContain("var2");
  }

  @Test
  void shouldApplyOverrides_legacy() {
    // given
    ZeebeClientConfigurationProperties properties = legacyProperties();
    ZeebeWorkerValue override = new ZeebeWorkerValue();
    override.setEnabled(false);
    properties.getWorker().getOverride().put("sampleWorker", override);
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(properties, properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    assertThat(zeebeWorkerValue.getEnabled()).isNull();
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getEnabled()).isFalse();
  }

  @Test
  void shouldApplyOverrides() {
    // given
    CamundaClientProperties properties = properties();
    ZeebeClientProperties zeebeClientProperties = new ZeebeClientProperties();
    ZeebeWorkerValue override = new ZeebeWorkerValue();
    override.setEnabled(false);
    Map<String, ZeebeWorkerValue> overrideMap = new HashMap<>();
    overrideMap.put("sampleWorker", override);
    zeebeClientProperties.setOverride(overrideMap);
    properties.setZeebe(zeebeClientProperties);
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties);
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    assertThat(zeebeWorkerValue.getEnabled()).isNull();
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getEnabled()).isFalse();
  }

  @Test
  void shouldApplyGlobalOverride() {
    // given
    CamundaClientProperties properties = properties();
    ZeebeClientProperties zeebeClientProperties = new ZeebeClientProperties();
    ZeebeWorkerValue override = new ZeebeWorkerValue();
    override.setEnabled(false);
    zeebeClientProperties.setDefaults(override);
    properties.setZeebe(zeebeClientProperties);
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties);
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    assertThat(zeebeWorkerValue.getEnabled()).isNull();
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getEnabled()).isFalse();
  }

  @Test
  void shouldApplyWorkerOverridesOverGlobalOverrides() {
    // given
    CamundaClientProperties properties = properties();
    ZeebeClientProperties zeebeClientProperties = new ZeebeClientProperties();
    ZeebeWorkerValue override = new ZeebeWorkerValue();
    override.setEnabled(false);
    Map<String, ZeebeWorkerValue> overrideMap = new HashMap<>();
    overrideMap.put("sampleWorker", override);
    zeebeClientProperties.setOverride(overrideMap);
    ZeebeWorkerValue globalOverride = new ZeebeWorkerValue();
    globalOverride.setEnabled(true);
    zeebeClientProperties.setDefaults(globalOverride);
    properties.setZeebe(zeebeClientProperties);
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties);
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorker"));
    assertThat(zeebeWorkerValue.getEnabled()).isNull();
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getEnabled()).isFalse();
  }

  @Test
  void shouldApplyPropertyAnnotationOnVariableFiltering() {
    // given
    PropertyBasedZeebeWorkerValueCustomizer customizer =
        new PropertyBasedZeebeWorkerValueCustomizer(legacyProperties(), properties());
    ZeebeWorkerValue zeebeWorkerValue = new ZeebeWorkerValue();
    zeebeWorkerValue.setMethodInfo(methodInfo(this, "testBean", "sampleWorkerWithJsonProperty"));
    // when
    customizer.customize(zeebeWorkerValue);
    // then
    assertThat(zeebeWorkerValue.getFetchVariables()).containsExactly("weird_name");
  }

  private static class ComplexProcessVariable {
    private String var3;
    private String var4;

    public String getVar3() {
      return var3;
    }

    public void setVar3(String var3) {
      this.var3 = var3;
    }

    public String getVar4() {
      return var4;
    }

    public void setVar4(String var4) {
      this.var4 = var4;
    }
  }

  private static class PropertyAnnotatedClass {
    @JsonProperty("weird_name")
    private String value;
  }
}
