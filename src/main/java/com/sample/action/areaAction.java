package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.*;
import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.regex.Pattern;

public class areaAction extends ActionSupport{

    private int page, limit, countyId, townId;
    private String countyName, ids, searchInfo, getVersion, countrysiteNo, site_group_no;
    private int countyNo, version;
    private JSONObject jsonObject = null;
    private JdbcUtils jdbcUtils = null;
    private County county = null;
    private Town town = null;
    private Countrysite cst = null;
    private Site_group sg = null;
    public String getCounties() {
        int isEmpty = StringUtil.isEmpty(searchInfo) ? 0 : 1;
        switch (isEmpty) {
            case 0 : county = new County(); break;
            case 1 :
                boolean flag = Pattern.matches("^[\\d]+$", searchInfo.trim());
                if(flag)
                    county = new County(Integer.parseInt(searchInfo.trim()));
                 else
                    county = new County(searchInfo.trim());
                break;
        }
        jsonObject = new JSONObject();
        jdbcUtils = county.getCounties(new PageBean(page == 0 ? 1 : page, limit == 0 ? 1000 : limit), county);
        Object[] ob = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("data", (JSONArray) ob[0]);
        jsonObject.put("msg", "");
        jsonObject.put("code", 0);
        jsonObject.put("count", ob[1]);
        return SUCCESS;
    }
    public String getTowns() {
        int isEmpty = StringUtil.isEmpty(searchInfo) ? 0 : 1;
        jsonObject = new JSONObject();
        switch (isEmpty) {
            case 0 : town = new Town(); break;
            case 1 :
                boolean flag = Pattern.matches("^[\\d]+$", searchInfo.trim());
                if(flag)
                    town = new Town(Integer.parseInt(searchInfo.trim()));
                else
                    town = new Town(searchInfo.trim());
                break;
        }
        if(countyId != 0) town.setCountyNo(countyId);
        jdbcUtils = town.getTowns(new PageBean(page == 0 ? 1 : page, limit == 0 ? 1000 : limit), town, getVersion);
        Object[] ob = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("data", (JSONArray) ob[0]);
        jsonObject.put("msg", "");
        jsonObject.put("code", 0);
        jsonObject.put("count", ob[1]);
        return SUCCESS;
    }

    public String getCsites() {
        int isEmpty = StringUtil.isEmpty(searchInfo) ? 0 : 1;
        switch (isEmpty) {
            case 0 : cst = new Countrysite(); break;
            case 1 :
                cst = new Countrysite(searchInfo.trim());
                break;
        }
        if(townId != 0) cst.setTownNo(townId);
        jsonObject = new JSONObject();
        jdbcUtils = cst.getCsites(new PageBean(page == 0 ? 1 : page, limit == 0 ? 1000 : limit), cst, getVersion);
        Object[] ob = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("data", (JSONArray) ob[0]);
        jsonObject.put("msg", "");
        jsonObject.put("code", 0);
        jsonObject.put("count", ob[1]);
        return SUCCESS;
    }
    public String getSgs() {
        int isEmpty = StringUtil.isEmpty(searchInfo) ? 0 : 1;
        switch (isEmpty) {
            case 0 : sg = new Site_group(); break;
            case 1 :
                sg = new Site_group(searchInfo.trim());
                break;
        }
        jsonObject = new JSONObject();
        jdbcUtils = sg.getSgs(new PageBean(page, limit), sg, countrysiteNo);
        Object[] ob = formatRsToJson.formatRsToJa(jdbcUtils);
        jsonObject.put("data", (JSONArray) ob[0]);
        jsonObject.put("msg", "");
        jsonObject.put("code", 0);
        jsonObject.put("count", ob[1]);
        return SUCCESS;
    }

    public String delCounty() {
        jsonObject = new JSONObject();
        county = new County();
        Town town = new Town();
        String[] strIds = ids.split(",");
        for(String id : strIds){
            String townName = town.isHaveCounty(Integer.parseInt(id));
            if(townName != null) {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "此县下级存在" + townName + "，不可删除");
                return SUCCESS;
            }
        }
        int flag = county.delCounty(ids);
        if(flag > 0) jsonObject.put("success", true);
        else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除失败");
        }
        return SUCCESS;
    }

    public String delTown() {
        jsonObject = new JSONObject();
        town = new Town();
        Countrysite countrysite = new Countrysite();
        String[] strIds = ids.split(",");
        for(String id : strIds){
            String countryisteName = countrysite.isHaveCountrysite(Integer.parseInt(id));
            if(countryisteName != null) {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "此镇下级存在" + countryisteName + "，不可删除");
                return SUCCESS;
            }
        }
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < strIds.length; i++) {
            if(i == (strIds.length - 1))
                buffer.append(strIds[i]);
            else
                buffer.append(strIds[i] + ",");
        }
        int flag = town.delTown(buffer.toString());
        if(flag > 0) jsonObject.put("success", true);
        else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除失败");
        }
        return SUCCESS;
    }

    public String saveCounty() {
        jsonObject = new JSONObject();
        County c = new County();
        int n = c.saveCounty(county, version);
        switch (n) {
            case 0 : jsonObject.put("success", false); jsonObject.put("errMsg", "保存失败"); break;
            case 1 : jsonObject.put("success", true); break;
        }
        return SUCCESS;
    }
    public String saveTown() {
        jsonObject = new JSONObject();
        Town t = new Town();
        int n = t.saveTown(town, version);
        switch (n) {
            case 0 : jsonObject.put("success", false); jsonObject.put("errMsg", "保存失败"); break;
            case 1 : jsonObject.put("success", true); break;
        }
        return SUCCESS;
    }

    public String saveCsite() {
        jsonObject = new JSONObject();
        Countrysite cs = new Countrysite();
        int n = cs.saveCsite(cst, version);
        switch (n) {
            case 0 : jsonObject.put("success", false); jsonObject.put("errMsg", "保存失败"); break;
            case 1 : jsonObject.put("success", true); break;
        }
        return SUCCESS;
    }

    public String saveSg() {
        jsonObject = new JSONObject();
        Site_group site_group = new Site_group();
        int n = sg.saveSg(sg, countrysiteNo, version);
        switch (n) {
            case 0 : jsonObject.put("success", false); jsonObject.put("errMsg", "保存失败"); break;
            case 1 : jsonObject.put("success", true); break;
        }
        return SUCCESS;
    }

    public String delCsite() {
        jsonObject = new JSONObject();
        cst = new Countrysite();
        Site_group sp = new Site_group();
        String[] strIds = ids.split(",");
        for(String id : strIds){
            String spName = sp.isHaveCsite(id);
            if(spName != null) {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "此村下级存在" + spName + "，不可删除");
                return SUCCESS;
            }
        }
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < strIds.length; i++) {
            if(i == (strIds.length - 1))
                buffer.append(strIds[i]);
            else
                buffer.append(strIds[i] + ",");
        }
        int flag = cst.delCsite(buffer.toString());
        if(flag > 0) jsonObject.put("success", true);
        else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除失败");
        }
        return SUCCESS;
    }

    public String delSg() {
        jsonObject = new JSONObject();
        sg = new Site_group();
         Family fm = new Family();
        String[] strIds = ids.split(",");
        for(String id : strIds){
            boolean flag = fm.isHaveFm(id);
            if(flag) {
                jsonObject.put("success", false);
                jsonObject.put("errMsg", "此组下级存在家庭，不可删除");
                return SUCCESS;
            }
        }
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < strIds.length; i++) {
            if(i == (strIds.length - 1))
                buffer.append(strIds[i]);
            else
                buffer.append(strIds[i] + ",");
        }
        int flag = sg.delSg(buffer.toString());
        if(flag > 0) jsonObject.put("success", true);
        else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "删除失败");
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

    public String getCountyName() {
        return countyName;
    }

    public int getCountyNo() {
        return countyNo;
    }

    public void setCountyNo(int countyNo) {
        this.countyNo = countyNo;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(String searchInfo) {
        this.searchInfo = searchInfo;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public String getGetVersion() {
        return getVersion;
    }

    public void setGetVersion(String getVersion) {
        this.getVersion = getVersion;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public Countrysite getCst() {
        return cst;
    }

    public void setCst(Countrysite cst) {
        this.cst = cst;
    }

    public Site_group getSg() {
        return sg;
    }

    public void setSg(Site_group sg) {
        this.sg = sg;
    }

    public int getTownId() {
        return townId;
    }

    public void setTownId(int townId) {
        this.townId = townId;
    }

    public String getCountrysiteNo() {
        return countrysiteNo;
    }

    public void setCountrysiteNo(String countrysiteNo) {
        this.countrysiteNo = countrysiteNo;
    }

    public String getSite_group_no() {
        return site_group_no;
    }

    public void setSite_group_no(String site_group_no) {
        this.site_group_no = site_group_no;
    }
}
