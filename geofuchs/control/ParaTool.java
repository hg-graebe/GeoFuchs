/*
 * Created on 14.04.2004
 */
package geofuchs.control;
import geofuchs.model.GeoConstruction;

/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class ParaTool implements GeoTool {

	/**
	 * The constructor method, which e.g. sets the expected object types.
	 * @param c The construction the tool should be applied to.
	 */
		public ParaTool(GeoConstruction c)
		{
			if(c==null) return;
			c.toolChanged(GeoStrings.PARALINE);
			GeoConstruction.expectObject(GeoStrings.POINT,1);
			GeoConstruction.expectObject(GeoStrings.LINE,1);
			GeoConstruction.setExpectedObjectNr(2);
		}
	
	/**
	 * Returns an identifying string.
	 * @return GeoStrings.PARALINE
	 */
		public String getType() {
			return GeoStrings.PARALINE;

		}


}
