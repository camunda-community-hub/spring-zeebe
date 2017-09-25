package io.zeebe.spring.client.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "zeebe.topic=foo",
        "zeebe.isSomething=true",
})
@ContextConfiguration(classes = ZeebeExpressionResolver.class)
public class ZeebeExpressionResolverTest {

    @Autowired
    private ZeebeExpressionResolver resolver;

    @Value("${zeebe.isSomething}")
    private boolean isSomeThing;

    @Value("${zeebe.isSomethingNotSet:true}")
    private boolean isSomethingNotSet;

    @Test
    public void resolve_topic() throws Exception {
        String topic = resolver.resolve("${zeebe.topic}");
        assertThat(topic).isEqualTo("foo");
    }

    @Test
    public void resolve_isSomething() throws Exception {
        assertThat(isSomeThing).isTrue();
    }

    @Test
    public void resolve_default_value_for_isSomethingNotSet() throws Exception {
        assertThat(isSomethingNotSet).isTrue();
    }

    @Test
    public void use_value_if_no_expression() throws Exception {
        String normalString = resolver.resolve("normalString");
        assertThat(normalString).isEqualTo("normalString");
    }
}