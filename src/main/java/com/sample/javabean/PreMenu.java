package com.sample.javabean;

public class PreMenu implements java.io.Serializable {
    private String name;
    private int order;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public PreMenu() {
    }
}
