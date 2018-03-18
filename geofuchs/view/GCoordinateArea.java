
package geofuchs.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import geofuchs.model.*;
import geofuchs.control.*;


/** This class represents coordinate areas. */
public class GCoordinateArea extends GInteractionArea {


    Point point = null;
    public Point cp = new Point(0,0);
    Dimension lastDimension=new Dimension(0,0);


    /*image for buffered rendering */
    BufferedImage bi;
    /*Graphics2D for rendering*/
    Graphics2D big;
    /* bi & big have to be recreated in case the window size is
     * changed (firstTime=true).*/
    boolean firstTime=true;
    /* The AffineTransform */
    AffineTransform at = new AffineTransform();
    /* boolean variable for view state */
    boolean bGridOn = false;
    /* boolean variable for view state */
    boolean bCoordAxesOn = false;
    /* boolean variable for view state */
    boolean bViewPoints=true;
    /* boolean variable for view state */
    boolean bShowPointInfo=true;
    /* boolean variable for view state */
    boolean bShowLineInfo=true;
    /* the current font*/
    Font f;
    /* The current rendering hints */
    RenderingHints renderingHints;
    double zoomfactor = 1;		//the original zoomfactor
    double zoomstep = 0.05;		//the zoomfactor's step width
        //last mouse position while dragging without a selected point
    Point2D.Double lastDragPos=null;	
    int lastButton;			//last button that was pressed

    private GChildFrame controller;
    private GeoConstruction construction;
    private GMainFrame parent;
    /**
     * 
     * @param controller The coordinate area's parent.
     */
    public GCoordinateArea(GChildFrame controller) {
	super();
	Font f=new Font("TimesRoman", Font.BOLD, 14);
	this.controller = controller;	
	this.construction = controller.getConstruction();	
	this.parent = controller.getMainFrame();	
    }

    /**
     * This method sets the rendering hints for drawing the
     * associated construction.
     * @param hints The rendering hints.
     */
    public void setRenderingHints(RenderingHints hints)    {
	renderingHints=hints;
	if(big!=null) big.setRenderingHints(hints);
    }

    /** Zooms in. */
    public void zoomIn(){
	if(zoomfactor<2) zoomfactor+=zoomstep;
	construction.setTolerance((100)/(zoomfactor*zoomfactor));
	//construction.setLineConstant(zoomfactor*10000+(Math.max(0,1-zoomfactor)*80000));
	repaint();
    }

    /** Zooms out. */
    public void zoomOut(){
	if (zoomfactor-zoomstep*2>0.2) zoomfactor-=zoomstep;
	construction.setTolerance((100)/(zoomfactor*zoomfactor));
	repaint();

    }

    /** Resets the View*/
    public void resetView(){
	cp.y=0; cp.x=0;
	zoomfactor=GDefaults.zoomfactor;
	construction.setTolerance((100)/(zoomfactor*zoomfactor));
	repaint();
    }

    /** Turns the grid on/off.*/
    public void switchGrid()    {
	bGridOn=!bGridOn;
	repaint();
    }

    /** Turns the coordinate axes on/off. */
    public void switchCoords(){
	bCoordAxesOn=!bCoordAxesOn;
	repaint();
    }

    /** Callback Method for key events. 
     * @param KeyEvent The key event.
     */
    public void keyTyped(KeyEvent e) {
	switch(e.getKeyChar()){
	    case '1':		//zoomout
		if (zoomfactor-zoomstep*2>0) zoomfactor-=zoomstep;
		construction.setTolerance((100)/(zoomfactor*zoomfactor));
		break;
	    case '3':		//zoomin
		zoomfactor+=zoomstep;
		construction.setTolerance((100)/(zoomfactor*zoomfactor));
		break;
	    case '2':		//centerpoint down
		cp.y+= GDefaults.gridPan;
		break;
	    case '4':		//centerpoint left
		cp.x-= GDefaults.gridPan;
		break;
	    case '6':		//centerpoint right
		cp.x+= GDefaults.gridPan;
		break;
	    case '8':		//centerpoint up
		cp.y-= GDefaults.gridPan;
		break;
	    case '5':		//reset
		cp.y=0; cp.x=0;
		zoomfactor=GDefaults.zoomfactor;
		break;
	}
	repaint();
    }

    /** The mouse position is forwarded to the construction. By
     * this, the construction is enabled to check for expected
     * objects near that position.
     */
    public void mouseMoved(MouseEvent e) { 
	String description=new String();
	description = construction.setMousePosition
	    (pointToReal2D(e.getX(),e.getY()));
	//reset the status bar
	parent.setStatusBarText(langStrings.s_tool+": "+parent.getToolType(),
			     0, Color.BLACK);
	if(description !=null) 
	    parent.setStatusBarText(description,1,Color.BLACK);
	//repaint
	parent.refresh();

    }

    /** If there has been a point selected for movement before, and
     *  the right mousebutton is pressed, move it! Otherwise, the
     *  coordinate area will be scrolled.
     */
    public void mouseDragged(MouseEvent e) {
	Point2D.Double p = pointToReal2D(e.getX(),e.getY());
	boolean pointSelected=construction.movePoint(p);
	if(pointSelected) {
	    parent.setStatusBarText
		(langStrings.s_move+" "+langStrings.s_point+"...",
		 0,Color.BLACK);
	    lastDragPos=null;
	}
	else {
	    if(lastDragPos!=null) {
		double dx=lastDragPos.x-e.getX();
		double dy=lastDragPos.y-e.getY();
		if(lastButton!=MouseEvent.BUTTON1) //right button
		{ 
		    cp.x+=dx;
		    cp.y+=dy;		
		}
		else  {
		    if(dy>0) zoomIn();
		    else zoomOut();					
		}
	    }
	    lastDragPos=new Point2D.Double(e.getX(),e.getY());
	}
	parent.refresh(); 
    };

    /** Handles mouse clicks. By this, geometric objects are
     * selected, added, their (display) properties or their
     * algorithms are changed, depending on the currently selected
     * tool.
     */
    public void mouseClicked(MouseEvent	e) {
	//left Button for creating new objects or changing their
	//(display) properties
	String mode=parent.getMode();
	if(e.getButton()==MouseEvent.BUTTON1)  {	
	    //The display option modes display or hide names
	    if(mode.equals(GeoStrings.SHOWNAME)) {
		//transformation to world coordinates
		Point2D.Double p = pointToReal2D(e.getX(),e.getY());
		ArrayList matchingObjects=construction.getMatchingObjectsAt(p);
		construction.showNames(matchingObjects, p);
	    }
	    //rename
	    else if(mode.equals(GeoStrings.SETNAME)) {
		//transformation to world coordinates
		Point2D.Double p = pointToReal2D(e.getX(),e.getY());
		ArrayList matchingObjects=construction.getMatchingObjectsAt(p);
		String selection=null;
		//if there's no unique selection, show a selection dialogue
		if(matchingObjects.size()>1)
		    selection=showSelectionDialogue
			(matchingObjects, langStrings.q_whichobject, 
			 langStrings.q_chooseobject);
		//otherwise, select the object
		else if((matchingObjects.size()==1))
		    selection=(String) matchingObjects.get(0);
		if(selection!=null) {
		    //get new name and check if it's already in use
		    //or forbidden (like algorithm type names) - if
		    //yes, show the rename dialogue again
		    while(true) {
			String newName=JOptionPane.showInputDialog
			    (this, selection+" "+langStrings.q_rename+": "
			     +langStrings.q_newname, selection);
			if(construction.setObjectName(selection, newName)) 
			    break;
		    }		
		}
	    }
	    //set color -- HGG: to be fixed
	    else if(mode.equals(GeoStrings.SETCOLOR)) {
		//transformation to world coordinates
		Point2D.Double p = pointToReal2D(e.getX(),e.getY());
		ArrayList matchingObjects=construction.getMatchingObjectsAt(p);
		// construction.setColor(matchingObjects, parent.currentColor);
	    }
	    //set emphasis factor
	    else if(mode.equals(GeoStrings.EMPH0) 
		    || mode.equals(GeoStrings.EMPH1) 
		    || mode.equals(GeoStrings.EMPH2) 
		    || mode.equals(GeoStrings.EMPH3)) {
		//transformation to world coordinates
		Point2D.Double p = pointToReal2D(e.getX(),e.getY());
		ArrayList matchingObjects=construction.getMatchingObjectsAt(p);
		//get emphasis factor
		int emph;
		if(mode.equals(GeoStrings.EMPH0)) emph=0;
		else if(mode.equals(GeoStrings.EMPH1)) emph=1;
		else if(mode.equals(GeoStrings.EMPH2)) emph=2;		
		else  emph=3;				
		construction.setEmph(matchingObjects, emph);
	    }			
	    //add a free point, floater or intersection point
	    else if(mode.equals(GeoStrings.POINT)) {
		Point2D.Double p = pointToReal2D(e.getX(),e.getY());
		//if there is no floatable object at this position,
		//create a simple point
		if(!construction.isAnyObjectAt(p)) construction.addPoint(p);
		//there is a floatable object or there are two
		//intersectable objects the point can be attached to
		else {
		    ArrayList matchingObjects=
			construction.getMatchingObjectsAt(p);
		    //One matching object --> floater
		    if(matchingObjects.size()==1) {
			construction.addFloater
			    (p,	(String)matchingObjects.get(0));
		    }
		    //Two or more objects --> intersection of the
		    //first two objects
		    else if (matchingObjects.size()>1) {
			construction.addIntersection
			    ((String)(matchingObjects.get(0)), 
			     (String)(matchingObjects.get(1)), 
			     pointToReal2D(e.getX(),e.getY()));
		    }
		}	
	    }
	    //the other modes
	    else {
		//transformation to world coordinates and look for
		//matching objects
		Point2D.Double p = pointToReal2D(e.getX(),e.getY());
		ArrayList matchingObjects=construction.getMatchingObjectsAt(p);
		//no matching objects
		if(matchingObjects.size()==0) {
		    //if the current tool isn't the change tool and
		    //a point is expected, create one "on the fly"
		    //and select it
		    if(construction.isTypeExpected(GeoStrings.POINT)
		       && !parent.getToolType().equals(GeoStrings.CHANGER) ) { 
			construction.addAndSelectPoint(p);
		    }
		    else { //no matching objects found
			parent.refresh(); 
			return;
		    } 
		} 
		//special treatment for change operations which
		//create intersection objects
		if(parent.getToolType().equals(GeoStrings.CHANGER) 
		   && construction.getChoice().equals(GeoStrings.INTERSECTION)) {
		    //choose both directly defining objects at once
		    if(matchingObjects.size()>1) {	
			construction.selectObject((String)matchingObjects.get(0));
			construction.selectObject((String)matchingObjects.get(1));
		    }
		    //choose one of the directly defining objects
		    else if(matchingObjects.size()==1)
			construction.selectObject((String)matchingObjects.get(0));
		}
		//multiple objects matching: show a selection dialogue
		else if(matchingObjects.size()>1) {
		    //create a selection dialogue
		    String selection=showSelectionDialogue
			(matchingObjects, langStrings.q_whichobject, langStrings.q_chooseobject);
		    //select chosen object 
		    construction.selectObject(selection);
		}	
		//exactly one matching object: select it
		else if(matchingObjects.size()==1) 
		    construction.selectObject((String)(matchingObjects.get(0)));
		//try to add a new object to the construction
		tryToAddObjects(new Point2D.Double(e.getX(),e.getY()));
	    }
	}
	//refresh the main frame
	parent.refresh(); 
    };

    /** Selects a point for movement. */
    public void mousePressed(MouseEvent	e) {
	lastButton=e.getButton();
	//right button: if there's a point, select it for movement!
	if(e.getButton()==MouseEvent.BUTTON3) {
	    Point2D.Double p = pointToReal2D(e.getX(),e.getY());
	    //Get points at this position
	    ArrayList points = construction.getObjectsAt(GeoStrings.POINT,p);
	    //if several points have been found, just move the first one
	    //...after all it's reversible 
	    if(points.size()>0) {
		construction.setPointToMove(
		    ((DrawableGeoPoint)points.get(0)).getName());
	    }
	}
    };

    /**
     * Releases the currently moved point.
     */
    public void mouseReleased(MouseEvent e){
	//if the mouse button is released, there is no more point to be moved.
	construction.releasePointToMove();
    };

    /**
     * This method calls one of the associated construction's
     * methods for adding objects, depending on the currently
     * selected tool.  Note that these methods won't add any objects
     * to the construction until a sufficient number of defining
     * objects of an appropriate type are selected.
     * @param p A position in world coordinates.
     */
    void tryToAddObjects(Point2D.Double p) {
	//check the tool and call method for adding object; for
	//"change mode" see below two point line
	String mode=parent.getMode();
	if(mode.equals(GeoStrings.LINE))
	    construction.addTwoPointLine(); //creates the line or shows preview
	//two point circle
	else if (mode.equals(GeoStrings.CIRCLE)) 
	    construction.addTwoPointCircle();
	//midpoint
	else if (mode.equals(GeoStrings.MIDPOINT)) 
	    construction.addMidPoint();
	//parallel line
	else if (mode.equals(GeoStrings.PARALINE)) 
	    construction.addParaLine();	
	//orthogonal line
	else if (mode.equals(GeoStrings.ORTHOLINE)) 
	    construction.addOrthoLine();
	//special treatment: change an object's algorithm
	else if(mode.equals(GeoStrings.CHANGER)) {
	    //get the matching algorithm types and let the user
	    //choose a new type

	    //returns null, if this choice has already been made
	    //(this method will be called again if defining objects
	    //have to be selected!!!)
	    ArrayList matchingAlgorithmTypes = construction.getMatchingAlgorithmTypes();
	    String choice=null;
	    if(matchingAlgorithmTypes!=null) {
		String selection=showSelectionDialogue
		    (matchingAlgorithmTypes, langStrings.q_whichconf, 
		     langStrings.q_chooseconf);
		if(selection==null) {	
		    //set the expected objects
		    Object dummy = new ChangerTool(construction);
		    parent.refresh();
		    return;
		} 
		else choice=selection;
	    }
	    if(choice!=null)
		construction.setChoice(choice); 
	    construction.change(pointToReal2D(p.x,p.y));
	}
    }

    /**
     * Transforms view to world coordinates. 
     * @param x The x coordinate (view).
     * @param y The y coordinate (view).
     * @return The corresponding point (as instance of Point) in world coordinates.
     */
    public Point pointToReal(double x,double y) {
	double arr[]= new double[2];
	double ret[]=new double[2];
	arr[0]=x;
	arr[1]=y;

	try {
	    at.inverseTransform(arr,0,ret,0,1);
	} catch(NoninvertibleTransformException e) {
	    System.err.println("Error: Non-invertible coordinate transformation!");	
	}
	return new Point((int)ret[0],(int)ret[1]);
    }

    /**
     *  Transforms view to world coordinates 
     * @param x The x coordinate (view).
     * @param y The y coordinate (view).
     * @return The point (instance of Point2D.Double) in world coordinates.
     * */
    public Point2D.Double pointToReal2D(double x,double y){

	double arr[]= new double[2];
	double ret[]=new double[2];
	arr[0]=x;
	arr[1]=y;

	try   {
	    at.inverseTransform(arr,0,ret,0,1);
	} catch(NoninvertibleTransformException e)  {
	    System.err.println("Error: Non-invertible coordinate transformation!");		
	}
	return new Point2D.Double(ret[0],ret[1]);
    }

    /** overload of paintComponent: calls paintGrid with a value of
     * 10px for the gridsize at Scalefactor 1 
     */
    public void paintComponent(Graphics g) {	
	Dimension dim = getSize();
	int w = dim.width;
	int h = dim.height; 
	Graphics2D g2 = (Graphics2D)g;

	if(!lastDimension.equals(dim)) {
	    bi = (BufferedImage)createImage(w, h);
	    big = bi.createGraphics();
	    big.setRenderingHints(renderingHints);
	    big.setFont(f);
	    big.setColor(Color.black);
	} 
	lastDimension.setSize(dim);
	at = new AffineTransform(zoomfactor,0,0,zoomfactor,
				 (lastDimension.width/2)+cp.x*zoomfactor,
				 (lastDimension.height/2)+cp.y*zoomfactor);

	// Clears the rectangle that was previously drawn.
	big.clearRect(0, 0, dim.width, dim.height);
	big.setColor(GDefaults.backColor);
	big.fillRect(0, 0, dim.width, dim.height);
	big.setTransform(new AffineTransform());
	paintGrid(big,getSize(),GDefaults.gridSpacing,cp);
	big.setTransform(at);

	//Compute the upper left and lower right corners of the coordinate area.
	Point2D.Double uL=pointToReal2D(0,0);
	Point2D.Double lR=pointToReal2D(getWidth(),getHeight());

	//get the construction's drawable objects
	ArrayList objects = construction.getDrawableGeoObjects();

	//paint them
	for(int i=0;i<objects.size();i++) {
	    ((Drawable)objects.get(i)).draw(big,uL,lR);
	}
	big.setTransform(new AffineTransform());
	// Draws the buffered image to the screen.
	g2.drawImage(bi, 0, 0, null);
	requestFocus();
    };

    /**
     * Draws a grid.
     * @param g The Graphics2D object.
     * @param d The dimesion.
     * @param delta The distance betwwen two grid lines at scale factor 1.
     * @param centeroffset The center offset.
     */
    public void paintGrid(Graphics2D g, Dimension d,int delta, Point centeroffset){
	Point centerWin = new Point(d.width/2,d.height/2);
	Point cenc = new Point();
	Color c = Color.lightGray;
	Point t = new Point(0,0);
	int offset;

	cenc.x = (centerWin.x+(int)(centeroffset.x*zoomfactor));
	cenc.y = (centerWin.y+(int)(centeroffset.y*zoomfactor));

	//The drawn lines are either horizontally or vertically aligned.
	//Therefore, anti-aliasing is turned off for performance reasons.	
	g.setColor(c);
	if (bGridOn) {
	    g.setRenderingHint(RenderingHints.KEY_RENDERING, 
			       RenderingHints.VALUE_RENDER_SPEED);
	    for(int i=0;i<d.width/2;i++) {
		//vertical lines
		offset = (int)(i*delta*zoomfactor);	
		t.x = centerWin.x+offset+(int)(centeroffset.x*zoomfactor);	
		g.drawLine(t.x,0,t.x,d.height);
		t.x = centerWin.x-offset+(int)(centeroffset.x*zoomfactor);		
		g.drawLine(t.x,0,t.x,d.height);	
	    }

	    for(int i=0;i<d.height/2;i++){
		// horizontal lines
		offset = (int)(i*delta*zoomfactor);
		t.y = centerWin.y+offset+(int)(centeroffset.y*zoomfactor);
		g.drawLine(0,t.y,d.width,t.y);
		t.y = centerWin.y-offset+(int)(centeroffset.y*zoomfactor);
		g.drawLine(0,t.y,d.width,t.y);
	    }
	}	
	c = Color.gray;
	g.setColor(c);
	if (bCoordAxesOn){
	    //paint the axes
	    g.drawLine(cenc.x,0,cenc.x,d.height);
	    g.drawLine(0,cenc.y,d.width,cenc.y);
	}
    }

    /**
     * Shows a selection dialogue and returns one of the given strings,
     * chosen by the user.
     * @param matchingObjects List of the matching objects' names.
     * @return The selected one of the given strings.
     */
    protected String showSelectionDialogue(ArrayList matchingObjects, 
					   String caption, String text) {
	String[] options = new String[matchingObjects.size()];
	for(int i=0;i<matchingObjects.size();i++)
	    options[i]=(String)matchingObjects.get(i);
	String selection= (String) JOptionPane.showInputDialog
	    (null, caption, text, JOptionPane.QUESTION_MESSAGE,null, options,options[0]);
	return selection;
    }
}

