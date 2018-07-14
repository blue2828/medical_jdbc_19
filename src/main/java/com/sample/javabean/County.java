package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class County implements java.io.Serializable{
    private int id;
    private int countyNo;
    private String countyName;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public JdbcUtils getCounties(PageBean pageBean, County county) {
        StringBuffer sql = new StringBuffer("select * from county where 1=1 ");
        JdbcUtils returnArg = null;
        if( !StringUtil.isEmpty( county.getCountyName() ) ) {
            sql.append(" and countyName like ? ");
        }
        if( county.getCountyNo() != 0) {
            sql.append(" and countyNo like ? ");
        }
        sql.append(" limit ?,?");
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            if (count > 2) {
                if(count == 3) {
                    if(sql.toString().contains("countyNo"))
                        pstm.setString(1, "%" + county.getCountyNo() + "%");
                    else
                        pstm.setString(1, "%" + county.getCountyName() + "%");
                    pstm.setInt(2, pageBean.getStart());
                    pstm.setInt(3, pageBean.getLimit());
                }
                if(count == 4) {
                    pstm.setString(1, "%" + county.getCountyName() + "%");
                    pstm.setString(2, "%" + county.getCountyNo() + "%");
                    pstm.setInt(3, pageBean.getStart());
                    pstm.setInt(4, pageBean.getLimit());
                }
            }else {
                    pstm.setInt(1, pageBean.getStart());
                    pstm.setInt(2, pageBean.getLimit());
            }
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs , pstm);
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnArg;
    }

    public int delCounty(String ids) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from county where id in (" + ids + ")");
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

    /*public boolean isNoExist(int countyNo) {
        boolean flag = false;
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select id from county where countyNo = ?");
            pstm.setInt(1, countyNo);
            rs = pstm.executeQuery();
            if(rs.next()) flag = true;
            else flag = false;
        }catch (SQLException e) {
            flag = false;
            e.printStackTrace();
        }catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }*/

    public int saveCounty(County county, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        switch (opVersion) {
            case 0 :
                getInsertNo = "select max(countyNo) + 1 as countyNo from county";
                sql = "insert into county values(null, ?, ?)"; version = 0;
                break;
            case 1 :
                sql = "update county set countyName = ? where id = ? ;"; version = 1;
                break;
        }
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            int cno = 0;
            switch (version) {
                case 0 :
                    pstm = conn.prepareStatement(getInsertNo);
                    rs = pstm.executeQuery();
                    if(rs.next()) cno = rs.getInt("countyNo");
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, cno);
                    pstm.setString(2, county.getCountyName());
                    returnInt = pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, county.getCountyName());
                    pstm.setInt(2, county.getId());
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

    public int getCountyNo() {
        return countyNo;
    }

    public void setCountyNo(int countyNo) {
        this.countyNo = countyNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public County() {
    }

    public County(int countyNo, String countyName) {
        this.countyNo = countyNo;
        this.countyName = countyName;
    }

    public County(int countyNo) {
        this.countyNo = countyNo;
    }

    public County(String countyName) {
        this.countyName = countyName;
    }
}
