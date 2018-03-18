/*
 * Created on 26.03.2003
 */
package geofuchs.model;

/**
 * This interface is must be implemented by every algorithm class.
 * @author Andy Stock
 *
 */
public interface GeoAlgorithm 
{
	/**
	 * This method calculates the current position of the algorithm's depending 
	 * geometric object.
	 */
	public void calculate();
	
	/**
	 * @return A description of the algorithm, e. g. "Parallel to Line1"
	 */
	public String getDescription();
	
	/**
	 * 
	 * @param name The geometric object's name.
	 * @return A geometric object whose coordinates are managed by the algorithm.
	 */
	public Geometric createObject(String name);

	/**
	 * 
	 * @param c An algorithm.
	 * @return True, if the algorithms are equal (for instance, if both algorithms 
	 * are of the same type, their depending objects are fixed and their 
	 * defining objects are equal).
	 */
	public boolean equals(GeoAlgorithm c);

	/**
	 * 
	 * @return The defining objects' names.
	 */
	public String[] getDefiningObjectNames();
	
	/**
	 * 
	 * @return The algorithm's GeoXML type.
	 * @see GeoXML.GeoXMLStrings
	 */
	public String getGeoXMLType();

	/**
	 * 
	 * @return The coordinates that can be set freely by the user.
	 */
	public double[] getFreeCoordinates();

	/**
	 * @return The algorithm type, for instance GeoStrings.FREEPOINT.
	 * @see geofuchs.control.GeoStrings
	 */
	public String getType();

}
