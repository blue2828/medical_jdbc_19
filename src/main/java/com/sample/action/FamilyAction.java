package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.Family;
import com.sample.javabean.PageBean;
import com.sample.util.JdbcUtils;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONObject;

public class FamilyAction extends ActionSupport {
    private int page, limit, version;
    private JSONObject jsonObject = null;
    private Family family = null;
    public String getAllFms () {
        jsonObject = new JSONObject();
        family = new Family();
        JdbcUtils jdbcUtils = family.getAllFms(new PageBean(page, limit));
        Object[] o = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", o[0]);
        return SUCCESS;
    }

    public String saveFm () {
        jsonObject = new JSONObject();
        boolean flag = family.isDulpicate(family.getIdentity_id());
        if(flag && version == 0) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "一个身份证只有一个家庭档案");
            return SUCCESS;
        }
        int n = family.saveFm(family, version);
        switch (n) {
            case 0 :
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "保存失败");
                break;
             default:
                 jsonObject.put("success", true);
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

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
