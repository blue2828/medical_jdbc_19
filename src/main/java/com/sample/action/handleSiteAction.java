package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.Handle_site;
import com.sample.javabean.PageBean;
import com.sample.util.JdbcUtils;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class handleSiteAction extends ActionSupport {
    private int page = 1, limit = 1000, version;
    private Handle_site handle_site = null;
    private JSONObject jsonObject = null;
    public String getAllSites () {
        jsonObject = new JSONObject();
        if(handle_site == null)
            handle_site = new Handle_site();
        JdbcUtils jdbcUtils = handle_site.getAllSites(new PageBean(page, limit), handle_site);
        Object[] o = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", (JSONArray) o[0]);
        return SUCCESS;
    }
    public String saveSite() {
        jsonObject = new JSONObject();
        if (version == 0) {
            int flag = handle_site.isDupicate(handle_site);
            boolean isHave = false;
            switch (flag) {
                case 0 :
                    break;
                case 1 :
                    isHave = true;
                    jsonObject.put("success", false);
                    jsonObject.put("errMsg", "一个县只有一个县级经办点");
                    break;
                case 2 :
                    isHave = true;
                    jsonObject.put("success", false);
                    jsonObject.put("errMsg", "一个乡镇只有一个镇级经办点");
                    break;
            }
            if (isHave) return SUCCESS;
        }
        int n = handle_site.saveSite(handle_site, version);
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Handle_site getHandle_site() {
        return handle_site;
    }

    public void setHandle_site(Handle_site handle_site) {
        this.handle_site = handle_site;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
