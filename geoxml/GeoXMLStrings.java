/*
 * Created on 11.07.2004
 */
package geoxml;

/**
 *  *
 * This class contains strings for the object and algorithm types.
 * They should (but don't have to) be used whereever possible when
 * dealing with the GeoXML package. 
 * @author Andy Stock
 */
public class GeoXMLStrings 
{
	//public strings
	
	//object type strings
	public static final String POINT=new String("point");
	public static final String LINE=new String("line");
	public static final String CIRCLE=new String("circle");
	
	//algorithm type strings
	public static final String TWOPOINTLINE = new String("twopointline");
	public static final String TWOPOINTCIRCLE = new String("twopointcircle");	
	public static final String FREE = new String("free");
	public static final String MIDPOINT = new String("midpoint");
	public static final String PARALLEL = new String("parallel");
	public static final String PLUMB = new String("orthogonal");
	public static final String INTERSECTION = new String("intersection");
	public static final String FLOATER = new String("floater");

	//non-public strings
	
	//text
	static final String VERSION="1.0";
	static final String FORMAT="geoxml";
	static final String IDPREFIX="gxid";
	static final String VARIDPREFIX="var";
	
	//tag names
	static final String TCONSTRUCTION="construction";
	static final String TOBJECTS="objects";
	static final String TOBJECT="object";
	static final String TPARAM="param";
	static final String TVARIABLES="variables";
	static final String TVARIABLE="variable";
	static final String TDO="display-options";
	static final String TOPTION="option";
	static final String TCOLOR="color";
	static final String TEMPH="thickness";
		
	static final String ANAME="name";
	static final String ATYPE="type";
	static final String AALGORITHM="function";
	static final String AVERSION="version";
	static final String AFORMAT="format";
	static final String AID="id";
	static final String AREF="ref";
	static final String ASHOWNAME="showname";
	
}
