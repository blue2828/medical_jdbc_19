package com.sample.javabean;

import com.sample.util.DateUtil;
import com.sample.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class baoxiao {
    private int order;
    private String joiner_no;
    private Date time;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;
    private double money;
    public Object[] getYearLine () {
        jdbcUtils = new JdbcUtils();
        Object[] o = new Object[2];
        String sql = "select * from patient_proxy where annual = ?";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, Integer.parseInt(DateUtil.getCurrentTime().substring(0,4)));
            rs = pstm.executeQuery();
            if(rs.next()) {
                o[0] = rs.getInt("top_line");
                o[1] = rs.getDouble("proportion");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
    public double getAlreadyUse (String identity_id) {
        jdbcUtils = new JdbcUtils();
        double m = 0;
        String sql = "select money from baoxiao where joiner_no = ?";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, identity_id);
            rs = pstm.executeQuery();
            while (rs.next()) {
                m += rs.getDouble("money");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public int doBx (baoxiao b, String date) {
        jdbcUtils = new JdbcUtils();
        int returnInt = 0;
        String sql = "insert into baoxiao values (null, ?, ?, ?)";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, b.getJoiner_no());
            pstm.setString(2, date);
            pstm.setDouble(3, b.getMoney());
            returnInt = pstm.executeUpdate();
        }catch (Exception e) {
            e.printStackTrace();
            returnInt = 0;
        }finally {
            try {
                if(jdbcUtils != null)
                jdbcUtils.closeCon(conn, pstm, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public JdbcUtils viewResult (String identity_id) {
        JdbcUtils returnArg = null;
        jdbcUtils = new JdbcUtils();
        String sql = "SELECT pc.*,cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻'\n" +
                "  WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                "   CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                "   CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_certi_id\n" +
                "   LEFT JOIN patient_certificate pc ON cp.patient_ticket = pc.patient_certi_id    INNER JOIN baoxiao b ON b.joiner_no = fb.identity_id WHERE cp.patient_ticket IS NOT NULL\n" +
                "   AND fb.identity_id = ?";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, identity_id);
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
            returnArg = null;
        }
        return returnArg;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getJoiner_no() {
        return joiner_no;
    }

    public void setJoiner_no(String joiner_no) {
        this.joiner_no = joiner_no;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public baoxiao() {
    }

    public baoxiao(String joiner_no, double money) {
        this.joiner_no = joiner_no;
        this.money = money;
    }
}
