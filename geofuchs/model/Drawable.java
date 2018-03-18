/*
 * Created on 26.03.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package geofuchs.model;

import java.awt.*;
import java.awt.geom.*;

/**
 * This interface defines the minimum set of methods for
 * objects which can be displayed graphically on a drawing
 * plane.
 * @author Andy Stock
 */
public interface Drawable {

	/**
	 * Draws the object.
	 * @param g
	 * @param bShowInfo Shall the object's name be shown?
	 * @param upperLeft The upper left corner of the visible coordinate area (in world coordinates).
	 * @param lowerRight The lower right corner of the visible coordinate area (in world coordinates).
	 */
	public void draw(Graphics2D g, Point2D.Double upperLeft, Point2D.Double lowerRight);  

	/**
	 * Allows setting the point's drawable-property. For instance, a floater on a
	 * line that is not drawable also wouldn't be. 
	 * @param isDrawable True, if the object is drawable.
	 */
	public void setDrawable(boolean isDrawable);
	
	/**
	 * @return The object's type, according to the GeoStrings class - e.g. "Point" for floaters, intersection points etc. 
	 */
	public String getType();
	
	/**
	 * Sets or unsets the object's highlight property.
	 * @param isHighlighted True, if the object should be emphasized graphically.
	 */
	public void setHighlight(boolean isHighlighted);
	
	/**
	 * 
	 * @return The object's drawable property. False means that the object
	 * can't be displayed graphically for some reason.
	 */
	public boolean isDrawable();

	/**
	 * This method sets the color used for drawing the object.
	 * @param color The color.
	 */
	public void setColor(java.awt.Color color);

	/**
	 * Sets the showName-property.
	 * @param showName True, if the name should be displayed, false otherwise.
	 */
	public void setShowName(boolean showName);

	/**
	 * Sets the position of the name of the object, in case it should be displayed.
	 * @param p The position.
	 */
	public void setNamePosition(Point2D.Double p);

	/**
	 * Influences how the object is displayed.
	 * @param emph 0: hidden, 1: normal, larger: emphasized
	 */
	public void setEmph(int emph);
	
	/**
	 * @return 0 for a hidden object, 1 for a normal-sized object, 
	 * 2 for an emphasized object, and 3 for a more emphasized object
	 */	
	public int getEmph();
}
