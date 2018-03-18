/* $Id: OrthoTool.java,v 1.2 2005/08/21 20:29:56 hg-graebe Exp $ */
package geofuchs.control;
import geofuchs.model.GeoConstruction;
/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class OrthoTool extends GeoTool {

    /**
     * The constructor method, which e.g. sets the expected object types.
     * @param c The construction the tool should be applied to.
     */
    public void setTool(GeoConstruction c) {
	if(c==null) return;
	c.toolChanged(GeoStrings.ORTHOLINE);
	GeoConstruction.expectObject(GeoStrings.POINT,1);
	GeoConstruction.expectObject(GeoStrings.LINE,1);
	GeoConstruction.setExpectedObjectNr(2);
    }

    /**
     * Returns an identifying string.
     * @return GeoStrings.ORTHOLINE
     */
    public String getType() { return GeoStrings.ORTHOLINE; }
    public String getName() { return langStrings.mi_senk;} 


}
