package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.FamilyMember;
import com.sample.javabean.PageBean;
import com.sample.util.DateUtil;
import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;

public class fbAction extends ActionSupport {
    private int page, limit;
    private FamilyMember familyMember = null;
    private JSONObject jsonObject = null;
    private String f_host_name, identity_id, ids;
    public String getAllFbs () {
        jsonObject = new JSONObject();
        familyMember = new FamilyMember();
        JdbcUtils jdbcUtils = familyMember.getAllFbs(new PageBean(page == 0 ? 1 : page, limit == 0 ? 1000 : limit), f_host_name, identity_id);
        Object[] o = this.formatAge(jdbcUtils);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", o[0]);
        return SUCCESS;
    }
    public String addFb () {
        jsonObject = new JSONObject();
        boolean isHave = familyMember.isDulpicateId(familyMember.getIdentity_id());
        if(isHave) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "已存在相同的身份证");
            return SUCCESS;
        }
        int flag = familyMember.addMember(familyMember);
        switch (flag) {
            case 0 :
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "添加失败");
                break;
            default:
                jsonObject.put("success", true);
        }
        return SUCCESS;
    }

    public String getOne () {
        jsonObject = new JSONObject();
        familyMember = new FamilyMember();
        JdbcUtils jdbcUtils = familyMember.getInfoById(identity_id);
        Object[] o = formatAge(jdbcUtils);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", o[0]);
        return SUCCESS;
    }

    public String delFb () {
        jsonObject = new JSONObject();
        familyMember = new FamilyMember();
        String[] idArr = ids.split(",");
        Object[] o = new Object[2];
        for(String id : idArr) {
            o = familyMember.isSignUped(id);
            if((boolean) o[0]) {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", o[1] + "已经参合，不可删除");
                return SUCCESS;
            }
        }
        int count = familyMember.delFb(ids);
        if(count > 0)
            jsonObject.put("success", true);
        else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除失败，多次失败请联系管理员");
        }
        return SUCCESS;
    }

    public static Object[] formatAge (JdbcUtils jdbcUtils) {
        ResultSetMetaData md = null;
        int count = -1;
        Object[] ob = new Object[2];
        ResultSet rs = null;
        JSONArray array = new JSONArray();
        int rows = 0;
        try {
            rs = jdbcUtils.getRs();
            md = rs.getMetaData();
            count = md.getColumnCount();
            while(rs.next()) {
                rows ++;
                JSONObject temp = new JSONObject();
                for(int i = 1; i <= count; i++) {
                    if(rs.getObject(i) instanceof Date) {
                        temp.put(md.getColumnLabel(i), DateUtil.formatDateTOStr((Date) rs.getObject(i), "yyyy-MM-dd HH:mm:ss"));
                    }else if(md.getColumnLabel(i).equals("age")) {
                        temp.put("age", StringUtil.formatRsAge((String) rs.getObject(i)));
                    }
                    else {
                        temp.put(md.getColumnLabel(i), rs.getObject(i));
                    }
                }
                array.add(temp);
            }
            ob[0] = array;
            ob[1] = rows;
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (jdbcUtils != null)
                    jdbcUtils.closeCon(jdbcUtils.getCon(), rs, jdbcUtils.getRs());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ob;
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

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getF_host_name() {
        return f_host_name;
    }

    public void setF_host_name(String f_host_name) {
        this.f_host_name = f_host_name;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
