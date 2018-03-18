package geofuchs.view;

import javax.swing.*;
import java.awt.*;

/** This is the GeoFuchs status bar class. */
public class GStatusBar
{
	
	Container c; //for display
	JLabel fields[]; //the number of sections
	
	/**
	 * @return The container.
	 */
	public Container getStatusBar(){return c;};

	/**
	 * @param size The number of sections.
	 */
	public  GStatusBar(int size){
		c = new Container();
		fields = new JLabel[size];
		c.setLayout(new GridLayout(1,fields.length,0,0));
		
		for(int i=0;i<size;i++)
		{
			fields[i]= new JLabel("");
			fields[i].setBackground(Color.white);
			fields[i].setVerticalAlignment(SwingConstants.CENTER);
        	fields[i].setHorizontalAlignment(SwingConstants.LEFT);
			fields[i].setVisible(true);
			c.add(fields[i]);
		}
	}
	
	/**
	 * Sets a status bar section's text.
	 * @param text The text.
	 * @param i The section's number.
	 * @param back The status bar's background color.
	 */
	public void setText(String text,int i,Color back)
	{
		if (i<0 || i>=fields.length) return;
		fields[i].setText(text);	
	}
}
