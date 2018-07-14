package com.sample.javabean;

import com.sample.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Role {
    private int id;
    private int level;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;
    private String role_name;
    public JdbcUtils getAllRoles() {
        JdbcUtils returnArg = null;
        jdbcUtils = new JdbcUtils();
        String sql = "SELECT id, role_name, level, CASE level WHEN 1 THEN '超级管理员'\n" +
                " WHEN 2 THEN '县合管办领导' WHEN 3 THEN '县合管办经办人'\n" +
                " WHEN 4 THEN '乡镇农合经办人' END AS role_level\n" +
                " FROM t_role";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            rs=pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs , pstm);
        }catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return returnArg;
    }

    public int saveRole(Role role, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        switch (opVersion) {
            case 0 :
                sql = "insert into t_role values(null, ?, ?)"; version = 0;
                break;
            case 1 :
                sql = "update t_role set role_name = ? , level = ? where id = ? ;"; version = 1;
                break;
        }
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            switch (version) {
                case 0 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, role.getLevel());
                    pstm.setString(2, role.getRole_name());
                    returnInt = pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, role.getRole_name());
                    pstm.setInt(2, role.getLevel());
                    pstm.setInt(3, role.getId());
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

    public boolean isHaveCascade(int role_id) {
        boolean flag = false;
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select id from t_user u where u.role_id = ? ");
            pstm.setInt(1, role_id);
            rs = pstm.executeQuery();
            if(rs.next()) flag = true;
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

    public int delRole(String ids) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from t_role where id in ( " + ids +" ) ");
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

    public Role(int id, int level) {
        this.id = id;
        this.level = level;
    }

    public Role() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
