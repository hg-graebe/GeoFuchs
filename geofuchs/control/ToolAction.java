/* $Id: ToolAction.java,v 1.1 2005/08/21 20:29:56 hg-graebe Exp $ */

package geofuchs.control;
import geofuchs.control.GeoTool;
import geofuchs.view.GMainFrame;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
/** 
 * This is a class that collects all available GeoTools. A hook for
 * future dynamic loading. Does not yet work since actions cannot
 * consume local variables: non-static variable this cannot be
 * referenced from a static context
 */

public class ToolAction extends AbstractAction {
    private GeoTool tool;
    private GMainFrame mainFrame; 
    
    public ToolAction(GeoTool tool, GMainFrame mainFrame) {
	this.tool=tool;
	this.mainFrame=mainFrame;
    }
    
    public void actionPerformed(ActionEvent e) {
	mainFrame.setCurrentTool(tool);
    }
}

