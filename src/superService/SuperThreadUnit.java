package superService;

import java.lang.reflect.Method;

public class SuperThreadUnit extends Thread{

	private SuperService service;
	
	public SuperThreadUnit(SuperService _service){
		
		service = _service;
	}
	
	public void run(){

		while(service != null){
		
			Method method;
			Object[] arg;
			
			synchronized(service){
				
				method = service.methodArr.remove(0);
				arg = service.argArr.remove(0);
			}
			
			try{
				
				method.invoke(service, arg);
				
			}catch(Exception e){
				
				e.printStackTrace();
			}
			
			synchronized(service){
				
				if(service.methodArr.size() == 0){
				
					service.inQueue = false;
					
					service = null;
				}
			}
		}
	}
}
