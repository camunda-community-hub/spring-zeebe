package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.exception.OperateException;

@JsonInclude(Include.NON_NULL)
public class ProcessInstanceFilter implements Filter {
    private Long processVersion;
    private String bpmnProcessId;
    private Long parentKey;
    private DateFilter startDate;
    private DateFilter endDate;
    private ProcessInstanceState state;
    private Long processDefinitionKey;

    public Long getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(Long processVersion) {
        this.processVersion = processVersion;
    }

    public String getBpmnProcessId() {
        return bpmnProcessId;
    }

    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
    }

    public Long getParentKey() {
        return parentKey;
    }

    public void setParentKey(Long parentKey) {
        this.parentKey = parentKey;
    }

    public DateFilter getStartDate() {
        return startDate;
    }

    public void setStartDate(DateFilter startDate) {
        this.startDate = startDate;
    }

    public DateFilter getEndDate() {
        return endDate;
    }

    public void setEndDate(DateFilter endDate) {
        this.endDate = endDate;
    }

    public ProcessInstanceState getState() {
        return state;
    }

    public void setState(ProcessInstanceState state) {
        this.state = state;
    }

    public Long getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(Long processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public static class Builder {
        private Long processVersion;
        private String bpmnProcessId;
        private Long parentKey;
        private DateFilter startDate;
        private DateFilter endDate;
        private ProcessInstanceState state;
        private Long processDefinitionKey;

        public Builder() {
            super();
        }

        public Builder processVersion(Long processVersion) {
            this.processVersion = processVersion;
            return this;
        }

        public Builder bpmnProcessId(String bpmnProcessId) {
            this.bpmnProcessId = bpmnProcessId;
            return this;
        }

        public Builder parentKey(Long parentKey) {
            this.parentKey = parentKey;
            return this;
        }

        public Builder startDate(DateFilter startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(DateFilter endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder state(ProcessInstanceState state) {
            this.state = state;
            return this;
        }

        public Builder processDefinitionKey(Long processDefinitionKey) {
            this.processDefinitionKey = processDefinitionKey;
            return this;
        }

        public ProcessInstanceFilter build() throws OperateException {
            ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
            processInstanceFilter.processVersion = processVersion;
            processInstanceFilter.bpmnProcessId = bpmnProcessId;
            processInstanceFilter.parentKey = parentKey;
            processInstanceFilter.startDate = startDate;
            processInstanceFilter.endDate = endDate;
            processInstanceFilter.state = state;
            processInstanceFilter.processDefinitionKey = processDefinitionKey;
            return processInstanceFilter;
        }
    }
}
