package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Town implements java.io.Serializable{
    private int townNo;
    private String townName;
    private int countyNo;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public String isHaveCounty(int countyNo) {
        String name = null;
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select townName from town where countyNo = (select countyNo from county where id = ? )");
            pstm.setInt(1, countyNo);
            rs = pstm.executeQuery();
            if(rs.next()) name = rs.getString("townName");
        }catch (Exception e) {
            name = null;
            e.printStackTrace();
        }finally {
            try {
                jdbcUtils.closeCon(conn, pstm, rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public JdbcUtils getTowns(PageBean pageBean, Town town, String getVersion) {
        StringBuffer sql = null;
        if(StringUtil.isEmpty(getVersion))
            sql =  new StringBuffer("select t.*,co.countyName, case length(t.townNo) when 1 " +
                    "then concat(t.countyNo,concat(0, t.townNo)) ELSE CONCAT(t.countyNo,t.townNo) end as finalNo " +
                    "from town t INNER join county co on t.countyNo = co.countyNo ");
        else
            sql = new StringBuffer("SELECT t.*, CASE LENGTH(t.townNo) WHEN 1 THEN \n" +
                    " CONCAT(t.countyNo, CONCAT(0, t.townNo)) ELSE \n" +
                    " CONCAT(t.countyNo, t.townNo) END AS t_townNo\n" +
                    " FROM town t INNER JOIN county co ON t.countyNo = co.countyNo AND co.countyNo = ? ");
        JdbcUtils returnArg = null;
        if( !StringUtil.isEmpty( town.getTownName() ) ) {
            sql.append(" and t.townName like ? ");
        }
        if( town.getTownNo() != 0) {
            sql.append(" and case length(t.townNo) when 1 then CONCAT(t.countyNo, CONCAT(0,t.townNo)) else " +
                    " CONCAT(t.countyNo, t.townNo) end like ? ");
        }
        sql.append(" limit ?,?");
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            if(StringUtil.isEmpty(getVersion)){
                if (count > 2) {
                    if(count == 3) {
                        if(sql.toString().substring(sql.toString().lastIndexOf("and")).contains("townNo"))
                            pstm.setString(1, "%" + town.getTownNo() + "%");
                        else
                            pstm.setString(1, "%" + town.getTownName() + "%");
                        pstm.setInt(2, pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit());
                    }
                    if(count == 4) {
                        pstm.setString(1, "%" + town.getTownName() + "%");
                        pstm.setString(2, "%" + town.getTownNo() + "%");
                        pstm.setInt(3, pageBean.getStart());
                        pstm.setInt(4, pageBean.getLimit());
                    }
                }else {
                    pstm.setInt(1, pageBean.getStart());
                    pstm.setInt(2, pageBean.getLimit());
                }
            }else {
                pstm.setInt(1, town.getCountyNo());
                pstm.setInt(2, pageBean.getStart());
                pstm.setInt(3, pageBean.getLimit());
            }
            rs = pstm.executeQuery();
            returnArg = new JdbcUtils(conn, rs , pstm);
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnArg;
    }

    public int saveTown(Town town, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        switch (opVersion) {
            case 0 :
                getInsertNo = "select max(townNo) + 1 as townNo from town";
                sql = "insert into town values(?, ?, ?)"; version = 0;
                break;
            case 1 :
                sql = "update town set townName = ? , countyNo = ? where townNo = ? ;"; version = 1;
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
                    if(rs.next()) to = rs.getInt("townNo");
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, to);
                    pstm.setString(2, town.getTownName());
                    pstm.setInt(3, town.getCountyNo());
                    returnInt = pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, town.getTownName());
                    pstm.setInt(2, town.getCountyNo());
                    pstm.setInt(3, town.getTownNo());
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

    public int delTown(String ids) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from town where case length(townNo) when 1 then " +
                    "concat(countyNo, concat(0, townNo)) else concat(countyNo, townNo) end in (" + ids + ")");
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

    public int getTownNo() {
        return townNo;
    }

    public void setTownNo(int townNo) {
        this.townNo = townNo;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public int getCountyNo() {
        return countyNo;
    }

    public void setCountyNo(int countyNo) {
        this.countyNo = countyNo;
    }

    public Town() {
    }

    public Town(int townNo) {
        this.townNo = townNo;
    }

    public Town(int townNo, String townName) {
        this.townNo = townNo;
        this.townName = townName;
    }

    public Town(String townName) {
        this.townName = townName;
    }
}
