/*
 * Created on 14.06.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package geofuchs.model;
import geofuchs.control.*;
import geoxml.*;

/**
 * This is the algorithm class for free points. 
 * @author Andy Stock
 */
public class FreePointAlgorithm implements GeoAlgorithm {

	double x,y;  //coordinates, only used for initialising the point 
	DrawableGeoPoint point; //the free point

	public FreePointAlgorithm(double x, double y)
	{
		this.x=x;
		this.y=y;
	}


	/**
	 * Free points are free. Hence, the claculate-method has nothing to do. 
	 * @see geofuchs.model.GeoAlgorithm#calculate()
	 */
	public void calculate() 
	{
		return;	
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getDescription() 
	{	
		return new String(point.getName()+": "+GeoStrings.FREEPOINT);
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public Geometric createObject(String name) {
		point=new DrawableGeoPoint(x,y,name);
		point.setColor(GDefaults.pointColor);
		point.setAlgorithm(this);
		return point;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public boolean equals(GeoAlgorithm c) {
		//always false
		return false;
	}

	/**
	 * 
	 * @return An object that encapsulates information on the algorithm,
	 * @see geofuchs.model.AlgorithmInfo
	 */
	public static AlgorithmInfo getInfo() {
	
		return new AlgorithmInfo(GeoStrings.FREEPOINT, GeoStrings.POINT, 0, null);

	}

	/**
 	*Sets the algoruthm's depending object. If the object's type doesn't fit,  
 	* nothing is happening at all.
 	*@param geoObject The depending object.
 	*/
	public void setDependingObject(Geometric geoObject) 
	{	
		if(geoObject instanceof DrawableGeoPoint)
			point=(DrawableGeoPoint) geoObject;	
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String[] getDefiningObjectNames() {
		String[] objects = new String[0];
		return objects;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getGeoXMLType() {
		return GeoXMLStrings.FREE;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm#getFreeCoordinates()
	 */
	public double[] getFreeCoordinates() {
		double[] coordinates = new double[3];
		coordinates[0]=point.coords.x;
		coordinates[1]=point.coords.y;
		coordinates[2]=point.coords.h;
		return coordinates;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm#getType()
	 */
	public String getType()
	{
		return GeoStrings.FREEPOINT;
	}

}
