/*
 * Created on 11.07.2004
 */
package geoxml;

/**
 * Instances of this class describe a geometric object's properties
 * and are used for communication between the application and 
 * the GeoXML package.
 * @author Andy Stock
 */
public class GeoObjectDescription 
{

	private String name;	//the object's name
	private String id;		//the object's ID
	private String type;	//the object type, e. g. point, line, ...
	private String algorithm; //the algorithm type, e. g. midpoint, intersection point, ...
	private String[] defObjects;    //the names of the directly defining objects
	private double[] variables;	   //e.g. coordinates
	private double[] color=new double[] {0,0,0}; //The display color, should be given as RGB
	private int emphFactor=1;	//the emphasis factor
	private boolean showName=false; //the show name property
	
	/**
	 * The standard constructor. It's only creating an instance, but the
	 * attributes name, type and so on will be null!
	 */
	public GeoObjectDescription(){}
	
	/**
	 * This constructor method sets the attributes to the given values.
	 * @param name The geometric object's name.
	 * @param type The geometric object's type, e.g. point.
	 * @param algorithm The geometric object's algorithm type, e.g. midpoint.
	 * @param defObjects The objects that are necessary for defining the geometric objects, e.g. two other points for a midpoint.
	 * @param variables Floating number variables like coordinates.
	 **/
	public GeoObjectDescription(String id, String name, String type, String configuration, String[] defObjects, double[] variables)
	{
		this.id=id;
		this.name=name;
		this.type=type;
		this.algorithm=configuration;
		this.defObjects=defObjects;
		this.variables=variables;
	}	
	
	/**
		 * This constructor method sets the attributes to the given values.
		 * @param name The geometric object's name.
		 * @param type The geometric object's type, e.g. point.
		 * @param algorithm The geometric object's algorithm, e.g. midpoint.
		 * @param defObjects The objects that are necessary for defining the geometric object, e.g. two other points for a midpoint.
		 * @param variables Floating number variables like coordinates.
		 * @param showName The show-name-property value.
		 * @param emphasis The emphasis parameter.
		 * @param color The color parameters, preferably RGB.
	***/
	
	public GeoObjectDescription(String id, String name, String type, String algorithm, String[] defObjects, double[] variables, boolean showName, int thickness, double[] color)
	{
		this.id=id;
		this.name=name;
		this.type=type;
		this.algorithm=algorithm;
		this.defObjects=defObjects;
		this.variables=variables;
		this.showName=showName;
		this.emphFactor=thickness;
		this.color=color;
	}	
	
	/**
	 * Sets the name of the object.
	 * @param name The object's name.
	 */
	public void setName(String name)
	{
		this.name=name;
	}
	
	/**
	 * Sets the type of the object, e.g. point, line, circle...
	 * @param type The object's type.
	 */	
	public void setType(String type)
	{
		this.type=type;
	}
	
	/**
	 * Sets the algorithm of the object, e.g. midpoint, floater, free point...
	 * @param algorithm The object's algorithm.
	 */	
	public void setAlgorithm(String configuration)
	{
		this.algorithm=configuration;
	}

	/**
	 * Sets the id of the object.
	 * @param id The object's id.
	 */	
	public void setID(String id)
	{
		this.id=id;
	}
	
	/**
	 * Sets the objects that directly define this object, e.g. two points for a midpoint 
	 * @param defObjects The directly defining objects' names.
	 */	
	public void setDefObjects(String[] defObjects)
	{
		this.defObjects=defObjects;
	}

	/**
	 * Sets variables for this object, e.g. the coordinates of a free point
	 * @param variables The variables.
	 */	
		public void setVariables(double[] variables)
		{
			this.variables=variables;
		}
	

	/**
	 * 
	 * @return The object's name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @return The object's type.
	 */
	public String getType()
	{
		return type;
	}	
	
	/**
	 * 
	 * @return The object's id. Will be set automatically when added to an XMLConstruction.
	 */
	public String getID()
	{
		return id;
	}	
	
	/**
	 * 
	 * @return The object's algorithm type.
	 */
	public String getAlgorithm()
	{
		return algorithm;
	}
	
	/**
	 * 
	 * @return The object's directly defining objects' names.
	 */
	public String[] getDefObjects()
	{
		return defObjects;
	}	

	/**
	 * 
	 * @return The object's variables, e.g. coordinates for free points.
	 */
	public double[] getVariables()
	{
		return variables;
	}	
	
	/**
	 * 
	 * @return The object's show-name-property value.
	 */
	public boolean getShowName()
	{
		return showName;
	}
	
	/**
	 * Sets the described object's show-name-property value. 
	 * @param showName The described object's show-name-property value.
	 */
	public void setShowName(boolean showName)
	{
		this.showName=showName;
	}
	/**
	 * Sets the described object's thickness parameter. 
	 * @param showName The described object's thickness parameter.
	 */
	public void setEmphFactor(int emphFactor)
	{
		this.emphFactor=emphFactor;
	}
	/**
	 * 
	 * @return The described object's emphasis factor.
	 */
	public int getEmphFactor()
	{
		return emphFactor;
	}
	
	/**
	 * Sets the described object's color paramters. 
	 * @param showName The described object's color parameters, preferably RGB.
	 */
	public void setColor(double[] color)
	{
		 this.color=color;
	}
	
	/**
	 * 
	 * @return The described object's color parameters.
	 */
	public double[] getColor()
	{
		return color;
	}
	
}
