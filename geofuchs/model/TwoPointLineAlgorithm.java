/*
 * Created on 28.03.2004
 */
package geofuchs.model;

import geofuchs.control.*;
import geofuchs.mathtools.*;
import geoxml.*;

/**
 * This is the algorithm class for lines which are defined by two points.
 * @author Andy Stock
 */
public class TwoPointLineAlgorithm implements GeoAlgorithm {

	//The points which define the line.
	protected DrawableGeoPoint p1;
	protected DrawableGeoPoint p2;
	protected DrawableGeoLine line;

	/**
	 * 
	 * @param p1 The first directly defining point.
	 * @param p2 The second directly defining point.
	 */
	public TwoPointLineAlgorithm(DrawableGeoPoint p1, DrawableGeoPoint p2)
	{
		this.p1=p1;
		this.p2=p2;
	}

	/**
	 * Creates a new DrawableGeoLine object with this algorithm.
	 * @param name The line's name.
	 * @return A line with this algorithm.
	 */
	public Geometric createObject(String name)
	{
		
		DrawableGeoLine line = new DrawableGeoLine(this, name);
		line.setColor(GDefaults.lineColor);
		this.line=line;
		calculate();
		return line;
	}

	/**
	 * 
	 * @return The type - in this case GeoStrings.TWOPOINTLINE
	 */
	public String getType()
	{
		return new String(GeoStrings.TWOPOINTLINE);
	}
	
	
	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public void calculate() 
	{
		
		Point3D dp1=p1.coords;
		Point3D dp2=p2.coords;

		//if one of the directly defining points is not drawable, the line's neither 
		//(the same goes if the points are at the same position)
		if((!p1.isDrawable()) || (!p2.isDrawable())) 
			{line.setDrawable(false);return;}
		else if(dp1.x/dp2.h==dp2.x/dp2.h&&dp1.y/dp1.h==dp2.y/dp2.h) 
		{
			line.setDrawable(false);
			return;
		} 
		else line.setDrawable(true);
		if(line.isDrawable()) line.setPoints(dp1, dp2);
	}
	/**
 	* @see geofuchs.model.GeoAlgorithm
 	*/
	public String getDescription()
	{
		return new String(line.getName()+": "+langStrings.s_line+" "+langStrings.s_through+" "+p1.getName()+" "+langStrings.s_and+" "+p2.getName());
	}

	/**
	* TwoPointLine algorithms are considered to be equal if their directly defining points are.
	*/
	public boolean equals(GeoAlgorithm c) 
	{
		if(!(c instanceof TwoPointLineAlgorithm))  return false;
		TwoPointLineAlgorithm lc = (TwoPointLineAlgorithm) c;
		if(lc.p1.getName().equals(p1.getName())&&lc.p2.getName().equals(p2.getName())) return true;
		else if(lc.p1.getName().equals(p2.getName())&&lc.p2.getName().equals(p1.getName())) return true;
		return false;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public static AlgorithmInfo getInfo() 
	{
		
		ObjectExpectation[] obEx = new ObjectExpectation[]
		{
			new ObjectExpectation(GeoStrings.POINT,2)		
		};
	
		return new AlgorithmInfo(GeoStrings.TWOPOINTLINE, GeoStrings.LINE, 2, obEx);

	}

	/**
	*Sets the directly depending object. If the object's type doesn't fit
	*to the algorithm type,  nothing happens.
	*@param geoObject The directly depending object.
	*/
	public void setDependingObject(Geometric geoObject) 
	{
		if(geoObject instanceof DrawableGeoLine)
				line=(DrawableGeoLine) geoObject;	
	}
	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String[] getDefiningObjectNames() 
	{
		String[] objects = new String[2];
		objects[0]=p1.getName();
		objects[1]=p2.getName();
		return objects;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getGeoXMLType() 
	{
		return GeoXMLStrings.TWOPOINTLINE;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public double[] getFreeCoordinates() 
	{
		return new double[0];
	}

}
