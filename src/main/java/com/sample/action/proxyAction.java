package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.PageBean;
import com.sample.javabean.patient_proxy;
import com.sample.util.JdbcUtils;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONObject;

public class proxyAction extends ActionSupport {
    private int page, limit, version;
    private JSONObject jsonObject ;
    private patient_proxy proxy;
    public String getAllInfo () {
        jsonObject = new JSONObject();
        proxy = new patient_proxy();
        JdbcUtils jdbcUtils = proxy.getAllInfo(new PageBean(page == 0 ? 1 : page, limit == 0 ? 1000 : limit));
        Object[] o = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", o[0]);
        return SUCCESS;
    }

    public String saveProxy() {
        jsonObject = new JSONObject();
        int n = proxy.saveProxy(proxy, version);
        switch (n) {
            case 0 : jsonObject.put("success", false); jsonObject.put("errMsg", "保存失败"); break;
            case 1 : jsonObject.put("success", true); break;
        }
        return SUCCESS;
    }
    
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

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public patient_proxy getProxy() {
        return proxy;
    }

    public void setProxy(patient_proxy proxy) {
        this.proxy = proxy;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
