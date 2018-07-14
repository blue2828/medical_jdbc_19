package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Menu implements java.io.Serializable {
    private String menu_name, role_id;
    private int menu_id, parent_no, menu_no;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public JdbcUtils getAllMenus (PageBean pageBean) {
        String sql = "SELECT m.*, p.*, r.* FROM (t_menu m INNER JOIN parent_menu p ON m.parent_no = p.order)" +
                " INNER JOIN t_role r ON m.role_id = r.id  limit ?, ?";
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, pageBean.getStart());
            pstm.setInt(2, pageBean.getLimit());
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (SQLException e) {
            e.printStackTrace();
            returnArg = null;
        }catch (Exception e) {
            e.printStackTrace();
            returnArg = null;
        }
        return returnArg;
    }

    public int saveMenu (Menu menu, String parentName, int version, String[] str) {
        int returnInt = 0;
        String ckDulpicate = "", sql = "";
        String roles = StringUtil.formatArrToStr(str);
        jdbcUtils = new JdbcUtils();
        switch (version) {
            case 0 :
                ckDulpicate = "select menu_no from t_menu where menu_no = ?";
                sql = "insert into t_menu values(null, ?, ?, ?, ?); ";
                break;
            case 1 :
                sql = "update t_menu set role_id = ? where menu_no = ?";
                break;
        }
        try {
            conn = jdbcUtils.getCon();
            switch (version) {
                case 0 :
                pstm = conn.prepareStatement(ckDulpicate);
                pstm.setInt(1, menu.getMenu_no());
                rs = pstm.executeQuery();
                if (rs.next()) return -1;
                else
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, parentName);
                    pstm.setInt(2, menu.getMenu_no());
                    pstm.setInt(3, menu.getParent_no());
                    pstm.setString(4, roles);
                    returnInt = pstm.executeUpdate();
                break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, roles);
                    pstm.setInt(2, menu.getMenu_no());
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

    public int delMenu (String ids) {
        jdbcUtils = new JdbcUtils();
        int returnInt = 0;
        String sql = "delete from t_menu where menu_no in (" + ids + ")";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
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

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getParent_no() {
        return parent_no;
    }

    public void setParent_no(int parent_no) {
        this.parent_no = parent_no;
    }

    public int getMenu_no() {
        return menu_no;
    }

    public void setMenu_no(int menu_no) {
        this.menu_no = menu_no;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public Menu() {
    }

    public Menu(int menu_no, int parent_no) {
        this.menu_no = menu_no;
        this.parent_no = parent_no;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }
}
