package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.exception.OperateException;

@JsonInclude(Include.NON_NULL)
public class ProcessDefinitionFilter implements Filter {
    private String name;
    private Long version;
    private String bpmnProcessId;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    public String getBpmnProcessId() {
        return bpmnProcessId;
    }
    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
    }

    public static class Builder {
        private String name;
        private Long version;
        private String bpmnProcessId;

        public Builder() {
            super();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder bpmnProcessId(String bpmnProcessId) {
            this.bpmnProcessId = bpmnProcessId;
            return this;
        }

        public ProcessDefinitionFilter build() throws OperateException {
            ProcessDefinitionFilter processDefinitionFilter = new ProcessDefinitionFilter();
            processDefinitionFilter.name = name;
            processDefinitionFilter.version = version;
            processDefinitionFilter.bpmnProcessId = bpmnProcessId;
            return processDefinitionFilter;
        }
    }
}
