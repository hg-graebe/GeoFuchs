/* $Id: PointTool.java,v 1.2 2005/08/21 20:29:56 hg-graebe Exp $ */
package geofuchs.control;
import geofuchs.model.GeoConstruction;

/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */

public class PointTool extends GeoTool {

    /**
     * The setTool method, which e.g. sets the expected object types.
     * @param c The construction the tool should be applied to.
     */
    public void setTool(GeoConstruction c) {
	if(c==null) return;
	c.toolChanged(GeoStrings.FREEPOINT);
	GeoConstruction.expectObject(GeoStrings.LINE,1);
	GeoConstruction.expectObject(GeoStrings.CIRCLE,1);
	GeoConstruction.setExpectedObjectNr(1);
    }

    public String getType() { return GeoStrings.POINT; }
    public String getName() { return langStrings.mi_insertPoint;} 

}
