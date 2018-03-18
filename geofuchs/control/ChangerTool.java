/*
 * Created on 12.06.2004
 *
 */
package geofuchs.control;
import geofuchs.model.GeoConstruction;

/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class ChangerTool implements GeoTool 
{

	/**
	 * The constructor method, which e.g. sets the expected object types.
	 * @param c The construction the tool should be applied to.
	 */
	public ChangerTool(GeoConstruction c)
	{
		if(c==null) return;
		c.toolChanged(GeoStrings.CHANGER);
		GeoConstruction.expectObject(GeoStrings.POINT,1);
		GeoConstruction.expectObject(GeoStrings.LINE,1);		
		GeoConstruction.expectObject(GeoStrings.CIRCLE,1);		
		GeoConstruction.setExpectedObjectNr(1);
	}
	
		/**
		 * Returns an identifying string.
		 * @return GeoStrings.CHANGER
		 */
		public String getType() {
			return GeoStrings.CHANGER;
		}
}
