/*
 * Created on 08.09.2004
 */
package geofuchs.control;
import geofuchs.model.*;
/**
 *  This class represents a tool. An instance is created every time
 *  the corresponding tool is selected by the user.
 *  @author Andy Stock
 *
 */
public class EmphTool implements GeoTool 
{
	String emphFactorString;

	/**
	 * The constructor method, which e.g. sets the expected object types.
	 * @param c The active construction.
	 * @param factor The emphasis factor: 0-hidden, 1-normal,
	 * 2-emphasized, 3-even more emphasized
	 */
	public EmphTool(GeoConstruction c, int factor)
	{
		if(c==null) return;
		if(factor==0) emphFactorString=GeoStrings.EMPH0;
		else if(factor==1) emphFactorString=GeoStrings.EMPH1;
		else if(factor==2) emphFactorString=GeoStrings.EMPH2;
		else emphFactorString=GeoStrings.EMPH3;
		
		c.toolChanged(emphFactorString);
		GeoConstruction.expectObject(GeoStrings.POINT,1);
		GeoConstruction.expectObject(GeoStrings.LINE,1);		
		GeoConstruction.expectObject(GeoStrings.CIRCLE,1);		
		GeoConstruction.setExpectedObjectNr(1);
	}
	
	/**
	 * Returns an identifying string.
	 * @return GeoStrings.EMPH0, GeoStrings.EMPH1, GeoStrings.EMPH2,
	 * 			or GeoStrings.EMPH3 according to the tool's emphasis factor.
	 */
		public String getType() 
		{
			return emphFactorString;
		}
}
