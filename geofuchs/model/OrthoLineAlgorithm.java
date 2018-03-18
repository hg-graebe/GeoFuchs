/*
 * Created on 18.04.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package geofuchs.model;
import geofuchs.control.*;
import geoxml.*;
import geofuchs.mathtools.*;


/**
 * This is the algorithm class for orthogonal lines.
 * @author Andy Stock
 */
public class OrthoLineAlgorithm implements GeoAlgorithm 
{
		//The directly defining point and line
		protected DrawableGeoPoint p;
		protected DrawableGeoLine line;
	  	//The orthogonal line
		protected DrawableGeoLine orthoLine;  

		/** 
		 * @param p Point on the new line.
		 * @param l The line it's orthogonal to.
		 */
		public OrthoLineAlgorithm(DrawableGeoPoint p, DrawableGeoLine l)
		{
			this.p=p;
			line=l;
		}
	  
		  /** 
		  * @param p Point on the new line.
		  * @param l The line it is orthogonal to.
		  */
		  public OrthoLineAlgorithm(DrawableGeoLine l, DrawableGeoPoint p)
		  {
			  this.p=p;
			  line=l;
		  }

		/**
		 * Creates a new DrawableGeoLine object with this algorithm.
		 * @param name The orthogonal line's name.
		 * @return A new orthogonal line object.
		 */
		public Geometric createObject(String name)
		{

			DrawableGeoLine orthoLine = new DrawableGeoLine(this, name);
			orthoLine.setColor(GDefaults.orthoLineColor);
			this.orthoLine=orthoLine;
			calculate();
			return orthoLine;
		}

		/**
		 * 
		 * @return The type - in this case "OrthoLine"
		 */
		public String getType()
		{
			return GeoStrings.ORTHOLINE;
		}
	
	
		/**
		 * @see geofuchs.model.GeoAlgorithm
		 */

		
		public void calculate() {
		
		//Compute the orthogonal's parameters and set them. 

		Point3D pl = Calculator.getOrthoLine(new Point3D(line.a, line.b, line.c),p.coords); 	
		//if either the directly defining line or point aren't drawable,
		//the defined line is neither.
		if((!p.isDrawable()) || (!line.isDrawable())) 
		{
			orthoLine.setDrawable(false);
			return;
		}
		else orthoLine.setDrawable(true);
		if(orthoLine.isDrawable()) orthoLine.setParams(pl);	
	}
		
	/**
	 * @see geofuchs.model.Geometric
	 */
		public String getDescription()
		{
			return new String(orthoLine.getName()+": "+langStrings.s_ortholine+" "+langStrings.s_to+" "+line.getName()+" "+langStrings.s_through+" "+p.getName());
		}

	  /**
	  * OrthoLine algorithms are considered to be equal if their directly defining objects are.
	  */
	  public boolean equals(GeoAlgorithm c) {
		  if(!(c instanceof OrthoLineAlgorithm))  return false;
		  OrthoLineAlgorithm pc = (OrthoLineAlgorithm) c;
		  if(pc.p.getName().equals(p.getName())&&pc.line.name.equals(line.name)) return true;
		  return false;
	  }

	/**
	 * @return An object that encapsulates information on the algorithm,
	 * @see geofuchs.model.AlgorithmInfo
	 */
	public static AlgorithmInfo getInfo() {
		
		ObjectExpectation[] obEx = new ObjectExpectation[]
		{
			new ObjectExpectation(GeoStrings.LINE,1),
			new ObjectExpectation(GeoStrings.POINT,1)		
		};
		return new AlgorithmInfo(GeoStrings.ORTHOLINE, GeoStrings.LINE, 2, obEx);
	}

	/**
	*Sets the depending object. If the object's type doesn't fit
	*to the algorithm type,  nothing happens.
	*@param geoObject The depending object.
	*/
	public void setDependingObject(Geometric geoObject) 
	{
		if(geoObject instanceof DrawableGeoLine)
			orthoLine=(DrawableGeoLine) geoObject;	
	}
	
	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String[] getDefiningObjectNames() {
		String[] objects = new String[2];
		objects[0]=p.getName();
		objects[1]=line.getName();
		return objects;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getGeoXMLType() {
		return GeoXMLStrings.PLUMB;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public double[] getFreeCoordinates() 
	{
		return new double[0];
	}

}
