/*
 * Created on 01.05.2004
 */
package geofuchs.control;
import geofuchs.model.GeoConstruction;

/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class IntersectionTool implements GeoTool {

	/**
	 * The constructor method, which e.g. sets the expected object types.
	 * @param c The construction the tool should be applied to.
	 */
		public IntersectionTool(GeoConstruction c)
		{
			if(c==null) return;
			c.toolChanged(GeoStrings.INTERSECTION);
			GeoConstruction.expectObject(GeoStrings.LINE,2);
			GeoConstruction.expectObject(GeoStrings.CIRCLE,2);
			GeoConstruction.setExpectedObjectNr(2);
		}
	
	/**
	 * Returns an identifying string.
	 * @return GeoStrings.INTERSECTION
	 */
		public String getType() 
		{
			return GeoStrings.INTERSECTION;
		}


}
