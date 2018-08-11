package com.sdust.im.DataBase;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;

/**
 * 使用数据库连接池加大响应速度
 *
 */
public class DBPool {
	private static DataSource ds;
	private static ThreadLocal<Connection> connMap=new ThreadLocal<Connection>();
	private DBPool() {
	}

	static {
		try {
			InputStream in = DBPool.class.getClassLoader().getResourceAsStream(
					"dbcpconfig.properties");
			Properties pro = new Properties();
			pro.load(in);
			ds = BasicDataSourceFactory.createDataSource(pro);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataSource getDs(){
		return ds;
	}
	
	public static void main(String args[]) {
		for(int i=0;i<60;i++){
			Connection con = DBPool.getConnection();
			System.out.println("create");
			close(con);
			System.out.println("close");
		}
		System.out.println("end");
	}

	public static Connection getConnection() {
		Connection con = connMap.get();
		try {
			if(con==null)
			{
				con = ds.getConnection();
				connMap.set(con);
			}
		} catch (SQLException e) {
			System.out.println("获取数据库连接失败....");
			e.printStackTrace();
		}
		return con;
	}

	public static void close(Connection con) {
		/*try {
			//con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void closeConn(Connection con) {
		try {
			Connection conn = connMap.get();
			if(conn!=null)
				con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			connMap.remove();
		}
	}
}
