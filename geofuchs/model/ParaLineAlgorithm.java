/*
 * Created on 14.04.2004
 */
package geofuchs.model;
import geofuchs.control.*;
import geoxml.*;
import geofuchs.mathtools.*;

/**
 * This is the algorithm calss for parallels.
 * @author Andy Stock
 */
public class ParaLineAlgorithm implements GeoAlgorithm {

	//The directly defining point and line.
	  protected DrawableGeoPoint p;
	  protected DrawableGeoLine line;
	 //The parallel.
	  protected DrawableGeoLine paraLine;  

	/** 
	* @param p Point on the parallel line.
	* @param l The line it is parallel to.
	*/
	  public ParaLineAlgorithm(DrawableGeoPoint p, DrawableGeoLine l)
	  {
		  this.p=p;
		  line=l;
	  }
	  
		/** 
		* @param p Point on the parallel line.
		* @param l The line it is parallel to.
	 	*/
		public ParaLineAlgorithm(DrawableGeoLine l, DrawableGeoPoint p)
		{
			this.p=p;
			line=l;
		}

	  /**
	   * Creates a new DrawableGeoLine object with this algorithm.
	   * @param name The line's name.
	   * @return
	   */
	  public Geometric createObject(String name)
	  {
		  DrawableGeoLine paraLine = new DrawableGeoLine(this, name);
		  this.paraLine=paraLine;
		  paraLine.setColor(GDefaults.parLineColor);
		  calculate();
		  return paraLine;
	  }

	  /**
	   * 
	   * @return The type - in this case "ParaLine".
	   */
	  public String getType()
	  {
		  return GeoStrings.PARALINE;
	  }
	
	  /**
	   * @see geofuchs.model.GeoAlgorithm
	   */
	  public void calculate() {
		
		//Compute the parallel's parameters and set them. 
		Point3D pl = Calculator.getParaLine(new Point3D(line.a, line.b, line.c),p.coords); 	
		  	
		//if either the directly defining line or point aren't drawable,
		//the defined line is neither.
		if((!p.isDrawable()) || (!line.isDrawable())) 
			{paraLine.setDrawable(false);return;}
		else paraLine.setDrawable(true);
		if(paraLine.isDrawable()) paraLine.setParams(pl);
	  }
  	/**
  	 * See interface Geometric.
  	 */
	  public String getDescription()
	  {
		  return new String(paraLine.getName()+": "+langStrings.s_paraline+" "+langStrings.s_to+" "+line.getName()+" "+langStrings.s_through+" "+p.getName());
	  }

	/**
	* ParaLine algorithms are considered to be equal if their directly defining objects are.
	*/
	public boolean equals(GeoAlgorithm c) {
		if(!(c instanceof ParaLineAlgorithm))  return false;
		ParaLineAlgorithm pc = (ParaLineAlgorithm) c;
		if(pc.p.getName().equals(p.getName())&&pc.line.name.equals(line.name)) return true;
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
			new ObjectExpectation(GeoStrings.POINT,1)		
		};
	
		return new AlgorithmInfo(GeoStrings.PARALINE, GeoStrings.LINE, 2, obEx);

	}

	/**
	*Sets the depending object. If the object's type doesn't fit
	*to the algorithm type,  nothing happens.
	*@param geoObject The depending object.
	*/
	public void setDependingObject(Geometric geoObject) 
	{
		if(geoObject instanceof DrawableGeoLine)
			paraLine=(DrawableGeoLine) geoObject;		
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
	public String getGeoXMLType() 
	{
		return GeoXMLStrings.PARALLEL;
	}

	/**
	 *@see geofuchs.model.GeoAlgorithm 
	 */
	public double[] getFreeCoordinates() 
	{
		return new double[0];
	}

}
