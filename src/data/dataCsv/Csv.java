package data.dataCsv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import data.dataCsv.connect.Csv_connect;

public class Csv {

	public int id;
	
	private static String path;
	
	private static HashMap<String, Class<?>> clsMap;
	
	public static void init(String _path,Class<?> _cls) throws Exception{
		
		clsMap = new HashMap<>();
		
		clsMap.put("str", String.class);
		clsMap.put("int", int.class);
		clsMap.put("boo", boolean.class);
		
		Csv_connect.init(_cls);
		
		path = _path;
		
		setData(Csv_connect.class, "connect", Csv_connect.class.getDeclaredMethod("setMethod"));
	}
	
	public static void setData(Class<?> cls,String _name,Method _fixMethod) throws Exception{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path + _name + ".csv"),"UTF-8"));
		
		Constructor<?> cons = cls.getConstructor();
		
		HashMap<Integer, Csv> map = new HashMap<>();
		
		int index = 0;
		
		String[] nameVec = null;
		Field[] fieldVec = null;
		String[] strVec = null;
		String[] typeVec = null;
		
		String str = reader.readLine();
		
		while (str != null){
			
			if(str.indexOf("//") != -1){
				
				str = reader.readLine();
				
				continue;
			}
			
			if(index == 0){
				
				nameVec = str.split(",");
				
				fieldVec = new Field[nameVec.length];
				
				for(int i = 0 ; i < nameVec.length ; i++){
					
					if(nameVec[i].equals("id")){
						
						fieldVec[i] = Csv.class.getDeclaredField("id");
						
					}else{
					
						try{
						
							fieldVec[i] = cls.getDeclaredField(nameVec[i]);
							
						}catch(Exception e){
							
							
						}
					}
				}
				
			}else if(index == 1){
				
				typeVec = str.split(",");
				
			}else{
				
				Csv o = (Csv)cons.newInstance();
				
				strVec = str.split(",");
				
				for(int i = 0 ; i < nameVec.length ; i++){
					
					if(fieldVec[i] == null){
						
						continue;
					}
					
					String ss;
					
					if(i >= strVec.length){
						
						ss = "";
						
					}else{
						
						ss = strVec[i];
					}
					
					int length = (typeVec[i].length() - 3) / 2;
					
					String type = typeVec[i].substring(0, 3);
					
					Object arg = split(ss,length,type);
					
					fieldVec[i].set(o, arg);
				}
				
				if(_fixMethod != null){
					
					_fixMethod.invoke(o);
				}
				
				map.put(o.id, o);
			}
			
			index++;
			
			str = reader.readLine();
		}
		
		reader.close();
		
		Field field = cls.getDeclaredField("dic");
		
		field.set(cls, map);
	}
	
	private static Object split(String _str,int _times,String _type){
		
		if(_times == 0){
			
			switch(_type){
			
				case "int":
				
					return Integer.parseInt(_str);
					
				case "str":
				
					return _str;
				
				default:
				
					if(_str.equals("1")){
						
						return true;
						
					}else{
						
						return false;
					}
			}
		}
		
		String[] strV;
		
		if(!_str.equals("")){
			
			String splitStr = "";
			
			for(int i = 0 ; i < _times ; i++){
				
				splitStr = splitStr + "\\$";
			}
			
			strV = _str.split(splitStr);
			
		}else{
			
			strV = new String[0];
		}
		
		Class<?> cls = clsMap.get(_type);
		
		for(int i = 1 ; i < _times ; i++){
			
			cls = Array.newInstance(cls, 0).getClass();
		}
		
		Object o = Array.newInstance(cls, strV.length);
		
		for(int i = 0 ; i < strV.length ; i++){
			
			Array.set(o, i, split(strV[i], _times - 1, _type));
		}
		
		return o;
	}
}
