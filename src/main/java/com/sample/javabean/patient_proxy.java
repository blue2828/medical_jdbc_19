package com.sample.javabean;

import com.sample.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class patient_proxy implements java.io.Serializable{
    private int id;
    private int annual, top_line;
    private double proportion;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public JdbcUtils getAllInfo (PageBean pageBean) {
        jdbcUtils = new JdbcUtils();
        JdbcUtils returnArg = null;
        String sql = "select * from patient_proxy";
        try {
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs, pstm);
        }catch (Exception e) {
            e.printStackTrace();
            returnArg = null;
        }
        return returnArg;
    }
    public int saveProxy(patient_proxy proxy, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        switch (opVersion) {
            case 0 :
                sql = "insert into patient_proxy values(null, ?, ?, ?)"; version = 0;
                break;
            case 1 :
                sql = "update patient_proxy set annual = ? , top_line = ?, proportion = ? where id = ? "; version = 1;
                break;
        }
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            switch (version) {
                case 0 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, proxy.getAnnual());
                    pstm.setInt(2, proxy.getTop_line());
                    pstm.setDouble(3, proxy.getProportion());
                    returnInt = pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, proxy.getAnnual());
                    pstm.setInt(2, proxy.getTop_line());
                    pstm.setDouble(3, proxy.getProportion());
                    pstm.setInt(4, proxy.getId());
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

    public int getAnnual() {
        return annual;
    }

    public void setAnnual(int annual) {
        this.annual = annual;
    }

    public int getTop_line() {
        return top_line;
    }

    public void setTop_line(int top_line) {
        this.top_line = top_line;
    }

    public double getProportion() {
        return proportion;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public patient_proxy() {
    }
}
