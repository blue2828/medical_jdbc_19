package com.sample.javabean;

import com.sample.util.DateUtil;
import com.sample.util.JdbcUtils;

import java.sql.*;
import java.util.Calendar;

public class Family implements java.io.Serializable {
    private int id;
    private String familyNo, f_host_name, identity_id, site_group_no;
    private Date create_time;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public boolean isHaveFm(String site_group_no) {
        boolean flag = false;
        jdbcUtils = new JdbcUtils();
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("SELECT id FROM t_family t_f INNER JOIN site_group sg ON \n" +
                    "t_f.`site_group_no` = (\n" +
                    "  CONCAT(sg.`countrysiteNo`, CASE LENGTH(sg.`site_group_no`) WHEN 1 THEN CONCAT(0, sg.`site_group_no`)\n" +
                    "    ELSE sg.`site_group_no` END) ) and t_f.`site_group_no` = ? \n");
            pstm.setString(1, site_group_no);
            rs = pstm.executeQuery();
            if(rs.next()) flag = true;
            else flag = false;
        }catch (Exception e) {
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
    public boolean isDulpicate (String identity_id) {
        jdbcUtils = new JdbcUtils();
        boolean flag = false;
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select count(*) as no from t_family where identity_id = ?");
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
    public JdbcUtils getAllFms (PageBean pageBean) {
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        String sql = "SELECT f.*,f.id f_id, s.*,CONCAT(s.countrysiteNo, CASE LENGTH(s.site_group_no ) WHEN 1 THEN CONCAT(0, s.site_group_no ) ELSE s.site_group_no END) s_no,s.name s_name,cs.name cs_name, cs.*, CONCAT(cs.townNo, CASE LENGTH(cs.countrysiteNo) WHEN 1 THEN CONCAT(0, cs.countrysiteNo) ELSE cs.countrysiteNo END) cs_no,\n" +
                " t.*,CONCAT(t.countyNo, CASE LENGTH(t.townNo) WHEN 1 THEN CONCAT(0, t.townNo) ELSE t.townNo END) t_townNo,  co.*,\n" +
                " CONCAT(f.`site_group_no`, CASE LENGTH(f.familyNo) WHEN 1 THEN CONCAT('000',f.familyNo) WHEN 2 THEN CONCAT('00',f.`familyNo`) WHEN 3 \n" +
                " THEN CONCAT(0,f.`familyNo`) ELSE f.familyNo END) f_fno FROM t_family  f INNER JOIN site_group s ON f.site_group_no = \n" +
                "CONCAT(s.`countrysiteNo`, CASE LENGTH(s.site_group_no) WHEN 1 THEN CONCAT(0, s.site_group_no) ELSE s.`site_group_no` END)\n" +
                "INNER JOIN countrysite cs ON s.`countrysiteNo` = CONCAT(cs.townNo, CASE LENGTH(cs.countrysiteNo) WHEN 1 THEN CONCAT(0, cs.countrysiteNo) ELSE cs.countrysiteNo END) \n" +
                "INNER JOIN town t ON cs.townNo = CONCAT(t.countyNo, CASE LENGTH(t.townNo) WHEN 1 THEN CONCAT(0, t.townNo) ELSE t.townNo END)\n" +
                "INNER JOIN county co ON t.countyNo = co.countyNo  limit ?,?";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, pageBean.getStart());
            pstm.setInt(2, pageBean.getLimit());
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return returnArg;
    }
    public int saveFm(Family family, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        String addHostIn = "";
        String getLongNo = "";
        switch (opVersion) {
            case 0 :
                getInsertNo = "select max(familyNo) + 1 as nextNo from t_family";
                getLongNo = "SELECT CONCAT(t.site_group_no,CASE LENGTH(t.familyNo) WHEN 1 THEN CONCAT('000',t.familyNo) WHEN 2 THEN CONCAT('00',t.familyNo)\n" +
                        " WHEN 3 THEN CONCAT('0',t.familyNo) ELSE t.familyNo END ) AS f_no FROM t_family t WHERE t.identity_id = ?";
                sql = "insert into t_family values(null, ?, ?, ?, ?, ?);" ;
                addHostIn = "insert into family_member (familyNo, name, identity_id, gxWithHost) " +
                        "values (?, ?, ?, 3)";
                version = 0;
                break;
            case 1 :
                sql = "update t_family set f_host_name = ?  , identity_id = ?, site_group_no = ? where id = ? ;"; version = 1;
                break;
        }
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            int to = 0;
            switch (version) {
                case 0 :
                    pstm = conn.prepareStatement(getInsertNo);
                    rs = pstm.executeQuery();
                    if(rs.next()) to = rs.getInt("nextNo");
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, to);
                    pstm.setString(2, family.getF_host_name());
                    pstm.setString(3, family.getIdentity_id());
                    pstm.setString(4, DateUtil.getCurrentTime());
                    pstm.setString(5, family.getSite_group_no());
                    returnInt = pstm.executeUpdate();
                    String longNo = "";
                    pstm = conn.prepareStatement(getLongNo);
                    pstm.setString(1, family.getIdentity_id());
                    rs = pstm.executeQuery();
                    if(rs.next()) longNo = rs.getString("f_no");
                    pstm = conn.prepareStatement(addHostIn);
                    pstm.setString(1, longNo);
                    pstm.setString(2, family.getF_host_name());
                    pstm.setString(3, family.getIdentity_id());
                    returnInt += pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, family.getF_host_name());
                    pstm.setString(2, family.getIdentity_id());
                    pstm.setString(3, family.getSite_group_no());
                    pstm.setInt(4, family.getId());
                    returnInt = pstm.executeUpdate();
                    break;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            returnInt = 0;
        }catch (Exception e) {
            e.printStackTrace();
            returnInt = 0;
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFamilyNo() {
        return familyNo;
    }

    public void setFamilyNo(String familyNo) {
        this.familyNo = familyNo;
    }

    public String getF_host_name() {
        return f_host_name;
    }

    public void setF_host_name(String f_host_name) {
        this.f_host_name = f_host_name;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getSite_group_no() {
        return site_group_no;
    }

    public void setSite_group_no(String site_group_no) {
        this.site_group_no = site_group_no;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Family() {
    }
}
