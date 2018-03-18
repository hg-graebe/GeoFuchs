/*
 * Created on 06.04.2004
 */
package geofuchs.model;
import java.awt.geom.*;
import geofuchs.control.*;
import geoxml.*;
import geofuchs.mathtools.*;

/**
 * This is the algorithm class for circles which are defined
 * by their center and a point on their periphery.
 * @author Andy Stock
 */
public class TwoPointCircleAlgorithm implements GeoAlgorithm {

	
	DrawableGeoPoint center;	//the center
	DrawableGeoPoint p;			//the point on the circle's periphery.
	DrawableGeoCircle circle;   //the circle

	/**
	 * @param center The center.
	 * @param p A point on the circle's periphery.
	 */
	public TwoPointCircleAlgorithm(DrawableGeoPoint center, DrawableGeoPoint p)
	{
		this.center=center;
		this.p=p;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public void calculate() 
	{
		
		if(!center.isDrawable() || !p.isDrawable() || p.coords.h==0) circle.setDrawable(false)  ;
		else circle.setDrawable(true);	
		if(circle.isDrawable())
		{
			//set the center's and the periphery point's coordinates.
			Point3D c = center.coords;
			Point3D p = this.p.coords;
			circle.setPosition(c,p);
		}
	}
	
	/** 
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getDescription() 
	{	
		return new String(circle.getName()+": "+langStrings.s_circle+" "+langStrings.s_through+" "+p.getName()+" "+langStrings.s_with+" "+langStrings.s_center+" "+center.getName());
	}

	/** 
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public Geometric createObject(String name) 
	{
		Point2D.Double c = new Point2D.Double(center.coords.x/center.coords.h, center.coords.y/center.coords.h);
		DrawableGeoCircle circle = new DrawableGeoCircle(this,name);
		circle.setColor(GDefaults.circColor);
		this.circle=circle;
		calculate();
		return circle;
	}


	/**
	* TwoPointCircle algorithms are considered to be equal if their defining objects are.
	*/
	public boolean equals(GeoAlgorithm c) {
		if(!(c instanceof TwoPointCircleAlgorithm))  return false;
		TwoPointCircleAlgorithm cc = (TwoPointCircleAlgorithm) c;
		if(cc.p.getName().equals(p.getName())&&cc.center.getName().equals(center.getName())) return true;
		return false;
	}

	/**
	 * 
	 * @return An object that encapsulates information on the algorithm.
	 * @see geofuchs.model.AlgorithmInfo
	 */
	public static AlgorithmInfo getInfo() 
	{
		ObjectExpectation[] obEx = new ObjectExpectation[]
		{
			new ObjectExpectation(GeoStrings.POINT,2)		
		};
		return new AlgorithmInfo(GeoStrings.TWOPOINTCIRCLE, GeoStrings.CIRCLE, 2, obEx);
	}

	/**
	*Sets the directly depending object. If the object's type doesn't fit
	*to the algorithm type,  nothing happens.
	*@param geoObject The directly depending object.
	*/
	public void setDependingObject(Geometric geoObject) 
	{
		if(geoObject instanceof DrawableGeoCircle)
			circle=(DrawableGeoCircle) geoObject;	
	}
	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String[] getDefiningObjectNames() {
		String[] objects = new String[2];
		objects[0]=center.getName();
		objects[1]=p.getName();
		return objects;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getGeoXMLType() 
	{
		return GeoXMLStrings.TWOPOINTCIRCLE;
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
		return GeoStrings.TWOPOINTCIRCLE;
	}
}
