/* $Id: MidPointTool.java,v 1.2 2005/08/21 20:29:56 hg-graebe Exp $ */
package geofuchs.control;
import geofuchs.model.GeoConstruction; 

/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class MidPointTool extends GeoTool {

    /**
     * The constructor method, which e.g. sets the expected object types.
     * @param c The construction the tool should be applied to.
     */
    public void setTool(GeoConstruction c)  {
	if(c==null) return;
	c.toolChanged(GeoStrings.MIDPOINT);
	GeoConstruction.expectObject(GeoStrings.POINT,2);
	GeoConstruction.setExpectedObjectNr(2);
    }

    /**
     * Returns an identifying string.
     * @return GeoStrings.MIDPOINT
     */
    public String getType() { return GeoStrings.MIDPOINT; }
    public String getName() { return langStrings.mi_calcMidpoint;} 

}
