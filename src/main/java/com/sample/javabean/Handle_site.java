package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Handle_site {
    private int id, ct_no, level;
    private String town_no, site_no, agentName;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public JdbcUtils getAllSites (PageBean pageBean, Handle_site site) {
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        StringBuffer sql = new StringBuffer("SELECT CASE LENGTH(town_no) WHEN 0 THEN ct_no ELSE town_no END AS agentNo," +
                " t.* FROM handle_site t WHERE 1 = 1");
        if (!StringUtil.isEmpty(site.getSite_no())) {
            sql.append(" and site_no like ?");
        }
        if (!StringUtil.isEmpty(site.getAgentName())) {
            sql.append(" and agentName like ?");
        }
        sql.append(" limit ?,?");
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            switch (count) {
                case 2 :
                    pstm.setInt(1, pageBean.getStart());
                    pstm.setInt(2, pageBean.getLimit());
                    break;
                case 3:
                    if (!StringUtil.isEmpty(site.getSite_no())) {
                        pstm.setString(1, "%" + site.getSite_no() + "%");
                        pstm.setInt(2, pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit());
                    }else {
                        pstm.setString(1, "%" + site.getAgentName() + "%");
                        pstm.setInt(2, pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit());
                    }
                    break;
                case 4 :
                    pstm.setString(1, "%" + site.getSite_no() + "%");
                    pstm.setString(2, "%" + site.getAgentName() + "%");
                    pstm.setInt(3, pageBean.getStart());
                    pstm.setInt(4, pageBean.getLimit());
                    break;
            }
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
            returnArg = null;
        }
        return returnArg;
    }
    public int isDupicate (Handle_site site) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        String sql = "";
        if (!StringUtil.isEmpty(site.getTown_no()))
            sql = "select town_no from handle_site where town_no = ?";
        else
            sql = "select ct_no from handle_site where ct_no = ?";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            if (!StringUtil.isEmpty(site.getTown_no())) {
                pstm.setString(1, site.getTown_no());
                rs = pstm.executeQuery();
                if (rs.next()) flag = 2;
            }
            else {
                pstm.setInt(1, site.getCt_no());
                rs = pstm.executeQuery();
                if (rs.next()) flag = 1;
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
        return flag;
    }
    public int saveSite (Handle_site site, int saveType) {
        int returnInt = 0;
        jdbcUtils = new JdbcUtils();
        String sql = "";
        String getNextNo = "";
        switch (saveType) {
            case 0 :
                getNextNo = "SELECT MAX(site_no) + 1 AS nextNo FROM handle_site ";
                if (!StringUtil.isEmpty(site.getTown_no())) {
                    getNextNo = "SELECT site_no AS nextNo FROM handle_site where ct_no = ? ";
                    sql = "insert into handle_site values(null, ?, ?, ?, ?, ?)";
                }
                else
                    sql = "insert into handle_site (id, ct_no, site_no, agentName, level) values(null, ?, ?, ?, ?)";
                break;
            case 1 :
                sql = "update handle_site set agentName = ? where id = ?";
                break;
        }
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(getNextNo);
            switch (StringUtil.countCertainChar(getNextNo, "?")) {
                case 1 :
                    pstm.setInt(1, site.getCt_no());
                    break;
            }
            String nextNo = "";
            if(saveType == 0){
                rs = pstm.executeQuery();
                if (rs.next()) nextNo = rs.getString("nextNo");
            }
            int count = StringUtil.countCertainChar(sql, "?");
            pstm = conn.prepareStatement(sql);
            switch (count) {
                case 2 :
                    pstm.setString(1, site.getAgentName());
                    pstm.setInt(2, site.getId());
                    break;
                case  4 :
                    pstm.setInt(1, site.getCt_no());
                    pstm.setString(2, nextNo);
                    pstm.setString(3, site.getAgentName());
                    pstm.setInt(4, 1);
                    break;
                case 5 :
                    pstm.setInt(1, site.getCt_no());
                    pstm.setString(2, site.getTown_no());
                    pstm.setString(3, nextNo);
                    pstm.setString(4, site.getAgentName());
                    pstm.setInt(5, 2);
                    break;
            }
            returnInt = pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            returnInt = 0;
        } finally {
            try {
                jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnInt;
    }

    public int delSite(String ids) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from handle_site where id in (" + ids + ")");
            flag = pstm.executeUpdate();
        }catch (SQLException e){
            flag = 0;
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, null);
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

    public int getCt_no() {
        return ct_no;
    }

    public void setCt_no(int ct_no) {
        this.ct_no = ct_no;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTown_no() {
        return town_no;
    }

    public void setTown_no(String town_no) {
        this.town_no = town_no;
    }

    public String getSite_no() {
        return site_no;
    }

    public void setSite_no(String site_no) {
        this.site_no = site_no;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
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

    public Handle_site() {
    }

    public Handle_site(String site_no, String agentName) {
        this.site_no = site_no;
        this.agentName = agentName;
    }
}
