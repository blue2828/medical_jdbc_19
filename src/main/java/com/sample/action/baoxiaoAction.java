package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.baoxiao;
import com.sample.util.DateUtil;
import com.sample.util.JdbcUtils;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONObject;

public class baoxiaoAction extends ActionSupport {
    private int page, limit;
    private JSONObject jsonObject = null;
    private String identity_id;
    private double money;
    public String doBaoxiao () {
        jsonObject = new JSONObject();
        baoxiao b = new baoxiao();
        Object[] ob = b.getYearLine();
        int top_line = (int) ob[0] ;
        double proportion = (double) ob[1];
        double used = b.getAlreadyUse(identity_id);
        double preBx = money * proportion;
        double finalM = (top_line - preBx) >= preBx ? preBx : (top_line - used);
        b = new baoxiao(identity_id, finalM);
        int count =b.doBx(b, DateUtil.getCurrentTime());
        if(count > 0) {
            jsonObject.put("success", true);
            jsonObject.put("finalM", finalM);
        }else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "报销未成功");
        }
        return SUCCESS;
    }

    public String viewResult () {
        jsonObject = new JSONObject();
        baoxiao b = new baoxiao();
        JdbcUtils jdbcUtils = b.viewResult(identity_id);
        Object[] ob = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("data", ob[0]);
        jsonObject.put("count", ob[1]);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
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

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
