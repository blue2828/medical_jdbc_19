package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.Menu;
import com.sample.javabean.PageBean;
import com.sample.javabean.PreMenu;
import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SystemAction extends ActionSupport {
    private int page = 1, limit = 1000, version;
    private String role_id1, role_id2, role_id3, role_id4, ids;
    private Menu menu = null;
    private PreMenu preMenu = null;
    private JSONObject jsonObject = null;
    public String getAllMenus () {
        jsonObject = new JSONObject();
        menu = new Menu();
        JdbcUtils jd = menu.getAllMenus(new PageBean(page, limit));
        Object[] o = this.formatJson(jd);
        jsonObject.put("code", 0);
        jsonObject.put("msg", "");
        jsonObject.put("count", o[1]);
        jsonObject.put("data", (JSONArray) o[0]);
        return SUCCESS;
    }

    public String saveMenu () {
        jsonObject = new JSONObject();
        String menu_name = StringUtil.formatMenu(menu.getMenu_no());
        String str[] = {
                !StringUtil.isEmpty(role_id1) ? role_id1.equals("on") ? "1" : "" : "",
                !StringUtil.isEmpty(role_id2) ? role_id2.equals("on") ? "2" : "" : "",
                !StringUtil.isEmpty(role_id3) ? role_id3.equals("on") ? "3" : "" : "",
                !StringUtil.isEmpty(role_id4) ? role_id4.equals("on") ? "4" : "" : ""
        };
        int n = menu.saveMenu(menu, menu_name, version, str);
        switch (n) {
            case -1 :
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "系统已存在此菜单，无需重新添加");
                break;
            case 0 :
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "系统内部错误");
                break;
            default:
                jsonObject.put("success", true);
        }
        return SUCCESS;
    }

    public String delMenu () {
        jsonObject = new JSONObject();
        menu = new Menu();
        int n = menu.delMenu(ids);
        switch (n) {
            case 0 :
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "系统内部错误");
                break;
            default :
                jsonObject.put("success", true);
        }
        return SUCCESS;
    }

    public Object[] formatJson (JdbcUtils jdbcUtils) {
        ResultSet rs = jdbcUtils.getRs();
        JSONArray array = new JSONArray();
        Object[] o = new Object[2];
        try {
            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            int rows = 0;
            while (rs.next()) {
                rows ++;
                JSONObject temp = new JSONObject();
                for(int i = 1; i <= count; i ++) {
                    if(md.getColumnLabel(i).equals("role_id")) {
                        String role_ids = (String) rs.getObject(i);
                        String ids[] = role_ids.split(",");
                        String role_name = StringUtil.formatRole(ids);
                        temp.put("role_group", role_name);
                    }
                    temp.put(md.getColumnLabel(i), rs.getObject(i));
                }
                array.add(temp);
            }
            o[0] = array;
            o[1] = rows;
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(jdbcUtils.getConn(), jdbcUtils.getPs(), jdbcUtils.getRs());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return o;
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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public PreMenu getPreMenu() {
        return preMenu;
    }

    public void setPreMenu(PreMenu preMenu) {
        this.preMenu = preMenu;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRole_id1() {
        return role_id1;
    }

    public void setRole_id1(String role_id1) {
        this.role_id1 = role_id1;
    }

    public String getRole_id2() {
        return role_id2;
    }

    public void setRole_id2(String role_id2) {
        this.role_id2 = role_id2;
    }

    public String getRole_id3() {
        return role_id3;
    }

    public void setRole_id3(String role_id3) {
        this.role_id3 = role_id3;
    }

    public String getRole_id4() {
        return role_id4;
    }

    public void setRole_id4(String role_id4) {
        this.role_id4 = role_id4;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
