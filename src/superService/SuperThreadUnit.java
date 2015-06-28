package superService;

import java.lang.reflect.Method;

public class SuperThreadUnit extends Thread{

	private SuperService service;
	
	public SuperThreadUnit(SuperService _service){
		
		service = _service;
	}
	
	public void run(){
		
		doService(service);
	}
	
	static void doService(SuperService _service){
		
		while(true){
			
			Method method;
			Object[] arg;
			
			synchronized(_service){
				
				if(_service.methodArr.size() == 0){
					
					_service.inQueue = false;
					
					return;
				}
				
				method = _service.methodArr.remove(0);
				arg = _service.argArr.remove(0);
			}
			
			try{
				
				_service.locker.lock();
				
				method.invoke(_service, arg);
				
				_service.locker.unlock();
				
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}
}
