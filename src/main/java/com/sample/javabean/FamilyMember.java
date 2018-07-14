package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class FamilyMember implements java.io.Serializable{
    private int id, sex, gxWithHost;
    private String familyNo, nh_id, name, identity_id, address, phone, joinNh_ticket_id;
    private Date joinNh_time;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public JdbcUtils getAllFbs (PageBean pageBean,String f_host_name,String identity_id) {
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        StringBuffer sql = null;
        if(!StringUtil.isEmpty(identity_id)) {
            sql = new StringBuffer("SELECT cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id as fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' " +
                    "WHEN 1 THEN '父子' when 3 then '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`," +
                    " CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN" +
                    " CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) left join ch_sign_up cp on fb.nh_id = cp.ch_certi_id WHERE f.identity_id = ?");
        } else
            sql = new StringBuffer("SELECT cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id as fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' " +
        "WHEN 1 THEN '父子' when 3 then '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`," +
                " CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN" +
                " CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) left join ch_sign_up cp on fb.nh_id = cp.ch_certi_id ");
        if(!StringUtil.isEmpty(f_host_name))
            sql = new StringBuffer("SELECT cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' \n" +
                    "                    WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                    "                     CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                    "                     CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_certi_id WHERE f.f_host_name = ? ");
        sql.append(" limit ?,?");
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            if(sql.toString().toLowerCase().indexOf("where f.identity_id =") > -1) {
                pstm.setString(1, identity_id);
                pstm.setInt(2, pageBean.getStart());
                pstm.setInt(3, pageBean.getLimit());
            }else{
                switch (count) {
                    case 2 :
                        pstm.setInt(1, pageBean.getStart());
                        pstm.setInt(2, pageBean.getLimit());
                        break;
                    case 3 :
                        pstm.setString(1, f_host_name);
                        pstm.setInt(2, pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit());
                        break;
                }
            }
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return returnArg;
    }
    public JdbcUtils getInfoById (String identity_id) {
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        String sql = "SELECT pc.*,cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻'\n" +
                "                                    WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                "                                     CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                "                                     CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_certi_id\n" +
                "                                     LEFT JOIN patient_certificate pc ON cp.patient_ticket = pc.patient_certi_id WHERE cp.patient_ticket IS NOT NULL\n" +
                "                                     AND fb.identity_id =  ?";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, identity_id);
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return returnArg;
    }
    public int addMember (FamilyMember familyMember) {
        int returnInt = 0;
        jdbcUtils = new JdbcUtils();
        String sql = "insert into family_member (familyNo, name, identity_id, sex, address, phone, gxWithHost)" +
                " values (?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, familyMember.getFamilyNo());
            pstm.setString(2, familyMember.getName());
            pstm.setString(3, familyMember.getIdentity_id());
            pstm.setInt(4, familyMember.getSex());
            pstm.setString(5, familyMember.getAddress());
            pstm.setString(6, familyMember.getPhone());
            pstm.setInt(7, familyMember.getGxWithHost());
            returnInt = pstm.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
            returnInt = 0;
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public boolean isDulpicateId (String identity_id) {
        jdbcUtils = new JdbcUtils();
        boolean flag = false;
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select count(*) as no from family_member where identity_id = ?");
            pstm.setString(1, identity_id);
            ResultSet rs = pstm.executeQuery();
            int count = 0;
            if(rs.next())
                count = rs.getInt("no");
            if(count > 0) flag = true;
            else flag = false;
        }catch (Exception e ){
            flag = false;
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
    public int delFb (String identity_id) {
        int returnInt = 0;
        jdbcUtils = new JdbcUtils();
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from family_member where identity_id in ('" + identity_id + "')");
            returnInt = pstm.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
            returnInt = 0;
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public Object[] isSignUped (String identity_id) {
        jdbcUtils = new JdbcUtils();
        Object[] o = new Object[2];
        boolean flag = false;
        String sql = "SELECT cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' \n" +
                "                    WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                "                     CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                "                     CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_ticket_id WHERE fb.identity_id = ?";
        String getName = "select name from family_member where identity_id = ?";
        String name = "";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, identity_id);
            ResultSet rs = pstm.executeQuery();
            int count = 0;
            if(rs.next()) {
                if (!StringUtil.isEmpty(rs.getString("nh_id"))) {
                    flag = true;
                    pstm = conn.prepareStatement(getName);
                    pstm.setString(1, identity_id);
                    rs = pstm.executeQuery();
                    if(rs.next()) name = rs.getString("name");
                }
            }
            o[0] = flag;
            o[1] = name;
        }catch (Exception e ){
            flag = false;
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return o;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getGxWithHost() {
        return gxWithHost;
    }

    public void setGxWithHost(int gxWithHost) {
        this.gxWithHost = gxWithHost;
    }

    public String getFamilyNo() {
        return familyNo;
    }

    public void setFamilyNo(String familyNo) {
        this.familyNo = familyNo;
    }

    public String getNh_id() {
        return nh_id;
    }

    public void setNh_id(String nh_id) {
        this.nh_id = nh_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJoinNh_ticket_id() {
        return joinNh_ticket_id;
    }

    public void setJoinNh_ticket_id(String joinNh_ticket_id) {
        this.joinNh_ticket_id = joinNh_ticket_id;
    }

    public Date getJoinNh_time() {
        return joinNh_time;
    }

    public void setJoinNh_time(Date joinNh_time) {
        this.joinNh_time = joinNh_time;
    }

    public FamilyMember() {
    }
}
