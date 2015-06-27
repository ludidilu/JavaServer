package userData;

import java.lang.reflect.Field;

import org.json.JSONObject;

public class UserDataUnit {

	private boolean dirty = false;
	private boolean dbDirty = false;
	
	public void init(){
		
		
	}
	
	public void setDirty() throws Exception{
		
		if(!dirty){
			
			dirty = true;
		}
		
		if(!dbDirty){
			
			dbDirty = true;
		}
	}
	
	public boolean getDirty(){
		
		return dirty;
	}
	
	public boolean getDBDirty(){
		
		return dbDirty;
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
}
