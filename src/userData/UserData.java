package userData;

import java.lang.reflect.Field;

import org.json.JSONObject;

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
				
				dataUnit.clear();
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
			
			dataUnit.clear();
		}
		
		return obj.toString();
	}
}
