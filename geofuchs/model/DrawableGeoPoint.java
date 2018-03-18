/*
 * Created on 26.03.2003
 */
package geofuchs.model;

import geofuchs.control.*;
import geofuchs.control.GeoStrings;
import geofuchs.mathtools.*;
import java.awt.*;
import java.awt.geom.*;
import geoxml.*;


/**
 * This class represents points.
 * @author Andy Stock
 */
public class DrawableGeoPoint extends DrawableGeoObject 
{

	public Point3D coords=new Point3D(0,0,0); //The coordinates
	private Point2D.Double nameVector=new Point2D.Double(0,20); //This vector is added to the point's coordinatesnand the point's name is drawn at the resulting position
	
	/**
	 * The constructor.
	 * @param p The point's coordinates.
	 * @param name The point's name.
	 */
	public DrawableGeoPoint(Point3D p, String name)
	{
		coords=p;
		this.name=name;
	}


	/**
	 * Constructor.
	 * @param x The real x coordinate.
	 * @param y The real y coordinate.
	 * @param name The point's name.
	 */
	public DrawableGeoPoint(double x, double y, String name)
	{
		this.coords=new Point3D(x,y,1);
		this.name=name;
	}

	/**
	 * Constructor.
	 * @param x The real x coordinate.
	 * @param y The real y coordinate.
	 * @param algorithm The point's algorithm.
	 * @param name The point's name.
	 */
	public DrawableGeoPoint(double x, double y, GeoAlgorithm algorithm, String name)
	{
		this(x,y,name);
		this.algorithm=algorithm;
	}

	/**
	* Constructor.
	* @param algorithm The point's algorithm.
	* @param name The point's name.
	*/
	public DrawableGeoPoint(GeoAlgorithm algorithm, String name)
	{
		this(0,0,name);
		this.algorithm=algorithm;
	}

	/**
	 * @see geofuchs.model.Geometric
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * @return The string  for points as defined in the GeoStrings class.
	 */
	public String getType()
	{
		return GeoStrings.POINT;
	}

	/**
	 * @return True, if the coordinates are real.
	 */
	public boolean isReal()
	{
		return coords.h!=0;
	}

	/**
		* @see geofuchs.model.Drawable
	*/
		public void draw(Graphics2D g, Point2D.Double uL, Point2D.Double lR) 
		{
			if(coords.x==0 && coords.y==0 && coords.h==0) isDrawable=false;
			
			//check if the point can be drawn
			if(!isDrawable || (emph==0 && !highlight) || coords.h==0) return; 
			if(coords.x/coords.h<uL.x || coords.x/coords.h>lR.x || coords.y/coords.h<uL.y || coords.y/coords.h>lR.y) return;		
			
			double scale = (double)g.getTransform().getScaleX();
			double rad = (double)GDefaults.pointRadius/scale;
		
			Color color = this.color;
			if(highlight) 
			{
				rad*=1.35;
				color=color.brighter();
			} 
		
			if(emph==0) rad*=0.5;
			else if(rad>1) rad+=emph;

			//get the cartesian (world) coordinates
			Point2D.Double p = new Point2D.Double(coords.x/coords.h, coords.y/coords.h);
			Point2D.Double pt = new Point2D.Double(p.x+rad,p.y);
			
			g.setPaint(new GradientPaint(p,color,pt,Color.BLACK));
			g.fill(new Ellipse2D.Double(p.getX()-rad,p.getY()-rad,rad*2,rad*2));
			//display the point's name (if applicable)
			if(showName)
				g.drawString(name, (int)(p.x+nameVector.x), (int)(p.y+nameVector.y));
									
		}

	

	/**
	* This method moves the point to (x,y,h). 
	* @param x The x-coordinate of the new position.
	* @param y The y-coordinate of the new position.
	* @param h The h-coordinate of the new position.
	**/
	public void move(double x, double y, double h)
	{
			if((x==0 && y==0 && h==0) || Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(h)) isDrawable=false;
			else 
			{
					isDrawable=true;
					coords.set(x,y,h);
			}	
	}

	/**
	 * @see geofuchs.model.Geometric
	 */
	public boolean isAt(Point2D.Double p, double tolerance) 
	{
		
		double h1 = coords.x/coords.h-p.x;
		double h2 = coords.y/coords.h-p.y;
		if(h1*h1+h2*h2<=tolerance) return true;
		
		return false;
	}

	/** 
	 * @see geofuchs.model.Geometric
	 */
	public String getObjectType() 
	{
		return new String(GeoStrings.POINT);
	}

	/**
	 * Sets the algorithm. Takes care of effects that occur when the old algorithm
	 * is an intersection algorithm.
	 */
	public void setAlgorithm(GeoAlgorithm algorithm)
	{
		if(this.algorithm instanceof IntersectionAlgorithm) ((IntersectionAlgorithm)this.algorithm).removeObjectFromAlgorithm(this.name);
		this.algorithm=algorithm;
	}

	/**
	 * @see geofuchs.model.Geometric
	 */
	public String getDescription()
	{
		if(algorithm!=null)
		{
			if(algorithm instanceof IntersectionAlgorithm) ((IntersectionAlgorithm)algorithm).selectIntersectionPoint(new Point2D.Double(coords.x/coords.h, coords.y/coords.h)); 
			return algorithm.getDescription();
		}
		else return new String();
	}


	/**
	 * @see geofuchs.model.Drawable
	 */
	public void setNamePosition(Point2D.Double p)
	{
		return;
	}

	/**
	 * @see geofuchs.model.DrawableGeoObject
	 */
	public String getGeoXMLType()
	{
		return GeoXMLStrings.POINT;
	}
	


}
