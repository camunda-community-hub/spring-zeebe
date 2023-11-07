package io.camunda.operate.search;

public class Sort {

    private String field;

    private SortOrder order;

    public Sort() {
        super();
    }

    public Sort(String field, SortOrder order) {
        super();
        this.field = field;
        this.order = order;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

}
