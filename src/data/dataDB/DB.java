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
		
		String url = "jdbc:mysql://localhost:3306/" + _dbName;    //JDBC��URL    
        //����DriverManager�����getConnection()���������һ��Connection����
        
        Connection conn = DriverManager.getConnection(url,_userName,_pwd);
        //����һ��Statement����
        stmt = conn.createStatement(); //����Statement����
        
        Panel.show("�������ݿ�ɹ�");
        
        DB_user.init(_serviceClass,_unitClass);
	}
}
