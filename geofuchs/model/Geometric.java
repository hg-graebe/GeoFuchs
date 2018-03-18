/*
 * Created on 26.03.2003
 */
package geofuchs.model;
import java.awt.geom.*;

/**
 * This interface must be implemented by geometric objects. 
 * @author Andy Stock
 */
public interface Geometric 
{
	/**
	 * @param x X-component of a position (in world coordinates).
	 * @param y Y-component of a position (in world coordinates).
	 * @param tolerance The maximum distance at which two points are considered to be equal.
	 * @return 	 True, if the geometric object is within the tolerance area of the given
	 * position.
	 */
	public boolean isAt(Point2D.Double p, double tolerance);
	/**
	 * 
	 * @return The geometric object's name.
	 */
	public String getName();
	/**
	 * 
	 * @return The geometric object's type.
	 */
	public String getType();
	/**
	 * Calculates the geometric object's coordinates and sets its state accordingly.
	 */
	public void calculate();
	
	/**
	 * 
	 * @return A description of the geometric object, e.g. "Line through point A and point B"
	 */
	public String getDescription();
	
	/**
	 * 
	 * @param A geometric object.
	 * @return True, if the general geometric objects are equal (if there algorithms are equal, that is).
	 */
	public boolean equals(Geometric g);
	
	/** 
	 * @return The geometric object's algorithm.
	 */
	public GeoAlgorithm getAlgorithm();

	/**
	 * @param algorithm The geometric object's algorithm.
	 * 
	 */
	public void setAlgorithm(GeoAlgorithm algorithm);

	/**
	 * Used for determining if an algorithm type is suitable for an object type.
	 * @return The geometric object's type, for instance GeoStrings.LINE for line objects.
	 * @see geofuchs.control.GeoStrings
	 */
	public String getObjectType();
	
	/**
	 * @param name The geometric object's name.
	 */
	public void setName(String name);
	
	/**
	 * @return The geometric object's defining objects.
	 */
	public String[] getDefiningObjectNames();
}
