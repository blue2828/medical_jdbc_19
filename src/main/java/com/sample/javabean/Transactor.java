package com.sample.javabean;

import com.sample.util.JdbcUtils;
import net.sf.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class Transactor implements java.io.Serializable{
    private int id;
    private String orderId;
    private String name;
    private String pwd;
    private String belong_site;
    JdbcUtils jdbcUtils = null;
    ResultSet rs = null;
    Connection conn = null;
    PreparedStatement ps = null;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getBelong_site() {
        return belong_site;
    }

    public void setBelong_site(String belong_site) {
        this.belong_site = belong_site;
    }

    public Transactor() {
    }
    public Object[] getAll(PageBean pageBean){
        jdbcUtils = new JdbcUtils();
        String sql = "";
        if(pageBean == null)
        sql = " select * from transactor ";
        else sql = "select * from transactor limit ?,?";
        JSONArray list = null;
        JdbcUtils jd = null;
        Object[] ob = null;
        try{
            conn = jdbcUtils.getCon();
            ps = conn.prepareStatement(sql);
            if(sql.indexOf("limit") >= 0){
                ps.setInt(1,pageBean.getStart());
                ps.setInt(2,pageBean.getLimit());
            }
            rs = ps.executeQuery();
            ob = new Object[2];
            ob[0] = new Integer(rs.getMetaData().getColumnCount());
            jd = new JdbcUtils(conn,rs,ps);
            ob[1] = jd;
        }catch (Exception e){
            e.printStackTrace();
            rs = null;
        }
        return ob;
    }

}
