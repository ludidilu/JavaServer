package server;

import panel.Panel;

public class Server {

	private static int port = 1983;
	private static Server_main server = new Server_main();
//	private static Server_main server2 = new Server_main();//因为debug才开了2个线程
	
	public static void init() throws Exception{
		
		Server_thread_service.init();
		
		server.init(port);
		
		server.start();
		
		Panel.show("等待客户端连接");
	}
}
