package data.dataDB.user;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import panel.Panel;
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
		
		Set<String> nameSet = DB.jedis.smembers(PLAYER_NAME_SET);
		
		for(String playerName : nameSet){
			
			DB_user_unit user = new DB_user_unit();
			
			user.name = playerName;
			
			user.pwd = DB.jedis.get(PWD + playerName);
			
			user.service = (Server_thread_service)serviceCons.newInstance(user);
			
			Constructor<?> userDataCons = _userDataCls.getConstructor();
			
			user.userData = (UserData) userDataCons.newInstance();
			
			Field[] fields = _userDataCls.getFields();
			
			for(Field field : fields){
				
				Class<?> cls = field.getType();
				
				Constructor<?> userDataUnitCons = cls.getConstructor(DB_user_unit.class,String.class);
				
				UserDataUnit userDataUnit = (UserDataUnit)userDataUnitCons.newInstance(user,field.getName());
				
				userDataUnit.setData(DB.jedis.get(PLAYER_DATA + field.getName() + "_" + playerName));
				
				field.set(user.userData, userDataUnit);
			}
			
			userMapByName.put(playerName, user);
		}
		
        Panel.show("user±Ì∂¡»°ÕÍ±œ");
	}
	
	public static DB_user_unit getUserByName(String _name){
		
		return userMapByName.get(_name);
	}
	
	public static Server_thread_service login(String _name, String _pwd) throws Exception{
		
		DB_user_unit unit = getUserByName(_name);
		
		if(unit != null){
			
			if(unit.pwd.equals(_pwd)){
				
				return unit.service;
				
			}else{
				
				return null;
			}
			
		}else{
			
			unit = new DB_user_unit();
			
			unit.name = _name;
			
			DB.jedis.sadd(PLAYER_NAME_SET, _name);
			
			unit.pwd = _pwd;
			
			DB.jedis.set(PWD + _name,_pwd);
			
			unit.service = (Server_thread_service) serviceCons.newInstance(unit);
			
			unit.userData = (UserData) userDataCls.getConstructor().newInstance();
			
			Field[] fields = userDataCls.getFields();
			
			for(Field field : fields){

				Class<?> cls = field.getType();
				
				Constructor<?> cons = cls.getConstructor(DB_user_unit.class,String.class);
				
				UserDataUnit userDataUnit = (UserDataUnit) cons.newInstance(unit,field.getName());
				
				field.set(unit.userData, userDataUnit);
					
				Field[] subFields = cls.getFields();
				
				for(Field subField : subFields){
					
					Class<?> subcls = subField.getType();
					
					try{

						Constructor<?> subCons = subcls.getConstructor();
						
						subField.set(userDataUnit, subCons.newInstance());
						
					}catch(Exception e){
						
						
					}
				}
				
				userDataUnit.saveDataToDB();
			}
			
			userMapByName.put(_name, unit);
			
			return unit.service;
		}
	}
}
