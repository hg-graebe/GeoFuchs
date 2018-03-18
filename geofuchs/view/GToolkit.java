/* $Id: GToolkit.java,v 1.1 2005/08/21 20:29:57 hg-graebe Exp $ */

package geofuchs.view;
import geofuchs.control.GeoTool;
import geofuchs.control.ToolAction;
import geofuchs.control.langStrings;
import javax.swing.*;
import java.awt.event.*;

/** 
 * This is a class that collects all available GeoTools. A hook for
 * future dynamic loading. 
 */

public abstract class GToolkit {

    private static final String[] tools = {
	"PointTool", "MidPointTool", "TwoPointLineTool", "OrthoTool",
	//"ParaTool", "TwoPointCircleTool"
	};

    static public JMenu createMenu(GMainFrame m){
	JMenu menu=new JMenu(langStrings.mi_tools); 
	JMenuItem menuItem;
	Class toolClass;
	GeoTool o;
	for(int i=0;i<tools.length;i++) {
	    try {	
		toolClass=Class.forName("geofuchs.control."+tools[i]);
		o=(GeoTool) toolClass.newInstance();
		menuItem=new JMenuItem(o.getName(), o.getUnselectedIcon());
		menuItem.addActionListener(new ToolAction(o,m));
		menu.add(menuItem);
	    } catch(Exception e) { e.printStackTrace(); } 
	}
	return menu;
    }
}

