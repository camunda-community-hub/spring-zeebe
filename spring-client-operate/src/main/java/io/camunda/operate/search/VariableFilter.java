package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.exception.OperateException;

@JsonInclude(Include.NON_NULL)
public class VariableFilter implements Filter {

    private Long processInstanceKey;
    private String name;
    private String value;
    private Long scopeKey;

    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }

    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getScopeKey() {
        return scopeKey;
    }

    public void setScopeKey(Long scopeKey) {
        this.scopeKey = scopeKey;
    }

    public static class Builder {
        private Long processInstanceKey;
        private String name;
        private String value;
        private Long scopeKey;

        public Builder() {
            super();
        }

        public Builder processInstanceKey(Long processInstanceKey) {
            this.processInstanceKey = processInstanceKey;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder scopeKey(Long scopeKey) {
            this.scopeKey = scopeKey;
            return this;
        }

        public VariableFilter build() throws OperateException {
            VariableFilter variableFilter = new VariableFilter();
            variableFilter.processInstanceKey = processInstanceKey;
            variableFilter.name = name;
            variableFilter.value = value;
            variableFilter.scopeKey = scopeKey;
            return variableFilter;
        }
    }
}
