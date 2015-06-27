package superService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperThread {

	private static ExecutorService threadPool = Executors.newFixedThreadPool(4);
	
	static void process(SuperService _service){
		
		threadPool.execute(new SuperThreadUnit(_service));
	}
}
