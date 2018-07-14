package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.Role;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RoleAction extends ActionSupport{
    private Role role = null;
    private JSONObject jb = null;
    private int version ;
    private String ids;
    public String getAllRoles() {
        role = new Role();
        jb = new JSONObject();
        Object[] ob = formatRsToJson.formatRsToJa(role.getAllRoles());
        jb.put("count", ob[1]);
        jb.put("msg", "");
        jb.put("data", (JSONArray) ob[0]);
        jb.put("code", 0);
        return SUCCESS;
    }

    public String saveRole() {
        jb = new JSONObject();
        int n = role.saveRole(role, version);
        switch (n) {
            case 0 : jb.put("success", false); jb.put("errMsg", "保存失败"); break;
            case 1 : jb.put("success", true); break;
        }
        return SUCCESS;
    }

    public String delRole() {
        jb = new JSONObject();
        String[] strIds = ids.split(",");
        role = new Role();
        for(String id : strIds){
            boolean flag = role.isHaveCascade(Integer.parseInt(id));
            if(flag) {
                jb.put("success", false);
                jb.put("errMsg", "此角色下级存在用户，不可删除");
                return SUCCESS;
            }
        }
        int flag = role.delRole(ids);
        if(flag > 0) jb.put("success", true);
        else {
            jb.put("success", false);
            jb.put("errMsg", "删除失败");
        }
        return SUCCESS;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public JSONObject getJb() {
        return jb;
    }

    public void setJb(JSONObject jb) {
        this.jb = jb;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
