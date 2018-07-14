package com.sample.javabean;

import com.sample.util.JdbcUtils;
import com.sample.util.StringUtil;
import com.sample.util.formatRsToJson;
import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletResponse;
import java.sql.*;
public class User implements java.io.Serializable{
	
	private String userid;
	private String pwd;
	private String fullname;
	private int id, sex, role_id;
	private String email,imageHeader;
	private JdbcUtils jdbcUtils = null;
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getImageHeader() {
		return imageHeader;
	}

	public void setImageHeader(String imageHeader) {
		this.imageHeader = imageHeader;
	}

	public boolean checkUser(User user){
		String sql = "select * from t_user  where userid = '"+user.userid+"' and pwd = '"+user.pwd+"'";
		jdbcUtils = new JdbcUtils();
		Statement stmt = null;
		JSONArray ar = null;
		boolean flag = false;
		try {
			conn = jdbcUtils.getCon();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()) flag = true;
			else flag = false;
		}catch(Exception e) {
			e.printStackTrace();
			flag = false;
		}finally {
			try {
				jdbcUtils.closeCon(conn, stmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	public int delUser(User u, int opFlag){
		String sql = "";
		if(opFlag==0)  sql = "delete from t_user where id = "+u.getId()+"";
		else sql = "update t_user set userid  =  ?, fullname  =  ?, email  =  ? where id  =  ?";
		jdbcUtils = new JdbcUtils();
		int flag = 0;
		try {
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement(sql);
			if(opFlag==1){
				ps.setString(1, u.getUserid());
				ps.setString(2, u.getUserid());
				ps.setString(3, u.getUserid());
				ps.setInt(4, u.getId());
			}
			flag = ps.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(conn, ps, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	public User selectOneUser(int id){
		String sql = "select *  from t_user where id = "+id+"";
		jdbcUtils = new JdbcUtils();
		Statement stmt = null;
		User user = null;
		try {
			conn = jdbcUtils.getCon();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				user = new User( rs.getString("userid"),  rs.getString("fullname"), rs.getInt("id"), rs.getString("email")  );
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(conn, stmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	/**
	 * @Author liuyunhuan
	 * @ 获取当前登录系统的用户
	 * @param u
	 * @return
	 */
	public JdbcUtils getCurrentUser(User u){
		String sql = "select * from t_user where userid = ?";
		jdbcUtils = new JdbcUtils();
		try{
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement(sql);
			ps.setString(1,u.getUserid());
			rs = ps.executeQuery();
			jdbcUtils = new JdbcUtils(conn, rs, ps);
		}catch (Exception e){
			e.printStackTrace();
		}
		return jdbcUtils;
	}

	/**
	 * @Author liuyunhuan
	 * @ 获取数据库中头像的地址
	 * @param user
	 * @return
	 */
	public String refreshImgHeader(User user) {
		String newImgHeader = "";
		jdbcUtils = new JdbcUtils();
		try{
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement("select imageHeader from t_user where userid = ?");
			ps.setString(1, user.getUserid());
			rs = ps.executeQuery();
			if(rs.next()) newImgHeader = rs.getString("imageHeader");
			else newImgHeader = null;
		}catch (SQLException e){
			newImgHeader = null;
			e.printStackTrace();
		}catch (Exception e){
			newImgHeader = null;
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(conn, ps ,rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newImgHeader;
	}

	/**
	 * @Author liuyunhuan
	 * 更改用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo (User user, int updateType) {
		String sql = "";
		switch (updateType) {
			case 0 : sql = "update t_user set fullname = ? , email = ? , sex = ? where userid = ?"; break;
			case 1 : sql = "update t_user set pwd = ? where userid = ?"; break;
		}
		jdbcUtils = new JdbcUtils();
		int n = 0;
		try {
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement(sql);
			int count = StringUtil.countCertainChar(sql, "?");
			switch(count) {
				case 2 :
					ps.setString(1, user.getPwd());
					ps.setString(2, user.getUserid());
					break;
				case 4 :
					ps.setString(1, user.getFullname());
					ps.setString(2, user.getEmail());
					ps.setInt(3, user.getSex());
					ps.setString(4, user.getUserid());
			}
			n = ps.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(conn, ps, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return n;
	}

	public int updateImg(User user) {
		jdbcUtils = new JdbcUtils();
		int n = 0;
		String sql = "update t_user set imageHeader = ? where userid = ? ";
		try {
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getImageHeader());
			ps.setString(2, user.getUserid());
			n = ps.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(conn, ps, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return n;
	}

	public JdbcUtils getAllUsers (PageBean pageBean) {
		String sql = "SELECT u.*, CASE u.sex WHEN 0 THEN '男' WHEN 1 THEN '女' END AS u_sex , r.role_name, r.id AS roleId " +
				"FROM t_user u INNER JOIN t_role r ON u.role_id = r.id limit ?, ?";
		jdbcUtils = new JdbcUtils();
		JdbcUtils returnArg = null;
		try{
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pageBean.getStart());
			ps.setInt(2, pageBean.getLimit());
			rs = ps.executeQuery();
			returnArg = new JdbcUtils(conn, rs, ps);
		}catch (Exception e) {
			e.printStackTrace();
			returnArg = null;
		}
		return returnArg;
	}

	public int saveUser (User user, int opVersion) {
		String sql = null;
		int returnInt = 0;
		int version = 0;
		String getInsertNo = null;
		switch (opVersion) {
			case 0 :
				getInsertNo = "select max(userid) + 1 as insertNo from t_user";
				sql = "insert into t_user (userid, fullname, email, role_id, sex) values(?, ?, ?, ?, ?)"; version = 0;
				break;
			case 1 :
				sql = "update t_user set fullname = ? , email = ?, role_id = ?, sex = ? where userid = ? ;"; version = 1;
				break;
		}
		jdbcUtils = new JdbcUtils();
		try{
			conn = jdbcUtils.getCon();
			switch (version) {
				case 0 :
					String insertNo = "";
					ps = conn.prepareStatement(getInsertNo);
					rs = ps.executeQuery();
					if (rs.next()) insertNo = rs.getString("insertNo");
					ps = conn.prepareStatement(sql);
					ps.setString(1, StringUtil.isEmpty(insertNo) ? "1" : insertNo);
					ps.setString(2, user.getFullname());
					ps.setString(3, user.getEmail());
					ps.setInt(4, user.getRole_id());
					ps.setInt(5, user.getSex());
					returnInt = ps.executeUpdate();
					break;
				case 1 :
					ps = conn.prepareStatement(sql);
					ps.setString(1, user.getFullname());
					ps.setString(2, user.getEmail());
					ps.setInt(3, user.getRole_id());
					ps.setInt(4, user.getSex());
					ps.setString(5, user.getUserid());
					returnInt = ps.executeUpdate();
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
				jdbcUtils.closeCon(conn, ps, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnInt;
	}

	public int delUser (String ids) {
		jdbcUtils = new JdbcUtils();
		int flag = 0;
		try{
			conn = jdbcUtils.getCon();
			ps = conn.prepareStatement("delete from t_user where userid in ( " + ids +" ) ");
			flag = ps.executeUpdate();
		}catch (SQLException e){
			flag = 0;
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(conn, ps, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public User(){
		
	}
	public User(String userid, String pwd, String fullname){
		this.userid = userid;
		this.pwd = pwd;
		this.fullname = fullname;
	}

	public User(String userid, String pwd) {
		this.userid = userid;
		this.pwd = pwd;
	}

	public User(String userid, String fullname, int id, String email) {
		this.userid = userid;
		this.fullname = fullname;
		this.id = id;
		this.email = email;
	}
	public User(String userid, String fullname, int id, String email, String pwd) {
		this.userid = userid;
		this.fullname = fullname;
		this.id = id;
		this.email = email;
		this.pwd = pwd;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getRole_id() {
		return role_id;
	}

	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
}
