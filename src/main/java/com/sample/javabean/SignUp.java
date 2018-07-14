package com.sample.javabean;

import com.sample.util.DateUtil;
import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class SignUp {
    private int id;
    private Date ch_time;
    private String ch_ticket_id, ch_certi_id;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public int signUp (String fm_no, String identity_id) {
        int returnInt = 0;
        jdbcUtils = new JdbcUtils();
        String upFb = "update family_member set nh_id = ? where identity_id = ?";
        String signUp = "insert into ch_sign_up values(null, ?, ?, ?, null)";
        String count = "select count(*) as count from ch_sign_up";
        String no = "select max(ch_ticket_id) + 1 as num from ch_sign_up";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(count);
            rs = pstm.executeQuery();
            String nextNo = "";
            int co = 0;
            if(rs.next())
                co = rs.getInt("count");
            if(co == 0) nextNo = "1";
            else {
                pstm = conn.prepareStatement(no);
                rs = pstm.executeQuery();
                if(rs.next()) nextNo = rs.getString("num");
            }
            pstm = conn.prepareStatement(signUp);
            pstm.setString(1, DateUtil.getCurrentTime());
            pstm.setString(2, nextNo);
            String cno = String.valueOf(System.currentTimeMillis());
            pstm.setString(3, fm_no + cno.substring(cno.length() - 2));
            returnInt = pstm.executeUpdate();
            pstm = conn.prepareStatement(upFb);
            pstm.setString(1, fm_no + cno.substring(cno.length() - 2));
            pstm.setString(2, identity_id);
            returnInt += pstm.executeUpdate();

        }catch (Exception e) {
            returnInt = 0;
            e.printStackTrace();
        }finally {
          try {
                  if (jdbcUtils != null)
                  jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public boolean isDulpicate (String identity) {
        jdbcUtils = new JdbcUtils();
        boolean flag = false;
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("SELECT cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' \n" +
                    "                    WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                    "                     CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                    "                     CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_certi_id WHERE fb.identity_id = ? ");
            pstm.setString(1, identity);
            ResultSet rs = pstm.executeQuery();
            if(rs.next())
            if(!StringUtil.isEmpty(rs.getString("ch_certi_id"))) flag = true;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCh_time() {
        return ch_time;
    }

    public void setCh_time(Date ch_time) {
        this.ch_time = ch_time;
    }

    public String getCh_ticket_id() {
        return ch_ticket_id;
    }

    public void setCh_ticket_id(String ch_ticket_id) {
        this.ch_ticket_id = ch_ticket_id;
    }

    public String getCh_certi_id() {
        return ch_certi_id;
    }

    public void setCh_certi_id(String ch_certi_id) {
        this.ch_certi_id = ch_certi_id;
    }
}
