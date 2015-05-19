package data.dataCsv.connect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;

import data.dataCsv.Csv;

public class Csv_connect extends Csv{

	private static Class<?> cls;
	
	public static void init(Class<?> _cls){
		
		cls = _cls;
	}
	
	public static HashMap<Integer, Csv_connect> dic = new HashMap<>();
	
	public String methodName;
	public Method method;
	public String[] arg;
	public int type;
	
	public void setMethod(){
		
		Class<?>[] classVec = new Class<?>[arg.length];
		
		for(int i = 0 ; i < arg.length ; i++){
			
			int length = arg[i].length();
			
			if(length == 3){
				
				switch(arg[i]){
				
				case "int":
					
					classVec[i] = int.class;
					
					break;
					
				case "str":
					
					classVec[i] = String.class;
					
					break;
					
				default:
				
					classVec[i] = boolean.class;
				}
					
			}else{
				
				String str = arg[i].substring(0, 3);
				
				int times = (length - 3) / 2;
				
				classVec[i] = getClass(times, str);
			}
		}
		
		try{
		
			method = cls.getDeclaredMethod(methodName, classVec);
			
		}catch(Exception e){
			
//			e.printStackTrace();
		}
	}
	
	private static Class<?> getClass(int _times,String _type){
		
		if(_times > 1){
			
			Class<?> cls;
			
			switch(_type){
			
			case "str":
				
				cls = String[].class;
				
				break;
				
			case "int":
				
				cls = int[].class;
				
				break;
				
			default:
				
				cls = boolean[].class;
			}
			
			for(int i = 1 ; i < _times ; i++){
				
				cls = Array.newInstance(cls, 0).getClass();
			}
			
			return cls;
			
		}else{
			
			switch(_type){
			
			case "int":
				
				return int[].class;
				
			case "str":
				
				return String[].class;
				
			default:
			
				return boolean[].class;
			}
		}
	}
}
