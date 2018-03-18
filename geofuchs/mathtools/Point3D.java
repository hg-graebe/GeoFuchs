/*
 * Created on 18.08.2004
 *
 */
package geofuchs.mathtools;
import java.awt.geom.*;

/**
 * * This class represents a point
 *   in the real projective plane.
 *  @author Andy Stock
 *
 */
public class Point3D 
{
	public double x;	//the coordinates...
	public double y;
	public double h;
	
	/**
	 * The constructor method.
	 * @param x The x component of the real projective point (x,y,h).
	 * @param y The y component of the real projective point (x,y,h).
	 * @param h The h component of the real projective point (x,y,h).
	 */
	public Point3D(double x,double y, double h)
	{
		this.x=x;
		this.y=y;
		this.h=h;
	}
	
	/**
	 * Sets the coordinates.
	 * @param x The x component of the real projective point (x,y,h).
	 * @param y The y component of the real projective point (x,y,h).
	 * @param h The h component of the real projective point (x,y,h).
	 */
	public void set(double x,double y, double h)
	{
		this.x=x;
		this.y=y;
		this.h=h;
	}
	
	/**
	 * Maps the real projective point (x,y,h) to the
	 * corresponding two-dimensional, euclidian point (x/h,y/h). 
	 * @return The corresponding euclidean point. Note that it doesn't 
	 * have to be exist, and thus, (Double.NaN,Double.NaN) may be returned.
	 */
	public Point2D.Double getPoint2D()
	{
		return new Point2D.Double(x/h,y/h);
	}
	
	/**
	 * @return The euclidean x component x/h.	 
	 */
	public double getX()
	{
		return x/h;
	}
	
	/**
	 * @return The euclidean y component y/h.	 
	 */	
	public double getY()
	{
		return y/h;
	}
	
	/**
	 * @return A String "(x,y,h)".
	 */
	public String toString()
	{
		return new String("("+x+","+y+","+h+")");
	}
	
}
