/*
 * Created on 01.05.2004
 */
package geofuchs.model;
import geofuchs.mathtools.*;

/**
 * This interface must be implemented by all geometric objects that support intersections.
 * It has to be extended if new types of intersectable geometric objects are added.
 *  @author Andy Stock
 *
 */
public interface Intersectable extends Drawable, Geometric {

	
	/**
	 * Calculates an intersection set.
	 * @param intObject The object whose intersections with the geometric object shall be computed.
	 * @return The intersection points' coordinates.
	*/
	public Point3D[] getIntersectionSet(Intersectable intObject);

}
