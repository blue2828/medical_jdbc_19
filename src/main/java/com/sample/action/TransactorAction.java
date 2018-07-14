package com.sample.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.PageBean;
import com.sample.javabean.Transactor;
import com.sample.util.JdbcUtils;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONObject;

import java.util.Map;

public class TransactorAction extends ActionSupport {
    private JdbcUtils jdbcUtils = null;
    private Transactor transactor = null;
    private JSONObject jb = null;
    private String page;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page  =  page;
    }

    public JSONObject getJb() {
        return jb;
    }

    public void setJb(JSONObject jb) {
        this.jb  =  jb;
    }

    public String getAll(){
        return SUCCESS;
    }
}
