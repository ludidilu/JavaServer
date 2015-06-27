package data.dataDB.user;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

import server.Server_thread_service;
import userData.UserData;
import userData.UserDataUnit;
import data.dataDB.DB;


public class DB_user {
	
	private static String PLAYER_NAME_SET = "player_name_set";
	private static String PWD = "pwd_";
	public static String PLAYER_DATA = "player_data_";
	
	private static HashMap<String, DB_user_unit> userMapByName = new HashMap<>();
	
	private static Constructor<?> serviceCons;
	private static Class<?> userDataCls;
	
	public static void init(Class<?> _serviceClass,Class<?> _userDataCls) throws Exception{
		
		serviceCons = _serviceClass.getConstructor(DB_user_unit.class);
		
		userDataCls = _userDataCls;
	}
	
	public static Server_thread_service login(String _name, String _pwd) throws Exception{
		
		synchronized (userMapByName) {
			
			DB_user_unit unit = userMapByName.get(_name);
			
			if(unit != null){
				
				if(unit.pwd.equals(_pwd)){
					
					return unit.service;
					
				}else{
					
					return null;
				}
				
			}else{
				
				return getService(_name,_pwd);
			}
		}
	}
	
	public static void logout(String _name) throws Exception{
		
		synchronized (userMapByName) {
			
			DB_user_unit unit = userMapByName.get(_name);
			
			unit.userData.saveDataToDB(_name);

			userMapByName.remove(_name);
		}
	}
	
	private static Server_thread_service getService(String _name, String _pwd) throws Exception{
		
		String pwd = DB.jedis.get(PWD + _name);
		
		if(pwd == null){
			
			DB_user_unit unit = new DB_user_unit();
			
			unit.name = _name;
			
			DB.jedis.sadd(PLAYER_NAME_SET, _name);
			
			unit.pwd = _pwd;
			
			DB.jedis.set(PWD + _name,_pwd);
			
			unit.service = (Server_thread_service) serviceCons.newInstance(unit);
			
			unit.userData = (UserData) userDataCls.getConstructor().newInstance();
			
			Field[] fields = userDataCls.getFields();
			
			for(Field field : fields){

				Class<?> cls = field.getType();
				
				Constructor<?> cons = cls.getConstructor();
				
				UserDataUnit userDataUnit = (UserDataUnit) cons.newInstance();
				
				field.set(unit.userData, userDataUnit);
					
				userDataUnit.init();
			}
			
			unit.userData.saveAllDataToDB(_name);
			
			userMapByName.put(_name, unit);
			
			return unit.service;
			
		}else{
			
			if(pwd == _pwd){
				
				DB_user_unit unit = new DB_user_unit();
				
				unit.name = _name;
				
				unit.pwd = DB.jedis.get(PWD + _name);
				
				unit.service = (Server_thread_service)serviceCons.newInstance(unit);
				
				Constructor<?> userDataCons = userDataCls.getConstructor();
				
				unit.userData = (UserData) userDataCons.newInstance();
				
				Field[] fields = userDataCls.getFields();
				
				for(Field field : fields){
					
					Class<?> cls = field.getType();
					
					Constructor<?> userDataUnitCons = cls.getConstructor();
					
					UserDataUnit userDataUnit = (UserDataUnit)userDataUnitCons.newInstance();
					
					userDataUnit.setData(DB.jedis.get(PLAYER_DATA + field.getName() + "_" + _name));
					
					field.set(unit.userData, userDataUnit);
				}
				
				userMapByName.put(_name, unit);
				
				return unit.service;
				
			}else{
				
				return null;
			}
		}
	}
}
