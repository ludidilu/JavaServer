package userData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.json.JSONObject;

import publicTools.SerializeUtil;
import data.dataDB.DB;
import data.dataDB.user.DB_user;

public class UserData {
	
	public String getSyncData() throws Exception{
		
		JSONObject obj = null;
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			UserDataUnit dataUnit = (UserDataUnit)field.get(this);
			
			if(dataUnit.getDirty()){
				
				if(obj == null){
					
					obj = new JSONObject();
				}
				
				obj.put(field.getName(), dataUnit.getJSONObject());
			}
		}

		if(obj != null){
		
			return obj.toString();
			
		}else{
			
			return null;
		}
	}
	
	public String getAllData() throws Exception{
		
		JSONObject obj = new JSONObject();
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			UserDataUnit dataUnit = (UserDataUnit)field.get(this);
			
			obj.put(field.getName(), dataUnit.getJSONObject());
		}
		
		return obj.toString();
	}
	
	public void saveDataToDB(String _name) throws Exception{
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			UserDataUnit userDataUnit = (UserDataUnit) field.get(this);
			
			if(userDataUnit.getDBDirty()){
				
				byte[] bytes = SerializeUtil.serialize(userDataUnit);
				
				DB.jedis.set((DB_user.PLAYER_DATA + field.getName() + "_" + _name).getBytes(), bytes);
			}
		}
	}
	
	public void saveAllDataToDB(String _name) throws Exception{
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			byte[] bytes = SerializeUtil.serialize(field.get(this));
			
			DB.jedis.set((DB_user.PLAYER_DATA + field.getName() + "_" + _name).getBytes(), bytes);
		}
	}
	
	public void initAllData() throws Exception{
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){

			Class<?> cls = field.getType();
			
			Constructor<?> cons = cls.getConstructor();
			
			UserDataUnit userDataUnit = (UserDataUnit) cons.newInstance();
			
			field.set(this, userDataUnit);
				
			userDataUnit.init();
		}
	}
	
	public void loadAllDataFromDB(String _name) throws Exception{
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			UserDataUnit userDataUnit = (UserDataUnit)SerializeUtil.unserialize(DB.jedis.get((DB_user.PLAYER_DATA + field.getName() + "_" + _name).getBytes()));
			
			field.set(this, userDataUnit);
		}
	}
	
	
}
