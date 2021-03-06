/*
 * Created on 07.09.2004
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
public class ColorTool implements GeoTool 
{
	
	/**
	 * The constructor method, which e.g. sets the expected object types.
	 * @param c The construction the tool should be applied to.
	 */
	public ColorTool(GeoConstruction c)
	{
		if(c==null) return;
		c.toolChanged(GeoStrings.SHOWNAME);
		GeoConstruction.expectObject(GeoStrings.POINT,1);
		GeoConstruction.expectObject(GeoStrings.LINE,1);		
		GeoConstruction.expectObject(GeoStrings.CIRCLE,1);		
		GeoConstruction.setExpectedObjectNr(1);
	}
	
	/**
	 * Returns an identifying string.
	 * @return GeoStrings.SETCOLOR
	 */
	public String getType() {
		return GeoStrings.SETCOLOR;
	}
}
