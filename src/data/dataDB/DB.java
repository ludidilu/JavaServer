package data.dataDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import panel.Panel;
import data.dataDB.user.DB_user;

public class DB {
	
	public static Statement stmt;

	public static void init(String _dbName, String _userName, String _pwd, Class<?> _serviceClass, Class<?> _unitClass) throws Exception{
		
		Class.forName("com.mysql.jdbc.Driver");
		
		String url = "jdbc:mysql://localhost:3306/" + _dbName;    //JDBC的URL    
        //调用DriverManager对象的getConnection()方法，获得一个Connection对象
        
        Connection conn = DriverManager.getConnection(url,_userName,_pwd);
        //创建一个Statement对象
        stmt = conn.createStatement(); //创建Statement对象
        
        Panel.show("连接数据库成功");
        
        DB_user.init(_serviceClass,_unitClass);
	}
}
