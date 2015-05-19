package data.dataDB.user;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.HashMap;

import panel.Panel;
import server.Server_thread_service;
import data.dataDB.DB;


public class DB_user {
	
	private static HashMap<String, DB_user_unit> userMapByName = new HashMap<>();
	private static HashMap<Integer, DB_user_unit> userMapByID = new HashMap<>();

	public static void init(Class<?> _serviceClass,Class<?> _unitClass) throws Exception{
		
		Constructor<?> cons = _serviceClass.getConstructor(_unitClass);
		
		String sql = "select * from user";
        
        ResultSet rs = DB.stmt.executeQuery(sql);  
        
        while (rs.next()){
        	
        	DB_user_unit user = (DB_user_unit)_unitClass.newInstance();
        	
        	user.uid = rs.getInt("uid");
        	user.name = rs.getString("name");
        	user.pwd = rs.getString("pwd");
        	
        	user.service = (Server_thread_service)cons.newInstance(user);
        	
        	userMapByName.put(user.name, user);
        	userMapByID.put(user.uid, user);
        }
        
        Panel.show("user±Ì∂¡»°ÕÍ±œ");
	}
	
	public static DB_user_unit getUserByName(String _name){
		
		return userMapByName.get(_name);
	}
	
	public static DB_user_unit getUserByID(int _uid){
		
		return userMapByID.get(_uid);
	}
	
	public static Server_thread_service login(String _name, String _pwd) throws Exception{
		
		DB_user_unit unit = getUserByName(_name);
		
		if(unit != null){
			
			if(unit.pwd.equals(_pwd)){
				
				return unit.service;
			}
		}
		
		return null;
	}
}
