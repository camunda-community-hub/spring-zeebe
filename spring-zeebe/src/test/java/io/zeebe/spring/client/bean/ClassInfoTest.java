package io.zeebe.spring.client.bean;

import io.zeebe.spring.client.annotation.ZeebeDeployment;
import io.zeebe.spring.client.annotation.ZeebeTaskListener;
import org.junit.Test;

import java.beans.Introspector;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassInfoTest {

    @ZeebeDeployment(topicName = "t", classPathResource = "/1.bpmn")
    public static class WithDeploymentAnnotation {

    }

    public static class WithoutDeploymentAnnotation {

    }

    public static class WithTaskListener {

        @ZeebeTaskListener(topicName = "foo", taskType = "bar", lockTime = 100L, lockOwner = "kermit")
        public void handle() {

        }
    }

    @Test
    public void getBeanInfo() throws Exception {
        WithDeploymentAnnotation withDeploymentAnnotation = new WithDeploymentAnnotation();

        ClassInfo beanInfo = beanInfo(withDeploymentAnnotation);

        assertThat(beanInfo.getBean()).isEqualTo(withDeploymentAnnotation);
        assertThat(beanInfo.getBeanName()).isEqualTo("withDeploymentAnnotation");
        assertThat(beanInfo.getTargetClass()).isEqualTo(WithDeploymentAnnotation.class);
    }

    @Test
    public void hasZeebeeDeploymentAnnotation() throws Exception {
        assertThat(beanInfo(new WithDeploymentAnnotation()).hasClassAnnotation(ZeebeDeployment.class)).isTrue();
    }

    @Test
    public void hasNoZeebeeDeploymentAnnotation() throws Exception {
        assertThat(beanInfo(new WithoutDeploymentAnnotation()).hasClassAnnotation(ZeebeDeployment.class)).isFalse();
    }

    @Test
    public void hasTaskListenerMethod() throws Exception {
        assertThat(beanInfo(new WithTaskListener()).hasMethodAnnotation(ZeebeTaskListener.class)).isTrue();
    }

    @Test
    public void hasNotTaskListenerMethod() throws Exception {
        assertThat(beanInfo("normal String").hasMethodAnnotation(ZeebeTaskListener.class)).isFalse();
    }

    private ClassInfo beanInfo(Object bean) {
        return new ClassInfo(
                bean,
                Introspector.decapitalize(bean.getClass().getSimpleName())
        );
    }

}