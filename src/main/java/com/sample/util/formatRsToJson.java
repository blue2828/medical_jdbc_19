package com.sample.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class formatRsToJson {
	public static Object[] formatRsToJa(JdbcUtils jdbcUtils) {
		ResultSetMetaData md = null;
		int count = -1;
		Object[] ob = new Object[2];
		ResultSet rs = null;
		JSONArray array = new JSONArray();
		int rows = 0;
		try {
			rs = jdbcUtils.getRs();
			md = rs.getMetaData();
			count = md.getColumnCount();
			while(rs.next()) {
				rows ++;
				JSONObject temp = new JSONObject();
				for(int i = 1; i <= count; i++) {
					if(rs.getObject(i) instanceof Date) {
						temp.put(md.getColumnLabel(i), DateUtil.formatDateTOStr((Date) rs.getObject(i), "yyyy-MM-dd HH:mm:ss"));
					}else {
						temp.put(md.getColumnLabel(i), rs.getObject(i));
					}
				}
				array.add(temp);
			}
			ob[0] = array;
			ob[1] = rows;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				jdbcUtils.closeCon(jdbcUtils.getCon(), rs, jdbcUtils.getRs());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ob;
	}
}
