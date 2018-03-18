/* $Id: GChildFrame.java,v 1.2 2005/08/21 20:29:57 hg-graebe Exp $ */
package geofuchs.view;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import java.awt.event.*;
import java.awt.*;

import geofuchs.control.*;
import geofuchs.model.*;

/** GChildFrame is a JInternalFrame which includes a coordinate
 * area. It's associated with one and only one construction, and
 * also knows the superordinate GMainFrame. The real coordinate area
 * is GCoordinateArea, which is the Frame's core.
*/
public class GChildFrame extends JInternalFrame 
    implements InternalFrameListener {

    private static int openFrameCount = 0;
    private static final int xOffset = 30, yOffset = 30;

    //the construction
    private GeoConstruction myConstruction;
    //the parent frame
    private GMainFrame parent;
    //the status bar
    private GStatusBar statusBar;
    //the coordinate area
    private GCoordinateArea iArea;

    /* getter methods */

    public GeoConstruction getConstruction() {
	return myConstruction;
    }

    public GMainFrame getMainFrame() {
	return parent;
    }

    /** Forwards the call to the associated instance of GCoordinateArea. 
     * @see geofuchs.view.GCoordinateArea
     */
    public void zoomIn(){
	    iArea.zoomIn();
    }

    /** Forwards the call to the associated instance of GCoordinateArea. 
     * @see geofuchs.view.GCoordinateArea
     */
    public void zoomOut(){
	    iArea.zoomOut();
    }

    /** Forwards the call to the associated instance of GCoordinateArea. 
     * @see geofuchs.view.GCoordinateArea
     */
    public void resetView(){
	    iArea.resetView();
    }

    /** Forwards the call to the associated instance of GCoordinateArea. 
     * @see geofuchs.view.GCoordinateArea
     */
    public void switchGrid(){
	    iArea.switchGrid();
    }

    /** Forwards the call to the associated instance of GCoordinateArea. 
     * @see geofuchs.view.GCoordinateArea
     */
    public void switchCoordAxes(){
	    iArea.switchCoords();
    }

    /* -- HGG: obsolete ?
    * 
    * @param caption The frame's caption.
    *
    protected  GChildFrame(String caption){
	super(caption, 
	      true, //resizable
	      true, //closable
	      true, //maximizable
	      true);//iconifiable
	addInternalFrameListener(this);
	//set default rendering hints
	RenderingHints renderingHints=new RenderingHints(null);
	renderingHints.put(RenderingHints.KEY_ANTIALIASING, 
			   RenderingHints.VALUE_ANTIALIAS_ON);
	renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, 
			   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	setRenderingHints(renderingHints);
	} */

    /**
    @param g The frame's construction.
    @param p The main frame. 
    */
    public GChildFrame(GeoConstruction g,GMainFrame p) {
	// -- HGG: g knows the caption
	super(g.getViewCaption(), 
	      true, //resizable
	      true, //closable
	      true, //maximizable
	      true);//iconifiable

	//some initialization stuff

	// -- HGG: why do we need that?
	addInternalFrameListener(this);
	this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	myConstruction = g;
	setSize(500,500);
	parent = p;
	openFrameCount++;

	//Set the window's position.
	setLocation(xOffset*openFrameCount, yOffset*openFrameCount);

	//create the status bar
	statusBar = new GStatusBar(3);
	iArea = new GCoordinateArea(this);

	JPanel contentPane = new JPanel();
	contentPane.setLayout(new BorderLayout());
	contentPane.setPreferredSize(new Dimension(400, 100));
	contentPane.add(iArea, BorderLayout.CENTER);
	contentPane.add(statusBar.getStatusBar(), BorderLayout.NORTH);
	JPanel horPane = new JPanel();

	//add the buttons
	horPane.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
	JButton button ;	

	//coordinate axes on/off
	button = new JButton(parent.getImageIcon("img/p_coords.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    switchCoordAxes();  } });
	horPane.add(button);

	//grid on/off
	button = new JButton(parent.getImageIcon("img/p_grid.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    switchGrid(); }});
	horPane.add(button);

	//reset view
	button = new JButton(parent.getImageIcon("img/p_resetView.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    resetView(); } });
	horPane.add(button);

	//zoom in
	button = new JButton(parent.getImageIcon("img/p_zoomin.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    zoomIn();  } });
	horPane.add(button);

	//zoom out
	button = new JButton(parent.getImageIcon("img/p_zoomout.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    zoomOut();  } });
	horPane.add(button);

	//scroll left
	button = new JButton(parent.getImageIcon("img/p_left.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    iArea.cp.x-= GDefaults.gridPan;
		    iArea.repaint();
		}
	    });

	horPane.add(button);

	//scroll right
	button = new JButton(parent.getImageIcon("img/p_right.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    iArea.cp.x+= GDefaults.gridPan;
		    iArea.repaint();
		}
	    });
	horPane.add(button);

	//scroll up
	button = new JButton(parent.getImageIcon("img/p_up.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    iArea.cp.y-= GDefaults.gridPan;
		    iArea.repaint();
		}
	    });
	horPane.add(button);

	//scroll down
	button = new JButton(parent.getImageIcon("img/p_down.jpg"));
	button.setBackground(Color.white);
	button.setMargin(new Insets(0,0,0,0));
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    iArea.cp.y+= GDefaults.gridPan;
		    iArea.repaint();
		}
	    });
	horPane.add(button);

	//add the button bar      
	contentPane.add(horPane,BorderLayout.SOUTH);
	setContentPane(contentPane);

	//set the default rendering hints
	RenderingHints renderingHints=new RenderingHints(null);
	renderingHints.put(RenderingHints.KEY_ANTIALIASING, 
			   RenderingHints.VALUE_ANTIALIAS_ON);
	renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, 
			   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	setRenderingHints(renderingHints);

    }

    /**
     * This method sets the rendering hints for drawing the
     * associated construction.
     * @param hints The rendering hints.
     */
    public void setRenderingHints(RenderingHints hints) {
	iArea.setRenderingHints(hints);
    }

    /** This method does nothing at all.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameOpened(InternalFrameEvent arg0) {}

    /** This method does nothing at all.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameClosing(InternalFrameEvent arg0) {}

    /** This method does nothing at all.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameClosed(InternalFrameEvent arg0) {}

    /** This method does nothing at all.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameIconified(InternalFrameEvent arg0) {}

    /** This method does nothing at all.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameDeiconified(InternalFrameEvent arg0) {}

    /** This method refreshes the selection tolerance value and the
     * tree whenever a frame is activated.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameActivated(InternalFrameEvent arg0) {
	myConstruction.setTolerance
	    ((100)/(iArea.zoomfactor*iArea.zoomfactor));
	parent.mbs1();
    }

    /** This method forwards the event to the construction.
     * @see javax.swing.event.InternalFrameListener
     */
    public void internalFrameDeactivated(InternalFrameEvent arg0) {
	myConstruction.focusLost();	
	parent.mbs1();
    }

}

