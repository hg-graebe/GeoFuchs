/*
 * Created on 05.04.2004
 *
 */
package geofuchs.control;
import geofuchs.model.*;

/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class TwoPointCircleTool implements GeoTool
{

	/**
	 * The constructor method, which e.g. sets the expected object types.
	 * @param c The construction the tool should be applied to.
	 */
	public TwoPointCircleTool(GeoConstruction c)
	{
		if(c==null) return;
		c.toolChanged(GeoStrings.TWOPOINTCIRCLE);
		GeoConstruction.expectObject(GeoStrings.POINT,2);
		GeoConstruction.setExpectedObjectNr(2);
	}
	
	/**
	 * Returns an identifying string.
	 * @return GeoStrings.CIRCLE
	 */
	public String getType() {
		return GeoStrings.CIRCLE;

	}


}
