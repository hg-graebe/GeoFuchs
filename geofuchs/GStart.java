package geofuchs;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import geofuchs.view.GMainFrame;
/**The main applet class. */
public class GStart extends JApplet {
				
	JButton but;
	JFrame frame;
	public static URL path = null;
	Action action_but;
	boolean inAnApplet = true;
	
	/** Executed if the programm is started from the command line*/
	public static void main(String[] args) {
		JFrame f= new JFrame("Geo-Kontroll-Frame");
		
		f.addWindowListener(new WindowAdapter() {
		  public void windowClosing(WindowEvent e) {
	                    System.exit(0);
		  }
		});	
		GStart controller = new GStart();
		controller.frame = f;
		controller.inAnApplet = false;
		path=null;
		controller.buildUI(f.getContentPane());
		f.pack();
		f.setVisible(true);
		
	}

	/** Executed if the programm is started as an applet*/
	public void init() {
		path = getCodeBase();	
		buildUI(getContentPane());

	}
	/** Closing everything if the main window is closed*/
	void closeWindow(){
		if(!inAnApplet)
			System.exit(0);	
	}
	
	/** Builds the user interface elements*/
	public void buildUI(Container container) {
	      
		//Creating the Actions for the Buttons
		action_but = new AbstractAction("Start GeoFuchs",null){
		public void actionPerformed(ActionEvent e) {
			GMainFrame frame = new GMainFrame(path);
			frame.setVisible(true);
			}
		};
		;
				
		but = new JButton(action_but);
			
		//simple Grid Layout
		container.setLayout(new GridLayout(0,1));
		container.add(but);
	}

}
