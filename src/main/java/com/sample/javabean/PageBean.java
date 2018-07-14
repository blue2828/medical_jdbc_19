package com.sample.javabean;

public class PageBean implements java.io.Serializable{
    private int page = 1; //开始页
    private int limit = 10; //一页限制多少条

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getStart(){
        return (this.page-1) * this.limit;
    }

    public PageBean() {
    }

    public PageBean(int page, int limit) {
        this.page = page;
        this.limit = limit;
    }
}
