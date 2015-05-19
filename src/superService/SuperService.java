package superService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class SuperService {

	public boolean inQueue = false;
	
	public ArrayList<Method> methodArr = new ArrayList<>();
	public ArrayList<Object[]> argArr = new ArrayList<>();
	
	protected Map<String, Method> getMethodMap(){
		
		return null;
	}
	
	public void process(String _methodName,Object..._arg){
		
		synchronized (this) {
			
			Map<String, Method> methodMap = getMethodMap();
			
			if(methodMap == null){
				
				System.err.println("methodMap is null! Class = " + this.getClass().toString());
			}
			
			Method method = methodMap.get(_methodName);
			
			if(method == null){
				
				System.err.println("Method is null. Method Class = " + this.getClass().toString() + "  name = " + _methodName);
			}
			
			methodArr.add(method);
			argArr.add(_arg);
			
			if(!inQueue){
				
				inQueue = true;
				
				SuperThread.process(this);
			}
		}
	}
}
