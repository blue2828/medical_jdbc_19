package com.sample.action;

import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.SignUp;
import net.sf.json.JSONObject;

public class signUpAction extends ActionSupport {
    private String familyNo, identity_id;
    private JSONObject jsonObject;
    public String signUp () {
        jsonObject = new JSONObject();
        SignUp signUp = new SignUp();
        boolean flag = signUp.isDulpicate(identity_id);
        if(flag) {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "已经参合过,无须再参合");
            return SUCCESS;
        }
        int count = signUp.signUp(familyNo, identity_id);
        if(count >= 2)
            jsonObject.put("success", true);
        else {
            jsonObject.put("success", false);
            jsonObject.put("errMsg", "参合失败, 请联系管理员");
        }
        return SUCCESS;
    }
    public String checkSignUp () {
        SignUp signUp = new SignUp();
        jsonObject = new JSONObject();
        boolean flag = signUp.isDulpicate(identity_id);
        if(flag)
            jsonObject.put("success", true);
        else
            jsonObject.put("success", false);
        return SUCCESS;
    }

    public String getFamilyNo() {
        return familyNo;
    }

    public void setFamilyNo(String familyNo) {
        this.familyNo = familyNo;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
