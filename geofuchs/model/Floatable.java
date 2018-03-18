/*
 * Created on 04.04.2004
 */
package geofuchs.model;
import geofuchs.mathtools.*;
/**
 * This interface must be implemented by objects which want to enable
 * floaters to float on them.
 * @author Andy Stock
 */
public interface Floatable extends Drawable, Geometric {
	
	/**
	 * This method projects the given point onto the object.
	 * @param p Non-projected position of the floater.
	 * @return The projected position of the floater.
	 */
	public Point3D projectFloater(Point3D p);

}
