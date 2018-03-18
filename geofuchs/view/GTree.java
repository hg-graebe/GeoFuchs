/*
 * Created on 06.09.2004
 */
package geofuchs.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
import javax.swing.tree.*;
import java.util.ArrayList;
import geofuchs.control.GeoStrings;
import geofuchs.control.langStrings;

/**
 * This class represents trees which can be displayed 
 * in swing-based GUI's. In Geofuchs, it's used to show
 * a tree representation of the active construction.
 * @author Andy Stock
 */

public class GTree extends JTree implements MouseListener, MouseMotionListener 
{
	
	private GMainFrame parentWindow; 

	/**
	 * @param root The root node.
	 * @param parent The parent window.
	 */
	public GTree(DefaultMutableTreeNode root, GMainFrame parent)
	{

		super(root);
		parentWindow=parent;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setExpandsSelectedPaths(true);
		this.setExpandsSelectedPaths(true);

	}

	

	/**
	 * The callback method for mouse clicks.
	 * @see java.awt.event.MouseListener
	 */
	public void mouseClicked(MouseEvent arg0) 
	{
		if(parentWindow.getActiveConstruction()==null) return;
		
		//get the selected objects' names
		ArrayList matchingObjects=parentWindow.getActiveConstruction().getObjectsOfType(this.getSelectionPath().getLastPathComponent().toString());
		//single geometric object, not type, selected -- add it to the matching
		//objects list
		if(matchingObjects.size()==0)	
			matchingObjects.add(this.getSelectionPath().getLastPathComponent().toString());
		
		//Mode: SHOWNAME
		if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.SHOWNAME)
			parentWindow.getActiveConstruction().showNames(matchingObjects);
		//Mode: SETNAME
		else if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.SETNAME)
		{
			//get new name and check if it's already in use - if yes, show the dialogue again
			String selection=this.getSelectionPath().getLastPathComponent().toString();
			//get the selection candidates; if there are any, the selected tree node wasn't an object, but a type
			//in case there's only one, rename it, otherwise return
			ArrayList selections=parentWindow.getActiveConstruction().getObjectsOfType(selection);
			if(selections.size()!=0) return;
			//show dialogue until the input is correct
			while(true)
			{
					String newName=JOptionPane.showInputDialog(this, selection+" "+langStrings.q_rename+": "+langStrings.q_newname, selection);
					if(parentWindow.getActiveConstruction().setObjectName(selection, newName)) break;
			}
		}
		//Mode: SETCOLOR
		else if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.SETCOLOR)
			parentWindow.getActiveConstruction().setColor(matchingObjects, parentWindow.currentColor);
		//Mode: HIDE
		else if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.EMPH0)
			parentWindow.getActiveConstruction().setEmph(matchingObjects, 0);
		//Mode: NORMALSIZE
		else if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.EMPH1)
			parentWindow.getActiveConstruction().setEmph(matchingObjects, 1);		
		//Mode: EMPHASIZE
		else if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.EMPH2)
			parentWindow.getActiveConstruction().setEmph(matchingObjects, 2);	
		//Mode: EMPHASIZE MORE
		else if(arg0.getButton()==MouseEvent.BUTTON3 &&  parentWindow.getMode()==GeoStrings.EMPH3)
			parentWindow.getActiveConstruction().setEmph(matchingObjects, 3);	
		//repaint all associated construction windows
		parentWindow.refresh();

	}

	/**
	 * This callback method does absolutely nothing.
	 * @see java.awt.event.MouseListener
	 */
	public void mousePressed(MouseEvent arg0) {parentWindow.refresh();}

	/** This callback method which does nothing at all. */
	public void mouseReleased(MouseEvent arg0) {}

	/** This callback method which does nothing at all.*/
	public void mouseEntered(MouseEvent arg0) {}

	/** This callback method which does nothing at all.*/
	public void mouseExited(MouseEvent arg0) {}

	/** This callback method which does nothing at all.*/
	public void mouseDragged(MouseEvent arg0) {}

	/**
	 * This method is called whenever the mouse is moved over the tree.
	 * If the mouse cursor is at a single geometric opbject's name,
	 * this object is highlighted.
	 */
	public void mouseMoved(MouseEvent arg0) {

		this.setSelectionPath(this.getClosestPathForLocation(arg0.getX(),arg0.getY()));
		if(parentWindow.getActiveConstruction()!=null)
			parentWindow.getActiveConstruction().highlightObjectByName((this.getSelectionPath().getLastPathComponent()).toString());
		parentWindow.refresh();	
	}
}
