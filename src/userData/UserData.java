package userData;

import java.lang.reflect.Field;

import org.json.JSONObject;

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
				
				obj.put(field.getName(), dataUnit.getData());
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
			
			obj.put(field.getName(), dataUnit.getData());
		}
		
		return obj.toString();
	}
	
	public void saveDataToDB(String _name) throws Exception{
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			UserDataUnit userDataUnit = (UserDataUnit) field.get(this);
			
			if(userDataUnit.getDBDirty()){
				
				JSONObject obj = userDataUnit.getData();
				
				DB.jedis.set(DB_user.PLAYER_DATA + field.getName() + "_" + _name, obj.toString());
			}
		}
	}
	
	public void saveAllDataToDB(String _name) throws Exception{
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			UserDataUnit userDataUnit = (UserDataUnit) field.get(this);
			
			JSONObject obj = userDataUnit.getData();
				
			DB.jedis.set(DB_user.PLAYER_DATA + field.getName() + "_" + _name, obj.toString());
		}
	}
}
