/*
 * Created on 11.07.2004

 */
package geoxml;

/**
 * This interface must be implemented by any geometric object which 
 * shall be stored in a GeoXML file.
 * @author Andy Stock
 */
public interface GeoXMLStorable 
{
	/**
	 * Creates and returns a GeoObjectDescription, containing information on the storable geometric object.
	 * @return The GeoObjectDescription.
	 */
	public GeoObjectDescription getGeoObjectDescription();


	
}
