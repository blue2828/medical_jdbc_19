package com.sample.javabean;

import com.sample.util.DateUtil;
import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class patient_ticket implements java.io.Serializable {
    private int id;
    private String patient_name, patient_certi_id;
    private Date begin_time, end_time;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;
    public JdbcUtils getAllInfo (PageBean pageBean, String joinerName) {
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        StringBuffer sql = null;
        if (StringUtil.isEmpty(joinerName))
            sql = new StringBuffer("SELECT pc.*,cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' \n" +
                    "                    WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                    "                     CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                    "                     CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_certi_id \n" +
                    "                     LEFT JOIN patient_certificate pc ON cp.patient_ticket = pc.patient_certi_id WHERE cp.patient_ticket IS NOT NULL\n" +
                    "                     ");
        else
            sql = new StringBuffer("SELECT pc.*,cp.*,CASE fb.sex WHEN 1 THEN '女' ELSE '男' END AS fb_sex,fb.identity_id AS fb_identity_id,SUBSTRING(fb.identity_id,7,4) AS age,fb.`familyNo` fb_fno,fb.*,f.*,fb.id fb_id,f.id f_id,CASE fb.`gxWithHost` WHEN 0 THEN '夫妻' \n" +
                    "                    WHEN 1 THEN '父子' WHEN 3 THEN '户主本人' ELSE '父女' END AS gx  FROM family_member fb INNER JOIN t_family f ON  fb.`familyNo` = CONCAT(f.`site_group_no`,\n" +
                    "                     CASE LENGTH(f.`familyNo`) WHEN 1 THEN CONCAT('000',f.`familyNo`) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 THEN\n" +
                    "                     CONCAT(0,f.`familyNo`) ELSE f.`familyNo` END) LEFT JOIN ch_sign_up cp ON fb.nh_id = ch_certi_id \n" +
                    "                     LEFT JOIN patient_certificate pc ON cp.patient_ticket = pc.patient_certi_id WHERE cp.patient_ticket IS NOT NULL\n" +
                    "                     AND fb.name = ?");
        sql.append(" limit ?, ?");
        try {
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            switch (count) {
                case 2 :
                    pstm.setInt(1, pageBean.getStart());
                    pstm.setInt(2, pageBean.getLimit());
                    break;
                case 3 :
                    pstm.setString(1, joinerName);
                    pstm.setInt(2, pageBean.getStart());
                    pstm.setInt(3, pageBean.getLimit());
            }
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
            returnArg = null;
        }
        return returnArg;
    }

    public int saveTicket (patient_ticket ticket, String nh_no, int updateType) {
        jdbcUtils = new JdbcUtils();
        int returnInt = 0;
        String count = "select count(*) as num from patient_certificate";
        String getMaxNoNext = "select max(patient_certi_id) + 1 as nextNo from patient_certificate ";
        String intoPt = "insert into patient_certificate values (null, ?, ?, ?, ?)";
        String toCh = "update ch_sign_up set patient_ticket = ? where ch_certi_id = ?";
        String update = "";
        if(updateType == 1) {
            update = "update patient_certificate set patient_name = ?, begin_time = ?, end_time = ? where patient_certi_id = ?";
        }
        try{
            conn = jdbcUtils.getCon();
            if(updateType == 0) {
                pstm = conn.prepareStatement(count);
                rs = pstm.executeQuery();
                int counts = 0;
                String num = "";
                if(rs.next()) counts = rs.getInt("num");
                if(counts == 0) num = "1";
                else {
                    pstm = conn.prepareStatement(getMaxNoNext);
                    rs = pstm.executeQuery();
                    if (rs.next()) num =rs.getString("nextNo");
                }
                pstm = conn.prepareStatement(intoPt);
                pstm.setString(1, num);
                pstm.setString(2, ticket.getPatient_name());
                pstm.setString(3, DateUtil.formatDateTOStr(ticket.getBegin_time(), "yyyy-MM-dd HH:mm:ss"));
                pstm.setString(4, DateUtil.formatDateTOStr(ticket.getEnd_time(), "yyyy-MM-dd HH:mm:ss"));
                returnInt = pstm.executeUpdate();
                pstm = conn.prepareStatement(toCh);
                pstm.setString(1, num);
                pstm.setString(2, nh_no);
                returnInt += pstm.executeUpdate();
            }else {
                pstm = conn.prepareStatement(update);
                pstm.setString(1, ticket.getPatient_name());
                pstm.setString(2, DateUtil.formatDateTOStr(ticket.getBegin_time(), "yyyy-MM-dd HH:mm:ss"));
                pstm.setString(3, DateUtil.formatDateTOStr(ticket.getEnd_time(), "yyyy-MM-dd HH:mm:ss"));
                pstm.setString(4, ticket.getPatient_certi_id());
                returnInt = pstm.executeUpdate();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public boolean isDulpicate (String nh_no) {
        jdbcUtils = new JdbcUtils();
        boolean flag = false;
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("    SELECT COUNT(*) AS num FROM ch_sign_up cp INNER JOIN patient_certificate pc ON\n" +
                    "                     cp.patient_ticket = pc.patient_certi_id AND cp.ch_certi_id = ? ");
            pstm.setString(1, nh_no);
            ResultSet rs = pstm.executeQuery();
            int count = 0;
            if(rs.next())
                count = rs.getInt("num");
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
    public JdbcUtils getJdbcUtils() {
        return jdbcUtils;
    }

    public void setJdbcUtils(JdbcUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public PreparedStatement getPstm() {
        return pstm;
    }

    public void setPstm(PreparedStatement pstm) {
        this.pstm = pstm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_certi_id() {
        return patient_certi_id;
    }

    public void setPatient_certi_id(String patient_certi_id) {
        this.patient_certi_id = patient_certi_id;
    }

    public Date getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(Date begin_time) {
        this.begin_time = begin_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public patient_ticket() {
    }
}
