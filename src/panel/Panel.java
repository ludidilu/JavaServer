package panel;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Panel {
	
	private static JFrame panel;
	private static String str = "";
	
	private static JLabel jl;
	
	private static String str1 = "<html><body>";
	private static String str2 = "</body></html>";
	
	public static void init(){
		
		panel = new JFrame("Server");
		
		panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel.setSize(500,500);
		
		panel.setVisible(true);
		
		jl = new JLabel();
		jl.setVerticalAlignment(JLabel.TOP);
		jl.setHorizontalAlignment(JLabel.CENTER);
		
		panel.add(jl);
		
		jl.setText(str);
	}
	
	public static void show(String _str){
		
		str = str + "<br>" + _str;
		
		jl.setText(str1 + str + str2);
	}

}
