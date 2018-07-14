package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Site_group implements java.io.Serializable{
    private int site_group_no;
    private int countrysiteNo;
    private String name;
    private JdbcUtils jdbcUtils = null;
    private Connection conn = null;
    private PreparedStatement pstm = null;
    private ResultSet rs = null;
    public String isHaveCsite(String countrysiteNo) {
        String name = null;
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("select name from site_group where countrysiteNo = ? ");
            pstm.setString(1, countrysiteNo);
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
    public JdbcUtils getSgs(PageBean pageBean, Site_group sg, String cs_no) {
        StringBuffer sql = null;
        if (StringUtil.isEmpty(cs_no)) {
            sql = new StringBuffer("SELECT sg.site_group_no, sg.countrysiteNo, sg.name AS sg_name, cs.name AS cs_name," +
                    "t.townName, co.countyName, co.countyNo, cs.townNo as t_townNo, case length(sg.site_group_no) when 1 then " +
                    "concat(sg.countrysiteNo, concat(0, sg.site_group_no)) else concat(sg.countrysiteNo, sg.site_group_no) end " +
                    "as t_sgNo FROM ((site_group sg INNER JOIN " +
                    "countrysite cs ON sg.countrysiteNo \n" +
                    "= (case length(cs.countrysiteNo) when 1 then concat(cs.townNo, concat(0, cs.countrysiteNo)) else " +
                    "concat(cs.townNo, cs.countrysiteNo) end )) INNER JOIN town t ON cs.townNo = (" +
                    " case length(t.townNo) when 1 then concat(t.countyNo, concat(0, t.townNo)) else " +
                    "concat(t.countyNo, t.townNo) end )) INNER JOIN \n" +
                    "county co ON t.countyNo = co.countyNo  ");
            boolean flag = Pattern.matches("^[\\d]+$", sg.getName() == null ? "" : sg.getName());
            if( !flag ) {
                if(!StringUtil.isEmpty(sg.getName())){
                    sql.append(" and sg.name like ? ");
                }
            }
            if( flag ) {
                sql.append(" and case length(cs.countrysiteNo) when 1 then CONCAT(cs.townNo, CONCAT(0,cs.countrysiteNo)) else " +
                        " CONCAT(cs.townNo, cs.countrysiteNo) end like ? ");
            }
        }else
            sql = new StringBuffer("SELECT s.name s_name, CONCAT(s.countrysiteNo,CASE LENGTH(s.site_group_no) WHEN 1 THEN CONCAT(0,s.site_group_no) ELSE s.site_group_no END) s_no,\n" +
                    "cs.name  cs_name FROM site_group s INNER JOIN countrysite cs ON s.`countrysiteNo` = CONCAT(cs.`townNo`,CASE LENGTH(cs.`countrysiteNo`) WHEN 1 THEN CONCAT(0,cs.`countrysiteNo`) " +
                    "ELSE cs.`countrysiteNo` END) \n" +
                    "INNER JOIN town t ON cs.`townNo` = CONCAT(t.countyNo, CASE LENGTH(t.townNo) WHEN 1 THEN CONCAT(0, t.townNo) ELSE t.townNo END)\n" +
                    "INNER JOIN county co ON t.countyNo = co.countyNo WHERE s.`countrysiteNo`= ?");
        JdbcUtils returnArg = null;
        sql.append(" limit ?,?");
        jdbcUtils = new JdbcUtils();
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement(sql.toString());
            int count = StringUtil.countCertainChar(sql.toString(), "?");
            if (count > 2) {
                if(count == 3) {
                    if (StringUtil.isEmpty(cs_no)) {
                        pstm.setString(1, "%" + sg.getName() + "%");
                        pstm.setInt(2, pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit());
                    }else {
                        pstm.setString(1, cs_no);
                        pstm.setInt(2, pageBean.getStart() == 0 ? 0 : pageBean.getStart());
                        pstm.setInt(3, pageBean.getLimit() == 0 ? 1000 : pageBean.getLimit());
                    }
                }
                if(count == 4) {
                    pstm.setString(1, "%" + sg.getName() + "%");
                    pstm.setString(2, "%" + sg.getName() + "%");
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

    public int saveSg(Site_group sg, String countrysiteNo, int opVersion) {
        String sql = null;
        int returnInt = 0;
        int version = 0;
        String getInsertNo = null;
        switch (opVersion) {
            case 0 :
                getInsertNo = "select max(site_group_no) + 1 as site_group_no from site_group";
                sql = "insert into site_group values(?, ?, ?)"; version = 0;
                break;
            case 1 :
                sql = "update site_group set name = ? , countrysiteNo = ? where site_group_no = ? ;"; version = 1;
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
                    if(rs.next()) to = rs.getInt("site_group_no");
                    pstm = conn.prepareStatement(sql);
                    pstm.setInt(1, to);
                    pstm.setString(2, countrysiteNo);
                    pstm.setString(3, sg.getName());
                    returnInt = pstm.executeUpdate();
                    break;
                case 1 :
                    pstm = conn.prepareStatement(sql);
                    pstm.setString(1, sg.getName());
                    pstm.setString(2, countrysiteNo);
                    pstm.setInt(3, sg.getSite_group_no());
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

    public int delSg(String ids) {
        jdbcUtils = new JdbcUtils();
        int flag = 0;
        try{
            conn = jdbcUtils.getCon();
            pstm = conn.prepareStatement("delete from site_group where case length(site_group_no) when 1 then " +
                    "concat(countrysiteNo, concat(0, site_group_no)) else concat(countrysiteNo, site_group_no) end in (" + ids + ")");
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

    public int getSite_group_no() {
        return site_group_no;
    }

    public void setSite_group_no(int site_group_no) {
        this.site_group_no = site_group_no;
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

    public Site_group() {
    }

    public Site_group(int countrysiteNo) {
        this.countrysiteNo = countrysiteNo;
    }

    public Site_group(String name) {
        this.name = name;
    }

    public Site_group(int site_group_no, int countrysiteNo, String name) {
        this.site_group_no = site_group_no;
        this.countrysiteNo = countrysiteNo;
        this.name = name;
    }
}
