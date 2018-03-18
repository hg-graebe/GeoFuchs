/* $Id: GMainFrame.java,v 1.2 2005/08/21 20:29:57 hg-graebe Exp $ */
package geofuchs.view;

import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.net.URL;
import java.io.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;

import geofuchs.control.*;
import geofuchs.model.*;

/**
* This class represents the application's main frame. It iss
* responsible for the management and construction of child frames,
* tools, menu-related events, constructions and their relationships
* to the child frames.

* @author Andy Stock,  Hans-Gert Graebe
*/

public class GMainFrame extends JFrame 
    implements ActionListener, ItemListener, InternalFrameListener {
// -- HGG: why do we need these listeners?  

// HGG: We do not need InternalFrameListener since each GChildFrame
// knows its parent.  


    /* ========== state variables ========== */

    //the currently selected tool
    private GeoTool activeTool;
    private Color pointColor=Color.BLUE;
    private Color lineColor=Color.RED;

    // -- HGG: active Frame and active Construction is inherited
    // from the active Child frame, hence now private getter methods
 
    /* ========== The main frame's parts ========== */

    private JDesktopPane desktop; // -- HGG: GDesktopPane is obsolete
    //The menu bar
    private JMenuBar menuBar;
    //The (left) tool bar
    private JToolBar toolBar;
    //The status and help bars
    private GStatusBar statusBar;
    private GStatusBar helpBar;
    //second tool bar for display options
    private JToolBar optionToolBar;
    // the codeBase 
    private URL codeBase=null;
    //The list of constructions. -- HGG: not explicitely required
    //here.  All items are known through desktop.getAllFrames()
    //counter for automatic construction name generation
    private int constructionCaptionCount = 1;

    /* ========== setter and getter methods ========== */

    /**
     * This method returns the active child frame.
     * @return The currently focused frame, or null, if there is no
     * such frame.
     */
    public GChildFrame getActiveFrame() {
	return (GChildFrame) desktop.getSelectedFrame();
    }

    /**
     * This method sets the active tool.
     */
    public void setCurrentTool(GeoTool tool) { 
	activeTool=tool; 
	if (tool!=null) statusBar.getStatusBar().repaint();
    }

    /**
     * This method returns the active construction.
     * @return The construction that is associated with the
     * currently focused frame, or null, if there is no such frame.
     */
    public GeoConstruction getActiveConstruction() {
	GChildFrame activeFrame=getActiveFrame();
	if (activeFrame==null) return null;
	return activeFrame.getConstruction();
    }

   /**
     * This method disables/enables menu bar entries according to
     * existence of an active frame. 
     */
    public void mbs1() {
	boolean state = false;
	if (getActiveFrame()!=null) state=true;		
	JMenu m = menuBar.getMenu(0);	
	JMenuItem mi; 

	for(int i=0;i<3;i++){
	    mi=m.getItem(i+1);	
	    mi.setEnabled(state);
	}
	menuBar.getMenu(2).setEnabled(state);
	menuBar.getMenu(3).setEnabled(state);
    }

    public void setStatusBarText(String text,int i,Color c) {
	statusBar.setText(text,i,c);
    } 

    // -- HGG: both do the same. Fix?
    public String getMode() { return getToolType(); }
    public String getToolType() { 
	if (activeTool==null) return null;
	return activeTool.getType(); 
    }

    /* ================= refresh main frame ================= */

    /**
     * Repaints all windows that display the active construction.
     */
    public void refresh() {
	GeoConstruction activeConstruction=getActiveConstruction();
	if (activeConstruction==null) return;
	JInternalFrame frames[] = desktop.getAllFrames();
	GChildFrame w;
	for(int i=0;i<frames.length;i++){
	    w = (GChildFrame)frames[i];
	    if (w.getConstruction()==activeConstruction) w.repaint();
	}
	// -- HGG: add hook for refreshTree here
    }

    /* ========== The main constructor ========== */

    /**
     * The constructor method creates the GUI.
     * @param _codeBase
     */
    public GMainFrame(URL codeBase) {
	setTitle("<<<<<GEOFUCHS 0.99>>>>>>");    	
	this.codeBase=codeBase;

	//Create the window
	int inset = 50;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	setBounds(inset, inset, 
		  screenSize.width - inset*2, screenSize.height-inset*2);
	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    a_exit();
		}
	    });

	//Set up the GUI.
	desktop = new JDesktopPane(); 
	helpBar = new GStatusBar(1);
	statusBar = new GStatusBar(2);

	setIconImage(getImageIcon("img/geofux.jpg").getImage());

	/*set the main window's layout */
	JPanel contentPane = new JPanel();
	contentPane.setPreferredSize(new Dimension(400, 100));    

	GridBagLayout layout=new GridBagLayout();
	contentPane.setLayout(layout);

	//help bar
	GridBagConstraints constraints=new GridBagConstraints();
	constraints.weightx=0;
	constraints.weighty=0;
	constraints.gridx=0;
	constraints.gridy=0;
	constraints.gridwidth=20;
	constraints.gridheight=2;
	constraints.fill=GridBagConstraints.BOTH;
	constraints.anchor=GridBagConstraints.NORTH;
	contentPane.add(helpBar.getStatusBar(), constraints);

	//status bar
	constraints=new GridBagConstraints();
	constraints.weightx=0;
	constraints.weighty=0;
	constraints.gridx=2;
	constraints.gridy=18;
	constraints.gridwidth=20;
	constraints.gridheight=2;
	constraints.fill=GridBagConstraints.BOTH;
	constraints.anchor=GridBagConstraints.SOUTH;
	contentPane.add(statusBar.getStatusBar(), constraints);	
	
	//desktop
	constraints=new GridBagConstraints();
	constraints.weightx=100;
	constraints.weighty=100;
	constraints.gridx=2;
	constraints.gridy=2;
	constraints.gridwidth=11;
	constraints.gridheight=16;
	constraints.fill=GridBagConstraints.BOTH;
	contentPane.add(desktop, constraints);	        

	setContentPane(contentPane);
	menuBar = new JMenuBar();
	menuBar.add(createFileMenu());
	menuBar.add(createSettingsMenu());
	menuBar.add(GToolkit.createMenu(this));
	menuBar.add(createViewMenu());
	menuBar.add(createHelpMenu());
	//menu bar state changer....if there are no child windows,
	//some entries should be disabled.
	mbs1();

	setJMenuBar(menuBar);		
	desktop.setBackground(Color.gray);
	//a_newConstruction();

    }

    /**
     * This method creates the settings menu of the menu bar.
     * @return The settings menu.
     */

    private JMenu createSettingsMenu() {
	//SETTINGS MENU
	JMenu menu = createMenu(langStrings.mi_settings, 
				langStrings.mim_settings);
	// -- HGG: Insert Menu items here
	return menu;
    }

    /**
     * This method creates the view menu of the menu bar.
     * @return The view menu.
     */

    private JMenu createViewMenu() {
	//VIEW MENU
	JMenu menu = createMenu(langStrings.mi_view, 
				langStrings.mim_view);

	///SHOW COORDINATE AXES///////////////////////////////////////
	JMenuItem menuItem = createItem(langStrings.mi_switchCoords,
			      langStrings.mim_switchCoords,
			      getImageIcon("img/p_coords.jpg"));
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getActiveFrame().switchCoordAxes(); 
		} 
	    });
	menu.add(menuItem);

	//SHOW GRID/////////////////////////////////////////////////
	menuItem = createItem(langStrings.mi_switchGrid,
			      langStrings.mim_switchGrid,
			      getImageIcon("img/p_grid.jpg"));
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getActiveFrame().switchGrid();
		} 
	    });
	menu.add(menuItem);

	///ZOOMIN//////////////////////////////////////////////////////////
	menu.addSeparator();
	menuItem = createItem(langStrings.mi_zoomIn,
			      langStrings.mim_zoomIn,
			      getImageIcon("img/p_zoomin.jpg"));        
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getActiveFrame().zoomIn();
		}
	    });
	menu.add(menuItem);

	//ZOOMOUT//////////////////////////////////////////////////////////
	menuItem = createItem(langStrings.mi_zoomOut,
			      langStrings.mim_zoomOut,
			      getImageIcon("img/p_zoomout.jpg"));      
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getActiveFrame().zoomOut();
		}
	    });
	menu.add(menuItem);

	//RESET VIEW//////////////////////////////////////////////////////
	menuItem = createItem(langStrings.mi_resetView,
			      langStrings.mim_resetView,
			      getImageIcon("img/p_resetView.jpg"));   
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getActiveFrame().resetView();
		}
	    });     
	menu.add(menuItem);
	return menu;
    }

    /**
     * This method creates the help menu of the menu bar.
     * @return The help menu.
     */
    private JMenu createHelpMenu() {
	//OPTIONS MENU
	JMenu menu = createMenu(langStrings.mi_options, 
				langStrings.mim_options);

	//ABOUT BOX////////////////////////////////////////////
	JMenuItem menuItem = createItem(langStrings.mi_about, 
					langStrings.mim_about);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    a_about();}  });	        
	menu.add(menuItem);	
	return menu;
    }

    /**
     * This method creates the file menu of the menu bar.
     * @return The file menu.
     */
    private JMenu createFileMenu() {
	JMenu menu = createMenu(langStrings.mi_file, 
				langStrings.mim_file);

 	///NEW CONSTRUCTION///////////////////////////////////////////
	JMenuItem menuItem = createItem(langStrings.mi_newConstruction, 
					langStrings.mim_newConstruction);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    a_newConstruction();
		} 
	    });
	menu.add(menuItem);

	//NEW View////////////////////////////////////////////
	menuItem = createItem(langStrings.mi_newChildFrame,
			      langStrings.mim_newChildFrame);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    a_newChildFrame();
		}
	    });
	menu.add(menuItem);

	//CLOSE CONSTRUCTION/////////////////////////////////////////
	menuItem = createItem(langStrings.mi_closeConstruction, 
			      langStrings.mim_closeConstruction);     
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    a_closeActiveConstruction();}});
	menu.add(menuItem);

	//CLOSE CHILD WINDOW/////////////////////////////////////
	menuItem = createItem(langStrings.mi_closeActiveWindow, 
			      langStrings.mim_closeActiveWindow);

	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    a_closeActiveWindow();}});
	menu.add(menuItem);

	///EXIT////////////////////////////////////////////////////////////
	menu.addSeparator();
	menuItem = createItem(langStrings.mi_exit, langStrings.mim_exit);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    a_exit();}});
	menu.add(menuItem);
	return menu;
    }

    /* ============== actions ======== */

    /**
     * Create a new child frame which displays a construction.
     * @param c The construction to be displayed.
     */

    private void createChildFrame(GeoConstruction g) {
	if (g==null) return;
	GChildFrame frame =new GChildFrame(g,this);
	frame.setVisible(true);
	desktop.add(frame);
	// frame.addInternalFrameListener(this);
	try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {}		
    }


    /**
     * Create a new construction and an associated child frame.
     *
     */
    public void a_newConstruction() {
	GeoConstruction g = new GeoConstruction
	    (langStrings.c_Construction+constructionCaptionCount+".geo");
	constructionCaptionCount++;
	createChildFrame(g);
	mbs1();
    }

    /**
     * /**
     * Create a new construction and an associated child frame.
     * @param name The construction's name.
     */
    public void a_newConstruction(String name)
    {
	GeoConstruction g = new GeoConstruction(name);
	constructionCaptionCount++;
	createChildFrame(g);
	mbs1();
    }

    /**
     * Create a new child frame which displays the active
     * construction.
     */
    public void a_newChildFrame() {
	createChildFrame(getActiveConstruction());
    }

    /**
     * Closes the active construction and all associated child frames.
     */
    public void a_closeActiveConstruction(){
	GeoConstruction activeConstruction=getActiveConstruction();
	if (activeConstruction==null) return;
	//request
	if (closeDialog(langStrings.q_AskCloseConstruction+" "
			+activeConstruction.getName())) {
	    JInternalFrame frames[] = desktop.getAllFrames();
	    GChildFrame w;
	    for(int i=0;i<frames.length;i++){
		w = (GChildFrame)frames[i];
		if (w.getConstruction()==activeConstruction) w.dispose();
	    }                
	    activeConstruction.dispose();
	    mbs1();
	}
    }

    /** Close the active child frame. If it's the last one,
     *  show a confirmation dialogue.
     */
    public void a_closeActiveWindow() {
	// -- HGG: Why this does not work?
	GChildFrame activeFrame=getActiveFrame();
	if (activeFrame==null) return;
	GeoConstruction c=activeFrame.getConstruction();
	int i=countFrames(c);
	if(i==1 && !closeDialog(langStrings.q_AskCloseLastWindow+" "
				+c.getName())) 
	    return;
	activeFrame.doDefaultCloseAction();
	if(i==1) c.dispose();
	mbs1();
    }

    /**
     * The application's heart: The exit method. Good to have guests,
     * but even better when they leave.
     */

    public void a_exit(){
	if(closeDialog(langStrings.s_suretoquit)) {
	    //normal mode
	    if (codeBase==null)	System.exit(0);
	    //applet mode
	    else this.dispose();
	}
    }

    /** Shows a brief about box. **/
    public void a_about() {	
	JOptionPane.showMessageDialog
	    (this, langStrings.q_info, langStrings.mi_about, 
	     JOptionPane.INFORMATION_MESSAGE);
    }

    /* ================= helper methods ================= */

    /**
     * used also in GChildFrame
     * @param location The image location.
     * @return The image.
     */
    public ImageIcon getImageIcon(String location)  {
	URL url=this.getClass().getResource(location);
	if (url==null) System.exit(0);
	return new ImageIcon(url);
    }

    /** Creates a dialogue which displays the given String and 
     * contains a Yes and a No button. 
     * @param s The text to display.
     * @return True, if the Yes button has been pressed, and false otherwise.
     */
    public boolean closeDialog(String s) {    
	Object[] options = {langStrings.s_yes, langStrings.s_no};
	int n = JOptionPane.showOptionDialog(this,
	    s,"",
	    JOptionPane.YES_NO_OPTION,
	    JOptionPane.QUESTION_MESSAGE,
	    null,
	    options,
	    options[0]);
	if (n == JOptionPane.YES_OPTION) return true;
	else return false;
    }	
    
    /* a hook for internationalization */

    private JMenu createMenu(String name, int mnemo) {
	JMenu menu = new JMenu(name);
	menu.setMnemonic(mnemo);
	return menu;
    }
	
    private JMenuItem createItem(String name, int mnemo) {
	JMenuItem menuItem = new JMenuItem(name);
	menuItem.setMnemonic(mnemo);
	return menuItem;
    }
	
    private JMenuItem createItem(String name, int mnemo, ImageIcon image) {
	JMenuItem menuItem = new JMenuItem(name,image);
	menuItem.setMnemonic(mnemo);
	return menuItem;
    }
	
    private int countFrames(GeoConstruction g) {
	int c=0;
	JInternalFrame frames[] = desktop.getAllFrames();
	GChildFrame w;
	for(int i=0;i<frames.length;i++) {
	    w = (GChildFrame)frames[i];
	    if(w.getConstruction().equals(g)) c++;
	}
	return c;
    }

    /**
     * @see javax.swing.filechooser.FileFilter
     * @author Andy Stock
     */
    private class XMLFilter extends FileFilter {
	public boolean accept(File f) {
	    return (f.getName().toLowerCase().endsWith(".xml") 
		    || f.isDirectory());
	}
	public String getDescription() { return "XML-Dokumente"; }
    }  

    /**
     * This is an internal class of GMainFrame which specifies
     * @see javax.swing.filechooser.FileFilter
     * @author Andy Stock
     */
    private class GEOFilter extends FileFilter {
	public boolean accept(File f) {
	    return (f.getName().toLowerCase().endsWith(".geo") 
		    ||  f.isDirectory());
	}

	public String getDescription() { return "GeoXML-Dokumente"; }
    }


    /* -- HGG: empty event handler, why do we need that? */

    public void actionPerformed(ActionEvent e) {}
    public void itemStateChanged(ItemEvent e){};

    /**
     * This callback method is called whenever the user tries
     * to close a child window.
     */
    public void internalFrameClosing(InternalFrameEvent e) {
	JInternalFrame c = (JInternalFrame)e.getInternalFrame();
	if (c==null) return;
	c.dispose();
    }

    /* Enables/disables affected menu entries. HGG: now handled
     * directly in GChildFrame. But it is possibly the very wrong
     * way to add an InternalFrameListener to the JInternalFrame
     * itself. */
    public void internalFrameClosed(InternalFrameEvent e) {}
    public void internalFrameOpened(InternalFrameEvent e) {}
    public void internalFrameIconified(InternalFrameEvent e) {}
    public void internalFrameDeiconified(InternalFrameEvent e) {}
    public void internalFrameActivated(InternalFrameEvent e) {}
    public void internalFrameDeactivated(InternalFrameEvent e) {}




}

