package userData;

import java.lang.reflect.Field;

import org.json.JSONObject;

public class UserDataUnit {

	private boolean dirty = false;
	
	public void setDirty(){
		
		if(!dirty){
			
			dirty = true;
		}
	}
	
	public void clear(){
		
		dirty = false;
	}
	
	public boolean getDirty(){
		
		return dirty;
	}
	
	public JSONObject getData() throws Exception{
		
		JSONObject obj = new JSONObject();
		
		Field[] fields = this.getClass().getFields();
		
		for(Field field : fields){
			
			String attName = field.getName();
			
			if(attName != "dirty"){
				
				obj.put(attName, field.get(this));
			}
		}
		
		return obj;
	}
}
