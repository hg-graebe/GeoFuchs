/*
 * Created on 13.06.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package geofuchs.model;
import geofuchs.control.ObjectExpectation;

/**
 * Instances of this class encapsulate information on algorithms.
 *  @author Andy Stock
 *
 */
public class AlgorithmInfo {

	private String name;   //the algorithm's name, for instance "Two-Point-Line" as specified in the GeoStrings class
	private int reqObjNr;  //The number of defining objects.
	private String allowedDependentObjectType; //The type of allowed dependent objects - eg GeoStrings.LINE for orthogonals, parallels, amd two-point-lines
	private ObjectExpectation[] expectedObjects; //Information on the defining objects.

	/**
	 * 
	 * @param name The algorithm's name, e.g. "Two-Point-Line" as specified in the GeoStrings class
	 * @param allowedDependentObjectType The type of allowed dependent objects - eg GeoStrings.LINE for orthogonals, parallels, amd two-point-lines
	 * @param reqObjNr The number of defining objects.
	 * @param expectedObjects Information on the defining objects.
	 */
	public AlgorithmInfo(String name, String allowedDependentObjectType, int reqObjNr, ObjectExpectation[] expectedObjects)
	{
		this.name=name;
		this.reqObjNr=reqObjNr;
		this.allowedDependentObjectType=allowedDependentObjectType;
		this.expectedObjects=expectedObjects;
		
	}

	/**
	 * 
	 * @return The algorithm's name, eg "Two-Point-Line" as specified in the GeoStrings class
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @return The number of defining objects.
	 */
	public int getReqObjNr()
	{
		return reqObjNr;
	}
	
	/**
	 * 
	 * @return The type of dependent objects - for instance GeoStrings.LINE for orthogonals, parallels, and two-point-lines
	 */
	public String getDependentObjectType()
	{
		return allowedDependentObjectType;
	}
	
	/**
	 * 
	 * @return Information on the defining objects.
	 * @see ObjectExpectation
	 */
	public ObjectExpectation[] getExpectedObjects()
	{
		return expectedObjects;
	}

}
