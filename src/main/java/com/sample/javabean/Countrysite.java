package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Countrysite implements java.io.Serializable{
    private int countrysiteNo;
    private String name;
    private int townNo;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pstm = null;

    public String isHaveCountrysite(int townNo) {
        String name = null;
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select name from countrysite where townNo = ? ");
            pstm.setInt(1, townNo);
            rs = pstm.executeQuery();
            if(rs.next()) name = rs.getString("name");
        }catch (Exception e){
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
    public JdbcUtils getCsites(PageBean pageBean, Countrysite countrysite, String getVersion) {
        StringBuffer sql = null;
        if(StringUtil.isEmpty(getVersion))
            sql = new StringBuffer("SELECT cs.*, t.townNo, t.townName, co.countyNo, co.countyName,\n" +
                    "\t CASE LENGTH(t.townNo) WHEN 1 THEN \n" +
                    "\t CONCAT(t.countyNo, CONCAT(0, t.townNo)) ELSE \n" +
                    "\t CONCAT(t.countyNo,t.townNo) END AS t_townNo,\n" +
                    "\t CASE LENGTH(cs.countrysiteNo) WHEN 1 THEN \n" +
                    "\t CONCAT(cs.townNo, (CONCAT(0, cs.countrysiteNo))) ELSE \n" +
                    "\t CONCAT(cs.townNo, cs.countrysiteNo) \n" +
                    "\t END AS t_csNo FROM (countrysite cs INNER JOIN \n" +
                    "\t town t ON case length(t.townNo) when 1 then concat(t.countyNo, concat(0, t.townNo)) else" +
                    " concat(t.countyNo, t.townNo) end = cs.townNo) INNER JOIN county co ON t.countyNo = co.countyNo ");
        else
            sql = new StringBuffer("SELECT cs.name AS cs_name, CASE LENGTH(cs.countrysiteNo) WHEN 1 THEN \n" +
                    "CONCAT(cs.townNo, CONCAT(0, cs.countrysiteNo)) ELSE \n" +
                    "CONCAT(cs.townNo, cs.countrysiteNo) END AS t_csNo \n" +
                    " FROM countrysite cs INNER JOIN town t ON cs.townNo = (case length(t.townNo) when 1 then " +
                    "concat(t.countyNo, concat(0, t.townNo)) else concat(t.countyNo, t.townNo) end) AND cs.townNo = ? ");
        JdbcUtils returnArg = null;
        boolean flag = Pattern.matches("^[\\d]+$", countrysite.getName() == null ? "" : countrysite.getName());
        if( !flag ) {
            if(!StringUtil.isEmpty(countrysite.getName())){
                sql.append(" and cs.name like ? ");
            }
        }
        if( flag ) {
            sql.append(" and case length(cs.countrysiteNo) when 1 then CONCAT(cs.townNo, CONCAT(0,cs.countrysiteNo)) else " +
                    " CONCAT(cs.townNo, cs.countrysiteNo) end like ? ");
        }
        sql.append(" limit ?,?");
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            if(StringUtil.isEmpty(getVersion)) {
                if (count > 2) {
                    if (count == 3) {
                        pstm.setString(1, "%" + countrysite.getName() + "%");
                        pstm.setInt(2, pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit());
                    }
                    if (count == 4) {
                        pstm.setString(1, "%" + countrysite.getName() + "%");
                        pstm.setString(2, "%" + countrysite.getName() + "%");
                        pstm.setInt(3, pageBean.getStart());
                        pstm.setInt(4, pageBean.getLimit());
                    }
                } else {
                    pstm.setInt(1, pageBean.getStart());
                    pstm.setInt(2, pageBean.getLimit());
                }
            }else {
                pstm.setInt(1, countrysite.getTownNo());
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

    public int saveCsite(Countrysite cs, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        switch (opVersion) {
            case 0 :
                getInsertNo = "select max(countrysiteNo) + 1 as countrysiteNo from countrysite";
                sql = "insert into countrysite values(?, ?, ?)"; version = 0;
                break;
            case 1 :
                sql = "update countrysite set name = ? , townNo = ? where countrysiteNo = ? ;"; version = 1;
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
                    if(rs.next()) to = rs.getInt("countrysiteNo");
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, to);
                    pstm.setString(2, cs.getName());
                    pstm.setInt(3, cs.getTownNo());
                    returnInt = pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, cs.getName());
                    pstm.setInt(2, cs.getTownNo());
                    pstm.setInt(3, cs.getCountrysiteNo());
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

    public int delCsite(String ids) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from countrysite where case length(countrysiteNo) when 1  then " +
                    "concat(townNo, concat(0, countrysiteNo)) else concat(townNo, countrysiteNo) end in (" + ids + ")");
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

    public int getCountrysiteNo() {
        return countrysiteNo;
    }

    public void setCountrysiteNo(int countrysiteNo) {
        this.countrysiteNo = countrysiteNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTownNo() {
        return townNo;
    }

    public void setTownNo(int townNo) {
        this.townNo = townNo;
    }

    public Countrysite() {
    }

    public Countrysite(String name) {
        this.name = name;
    }

    public Countrysite(int countrysiteNo) {
        this.countrysiteNo = countrysiteNo;
    }

    public Countrysite(int countrysiteNo, String name, int townNo) {
        this.countrysiteNo = countrysiteNo;
        this.name = name;
        this.townNo = townNo;
    }

    public Countrysite(String name, int countrysiteNo) {
        this.name = name;
        this.countrysiteNo = countrysiteNo;
    }
}
