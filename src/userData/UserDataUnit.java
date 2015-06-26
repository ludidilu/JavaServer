package userData;

import java.lang.reflect.Field;

import org.json.JSONObject;

import data.dataDB.DB;
import data.dataDB.user.DB_user;
import data.dataDB.user.DB_user_unit;

public class UserDataUnit {

	private String name;
	private DB_user_unit user_unit;
	
	public UserDataUnit(DB_user_unit _user_unit,String _name){
		
		user_unit = _user_unit;
		name = _name;
	}
	
	private boolean dirty = false;
	
	public void setDirty() throws Exception{
		
		if(!dirty){
			
			dirty = true;
			
			saveDataToDB();
		}
	}
	
	public boolean getDirty(){
		
		return dirty;
	}
	
	public void setData(String _str) throws Exception{
		
		JSONObject obj = new JSONObject(_str);
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){

			field.set(this, obj.get(field.getName()));
		}
	}
	
	public JSONObject getData() throws Exception{
		
		JSONObject obj = new JSONObject();
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			String attName = field.getName();
			
			obj.put(attName, field.get(this));
		}
		
		dirty = false;
		
		return obj;
	}
	
	public void saveDataToDB() throws Exception{
		
		JSONObject obj = new JSONObject();
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			String attName = field.getName();
			
			obj.put(attName, field.get(this));
		}
		
		DB.jedis.set(DB_user.PLAYER_DATA + name + "_" + user_unit.name, obj.toString());
	}
}
