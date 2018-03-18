/*
 * Created on 27.03.2003
 */
package geofuchs.control;

/**
 *  This class contain information on an expected object type and the number of 
 * 	expected objects of this type. 
 *  @author Andy Stock
 *
 */
public class ObjectExpectation {

	private String type;		//Type of the expected object
	private int cardinality;	//Number of expected objects of this type 
	
	/**
	 * 
	 * @param type The type of the expected objects.
	 * @param card How many objects of this type are expected (maximum)?
	 */
	public ObjectExpectation(String type, int card)
	{
		this.type=type;
		this.cardinality = card;	
	}

	/**
	 * @return The type of the expected object - e.g. GeoStrings.POINT.
	 */
	public String getType()
	{return type;}

	/**
	 * This method is called if an expected object the apprpriate type has been selected.
	 * It simply decreases the number of expected objects of this type.
	 * @return True, if no more objects of the appropriate type are expected.
	 */
	public boolean selected()
	{
		cardinality--;
		return cardinality==0;
	}

	/**
	 * 
	 * @return The number of expected objects of the appropriate type.
	 */
	public int getCardinality()
	{
		return cardinality;
	}

	/**
	 * Overwrites the Object.clone() method.
	 * @return An Object that can be casted into an ObjectExpectation.
	 */
	public Object clone()
	{
		return new ObjectExpectation(type, cardinality);
	}

}


