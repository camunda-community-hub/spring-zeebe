package io.camunda.operate.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IncidentFilter implements Filter {
    private Long processDefinitionKey;
    private Long processInstanceKey;
    private String type;
    private String message;
    private DateFilter creationTime;
    private String state;
    public Long getProcessDefinitionKey() {
        return processDefinitionKey;
    }
    public void setProcessDefinitionKey(Long processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }
    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }
    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public DateFilter getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(DateFilter creationTime) {
        this.creationTime = creationTime;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public static class Builder {
        private Long processDefinitionKey;
        private Long processInstanceKey;
        private String type;
        private String message;
        private DateFilter creationTime;
        private String state;
        public Builder processDefinitionKey(Long processDefinitionKey) {
            this.processDefinitionKey = processDefinitionKey;
            return this;
        }
        public Builder processInstanceKey(Long processInstanceKey) {
            this.processInstanceKey = processInstanceKey;
            return this;
        }
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder creationTime(DateFilter creationTime) {
            this.creationTime = creationTime;
            return this;
        }
        public Builder state(String state) {
            this.state = state;
            return this;
        }
        public IncidentFilter build() {
            IncidentFilter incidentFilter = new IncidentFilter();
            incidentFilter.processDefinitionKey = processDefinitionKey;
            incidentFilter.processInstanceKey = processInstanceKey;
            incidentFilter.type = type;
            incidentFilter.message = message;
            incidentFilter.creationTime = creationTime;
            incidentFilter.state = state;
            return incidentFilter;
        }
    }
}
