
package geofuchs.control;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* A base-class for GCoordinateArea. Its purpose is to implement the
* keyboard- and mouse listeners. Subclasses derive from this class and may overload
* the mouse or keyboard events in order to suit their task.
*/
public class GInteractionArea extends JPanel implements MouseMotionListener,KeyListener,MouseListener
{
	/** The preferred size of the panel
	 */
	Dimension preferredSize = new Dimension(300,300);
	
	/** adds mouse and keyboard listeners */
	public GInteractionArea() {
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		requestFocus();
	}
	
	/**
	 * @return The panel's preferred size.
	 */
	public Dimension getPreferredSize() {
		return preferredSize;
	}
	
	/**
	 * A dummy method, which does nothing at all.
	 */
	public void keyPressed(KeyEvent e) {};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void keyTyped(KeyEvent e) {};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void keyReleased(KeyEvent e) {};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void mouseDragged(MouseEvent e) {};
	
	/** requests the focus every time the mouse is moved */
	public void mouseMoved(MouseEvent e) { 
		requestFocus(); 
	}

	/**
	 * A dummy method, which does nothing at all.
	 */
	public void	mouseClicked(MouseEvent	e){};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void	mousePressed(MouseEvent	e){};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void	mouseReleased(MouseEvent e){};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void 	mouseEntered(MouseEvent	e){};
	/**
	 * A dummy method, which does nothing at all.
	 */	
	public void	mouseExited(MouseEvent e){};
	
	/**
	 * The standard paintComponent method.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);  //paint background
	}

}
