package publicTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PublicTools {

	public static<T> ArrayList<T> shuffleArrayList(ArrayList<T> _arr){
		
		_arr = new ArrayList<>(_arr);
		
		ArrayList<T> arr = new ArrayList<>();
		
		while(!_arr.isEmpty()){
			
			int i = (int) (Math.random() * _arr.size());
			
			arr.add(_arr.remove(i));
		}
		
		return arr;
	}
	
	public static<T> ArrayList<T> getSomeOfArr(ArrayList<T> _arr, int _num){
		
		ArrayList<T> resultArr = new ArrayList<>();
		
		while(!_arr.isEmpty() && _num > 0){
			
			int i = (int)(Math.random() * _arr.size());
			
			T data = _arr.remove(i);
			
			resultArr.add(data);
			
			_num--;
		}
		
		return resultArr;
	}
	
	public static<T,S> HashMap<T, S> getSomeOfMap(HashMap<T, S> _map, int _num){
		
		HashMap<T, S> resultMap = new HashMap<>();
		
		ArrayList<Entry<T, S>> arr = new ArrayList<>();
		
		Iterator<Entry<T, S>> iter = _map.entrySet().iterator();
		
		while(iter.hasNext()){
			
			Entry<T, S> entry = iter.next();
			
			arr.add(entry);
		}
		
		while(!arr.isEmpty() && _num > 0){
			
			int i = (int)(Math.random() * arr.size());
			
			Entry<T, S> entry = arr.remove(i);
			
			resultMap.put(entry.getKey(), entry.getValue());
			
			_num--;
		}
		
		return resultMap;
	}
}
