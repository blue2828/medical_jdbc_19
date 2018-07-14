package com.sample.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sample.javabean.PageBean;
import com.sample.javabean.User;
import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.UUID;
public class UserAction extends ActionSupport {
    private int page;
    private int limit, updateType;
    private String userid, pwd, email, fullname, code, ids;
    private User user;
    private File file;
    private String fileFileName, fileContentType;
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid  =  userid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd  =  pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email  =  email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname  =  fullname;
    }

    public int getDel_id() {
        return del_id;
    }

    public void setDel_id(int del_id) {
        this.del_id  =  del_id;
    }

    private int del_id;

    public int getOpFlag() {
        return opFlag;
    }

    public void setOpFlag(int opFlag) {
        this.opFlag  =  opFlag;
    }

    private int opFlag;
    private JSONObject jb = null;

    public JSONObject getJb() {
        return jb;
    }

    public void setJb(JSONObject jb) {
        this.jb  =  jb;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page  =  page;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit  =  limit;
    }
    public String checkOrInputUser(){
        jb = new JSONObject();
        User u = new User();
        Map<String, Object> session = ServletActionContext.getContext().getSession();
        String imgCode = (String) session.get("imageCode");
        if(code.equalsIgnoreCase(imgCode)) {
            boolean flag = u.checkUser(user);
            if(flag){
                jb.put("success", true);
                session.put("currentUser", user);
            }
            else {
                jb.put("success", false);
                jb.put("errMsg", "账号或密码错误");
            }
        }else {
            jb.put("success", false);
            jb.put("errMsg", "验证码有误");
        }
        return SUCCESS;
    }
    public String getAllUsers(){
        jb = new JSONObject();
        PageBean pageBean = new PageBean(page, limit);
        user = new User();
        JdbcUtils jd = user.getAllUsers(pageBean);
        Object[] ob = formatRsToJson.formatRsToJa(jd);
        jb.put("code", 0);
        jb.put("count", ob[1]);
        jb.put("msg", "");
        jb.put("data", (JSONArray) ob[0]);
        return SUCCESS;
    }

    public String getCurrentUser(){
        jb = new JSONObject();
        User currentUser = (User)ServletActionContext.getContext().getSession().get("currentUser");
        JdbcUtils jdbcUtils = new User().getCurrentUser(currentUser);
        JSONArray array = (JSONArray)formatRsToJson.formatRsToJa(jdbcUtils)[0];
        jb.put("data", array);
        return SUCCESS;
    }

    public String logout(){
        return "sessionTimeOut";
    }

    public String getImageHeader(){
        User currentUser = (User) ActionContext.getContext().getSession().get( "currentUser" );
        String filePath = "";
        String imgPath = "";
        User userUtil = new User();
        imgPath = userUtil.refreshImgHeader(currentUser);
        if ( !StringUtil.isEmpty(imgPath) ) {
            filePath = imgPath;
        } else filePath = ServletActionContext.getServletContext().getRealPath(File.separator) + "/images/imDf.jpg";
        File upfile = new File(filePath);
        BufferedInputStream inputStream = null;
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("image/jpeg");
        OutputStream writer = null;
        try {
            writer = response.getOutputStream();
            if (upfile != null && upfile.exists()) {
                inputStream = new BufferedInputStream(new FileInputStream(upfile));
            } else {
                inputStream = new BufferedInputStream(new FileInputStream(ServletActionContext.getServletContext().getRealPath(File.separator) + "/images/imgDf.jpg"));
                System.out.println("磁盘文件不存在或丢失");
            }
            int temp = 0;
            byte[] buffer = new byte[1024];
            while ((temp = inputStream.read(buffer)) != -1) {
                writer.write(buffer, 0, temp);
            }
            writer.flush();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SUCCESS;
    }

    public String updateUserInfo () {
        int n = user.updateUserInfo(user, updateType);
        jb = new JSONObject();
        if(n > 0)
            jb.put("success", true);
        else {
            jb.put("success", false);
            jb.put("errMsg", "修改失败");
        }
        return SUCCESS;
    }

    public String updateImg() throws Exception {
        jb = new JSONObject();
        if (file == null) {
            jb.put("success", false);
            jb.put("errMsg", "当前没有上传的头像，请选择后再重试");
        } else {
            if (file.length() > 1024 * 1024 * 10) {
                jb.put("success", false);
                jb.put("errMsg", "头像文件大小超过10mb");
                return SUCCESS;
            }
            User currentUser = (User) ActionContext.getContext().getSession().get("currentUser");
            String imgHeader = currentUser.refreshImgHeader(currentUser);
            BufferedInputStream input = null;
            BufferedOutputStream out = null;
            String savePath = "D:/fileUpload";
            String fileSaveName = this.mkSaveFileName(fileFileName, savePath);
            try {
                input = new BufferedInputStream(new FileInputStream(file));
                out = new BufferedOutputStream(new FileOutputStream(fileSaveName));
                byte[] buffer = new byte[1024];
                int temp = 0;
                while ((temp = input.read(buffer)) != -1) {
                    out.write(buffer, 0, temp);
                }
                int succFlag = -1;
                currentUser.setImageHeader(fileSaveName);
                succFlag = currentUser.updateImg(currentUser);
                if (succFlag > 0) {
                    jb.put("success", true);
                    if (!StringUtil.isEmpty(imgHeader)) {
                        File oldfile = new File(imgHeader);
                        if (oldfile != null) {
                            oldfile.delete();
                        }
                    }
                } else {
                    jb.put("success", false);
                    jb.put("errMsg", "服务器开了小差，请重试");
                }
            } catch (IOException e) {
                e.printStackTrace();
                jb.put("success", false);
                jb.put("errMsg", "服务器开了小差，请重试");
            } finally {
                input.close();
                out.close();
            }
        }
        return SUCCESS;
    }

    public String saveUser() {
        jb = new JSONObject();
        int n = user.saveUser(user, updateType);
        switch (n) {
            case 0 : jb.put("success", false); jb.put("errMsg", "保存失败"); break;
            case 1 : jb.put("success", true); break;
        }
        return SUCCESS;
    }

    public String delUser () {
        jb = new JSONObject();
        String[] strIds = ids.split(",");
        user = new User();
        int flag = user.delUser(ids);
        if(flag > 0) jb.put("success", true);
        else {
            jb.put("success", false);
            jb.put("errMsg", "删除失败");
        }
        return SUCCESS;
    }

    public String mkSaveFileName(String fileName, String savePath) {
        return savePath + File.separator + UUID.randomUUID().toString().replaceAll("-", "_") + "_" + fileName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
