package data.dataDB.user;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import server.Server_thread_service;
import superService.SuperService;
import userData.UserData;
import data.dataDB.DB;


public class DB_user extends SuperService{
	
	private static String PLAYER_NAME_SET = "player_name_set";
	public static String PWD = "pwd_";
	public static String PLAYER_DATA = "player_data_";
	
	private static Constructor<?> serviceCons;
	private static Constructor<?> userDataCons;
	
	private static HashMap<String, Method> methodMap;
	
	protected HashMap<String, Method> getMethodMap(){
		
		return methodMap;
	}
	
	public static DB_user instance;
	
	public static void init(Class<?> _serviceClass,Class<?> _userDataCls) throws Exception{
		
		instance = new DB_user();
		
		serviceCons = _serviceClass.getConstructor(DB_user_unit.class);
		
		userDataCons = _userDataCls.getConstructor();
		
		methodMap = new HashMap<>();
		
		methodMap.put("login",DB_user.class.getDeclaredMethod("login",String.class,String.class));
		methodMap.put("logout", DB_user.class.getDeclaredMethod("logout", String.class));
//		methodMap.put("getUserData", DB_user.class.getDeclaredMethod("getUserData", String.class));
	}
	
	private HashMap<String, DB_user_unit> userMapByName = new HashMap<>();
	
	public Server_thread_service login(String _name, String _pwd) throws Exception{
		
		DB_user_unit unit = userMapByName.get(_name);
		
		if(unit != null){
			
			if(unit.pwd.equals(_pwd)){
				
				return unit.service;
				
			}else{
				
				return null;
			}
			
		}else{
			
			unit = getUnitFromDB(_name, _pwd);
			
			if(unit != null){
				
				userMapByName.put(_name, unit);
				
				return unit.service;
				
			}else{
				
				return null;
			}
		}
	}
	
	public void logout(String _name) throws Exception{
		
		DB_user_unit unit = userMapByName.get(_name);
		
		unit.userData.saveDataToDB(_name);

		userMapByName.remove(_name);
	}
	
//	public UserData getUserData(String _name){
//		
//		
//	}
	
	private DB_user_unit getUnitFromDB(String _name, String _pwd) throws Exception{
		
		String pwd = DB.jedis.get(PWD + _name);
		
		if(pwd == null){
			
			DB_user_unit unit = new DB_user_unit();
			
			unit.name = _name;
			
			DB.jedis.sadd(PLAYER_NAME_SET, _name);
			
			unit.pwd = _pwd;
			
			DB.jedis.set(PWD + _name,_pwd);
			
			unit.service = (Server_thread_service) serviceCons.newInstance(unit);
			
			unit.userData = (UserData) userDataCons.newInstance();
			
			unit.userData.initAllData();
			
			unit.userData.saveAllDataToDB(_name);
			
			return unit;
			
		}else{
			
			if(pwd.equals(_pwd)){
				
				DB_user_unit unit = new DB_user_unit();
				
				unit.name = _name;
				
				unit.pwd = pwd;
				
				unit.service = (Server_thread_service)serviceCons.newInstance(unit);
				
				unit.userData = (UserData) userDataCons.newInstance();
				
				unit.userData.loadAllDataFromDB(_name);
				
				return unit;
				
			}else{
				
				return null;
			}
		}
	}
}
