package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

public class pageController extends ActionSupport{
    private String pageController;
    private HttpServletRequest request = null;

    public String getPageController() {
        return pageController;
    }

    public void setPageController(String pageController) {
        this.pageController = pageController;
    }

    public String controlPage(){
        return SUCCESS;
    }
}
