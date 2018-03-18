
package geofuchs.view;


import javax.swing.JDesktopPane;


import java.awt.*;

/** This is a subclass of JDesktopPane and is used
 * instead of it in Geofuchs. */
class GDesktopPane extends JDesktopPane
{
	Toolkit toolkit;
	Image image1;
	Color col;
	/**
	 * Calls the superclass JDesktopPane's constructor and calculates
	 * a random color.
	 * @see javax.swing.JDesktopPane
	 */
	public GDesktopPane()
	{
		super();
		col = new Color( (int)(Math.random()*255*0.2),(int)(Math.random()*255*0.2),(int)(Math.random()*255*0.2) );
	}
	/**
	 * Draws the desktop to the random color computed by the constructor
	 * method.
	 */
	public void paintComponent(Graphics g) 
	{
		Dimension dim = getSize();
		g.setColor(col);
		g.fillRect(0,0,dim.width,dim.height);
		
	}
	

}
