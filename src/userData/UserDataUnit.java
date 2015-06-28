package userData;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.json.JSONObject;

public class UserDataUnit implements Serializable{
	
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
	
	public JSONObject getJSONObject() throws Exception{
		
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
