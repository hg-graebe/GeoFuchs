/*
 * Created on 04.04.2004

 */
package geofuchs.model;
import java.awt.geom.*;
import geofuchs.control.*;
import geoxml.*;
import geofuchs.mathtools.*;

/**
 * This is the algorithm class for floaters.
 * @author Andy Stock
 */
public class FloaterAlgorithm implements GeoAlgorithm
{

	private DrawableGeoPoint floater; //the floater
	private Floatable object;		  //the object the floater is floating on
	private Point2D.Double pos;		  //the position of the floater

	/**
	 * The constructor method.
	 * @param pos The position of the floater.
	 * @param object The object the floater is floating on. It will
	 * be projected on this object depending on its type.
	 */
	public FloaterAlgorithm(Point2D.Double pos,Floatable object)
	{
		this.object = object;	
		this.pos=pos;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm#createObject()
	 */
	public Geometric createObject(String name)
	{
		DrawableGeoPoint p = new DrawableGeoPoint(pos.x,pos.y,this,name);
		p.setColor(GDefaults.floaterColor);
		this.floater=p;		
		p.calculate();
		return p;
	}

	/**
	 * Updates the floater by projecting it onto its defining object.
	 */
	public void calculate() {
		
		//if the object the floater floats on is not drawable, the floater's neither

		if(!object.isDrawable()) floater.setDrawable(false);
		else floater.setDrawable(true);
		
		if(!floater.isDrawable()) return;

		Point3D pos = floater.coords;
		Point3D projectedPos=object.projectFloater(pos);
		floater.move(projectedPos.x, projectedPos.y, projectedPos.h);
		
	}


	/**
	  * @see geofuchs.model.GeoAlgorithm#createObject()
	 */
	public String getDescription() {
		return new String(floater.getName()+": " +langStrings.s_floater+" "+langStrings.s_on+" "+object.getName());
	}

	/**
	 * Floater algorithms are never equal, allowing multiple floaters on the same object.
	 */
	public boolean equals(GeoAlgorithm c) {
		return false;
	}

	/**
	 * @return An object that encapsulates information on the algorithm.
	 * @see geofuchs.model.AlgorithmInfo
	 */
	public static AlgorithmInfo getInfo() {
		
		ObjectExpectation[] obEx = new ObjectExpectation[]
		{
			new ObjectExpectation(GeoStrings.LINE,1),
		 	new ObjectExpectation(GeoStrings.CIRCLE,1)		
		};
	
		return new AlgorithmInfo(GeoStrings.FLOATER, GeoStrings.POINT, 1, obEx);

	}

	/**
	 *Sets the depending object. If the object's type doesn't fit
	 *the algorithm,  nothing happens.
	 *@param geoObject The object.
	 */
	public void setDependingObject(Geometric geoObject) {
		if(geoObject instanceof DrawableGeoPoint)
			floater=(DrawableGeoPoint) geoObject;
		
	}

		/**
		 * @see geofuchs.model.GeoAlgorithm#getDefiningObjectNames()
		 */
		public String[] getDefiningObjectNames() {
			String[] objects = new String[1];
			objects[0]=object.getName();
			return objects;
		}

		/**
		 * @see geofuchs.model.GeoAlgorithm#getGeoXMLType()
		 */
		public String getGeoXMLType() {
			return GeoXMLStrings.FLOATER;
		}

		/**
		 * Note that for a floater, x,y and h are considered free here, though
		 * they aren't independent.
		 * @see geofuchs.model.GeoAlgorithm#getFreeCoordinates()
		 */
		public double[] getFreeCoordinates() 
		{
			double[] coordinates = new double[3];
			coordinates[0]=floater.coords.x;
			coordinates[1]=floater.coords.y;
			coordinates[2]=floater.coords.h;
			return coordinates;
		}


		/**
		 * @see geofuchs.model.GeoAlgorithm#getType()
		 */
		public String getType()
		{
			return GeoStrings.FLOATER;
		}
	
}
