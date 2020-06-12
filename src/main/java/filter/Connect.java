package filter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connect {
	static Map<String, String> config;
	private static String dbURL;
	private static String server;
	private static String host;
	private static String database;
	private static String userName;
	private static String Password;
	private static String forName;
	private static Connection conn = null;

	public Connect() {
		init();
	}

	// connect mySQL
	public static Connection getConnection() {
		System.err.println("FORNAME = " + forName);
		System.err.println("DBURL = " + dbURL);
		System.err.println("USERNAME = " + userName);
		System.err.println("PASSWORD = " + Password);
		try {
			Class.forName(forName).newInstance();
			Connection connection = DriverManager.getConnection(dbURL, userName, Password);
			return connection;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void init() {
		config = FileService.readConfig(new File("files/config.dat"), "-->");
		server = config.get("server").trim();
		host = config.get("host").trim();
		database = config.get("database").trim();
		userName = config.get("userName").trim();
		Password = config.get("password").trim();
		forName = config.get("forName").trim();
		dbURL = server + "://" + host + "/" + database + "?autoReconnect=true&useSSL=false";
	}

	public void Insert(String link, String code) {
		try {
			if (conn == null) {
				conn = getConnection();
			}

			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(
					"insert into `CodeData`.`Data`(`link`, `code`) values ('" + link + "','" + code + "');");
			int status = ps.executeUpdate();
			if (status != 0) {
				System.err.println("add " + link + " successully!");
			}
//			CallableStatement cstm = conn.prepareCall("{CALL `insertData`(?,?);");
//			System.err.println(link + " -> " + code);
//			cstm.setString(1, link);
//			cstm.setString(2, code);
//			cstm.executeUpdate();

		} catch (SQLException e) {
			System.err.println("add data failure!");
			System.err.println(e.getMessage());
		}
	}
	
	public void Insert_codeData(String link, String problem, String sufLink, String code) {
		try {
			if (conn == null) {
				conn = getConnection();
			}

			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(
					"insert into `CodeData`.`codeData`(`link`,`problem` , `sufLink`, `code`) values ('" + link + "','" + problem + "','" + sufLink + "','" + code + "');");
			int status = ps.executeUpdate();
			if (status != 0) {
				System.err.println("add " + link + " successully!");
			}
//			CallableStatement cstm = conn.prepareCall("{CALL `insertData`(?,?);");
//			System.err.println(link + " -> " + code);
//			cstm.setString(1, link);
//			cstm.setString(2, code);
//			cstm.executeUpdate();

		} catch (SQLException e) {
			System.err.println("add data failure!");
			System.err.println(e.getMessage());
		}
	}

	public Map<String,String> getAllData() {
		String sql = "select `data`.`link`, `data`.`code` from `codedata`.`data` order by `link`;";
		Map<String,String> mResult = new HashMap<>();

		try {
			if (conn == null) {
				conn = getConnection();
			}
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				String link = resultSet.getString(1);
				String code = resultSet.getString(2);
				if(!link.isEmpty() && !code.isEmpty()) {
					mResult.put(link, code);					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mResult;
	}
	
	public List<String> getAllLink() {
		String sql = "select `data`.`link` from `codedata`.`data`;";
		List<String> res = new ArrayList<>();

		try {
			if (conn == null) {
				conn = getConnection();
			}
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				res.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
