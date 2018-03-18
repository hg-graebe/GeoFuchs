/* $Id: GeoConstruction.java,v 1.2 2005/08/21 20:29:56 hg-graebe Exp $ */
package geofuchs.model;

import geoxml.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import geofuchs.control.*;
import java.awt.geom.*;
import geofuchs.graph.*;
import java.awt.*;

/**
 *  A construction is mainly a tuple of geometric objects.
 * @author Andy Stock
 */
public class GeoConstruction {
	
    /** A hook for a qualified dispose method.
     */
    public void dispose() {}

    private String name; //The construction's name.
	
	private ArrayList geoObjects = new ArrayList(); //The contained geometric objects.
	private static ArrayList expectedObjects = new ArrayList();	//currently expected objects
	private static int totalExpectedObjects;			//number of expected objects


	//both are set when the tool is changed and are used for resetting
	//the tool's expectation when a "tool cycle" is finished
	private static ArrayList defaultExpectedObjects = new ArrayList();	
	private static int defaultTotalExpectedObjects;

	DrawableGeoObject currentObject=null; //used for different purposes as a temporary reference to objects 
										  
	
	int pointToMove=-1;	//The index of the point that is currently moved by the user									

	private ArrayList cutoffObjects=new ArrayList();  //A list of objects that have been temporarily removed from the construction, but will be added again (e.g. for change operations)

	private double tolerance=10.0;  //The largest distance in the world coordinate system at which two points' positions are considered to be equal
	private int numberOfPoints=0;  //The number of points in the construction (used for creating unique default names in a simple way)
	private int numberOfLines=0;   // The same for lines...
	private int numberOfCircles=0; //...and circles
	
	private static boolean previewable=false;  //Is it possible to show a preview?
	private static boolean creatable=false;	//Is it possible to create a new geometric object? 

	private static ArrayList selectedObjects = new ArrayList(); //Objects selected by the user;
														  //only their index (an Intger object, that is) is stored!

	
	
	private static DrawableGeoObject previewObject=null; //This object will be shown as a preview and added to the construction when a mouseclick appears.	
	private static String objectType = GeoStrings.POINT; //the type of the object to be created
	
	private String choice=new String("unknown");          //A state variable that can be set from outside by the setChoice(String) method and is for instance used in the change() method.

	private ArrayList redoList=new ArrayList();			//the "undone" objects are added to the redo list, but it's cleared under certain conditions.
	private int possibleUndoOps=0;		//the number of allowed undo operations 

	private Map objectNameToObject=new HashMap();

	//information on supported algorithm types
	//when new algorithm types are added, this field should be extended respectively.
	//By this, they are automatically added to the choices presented to the user when
	//changing an object's algorithm.
	private static AlgorithmInfo[] algInfo = new AlgorithmInfo[]
	{
	
		FloaterAlgorithm.getInfo(),
		FreePointAlgorithm.getInfo(),
		IntersectionAlgorithm.getInfo(),
		MidPointAlgorithm.getInfo(),
		ParaLineAlgorithm.getInfo(),
		OrthoLineAlgorithm.getInfo(),
		TwoPointCircleAlgorithm.getInfo(),
		TwoPointLineAlgorithm.getInfo()
	
	};
		
	private void recreateMap()
	{
		objectNameToObject=new HashMap();
		for(int i=0; i<geoObjects.size();i++)
			objectNameToObject.put(((Geometric)geoObjects.get(i)).getName(),new Integer(i));
	}	
	
	/**
	 * This method adds a geometric object to the construction.
	 * @param o The object. If it's not an instance of Geometric,
	 * nothing happens at all.
	 */
	private void addObject(Object o)
	{
		if(o instanceof Geometric)
		{
			geoObjects.add(o);
			recreateMap();
		}
		else
		{
			System.err.println("Error: trying to add a non-geometric object to the construction "+name+".");
		}
	}
	
	/**
	 * This method removes an object from the construction.
	 * @param i The object's index in the construction list. 
	 */	
	private void removeObject(int i)
	{
		Geometric o=(Geometric)geoObjects.get(i);
		geoObjects.remove(i);
		recreateMap();
	}
	
	/**
	 * This method adds geometric objects to the construction.
	 * @param o A list of objects. Objects that are not an instance
	 * of Geometric are ignored.
	 */	
	private void addObjects(ArrayList objectList)
	{
		for(int i=0; i<objectList.size();i++)
			addObject(objectList.get(i));
		recreateMap();
	}

	/**
	 * This method removes geometric objects from the construction.
	 * @param o A list of objects. 
	 */	
	private void removeObjects(ArrayList objectList)
	{
		for(int i=0; i<objectList.size();i++)
		{
			Geometric o=(Geometric)objectList.get(i);
			geoObjects.remove(o);
		}
		recreateMap();
	}
	
	/**
	 * @param string The construction's name.
	 */
	public GeoConstruction(String string) {
		
		this.name=string;
	}

	/**
	 * 
	 * @return An array of AlgorithmInfo objects that can be used 
	 * for getting information on the implemented algorithm types.
	 */
	public static AlgorithmInfo[] getAlgorithmInfo()
	{
		return algInfo;
	}

	/**
	 * 
	 * @return The construction's name.
	 */
	public String getName() 
	{
		return name;
		
	}

	/**
	 * 
	 * @param name The construction's name.
	 */
	public void setName(String name) 
	{
		this.name=name;
		
	}
	
	/**
	 * Sets the type of the objects that can be created with the 
	 * current tool (used for preview).
	 * @param type The object type.
	 */
	public static void setObjectType(String type) 
	{
		objectType=type;
	}


	/**
	 * This method creates a preview of an object. 
	 * @param index The index of the object that should be used as still
	 * 				unknown parameter for the preview.
	 */
	private void createPreview(int index)
	{
		//previewable is true, if a mouseclick would immediately result in the creation of 
		//a geometric object
		if(previewable)
		{	
			if(objectType.equals(GeoStrings.TWOPOINTLINE)) previewTwoPointLine(index);
			else if(objectType.equals(GeoStrings.TWOPOINTCIRCLE)) previewTwoPointCircle(index);
			else if(objectType.equals(GeoStrings.MIDPOINT)) previewMidPoint(index);
			else if(objectType.equals(GeoStrings.PARALINE)) previewParaLine(index);
			else if(objectType.equals(GeoStrings.ORTHOLINE)) previewPlumbLine(index);
		}
		
	}

	/**
	 * This method creates a TwoPointLine as preview.
	 * @param index The index of the unknown parameter (geometric object, that is) in the construction.
	 */
	private void previewTwoPointLine(int index)
	{
		try {
			
			
			//get the objects
			int i = (((Integer)selectedObjects.get(0)).intValue());
			DrawableGeoPoint p1=(DrawableGeoPoint)(geoObjects.get(i));
			DrawableGeoPoint p2=(DrawableGeoPoint)(geoObjects.get(index));
				
			//create algorithm and line
			TwoPointLineAlgorithm conf = new TwoPointLineAlgorithm(p1,p2);
			DrawableGeoLine line = (DrawableGeoLine)conf.createObject(new String(langStrings.s_line+numberOfLines));
			previewObject = line;
			
		}
		catch(Exception e)
		{
			System.err.println("Exception while trying to create a preview two-point-line:");
			System.err.println(e.getMessage());
		}
	}

		/**
		 * This method creates a parallel line as preview.
		 * @param index The index of the unknown parameter (geometric object, that is) in the construction.
		 */
		private void previewParaLine(int index)
		{
			try {
			
			
				DrawableGeoPoint p;
				DrawableGeoLine l;
				
				Object o1 = geoObjects.get(index);
				index = (((Integer)selectedObjects.get(0)).intValue());
				Object o2 = geoObjects.get(index);
				
				//Find out which og the objects is the point and which is the line
				if(o1 instanceof DrawableGeoPoint)
				{
					p = (DrawableGeoPoint)o1;
					l = (DrawableGeoLine) o2;
				}
				else
				{
					p = (DrawableGeoPoint)o2;
					l = (DrawableGeoLine) o1;	
				}
				
				//create algorithm and line
				ParaLineAlgorithm conf = new ParaLineAlgorithm(p,l);
				DrawableGeoLine line = (DrawableGeoLine)conf.createObject(new String(langStrings.s_line+numberOfLines));
				previewObject = line;
			
			}
			catch(Exception e)
			{
				System.err.println("Exception while trying to create a preview parallel line:");
				System.err.println(e.getMessage());
			}
		}
		
	/**
	 * This method creates an orthogonal line for preview.
	 * @param index The index of the unknown parameter in the construction.
	 */
	private void previewPlumbLine(int index)
	{
		try 
		{			
			
			DrawableGeoPoint p;
			DrawableGeoLine l;
			
			Object o1 = geoObjects.get(index);
			index = (((Integer)selectedObjects.get(0)).intValue());
			Object o2 = geoObjects.get(index);
			
			//Find out which og the objects is the point and which is the line
			if(o1 instanceof DrawableGeoPoint)
			{
				p = (DrawableGeoPoint)o1;
				l = (DrawableGeoLine) o2;
			}
			else
			{
				p = (DrawableGeoPoint)o2;
				l = (DrawableGeoLine) o1;	
			}
		
			//create algorithm and line
			OrthoLineAlgorithm conf = new OrthoLineAlgorithm(p,l);
			DrawableGeoLine line = (DrawableGeoLine)conf.createObject(new String(langStrings.s_line+numberOfLines));
			previewObject = line;
	
		}
		catch(Exception e)
		{
			System.err.println("Exception while trying to create a preview orthogonal line:");
			System.err.println(e.getMessage());
		}
	}

	/**
	* This method creates a TwoPointCircle as preview.
	* @param index The index of the unknown parameter (geometric object, that is) in the construction.
	*/
	private void previewTwoPointCircle(int index)
	{
		try {
			
				//get the objects
				int i = (((Integer)selectedObjects.get(0)).intValue());
				DrawableGeoPoint p1=(DrawableGeoPoint)(geoObjects.get(i));
				DrawableGeoPoint p2=(DrawableGeoPoint)(geoObjects.get(index));
				
				//create algorithm and circle
				TwoPointCircleAlgorithm conf = new TwoPointCircleAlgorithm(p1,p2);
				DrawableGeoCircle circle = (DrawableGeoCircle)conf.createObject(new String(langStrings.s_circle+numberOfCircles));
				previewObject = circle;
			
			}
			catch(Exception e)
			{
				System.err.println("Exception while trying to create a preview two-point-circle:");
				System.err.println(e.getMessage());
			}
		}

		/**
		 * This method creates a midpoint as preview.
		 * @param index The index of the unknown parameter (geometric object, that is) in the construction.
		 */
		private void previewMidPoint(int index)
		{
			try {
			
			
				//get the objects
				int i = (((Integer)selectedObjects.get(0)).intValue());
				DrawableGeoPoint p1=(DrawableGeoPoint)(geoObjects.get(i));
				DrawableGeoPoint p2=(DrawableGeoPoint)(geoObjects.get(index));
				
				//create algorithm and line
				MidPointAlgorithm conf = new MidPointAlgorithm(p1,p2);
				DrawableGeoPoint point = (DrawableGeoPoint)conf.createObject(new String(langStrings.s_point+numberOfPoints));
				previewObject = point;
			
			}
			catch(Exception e)
			{
				System.err.println("Exception while trying to create a preview midpoint:");
				System.err.println(e.getMessage());
			}
		}

	/**
	 * Selects the specified object.
	 * @param name The object's name which can be found out by the
	 * getMatchingObjects-method.
	 */
	public void selectObject(String name)
	{
		
		//find the object
		int index = getObjectByName(name);
		//if there's no problem, add it to the selectedObjects-list
		//and remove it from the expectation list
		if(index>=0)
		{
			Geometric g = (Geometric)geoObjects.get(index);
			selectedObjects.add(0, new Integer(index));
			totalExpectedObjects--;
			for(int i=0;i<expectedObjects.size();i++)
			{   
				ObjectExpectation obEx = (ObjectExpectation)expectedObjects.get(i);
				if(g.getType().equals(obEx.getType()))
				{
					//Decrease the number of expected objects of this type;
					//if it's the last one remove the expectation
					if(obEx.selected()) {expectedObjects.remove(i);} 		
				}
			}
			if(totalExpectedObjects==1) previewable=true; else previewable=false;
			if(totalExpectedObjects==0) creatable=true; else creatable=false;
		}
		else 
		{
			System.err.println("Error while trying to select an object: "+name+" is not included in the construction "+this.name);
		}
	}

	/**
	 * This method adds a geometric point at the given position to the construction.
	 * If two or more intersectable objects are at the position, the point will be
	 * an intersection point. Otherwise, if a floatable object is at this position, 
	 * a floater will be added instead. Otherwise, the point will be free. 
	 * @param pos The point's position, which also determines the point's algorithm, in world coordinates.
	 */
	public void addAndSelectPoint(Point2D.Double pos)
	{
		
		//find all intersectable an floatable objects at this position
		ArrayList iobjects=new ArrayList();
		ArrayList fobjects=new ArrayList();
		for(int i=0; i<geoObjects.size(); i++)
		{
			DrawableGeoObject o = (DrawableGeoObject) geoObjects.get(i);
			if(o instanceof Floatable && o.isAt(pos, tolerance)) fobjects.add(o);
			if(o instanceof Intersectable && o.isAt(pos, tolerance)) iobjects.add(o);
		}
		
		//if there are two or more intersectable objects at the given position, create an intersection of the first ones.
		if(iobjects.size()>=2)
		{
			DrawableGeoPoint p; //the new intersection point
			IntersectionAlgorithm conf;
			
			DrawableGeoObject o1=(DrawableGeoObject)iobjects.get(0);
			DrawableGeoObject o2=(DrawableGeoObject)iobjects.get(1);
			
			//there's no intersection algorithm for the given
			//intersectable objects yet: create one!!
			if(getIntersectionConf(o1.getName(), o2.getName())==null)
					conf = new IntersectionAlgorithm((Intersectable)o1,(Intersectable)o2); 
			else //there's already one: use it!
				conf=getIntersectionConf(o1.getName(), o2.getName());
				 
			//create the intersection point
			conf.selectIntersectionPoint(pos);
			p = (DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
			if(p!=null) 
			{
				addObject(p);
				numberOfPoints++;
				selectObject(p.getName());
			} 
			return;
		}
		//create a floater
		if(fobjects.size()>=1)
		{
			Floatable o=(Floatable) fobjects.get(0);
			FloaterAlgorithm conf = new FloaterAlgorithm(pos,(Floatable)o);
			DrawableGeoPoint p = (DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
			addObject(p);
			numberOfPoints++;
			selectObject(p.getName());
			
		}
		
		//create a free point
		else
		{
			FreePointAlgorithm conf = new FreePointAlgorithm(pos.x, pos.y);
			DrawableGeoPoint p=(DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
			addObject(p);
			numberOfPoints++;
			selectObject(p.getName());
		}
		
		redoList=new ArrayList();
		possibleUndoOps++;
		setMousePosition(pos);
		
	}


	/**
	 * Adds a free point. 
	 * @param pos The point's position in world coordinates.
	 */
	public void addPoint(Point2D.Double pos)
	{
		
		//DrawableGeoPoint p = new DrawableGeoPoint(pos.x, pos.y, new String(langStrings.s_point+numberOfPoints));
		FreePointAlgorithm conf = new FreePointAlgorithm(pos.x, pos.y);
		DrawableGeoPoint p=(DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
		addObject(p);
		numberOfPoints++;
		restoreExpectedObjects();
		selectedObjects=new ArrayList();
		redoList=new ArrayList();
		possibleUndoOps++;
			
	}
	
	/**
	 * This method adds a floater to the construction.
	 * @param pos The floater's position in world coordinates.
	 * @param object The name of the object the floater should float on.
	 */
	public void addFloater(Point2D.Double pos, String objectName)
	{	
		//get the object
		int index = getObjectByName(objectName);
		FloaterAlgorithm conf = new FloaterAlgorithm(pos,(Floatable)geoObjects.get(index));
		DrawableGeoPoint p = (DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
		addObject(p);
		numberOfPoints++;
		restoreExpectedObjects();
		selectedObjects=new ArrayList();
		redoList=new ArrayList();
		possibleUndoOps++;
	}

		/**
		 * This method adds an intersection point to the construction.
		 * @param pos The intersection's position in world coordinates (used for selecting one of multiple intersections).
		 * @param name1 The name of the of an an intersectable object.
		 * @param name2 The name of another intersectable object.
		 */
		public void addIntersection(String name1, String name2, Point2D.Double pos)
		{	
			//get the objects
			int index1 = getObjectByName(name1);
			int index2 = getObjectByName(name2);
			DrawableGeoPoint p; //the new intersection point
			
			IntersectionAlgorithm conf;
			
			//there's no intersection algorithm for the given
			//intersectable objects yet: create one!!
			if(getIntersectionConf(name1, name2)==null)
				conf = new IntersectionAlgorithm((Intersectable)geoObjects.get(index1),(Intersectable)geoObjects.get(index2)); 
			else //there's already one: use it!
				conf=getIntersectionConf(name1, name2);
				 

			//create the intersection point
			conf.selectIntersectionPoint(pos);
			p = (DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
			if(p!=null) 
			{
				addObject(p);
				numberOfPoints++;
				redoList=new ArrayList();
				possibleUndoOps++;
			} 
			
			restoreExpectedObjects();
			selectedObjects=new ArrayList();
		}

	/**
	 * This method creates a line which is defined by two points. The directly defining points are known to the
	 * construction by previous calls of the selectObject method.
	 *
	 */
	public void addTwoPointLine()
	{
		
		if(creatable)  //two points must have been selected
		{
			
			try {
			
				//get the points
				int index = (((Integer)selectedObjects.get(0)).intValue());
				DrawableGeoPoint p1=(DrawableGeoPoint)(geoObjects.get(index));
				index = (((Integer)selectedObjects.get(1)).intValue());
				DrawableGeoPoint p2=(DrawableGeoPoint)(geoObjects.get(index));
				
				//create algorithm and line
				TwoPointLineAlgorithm conf = new TwoPointLineAlgorithm(p1,p2);
				DrawableGeoLine line = (DrawableGeoLine)conf.createObject(new String(langStrings.s_line+numberOfLines));
				addObject(line);
				numberOfLines++;
				
				selectedObjects = new ArrayList(); 
				restoreExpectedObjects();
				redoList=new ArrayList();
				possibleUndoOps++;
				
				}
			
				catch(Exception e)
				{
					System.err.println("Error while trying to add a two-point-line to construction "+this.name+":");	
					System.err.println(e.getMessage());
				}
		}
	}

	/**
	 * This method creates an orthogonal line which is defined by one point and a line.
	 * The directly defining objects are known to the
	 * construction by previous calls of the selectObject method.
	 *
	 */
	public void addOrthoLine()
	{
		
		if(creatable)  //there must have been two matching objects selected
		{
			
			try 
			{
		
				//get the objects
				DrawableGeoPoint p;
				DrawableGeoLine l;
				
				int index = (((Integer)selectedObjects.get(0)).intValue());
				Object o1 = geoObjects.get(index);
				index = (((Integer)selectedObjects.get(1)).intValue());
				Object o2 = geoObjects.get(index);
				
				//Try which of the objects is the point and which is the line
				if(o1 instanceof DrawableGeoPoint)
				{
					p = (DrawableGeoPoint)o1;
					l = (DrawableGeoLine) o2;
				}
				else
				{
					p = (DrawableGeoPoint)o2;
					l = (DrawableGeoLine) o1;	
				}
			
				
			
				//create algorithm and line
				OrthoLineAlgorithm conf = new OrthoLineAlgorithm(p, l);
				DrawableGeoLine line = (DrawableGeoLine)conf.createObject(new String(langStrings.s_line+numberOfLines));
				addObject(line);
				numberOfLines++;
			
				selectedObjects = new ArrayList(); 
				restoreExpectedObjects();
				redoList=new ArrayList();
				possibleUndoOps++;
			}
		
			catch(Exception e)
			{
				System.err.println("Error while trying to add an orthogonal line to construction "+this.name+":");	
				System.err.println(e.getMessage());
			}
		}

	}

		/**
		 * This method creates a line which is defined by one point and a parallel line.
		 * The directly defining objects are known to the
		 * construction by previous calls of the selectObject method.
		 *
		 */
		public void addParaLine()
		{
		
			if(creatable)  //a line and a point must have been selected
			{
			
				try {
		
					//get the objects
					DrawableGeoPoint p;
					DrawableGeoLine l;
				
					int index = (((Integer)selectedObjects.get(0)).intValue());
					Object o1 = geoObjects.get(index);
					index = (((Integer)selectedObjects.get(1)).intValue());
					Object o2 = geoObjects.get(index);
				
					//Try which of thwe objects is the point and which is the line
					if(o1 instanceof DrawableGeoPoint)
					{
						p = (DrawableGeoPoint)o1;
						l = (DrawableGeoLine) o2;
					}
					else
					{
						p = (DrawableGeoPoint)o2;
						l = (DrawableGeoLine) o1;	
					}
			
					//create algorithm and line
					ParaLineAlgorithm conf = new ParaLineAlgorithm(p, l);
					DrawableGeoLine line = (DrawableGeoLine)conf.createObject(new String(langStrings.s_line+numberOfLines));
					addObject(line);
					numberOfLines++;
					redoList=new ArrayList();
					possibleUndoOps++;
					selectedObjects = new ArrayList(); 
					restoreExpectedObjects();
				
				}
		
			catch(Exception e)
			{
				System.err.println("Error while trying to add a parallel line to construction "+this.name+":");	
				System.err.println(e.getMessage());
			}
			
		}
	
	}


		/**
		 This method creates a midpoint. The necessary points are known to the
		 * construction by previous calls of the selectObject method.
		 *
		 */
		public void addMidPoint()
		{	

			if(creatable)  //two points must have been selected
			{
				
				try {
			
					//get the objects
					int index = (((Integer)selectedObjects.get(0)).intValue());
					DrawableGeoPoint p1=(DrawableGeoPoint)(geoObjects.get(index));
					index = (((Integer)selectedObjects.get(1)).intValue());
					DrawableGeoPoint p2=(DrawableGeoPoint)(geoObjects.get(index));
				
					//create algorithm and midpoint
					MidPointAlgorithm conf = new MidPointAlgorithm(p1,p2);
					DrawableGeoPoint point = (DrawableGeoPoint)conf.createObject(new String(langStrings.s_point+numberOfPoints));
					addObject(point);
					numberOfPoints++;
				
				
					selectedObjects = new ArrayList(); 
					restoreExpectedObjects();

				
				}
			
				catch(Exception e)
				{
					System.err.println("Error while trying to add a midpoint to construction "+this.name+":");	
					System.err.println(e.getMessage());
				}
			}	
		}

		/**
		* This method creates a circle which is defined by 2 points: the center and another point on
		* the periphery. The directly defining points are known to the
		* construction by previous calls of the selectObject method.
		*
		*/
		public void addTwoPointCircle()
		{
			
			if(creatable)  //two points must have been selected
			{
				
				try {
		
					//get the objects
					int index = (((Integer)selectedObjects.get(0)).intValue());
					DrawableGeoPoint p1=(DrawableGeoPoint)(geoObjects.get(index));
					index = (((Integer)selectedObjects.get(1)).intValue());
					DrawableGeoPoint p2=(DrawableGeoPoint)(geoObjects.get(index));
			
					//create algorithm and circle
					TwoPointCircleAlgorithm conf = new TwoPointCircleAlgorithm(p2,p1);
					DrawableGeoCircle circle = (DrawableGeoCircle)conf.createObject(new String(langStrings.s_circle+numberOfCircles));
					addObject(circle);
					numberOfCircles++;
					redoList=new ArrayList();
					possibleUndoOps++;
			
					selectedObjects = new ArrayList(); 
					restoreExpectedObjects();
				
					}
			
				catch(Exception e)
				{
					System.err.println("Error while trying to add a two-point-circle to construction "+this.name+":");	
					System.err.println(e.getMessage());	
				}
			}
					
		}
		

	/**
	 * 
	 * @param name The name of a geometric object.
	 * @return The index of the object with that name; 
	 * 	if there is no object with the name, the method returns -1.
	 */
	private int getObjectByName(String name)
	{
		Integer i=(Integer)objectNameToObject.get(name);
		if(i!=null)	return i.intValue();		
		else return -1;

	}

	
	/**
	 * This method returns all objects that can be selected by the user by 
	 * a mouseclick. 
	 * @param p A position in world coordinates.
	 * @return The names (instances of String) of all the objects which are 
	 * 	sufficiently close to the given position, of an expected type, not hidden,
	 *  and aren't selected yet.
	 */
	public ArrayList getMatchingObjectsAt(Point2D.Double p)
	{
		ArrayList matchingObjects=new ArrayList();
		for(int i=0; i<geoObjects.size();i++)
		{
			//find matching objects which aren't hidden		
			if(((Geometric)geoObjects.get(i)).isAt(p,tolerance))
			{	
				//check if the object is selected yet;
				//in this case it can't be selected again
				boolean selectedBefore=false;
				for(int r=0; r<selectedObjects.size(); r++)
				{	
					if(((Integer)selectedObjects.get(r)).intValue() == i)
						selectedBefore=true;
					
				}
				//the object isn't selected yet
				if(!selectedBefore)for(int j=0; j<expectedObjects.size();j++)
				{
					//check if the object is of an expected type
					ObjectExpectation obEx=(ObjectExpectation)expectedObjects.get(j);
					if((((Geometric)geoObjects.get(i)).getType()).equals(obEx.getType()))
					{
						//check if the object that would be created is equal to an already existing object
						createPreview(i);
						if(!previewAlreadyExisting())
						{
								matchingObjects.add(((Geometric)geoObjects.get(i)).getName()); 
								break;
						}
					}
				} 
			}
		}
		
		return matchingObjects;
	
	}

	/**
	 * 
	 * @return True, if the current preview object is equal to a formerly created object.
	 */
	private boolean previewAlreadyExisting()
	{
		if(previewObject==null) return false;
		for(int i=0; i<geoObjects.size(); i++)
			if(((Geometric)geoObjects.get(i)).equals(previewObject)) return true; 
		return false;
	}

	/**
	 * Restores the expected objects to their last externally-set state.
	 */
	private static void restoreExpectedObjects()
	{
		expectedObjects=new ArrayList();
		//reset expectations by deep copy of default expectations
		for(int i=0; i<defaultExpectedObjects.size(); i++)
		{
			ObjectExpectation defaultObEx=((ObjectExpectation)defaultExpectedObjects.get(i));
			ObjectExpectation obEx = new ObjectExpectation((new String(defaultObEx.getType())), defaultObEx.getCardinality());
			expectedObjects.add(obEx); 
			
		}
		totalExpectedObjects=defaultTotalExpectedObjects;
		creatable=false;
		if(totalExpectedObjects!=1) previewable=false;
	}

	/**
	 * This method is used to give information on expected objects to the construction.
	 * @param maxCard Maximum number of objects of the given type.
	 * @param type Type (GeoStrings.POINT, GeoStrings.LINE, or GeoStrings.CIRCLE) of the expected objects. 
	 */
	public static void expectObject(String type, int maxCard)
	{
		ObjectExpectation obEx=new ObjectExpectation(type,maxCard);
		expectedObjects.add(obEx);	
		defaultExpectedObjects.add(obEx.clone());
	}

	/**
	 * This method removes all object expectations. Therefore,
	 * no objects are expected any more after calling it.
	 *
	 */
	private static void clearExpectedObjects()
	{
		defaultExpectedObjects=new ArrayList();
		defaultTotalExpectedObjects = 0;
		expectedObjects=new ArrayList();
	}
	
	/**
	 * This method "releases" all currently selected objects. Therefore,
	 * no objects are selected any more after calling it. 
	 */
	public static void releaseSelectedObjects()
	{
		GeoConstruction.selectedObjects=new ArrayList();
	}
	
	/**
	 * 
	 * @param nr The total number of objects that are expected by the current tool.
	 * Their types have to be given by calling the expectObject(...) method.
	 */
	public static void setExpectedObjectNr(int nr)
	{
		totalExpectedObjects=nr;
		defaultTotalExpectedObjects=nr;
	}

	/**
	 * This method updates the complete construction by recalculating semi-free and fixed objects' positions.
	 * 
	 */
	public void calculate()
	{
		int i;
		for(i=0; i<geoObjects.size();i++)
			((Geometric) geoObjects.get(i)).calculate();

	}
	
	/**
	 * This method highlights objects and creates previews (if applicable) in
	 * case the objects are close enough to a given position.
	 * @param p A position in world coordinates.
	 * @return A description of the objects at this position.
	 **/
	public String setMousePosition(Point2D.Double p)
	{
		//is there any object of an expected type at the current mouse position?
		
		boolean didHighlighting=false; //Has some Object been highlighted?
		int previewParam=-1;  //index of the object that is used as
								//unknown defining object of the preview object;
								//-1 if there is none
		boolean multipleObjectsMatching=false; //is there more than one  matching object?
		
		//reset the preview
		previewObject=null;
		
		ArrayList matchingObjects=new ArrayList();
		
		for(int i=0; i<geoObjects.size(); i++)
				//Check if the object is close to the current position
				if(((Geometric)geoObjects.get(i)).isAt(p,tolerance))
				{	
					//check if the object has already been selected;
					//in this case it won't be highlighted
						boolean selectedBefore=false;
						for(int r=0; r<selectedObjects.size(); r++)
							if(((Integer)selectedObjects.get(r)).intValue() == i)
								selectedBefore=true;
					
						//check if the object is of an expected type
						if(!selectedBefore)for(int j=0;j<expectedObjects.size();j++)
						{
							ObjectExpectation obEx = (ObjectExpectation)expectedObjects.get(j);
							if(((Geometric)geoObjects.get(i)).getType().equals(obEx.getType()))
							{	
								//Create a preview object. It won't be displayed but is used to
								//check if the selection of this object would result in the creation
								//of an object that is semantically equal to an already existing one.
								createPreview(i);
								//set highlights
								if(!previewAlreadyExisting())
								{
									((Drawable)geoObjects.get(i)).setHighlight(true);
									didHighlighting=true;
									matchingObjects.add(new Integer(i));
								}
								//destroy preview object
								previewObject=null;
								
								//set as preview parameter; if there's already one: no preview!!
								if(previewParam<=0)
								{
									previewParam=i;
									//matchingObjects.add(new Integer(i));
								} 
								else {multipleObjectsMatching=true;} 
							} 
						}
					}
					
			else ((Drawable)geoObjects.get(i)).setHighlight(false);
		
			//if there's exactly one matching object, try to create a preview
			if((previewParam>=0)&&(!multipleObjectsMatching)) createPreview(previewParam);
			//If a point is expected, it can be created "on the fly". 
			//Hence, a point which serves as directly defining object of the preview object
			//has to be added to the construction.
			else if(isTypeExpected(GeoStrings.POINT))
			{		
				FreePointAlgorithm conf = new FreePointAlgorithm(p.x, p.y);
				DrawableGeoPoint point=(DrawableGeoPoint)conf.createObject(langStrings.s_point+numberOfPoints);
				addObject(point);
				createPreview(geoObjects.size()-1);
				removeObject(geoObjects.size()-1);
			}
		
			//create a description of the matching objects
			String description = createDescription(matchingObjects);
			return description;
	}

	/** 
	 * This method creates a description of the given objects.
	 * @param matchingObjects The objects to describe.
	 * @return A description of the given objects.
	 */
	private String createDescription(ArrayList matchingObjects)
	{
		String description=new String();
		
		if(matchingObjects.size()==1) //exactly one matching object
		{	
			int index = (((Integer)(matchingObjects.get(0)))).intValue();
			Geometric g = (Geometric) geoObjects.get(index);
			description=g.getDescription();	
		} 
		else description=matchingObjects.size()+" "+langStrings.s_matchingobjects;
		return description;
	}

	/**
	 * @param tolerance The maximum distance at which two points
	 * are considered to have the same coordinates.
	 */
	public void setTolerance(double tolerance)
	{
		this.tolerance = tolerance;
	}

	/**
	 * Is there any object (at least partly) within the tolerance area
	 * of the given position?
	 * @param p A position in world coordinates.
	 * @return True, if there is such an object.
	 */
	public boolean isAnyObjectAt(Point2D.Double p)
	{
		//Check for every object if it's sufficiently close to the given position
		for(int i=0; i<geoObjects.size(); i++)
			if(((Geometric)geoObjects.get(i)).isAt(p,tolerance)) return true;
		return false;
	}
	
	/**
	 * This method can be used to get all the objects which are sufficiently close to a given position.
	 * @param type The type of the Object - e.g. GeoStrings.POINT.
	 * @param p A position in world coordinates.
	 * @return An ArrayList containing all the geometric objects (instances of Geometric) 
	 * which are within the tolerance area of the given position.
	 */
	public ArrayList getObjectsAt(String type, Point2D.Double p)
	{	
		//Check all the objects if they are at this position and match the
		//given type
		ArrayList objects = new ArrayList();
		for(int i=0; i<geoObjects.size(); i++)
		{
			if((((Geometric)geoObjects.get(i)).getType().equals(type)) && (((Geometric)geoObjects.get(i)).isAt(p,tolerance))) 
				objects.add(geoObjects.get(i));	
		}
		return objects;
	}

	
	/**
	 * Selects the point which can be moved.
	 * @param name The point's name.
	 */
	public void setPointToMove(String name)
	{
		pointToMove=((Integer)objectNameToObject.get(name)).intValue();	
	}
	
	/**
	 * Moves the point that has been selected by the setPointToMove(String)
	 * method to a new position.
	 * @param p The new position in world coordinates.
	 * @return True, if a point is selected for movement, otherwise false.
	 */
	public boolean movePoint(Point2D.Double p)
	{
		if(pointToMove>=0&&cutoffObjects.size()==0)
		{
			((DrawableGeoPoint)(geoObjects.get(pointToMove))).move(p.x,p.y,1); 
			calculate();
			return true;
		}
		else return false;
	}
	
	/**
	 * @return An ArrayList object, containing the drawable subset 
	 * (referring to being an instance of Drawable, not to the drawable property) of 
	 * the construction's geometric objects and, if existing, a preview object. 
	 * At the moment, every geometric object in a construction is drawable in the above-mentioned sense.
	 */
	public ArrayList getDrawableGeoObjects()
	{
		ArrayList drawableGeoObjects = new ArrayList();
		//iterate over the objects and add them to the drawable list if they are instances of Drawable
		for(int i=0; i<geoObjects.size(); i++) 
		{
			Object o = geoObjects.get(i);
			if(o instanceof Drawable) drawableGeoObjects.add(o);
		}
		if(previewObject!=null){
			drawableGeoObjects.add(previewObject);
			previewObject.setPreview();}
		return drawableGeoObjects;
	}

	/**
	 * Does some clean-up tasks and should be called every time the construction
	 * looses the focus. 
	 **/
	public void focusLost()
	{

		GeoConstruction.restoreExpectedObjects(); 
		GeoConstruction.releaseSelectedObjects();
		GeoConstruction.previewObject=null;
		addObjects(cutoffObjects);
		cutoffObjects=new ArrayList();
	}

	/**
	 * "Releases" the point which has been selected for movement before.
	 */
	public void releasePointToMove()
	{
		pointToMove=-1;
	}

	/**
	 * @return A caption fpr windows which display the construction. At the moment, simply the construction's name.
	 */
	public String getViewCaption() {
		return name;
	}

	/** Used for informing the construction about a tool change.
	 * @param string The new tool type, e.g. GeoStrings.TWOPOINTLINE.
	 */
	public void toolChanged(String type) 
	{
		GeoConstruction.setObjectType(type);
		clearExpectedObjects();
		GeoConstruction.releaseSelectedObjects();
		GeoConstruction.previewObject=null;
		addObjects(cutoffObjects);
		cutoffObjects=new ArrayList();
		currentObject=null;
		creatable=false;
		previewable=false;
	}

	/**
	 * This method finds out which algorithm types are suitable for
	 * the by the currently selected object's type.
	 * @return An array list containing String objects (as defined in the GeoStrings class) 
	 * specifying which algorithm
	 * types are suitable for the currently selected object. 
	 * Be aware that it returns null if there has already been a choice 
	 * made in the current change operation before!
	 */
	public ArrayList getMatchingAlgorithmTypes()
	{
		
		//if there's more than one object selected, a choice has been made before...
		//... and special treatment for intersections
		if(selectedObjects.size()>1 || choice.equals(GeoStrings.INTERSECTION)) return null;
		
		ArrayList matchingTypesList=new ArrayList(); //List of Strings that define the found matching conf-types
		//get the selected object (should be exeactly one for the change tool)
		Geometric selectedObject=(Geometric)geoObjects.get(((Integer)selectedObjects.get(0)).intValue());
		//search algorithm list for matching algorithm types
		for(int i=0; i<algInfo.length; i++)
		{
			if(selectedObject.getObjectType().equals(algInfo[i].getDependentObjectType()))
				matchingTypesList.add(algInfo[i].getName());
		}
		
		return matchingTypesList;
	}

	/**
	 * This methods simply sets a String variable. It's for instance used to store the target algorithm
	 * for change operations. 
	 * @param choice The string.
	 */
	public void setChoice(String choice)
	{
		this.choice=choice;
	}

	/**
	 * 
	 * @return The last choice that was set by the setChoice(String) method.
	 */
	public String getChoice()
	{
		return choice;
	}


	/**
	 * This method substitutes the algorithm of the first object that is selected by the 
	 * selectObject(...) method with an instance of an algorithm type 
	 * determined by the setChoice(String) method, using the other currently
	 * selected Objects as directly defining objects. If the method is called 
	 * while there is exactly one selected object, it initializes the change operation; 
	 * if it's called with at least one, but not sufficient selected objects to perform 
	 * the change operation, nothing happens.
	 * @param pos A position in world coordinates that influences the position of the
	 * changed geometric object (if it's not fixed)
	 */
	public void change(Point2D.Double pos)
	{
		if(choice==null) return; 
		
		int index=2; //the index of the algorithm info
		DrawableGeoObject objectToChange=currentObject; //the object that will be changed
				
		//find information on the target algorithm
		for(int i=0; i<algInfo.length; i++)
		{
			
			boolean found=false;
			if(algInfo[i].getName().equals(choice))
			{
				index=i;
				found=true;
			}
			if(found) break;
		}
		
		//only one object selected-->initialize change mode
		if(selectedObjects.size()==1)
		{
			//set information on the expected input
			objectType=algInfo[index].getName();
			expectedObjects=new ArrayList();
			ObjectExpectation obExp[]=algInfo[index].getExpectedObjects();
			if(obExp!=null)
			{
				for(int i=0; i<obExp.length; i++)
				{
					expectedObjects.add(obExp[i]);
				}
			}
			totalExpectedObjects=algInfo[index].getReqObjNr();
			
			objectToChange=(DrawableGeoObject)geoObjects.get(((Integer)selectedObjects.get(0)).intValue());
			currentObject=objectToChange;
			
			//remove the selected object's dependent objects from the construction

			//get the index of the object whose algorithm will be changed
			int objIndex=((Integer)selectedObjects.get(0)).intValue();
			//if there's more input necessary, split the list; two iterations are necessary for keeping the objects' order
			if(algInfo[index].getReqObjNr()!=0)
			{
				cutoffObjects=new ArrayList();
				//create a list of depending objects by using a dependency graph
				DependencyGraph dgraph=new DependencyGraph(geoObjects);
				String[] dependingObjects=dgraph.getDependingOrSelfObjects(objectToChange.getName());
				for(int i=0; i<geoObjects.size(); i++)
				{
					int run=0;
					while(run<dependingObjects.length)
					{
						if(dependingObjects[run].equals(((DrawableGeoObject)geoObjects.get(i)).getName()))
							cutoffObjects.add(geoObjects.get(i));
						run++;
					}

				}
				//temporarily remove the depending objects from the construction 
				for(int i=geoObjects.size()-1; i>=0; i--)
				{
					int run=dependingObjects.length;
					while(run>0)
					{
						boolean removed=false;
						run--;
						if(dependingObjects[run].equals(((DrawableGeoObject)geoObjects.get(i)).getName()))
						{
							removeObject(i);
							removed=true;
						} 
				
						if(removed) break;
				
					}
			
				}
				//refresh the map
				recreateMap();
			}

		}
	
		//if enough matching objects are selected, perform the change operation
		//keep in mind that the object whose algorithm will be changed is in the selectedObjects-List as well!!!
		if(algInfo[index].getReqObjNr()==selectedObjects.size()-1)
		{
			//get the object whose algorithm will be changed
		
				//free point
				if(choice.equals(GeoStrings.FREEPOINT))
				{

					DrawableGeoPoint p=(DrawableGeoPoint)objectToChange;		
					double x=p.coords.getX();
					double y=p.coords.getY();
					FreePointAlgorithm conf=new FreePointAlgorithm(x,y);
					p.setAlgorithm(conf);
					conf.setDependingObject(p);

				}
				
				else //objects which aren't free
				{
					Geometric g = objectToChange; 
					
					//floater
					if(choice.equals(GeoStrings.FLOATER))
					{

						DrawableGeoPoint p=(DrawableGeoPoint) g;
						FloaterAlgorithm conf = new FloaterAlgorithm(pos,(Floatable)geoObjects.get(((Integer)selectedObjects.get(0)).intValue()));
						conf.setDependingObject(p);
						p.setAlgorithm(conf);
						p.coords.set(pos.x,pos.y,1);

					}
					
					//midpoint
					else if(choice.equals(GeoStrings.MIDPOINT))
					{
						MidPointAlgorithm conf = new MidPointAlgorithm((DrawableGeoPoint)geoObjects.get(((Integer)selectedObjects.get(0)).intValue()),(DrawableGeoPoint)geoObjects.get(((Integer)selectedObjects.get(1)).intValue()));
						conf.setDependingObject(g);
						g.setAlgorithm(conf);
					}
					//intersection point
					else if(choice.equals(GeoStrings.INTERSECTION))
					{
						//get the directly defining objects
						Intersectable i1=(Intersectable)geoObjects.get(((Integer)selectedObjects.get(0)).intValue());
						Intersectable i2=(Intersectable)geoObjects.get(((Integer)selectedObjects.get(1)).intValue());

						//Find out if there's already an appropriate algorithm instance existing (that is, if another 
						//intersection of the directly defining objects is existing yet).
						//Otherwise, create one.
						IntersectionAlgorithm conf;
						conf=getIntersectionConf(i1.getName(),i2.getName());
						if(conf==null) conf = new IntersectionAlgorithm(i1,i2);
						conf.setDependentObject(g,pos);
						g.setAlgorithm(conf);
					}
					//parallel
					else if(choice.equals(GeoStrings.PARALINE))
					{
						Object o1 = geoObjects.get(((Integer)selectedObjects.get(0)).intValue());
						Object o2 = geoObjects.get(((Integer)selectedObjects.get(1)).intValue());
						ParaLineAlgorithm conf; 
						if(o1 instanceof DrawableGeoPoint)					
							conf=new ParaLineAlgorithm((DrawableGeoPoint)o1,(DrawableGeoLine)o2);
						else	
							conf=new ParaLineAlgorithm((DrawableGeoPoint)o2,(DrawableGeoLine)o1);
							conf.setDependingObject(g);
							g.setAlgorithm(conf);
					}
					//orthogonal
					else if(choice.equals(GeoStrings.ORTHOLINE))
					{
						Object o1 = geoObjects.get(((Integer)selectedObjects.get(0)).intValue());
						Object o2 = geoObjects.get(((Integer)selectedObjects.get(1)).intValue());
						OrthoLineAlgorithm conf; 
						if(o1 instanceof DrawableGeoPoint)					
							conf=new OrthoLineAlgorithm((DrawableGeoPoint)o1,(DrawableGeoLine)o2);
						else	
							conf=new OrthoLineAlgorithm((DrawableGeoPoint)o2,(DrawableGeoLine)o1);
											  conf.setDependingObject(g);
											  g.setAlgorithm(conf);
					}
					//two point line
					else if(choice.equals(GeoStrings.TWOPOINTLINE))
					{
						TwoPointLineAlgorithm conf = new TwoPointLineAlgorithm((DrawableGeoPoint)geoObjects.get(((Integer)selectedObjects.get(0)).intValue()),(DrawableGeoPoint)geoObjects.get(((Integer)selectedObjects.get(1)).intValue()));
						conf.setDependingObject(g);
						g.setAlgorithm(conf);
					}
					//two point circle
					else if(choice.equals(GeoStrings.TWOPOINTCIRCLE))
					{
						TwoPointCircleAlgorithm conf = new TwoPointCircleAlgorithm((DrawableGeoPoint)geoObjects.get(((Integer)selectedObjects.get(1)).intValue()),(DrawableGeoPoint)geoObjects.get(((Integer)selectedObjects.get(0)).intValue()));
						conf.setDependingObject(g);
						g.setAlgorithm(conf);
					}
				
					addObjects(cutoffObjects);
					cutoffObjects=new ArrayList();
					restoreExpectedObjects();
					selectedObjects=new ArrayList();
				
				}

			choice=new String("unknown");
			currentObject=null;
			redoList=new ArrayList();
			possibleUndoOps=0;
			recreateMap();
			
		}
	
		calculate();
	
	}
	
	/**
	 * Finds the intersection algorithm that is responsible for intersections of the objects of the given name.
	 * @param name1 The first intersectable object's name.
	 * @param name2 The second intersectable object's name.
	 * @return The algorithm, or null, if there is no such algorithm.
	 */
	private IntersectionAlgorithm getIntersectionConf(String name1, String name2)
	{
		//look at every point object and find out its algorithm is an intersection algorithm
		//if so, compare the directly defining objects' names to the given ones. 
		try
		{
			for(int i=0; i<geoObjects.size(); i++)
			{
				Geometric g=(Geometric)geoObjects.get(i);
				if(g.getAlgorithm() instanceof IntersectionAlgorithm)
				{   
					IntersectionAlgorithm c=(IntersectionAlgorithm)g.getAlgorithm();
					if(c.intObjectsAre(name1, name2)) return c;
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("There has been an error while trying to find an existing algorithm for the intersection of "+name1+" and "+name2+":");
			System.err.println(e.getMessage());
		}
		return null;
	}


	/**
	 * Saves the construction.
	 * @param file Name and path of the file.
	 */
	public void save(String file)
	{
		//create an XMLConstruction, call the getGeoObjectDescription methods of all 
		//objects and add them to the XML Construction
		XMLConstruction xmlConst=new XMLConstruction();
		for(int i=0; i<geoObjects.size(); i++)
		{
			Object o = geoObjects.get(i);
			if(o instanceof GeoXMLStorable)
			{
				GeoXMLStorable object=(GeoXMLStorable) o;
				xmlConst.addObject(object.getGeoObjectDescription());
			}
			
		}
	
		//write the construction to a file
		xmlConst.writeXML(file);
	
	}

	/**
	 * Loads the construction from a file.
	 * @param file Name and path of the file.
	 */
	public void load(String file)
	{
		
		//parse the xml file
		XMLConstruction xmlConst=new XMLConstruction();
		xmlConst.parseFile(file);
		
		//get object descriptions from the file and create according objects and algorithms.
		GeoObjectDescription objectDescription=xmlConst.getNextObject();
		
		while(objectDescription!=null)
		{
			double[] coords = objectDescription.getVariables();  //the coordinates
			
			//select the defining objects (enabling usage of the add...-methods)
			String[] defObjects=objectDescription.getDefObjects();
			for(int i=0; i<defObjects.length; i++)
				selectObject(defObjects[i]);
		
			//for usage of the add-methods
			creatable=true;
						
			//the object is a free point
			if(objectDescription.getType().equals(GeoXMLStrings.POINT)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.FREE))
					addPoint(new Point2D.Double(coords[0]/coords[2], coords[1]/coords[2]));
		
			//the object is a floater
			else if(objectDescription.getType().equals(GeoXMLStrings.POINT)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.FLOATER))
					addFloater(new Point2D.Double(coords[0]/coords[2], coords[1]/coords[2]), defObjects[0]);
			
			//the object is an intersection point
			else if(objectDescription.getType().equals(GeoXMLStrings.POINT)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.INTERSECTION))
					{
					addIntersection(defObjects[0], defObjects[1], new Point2D.Double(coords[0]/coords[2],coords[1]/coords[2]));
					}
			
			//the object is a midpoint
			else if(objectDescription.getType().equals(GeoXMLStrings.POINT)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.MIDPOINT))
					addMidPoint();
					
			//the object is a parallel line
			else if(objectDescription.getType().equals(GeoXMLStrings.LINE)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.PARALLEL))
					addParaLine();

			//the object is an orthogonal line
			else if(objectDescription.getType().equals(GeoXMLStrings.LINE)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.PLUMB))
					addOrthoLine();

			//the object is a TwoPointCircle
			else if(objectDescription.getType().equals(GeoXMLStrings.CIRCLE)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.TWOPOINTCIRCLE))
					addTwoPointCircle();

			//the object is a TwoPointLine
			else if(objectDescription.getType().equals(GeoXMLStrings.LINE)
				&& objectDescription.getAlgorithm().equals(GeoXMLStrings.TWOPOINTLINE))
					addTwoPointLine();

			//now set the display options
			//the current object is the most recently added object in the geoObjects list
			DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(geoObjects.size()-1);
			o.setEmph(objectDescription.getEmphFactor());
			o.setName(objectDescription.getName());
			o.setShowName(objectDescription.getShowName());
			double colorArray[]=objectDescription.getColor();
			java.awt.Color color;
			
			if(colorArray.length!=3) color=Color.BLACK;
			else 
			{	
				//the awt color components are int values 0..255
				color=new Color((int)colorArray[0],(int)colorArray[1],(int)colorArray[2]);
			}
			o.setColor(color);
			//get the next object
			objectDescription=xmlConst.getNextObject();
			selectedObjects=new ArrayList();
		}
		
		creatable=false;
		calculate();
	
	}



	/**
	 * This method returns the names of all objects of the given type.
	 * @param type The type, either an algorithm or an object type as defined in the GeoStrings class.
	 * @see geofuchs.control.GeoStrings
	 * @return
	 */
	public ArrayList getObjectsOfType(String type)
	{
		//search for objects with a matching object type or algorithm type
		
		ArrayList matchingObjects=new ArrayList();
		for(int i=0; i<geoObjects.size(); i++)
		{	
		
			DrawableGeoObject o= (DrawableGeoObject) geoObjects.get(i);
			if(o.getObjectType().equals(type) || o.getAlgorithm().getType().equals(type))
				matchingObjects.add(o.getName());
		
		}
				 
		return matchingObjects;
	
	}

	/**
	 * Highlights the object of the given name. "Unhighlights" all other objects.
	 * @param name The object's name.
	 */
	public void highlightObjectByName(String name)
	{
		
		for(int i=0; i<geoObjects.size(); i++)
		{
			DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(i);
			if(o.getName().equals(name))
			{o.setHighlight(true);} 
			else o.setHighlight(false);
		}
		
	}
	
	/**
	 * Returns true, if an object of the given type is expected.
	 * @param type The type, e.g. GeoStrings.POINT
	 * @return True, if an object of the given type is expected.
	 */

	public boolean isTypeExpected(String type)
	{
		//find out if one of the expected objects is of the given type
		for(int i=0; i<expectedObjects.size(); i++)
		{
			ObjectExpectation obEx=(ObjectExpectation) expectedObjects.get(i);
			if(obEx.getType().equals(type)) return true;
		}
		return false;
	}

	/**
	 * Returns true, if the object is of an expected type and 
	 * has not been selected yet.
	 * @param name The object's name.
	 * @return See above.
	 */
	public boolean isExpected(String name)
	{
		//check if the object is already selected
		for(int i=0; i<selectedObjects.size(); i++)
		{
			int index=((Integer)(selectedObjects.get(i))).intValue();
			DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(index);
			if(o.getName().equals(name)) return false;
		}
		
		//check if it's matching an expected type
		boolean isExpected=false;
		int index=getObjectByName(name);
		DrawableGeoObject o=null;
		if(index>=0) o=(DrawableGeoObject)geoObjects.get(getObjectByName(name));
		else return false;
		for(int i=0; i<expectedObjects.size(); i++)
		{
			ObjectExpectation obEx=(ObjectExpectation)expectedObjects.get(i);
			if(o!=null && obEx.getType().equals(o.getType())) isExpected=true;
		}
		return isExpected;
		
	}

	/**
	 * Enables display of the names of the objects with the given names;
	 * if the name is already shown it won't be shown any more.
	 * @param names An ArrayList containing the objects' names (type String).
	 */
	public void showNames(ArrayList names)
	{
		//iterate over the given  objects and enable/disable their show name property
		for(int i=0; i<names.size(); i++)
		{
			String name=(String)names.get(i);
			int index=getObjectByName(name);
			if(index>=0) 
			{
					
				DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(index);
				o.setShowName(!o.showName);
			}
			
		}
	}
	/**
	* Shows the names of the objects with the given names;
	* if the name is already shown it won't be shown any more.
	* @param names An ArrayList containing the objects' names (type String).
	* @param pos The names' position.
	**/
	public void showNames(ArrayList names, Point2D.Double pos)
	{
		//iterate over the given  objects and enable/disable their show property
		for(int i=0; i<names.size(); i++)
		{
			String name=(String)names.get(i);
			int index=getObjectByName(name);
			if(index>=0) 
			{
				
				DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(index);
				if(o.showName==false) o.setNamePosition(pos);
				o.setShowName(!o.showName);
			}
			
		}
	}
	
	/**
	 * Renames a geometric object. If there is already an object with a name equal to the new name,
	 * nothing happens at all. The same goes if there's no object with the old name, but in this case 
	 * the method returns TRUE!!!
	 * @param oldName The old name.
	 * @param newName The new name.
	 * @return False, if there is already an object with the given new name, or
	 * if one of the given names is null, or if the new name is empty, or if the new name equals an object type name 
	 * or algorithm type name; otherwise true.
	 * */
	public boolean setObjectName(String oldName, String newName)
	{
		//the new name may not be an object type's or algorithm type's name
		
		for(int i=0; i<algInfo.length; i++)
		{
			if(newName.equals(algInfo[i].getName()) || newName.equals(algInfo[i].getDependentObjectType()) || (newName.startsWith(algInfo[i].getDependentObjectType()) && !newName.equals(oldName))) return false;
		}


		if(newName==null || oldName==null || newName.equals("")) return false;
		int index=getObjectByName(oldName);
		
		if(index==-1) return true;
		DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(index);
		//is there already an object with the given new name?
		o.setName("");
		int newNameIndex=getObjectByName(newName);
		if(newNameIndex>=0) 
		{
			o.setName(oldName);
			return false;
		} 
		
		o.setName(newName);
		objectNameToObject.put(newName,new Integer(index));
		objectNameToObject.remove(oldName);

		//recreate object name to index map
		recreateMap();

		return true;
	}

	/**
	* Sets the colors of all given objects to the given color.
	* @param names An ArrayList containing the objects' names (type String).
	* @param color The new color.
	**/
	public void setColor(ArrayList names,  Color color)
	{
		//iterate over the given  objects and set their color
		for(int i=0; i<names.size(); i++)
		{
			String name=(String)names.get(i);
			int index=getObjectByName(name);
			if(index>=0) 
			{
				
				DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(index);
				o.setColor(color);
			}
		}
	}

	/**
	* Sets the emphasis factor of the objects given by their names to the given number.
	* @param names An ArrayList containing the objects' names (type String).
	* @param emph The emphasis factor.
	**/
	public void setEmph(ArrayList names,  int emph)
	{
		//iterate over the given  objects and set their color
		for(int i=0; i<names.size(); i++)
		{
			String name=(String)names.get(i);
			int index=getObjectByName(name);
			if(index>=0) 
			{
				DrawableGeoObject o=(DrawableGeoObject) geoObjects.get(index);
				o.setEmph(emph);
			}
		}
	}

	/**
	 * This method checks if an undo operation is currently possible. 
	 * If so, it removes the most recently added gepmetric object
	 * from the construction. Otherwise, nothing happens.
	 *
	 */
	public void undo()
	{
		if(possibleUndoOps>0)
		{
			Object o=geoObjects.get(geoObjects.size()-1);	
			redoList.add(o);
			removeObject(geoObjects.size()-1);
			possibleUndoOps--;
		}
	}

	/**
	 * This method checks if there are any undo operations
	 * (cancellations of operations which adding objects) which
	 * can be undone themselves. If so, the objects which were 
	 * removed from the construction are added again. Otherwise,
	 * nothing happens.
	 */
	public void redo()
	{
		if(redoList.size()>0)
		{
			Object o=redoList.get(redoList.size()-1);
			addObject(o);
			redoList.remove(redoList.size()-1);
			possibleUndoOps++;
		}
		return;
	}

}
