/* $Id: GeoTool.java,v 1.2 2005/08/21 20:29:56 hg-graebe Exp $ */
package geofuchs.control;
import geofuchs.model.GeoConstruction;
import javax.swing.ImageIcon;

/**
 * * This class is the prototype for tool representations.
 *  @author Andy Stock
 */

// -- HGG: Changed to an abstract class to define default values.
// To get work overloading all subclasses are member classes, but
// with void constructor for dynamic loading

public abstract class GeoTool {

    /**
     * @return The type of the tool.
     */
    public abstract String getType();

    /**
     * @return The type of the tool.
     */
    public abstract void setTool(GeoConstruction c);

    /**
     * @return The name of the tool to be displayed in the tool
     * menu.
     */
    public abstract String getName(); 

    /**
     * @return The image of the tool to be displayed in the tool bar
     * if unselected. 
     */
    public ImageIcon getUnselectedIcon() { return null;}

     /**
     * @return The image of the tool to be displayed in the tool bar
     * if selected. 
     */
    public ImageIcon getSelectedIcon() { return null;} 

}
