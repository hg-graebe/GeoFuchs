/*
 * Created on 06.04.2004
 */
package geofuchs.model;
import geofuchs.control.*;
import geoxml.*;
import geofuchs.mathtools.*;

/**
 * This is the algorithm class for midpoints.
 * @author Andy Stock
 */
public class MidPointAlgorithm implements GeoAlgorithm {

	//The two directly defining points and their midpoint.
	  protected DrawableGeoPoint p1;
	  protected DrawableGeoPoint p2;
	  protected DrawableGeoPoint midpoint;

	  public MidPointAlgorithm(DrawableGeoPoint p1, DrawableGeoPoint p2)
	  {
		  this.p1=p1;
		  this.p2=p2;

	  }

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public void calculate() 
	{
		midpoint.setDrawable(true);
		if(!p1.isDrawable() || !p2.isDrawable()) midpoint.setDrawable(false);
		else  
		{	
			Point3D p1=this.p1.coords;
			Point3D p2=this.p2.coords;
			Point3D m=Calculator.getMidpoint(p1, p2);
			midpoint.move(m.x,m.y,m.h);
		}
	}

	/** 
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getDescription() {
		return new String(midpoint.getName()+": "+langStrings.s_midpoint+" "+langStrings.s_of+" "+p1.getName()+" "+langStrings.s_and+" "+p2.getName());
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public Geometric createObject(String name) {
		DrawableGeoPoint midpoint = new DrawableGeoPoint(this, name);
		midpoint.setColor(GDefaults.midPointColor);
		this.midpoint=midpoint;
		calculate();
		return midpoint;
	}

	/**
	 * Midpoint algorithms are considered to be equal if their directly defining points are equal.
	 */
	public boolean equals(GeoAlgorithm c) {
		if(!(c instanceof MidPointAlgorithm))  return false;
		MidPointAlgorithm mc = (MidPointAlgorithm) c;
		if(mc.p1.getName().equals(p1.getName())&&mc.p2.getName().equals(p2.getName())) return true;
		if(mc.p1.getName().equals(p2.getName())&&mc.p2.getName().equals(p2.getName())) return true;
		return false;
	}

	/**
	 * 
	 * @see geofuchs.model.AlgorithmInfo
	 */
	public static AlgorithmInfo getInfo() {
		
		ObjectExpectation[] obEx = new ObjectExpectation[]
		{
			new ObjectExpectation(GeoStrings.POINT,2)	
		};
		return new AlgorithmInfo(GeoStrings.MIDPOINT, GeoStrings.POINT, 2, obEx);
	}

	/**
 	*Sets the depending object. If the object's type doesn't fit
 	*to the algorithm type,  nothing happens.
 	*@param geoObject The geometric object.
 	*/
	public void setDependingObject(Geometric geoObject) 
	{
		if(geoObject instanceof DrawableGeoPoint)
			midpoint=(DrawableGeoPoint) geoObject;	
			
	}
	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String[] getDefiningObjectNames() {
		String[] objects = new String[2];
		objects[0]=p1.getName();
		objects[1]=p2.getName();
		return objects;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getGeoXMLType() {
		return GeoXMLStrings.MIDPOINT;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public double[] getFreeCoordinates() 
	{
		return new double[0];
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getType()
	{
		return GeoStrings.MIDPOINT;
	}

}
