/*
 * Created on 26.03.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package geofuchs.model;

import geofuchs.control.*;
import geoxml.*;
import java.awt.Color;

/**
 *  This class represents geometric objects which can be drawn on 
 *  a drawing plane in Geofuchs.
 *  @author Andy Stock
 *
 */
public abstract class DrawableGeoObject implements Drawable, Geometric, GeoXMLStorable
{
	
	protected String name;		  //the object's name
	protected boolean highlight;  //is the object highlighted, for instance when it could be selected by a mouseclick?
	protected GeoAlgorithm algorithm; //The algorithm.
	protected boolean isDrawable = true; //Is the object drawable? For instance, points on the far line aren't.
	boolean previewed=false;  //is this a preview object???
	boolean showName=false;	//should the name be displayed?
	Color color = Color.BLACK; //the object's color
	int emph=1;				   //zero for hidden, one for normal, two for emphasis, three for more emphasis of the graphical representation
	
	
	/**
	 * @see geofuchs.model.Geometric
	 */
	public String getDescription()
	{
		if(algorithm==null) return new String(name+": "+langStrings.s_none+" "+langStrings.s_algorithm); //INT
		else return algorithm.getDescription();
	}
	/**
	 * @see geofuchs.model.Drawable
	 *
	 */	
	public boolean isDrawable()
	{
		return isDrawable;
	}

	/**
	 * Allows setting of the point's drawable-property. E.g. a floater on a
	 * line that is not drawable is neither. 
	 * @param isDrawable True or false...
	 */
	public void setDrawable(boolean isDrawable)
	{
		this.isDrawable=isDrawable;
	}
	/**
	 * @see geofuchs.model.Drawable
	 *
	 */	
	public void setHighlight(boolean highlight)
	{	
		this.highlight=highlight;
	}

	/**
	 * @see geofuchs.model.Drawable
	 *
	 */	
	public void setPreview()
	{
		previewed=true;
	}


	/**
	 * @see geofuchs.model.Geometric
	 *
	 */	
	public void calculate()
	{
		if(algorithm != null) {algorithm.calculate();} 
	}
	/**
	 * @see geofuchs.model.Geometric
	 *
	 */	
	public boolean equals(Geometric g)
	{
		GeoAlgorithm gconf=g.getAlgorithm();
		if(algorithm==null || gconf==null) return false; //simple point
		return gconf.equals(algorithm);
		
	}
	/**
	 * @see geofuchs.model.Geometric
	 *
	 */	
	public GeoAlgorithm getAlgorithm()
	{
		return algorithm;
	}
	/**
	 * @see geofuchs.model.Drawable
	 *
	 */	
	public void setAlgorithm(GeoAlgorithm conf)
	{
		algorithm=conf;
	}
	/**
	 * @see geofuchs.model.Geometric
	 *
	 */	
	public void setName(String name)
	{
		this.name=name;
	}
	/**
	 * @see geofuchs.model.Drawable
	 *
	 */	
	public String[] getDefiningObjectNames()
	{
		if(algorithm!=null)
			return algorithm.getDefiningObjectNames();
		else return new String[0];
	}
	
	/**
	 * @see geofuchs.model.Drawable
	 *
	 */	
	public void setColor(Color color)
	{
		this.color=color;
	}
	
	/**
	* 
	* @see geofuchs.model.Drawable
	*
	*/	
	public void setShowName(boolean showName)
	{
		this.showName=showName;
	}
	
	
	/**
	* 
	* @see geofuchs.model.Drawable
	*
	*/	
	public void setEmph(int emph)	
	{
		this.emph=emph;
	}
	
	/**
	* 
	* @see geofuchs.model.Drawable
	*
	*/	
	public int getEmph()	
	{
		return emph;
	}	
	
	/** 
	 * @see geoxml.GeoXMLStorable
	 */
	public GeoObjectDescription getGeoObjectDescription() 
	{
		double[] color=new double[] {this.color.getRed(), this.color.getGreen(), this.color.getBlue()};
		GeoObjectDescription object = new GeoObjectDescription(name, name, this.getGeoXMLType(), algorithm.getGeoXMLType(), algorithm.getDefiningObjectNames(), algorithm.getFreeCoordinates(), this.showName,this.emph,color);
		return object;
	}
	
	/**
	 * 
	 * @return The GeoXML-Type of the object.
	 */
	abstract public String getGeoXMLType();
	
}
