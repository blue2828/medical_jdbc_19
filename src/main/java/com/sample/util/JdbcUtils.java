package com.sample.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcUtils {
    private Connection conn;
    private ResultSet rs;
    private PreparedStatement ps;
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

    public PreparedStatement getPs() {
        return ps;
    }

    public void setPs(PreparedStatement ps) {
        this.ps = ps;
    }
    public Connection getCon(){
        Connection con = null;
        BufferedInputStream inputStream = null;
        InputStream input=null;
        Properties properties = new Properties();
        try{
            input=JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
            inputStream = new BufferedInputStream(input);
            properties.load(inputStream);
            String url = properties.getProperty("url"), username = properties.getProperty("username"),
                   pwd = properties.getProperty("pwd"), driverClassName = properties.getProperty("driverClassName");
            Class.forName(driverClassName);
            con = DriverManager.getConnection(url, username, pwd);
            return con;
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
             try {
                if(null != inputStream) inputStream.close();
                //if(null != input) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return con;
    }
    public void closeCon(Connection con, Object pstm, ResultSet rs) throws Exception{
        if(null != con) con.close();
        if(null != pstm){
          if(pstm instanceof PreparedStatement)
              ((PreparedStatement) pstm).close();
                else if(pstm instanceof Statement){
                ((Statement) pstm).close();
          }
        }
        if(null != rs) rs.close();
    }

    public JdbcUtils() {
    }

    public JdbcUtils(Connection conn, ResultSet rs, PreparedStatement ps) {
        this.conn = conn;
        this.rs = rs;
        this.ps = ps;
    }
}
