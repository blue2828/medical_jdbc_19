package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.PageBean;
import com.sample.javabean.patient_ticket;
import com.sample.util.JdbcUtils;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONObject;

public class patientTicketAction extends ActionSupport {
    private int page, limit, version, updateType;
    private JSONObject jsonObject ;
    private patient_ticket ticket;
    private String joinerName, identity_id, nh_no;
    public String getAllInfo () {
        jsonObject = new JSONObject();
        ticket = new patient_ticket();
        JdbcUtils jdbcUtils = ticket.getAllInfo(new PageBean(page == 0 ? 1 : page, limit == 0 ? 1000 : limit), joinerName);
        Object[] o = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", o[0]);
        return SUCCESS;
    }

    public String saveTicket () {
        jsonObject = new JSONObject();
        if(updateType == 0){
            boolean isHave = ticket.isDulpicate(nh_no);
            if(isHave) {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "已存在相同的慢性病证");
                return SUCCESS;
            }
        }
        int flag = ticket.saveTicket(ticket, nh_no, updateType);
        switch (flag) {
            case 0 :
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "添加失败");
                break;
            case 1:
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "添加失败,请联系管理员");
            case 2 :
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public patient_ticket getTicket() {
        return ticket;
    }

    public void setTicket(patient_ticket ticket) {
        this.ticket = ticket;
    }

    public String getJoinerName() {
        return joinerName;
    }

    public void setJoinerName(String joinerName) {
        this.joinerName = joinerName;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getNh_no() {
        return nh_no;
    }

    public void setNh_no(String nh_no) {
        this.nh_no = nh_no;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }
}
