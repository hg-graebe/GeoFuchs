/*
 * Created on 05.04.2004
 *
 */
package geofuchs.model;

import geoxml.*;

import java.awt.*;
import java.awt.geom.*;

import geofuchs.mathtools.*;

import geofuchs.control.GeoStrings;


/**
 * This class represents circles.
 * @author Andy Stock
*/
public class DrawableGeoCircle extends DrawableGeoObject implements Floatable, Intersectable {

	Point3D center = new Point3D(0,0,0);	//the center
	Point3D point = new Point3D(0,0,0); 	//the defining point on the periphery
	double a, b, c, d;  					//the circle's params
	double line_constant=10000;				//every circle with a larger radius will be treated as a line!
	
	Point2D.Double namePos=new Point2D.Double(0,0);					//the name's display position
	

	
	
	/**
	 * The constructor method.
	 * @param algorithm The algorithm.
	 * @param name The name.
	 **/
	public DrawableGeoCircle(GeoAlgorithm algorithm, String name)
	{
	
		this.algorithm = algorithm;
		this.name=name;

	
	}

	/**
	 *@see geofuchs.model.Floatable
	 **/
	public Point3D projectFloater(Point3D p) {
			
		//get line through the center and the given position
		Point3D line=Calculator.getLineThrough(center, p);
		//get this line's intersections with the circle
		Point3D[] points = Calculator.getLineCircleIntersection(line,a,b,c,d);
		//compute distances
		double distance1=Calculator.getSquareDistance(p, points[0]);
		double distance2=Calculator.getSquareDistance(p, points[1]);
		
		if(distance1<distance2) return points[0];
		else return points[1];
		
	}

	/**
	 * @see geofuchs.model.Drawable
	 **/
	public void draw(Graphics2D g, Point2D.Double upperLeft, Point2D.Double lowerRight) 
	{

		double size=1.5*emph;
		if(!isDrawable || (emph==0 && !highlight)) return; 
				
		g.setStroke(new BasicStroke((float)size));				
						
		Point2D.Double cp=new Point2D.Double(center.x/center.h, center.y/center.h);
		Point2D.Double pp=new Point2D.Double(point.x/point.h, point.y/point.h);
			
		double r=Math.sqrt((cp.x-pp.x)*(cp.x-pp.x)+(cp.y-pp.y)*(cp.y-pp.y));
		
		//center on the infinity line: draw a line!
		if(r>50000)
		{
			
			Point3D line1=Calculator.getLineThrough(point, center); 	
			Point3D line2=Calculator.getOrthoLine(line1, point);
			DrawableGeoLine drawableLine=new DrawableGeoLine(null, null);
			drawableLine.setParams(line2);
			drawableLine.setColor(color);
			if(highlight) drawableLine.setHighlight(true);
			drawableLine.setEmph(emph);
			drawableLine.setDrawable(isDrawable);
			drawableLine.setName(name);
			drawableLine.setShowName(showName);
			drawableLine.setNamePosition(namePos);			
			drawableLine.draw(g, upperLeft,lowerRight);
			return;
		}	

		g.setPaint(color);
		if(highlight)
		{
			g.setPaint(color.brighter());
			size*=2;
		}
		
		RectangularShape circle=new Ellipse2D.Double(0,0,10,10);
		Ellipse2D.Double ellipse=new Ellipse2D.Double(cp.x-r,cp.y-r,2*r,2*r);
		Shape shape=circle;
		
    	double angSt=0;  //start and end angle of the visible arc of very large circles 
    	double angEnd=0;

		if (r < 5000) 
		{              
			circle = ellipse;	                                                     
		} 
		//if the circle is very large, draw an arc: this is very important
		// for graphical continuity
		else 
		{            
			   // draw arc according to midpoint position                                                
				   angSt = Double.NaN;            
				   // left 
				   if (cp.x < upperLeft.x) { 
					   // top
					   if (cp.y < upperLeft.y) 
					   {                
						  angSt  = -Math.acos((cp.y-upperLeft.y) / r);                 
						  angEnd = -Math.asin((cp.x-upperLeft.x) / r);			
					   }
					   // bottom 
					 else if (cp.y > lowerRight.y) 
					 {                    
						    angSt =  Math.asin((cp.y - lowerRight.y) / r);
						    angEnd =  Math.acos((cp.x-upperLeft.x) / r); 
					   }
					   // middle
					   else 
					   {                    
						   angSt  = -Math.acos((upperLeft.y-cp.y) / r);
						   angEnd =  Math.acos((lowerRight.y-cp.y) / r);
					   }                             
				   }
				   // right 
				   else if (cp.x > lowerRight.x) 
				   {                 
					   // top
					   if (cp.y < upperLeft.y) 
					   {                   
						   angSt  = Math.PI + Math.asin((upperLeft.y-cp.y) / r);
						   angEnd = Math.PI + Math.acos((lowerRight.x - cp.x) / r);
					   }
					   // bottom
					   else if (cp.y>lowerRight.y) 
					   {                   
						   angSt  = Math.PI - Math.acos((lowerRight.y-cp.y) / r);
						   angEnd = Math.PI - Math.asin((lowerRight.x-cp.x) / r);
					   }
					   // middle
					   else 
					   {                    
						angSt  = Math.PI-Math.acos((lowerRight.y-cp.y) / r);
						angEnd = Math.PI+Math.acos((upperLeft.y-cp.y) / r);  
					   }                                                
					}
				   
					 // top or bottom middle - no problems
					 else
					 {                                 
						angSt=0;
						angEnd=2*Math.PI;
					 }
					Arc2D.Double arc=new Arc2D.Double();
					circle = arc;            
					arc.setArcByCenter(cp.x, cp.y, r, 
					Math.toDegrees(angSt), Math.toDegrees(angEnd - angSt), Arc2D.OPEN);
				}
		
		//draw the circle		   		
		g.draw(circle);
	
		g.setStroke(new BasicStroke(1));
		
		//draw the name
		if(showName)
		{
			g.setColor(Color.BLACK);
			Point3D pos=this.projectFloater(new Point3D(namePos.x, namePos.y,1));
			pos=Calculator.normalize(pos);
			namePos=new Point2D.Double(pos.x, pos.y);
			g.drawString(name, (float) (pos.x), (float) (pos.y));
		}
		
	}

	/** 
	 * @see geofuchs.model.Drawable
	 **/
	
	public String getType() {
		return GeoStrings.CIRCLE;
	}

	/**
	 * @see geofuchs.model.Geometric
	 **/
	public boolean isAt(Point2D.Double p, double tolerance) 
	{
		//compute line through center and p
		Point3D line=Calculator.getLineThrough(center, new Point3D(p.x, p.y,1));
		//get the intersection points with the circle (there are always two)
		Point3D[] points = Calculator.getLineCircleIntersection(line,a,b,c,d);
	
		//compute the square distances
		double dist1=Calculator.getSquareDistance(points[0], new Point3D(p.x,p.y,1));
		double dist2=Calculator.getSquareDistance(points[1], new Point3D(p.x,p.y,1)); 
	
		//return the closer point
		if(dist1<=tolerance || dist2<=tolerance) return true;
		else return false;
	}

	

	/** 
	 * @see geofuchs.model.Geometric
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see geofuchs.model.Geometric
	 */
	public String getDescription() {
		return algorithm.getDescription();
	}

	/**
	 * Sets the position of the circle.
	 * @param center The center.
	 * @param point A point on the periphery.
	 */
	public void setPosition(Point3D center, Point3D point)
	{
		
		this.center=center;
		this.point=point;
		
		//compute the parameters
		a=point.h*point.h*center.h; 
		b=-2*center.x*point.h*point.h; 
		c=-2*center.y*point.h*point.h;     
		d=-center.h*point.x*point.x-center.h*point.y*point.y+2*center.x*point.x*point.h+2*center.y*point.y*point.h;
		
	}

	/** 
	 * @see geofuchs.model.Geometric#getObjectType()
	 */
	public String getObjectType() {
	
		return new String(GeoStrings.CIRCLE);
	
	}



	/** 
	 * @see geofuchs.model.Intersectable
	 */
	public Point3D[] getIntersectionSet(Intersectable intObject) 
	{
		//intersections that may include two intersection points
		//implemented yet: line-circle and circle-circle
			
		if(intObject instanceof DrawableGeoLine || intObject instanceof DrawableGeoCircle)
		{
			//get the intersection points
			Point3D[] points=new Point3D[0];
			//line-circle-intersection
			if(intObject instanceof DrawableGeoLine) 
			{
				DrawableGeoLine line=(DrawableGeoLine) intObject;
				points=Calculator.getLineCircleIntersection(new Point3D(line.a, line.b, line.c),a,b,c,d);
			}
			else //circle-circle-intersection
			{
				DrawableGeoCircle circle=(DrawableGeoCircle) intObject;
				points=Calculator.getCircleCircleIntersection(center, point, circle.center, circle.point);
			}
	
			return points;
								
		}
				

		//waiting for extensions... 
		else 
		{
			return null;
		}
	}


	/**
	 * Sets the drawing position of the circle's name. However, it will be projected 
	 * towards the circle.
	 * @param p The name's drawing position.
	 */
	public void setNamePosition(Point2D.Double p) 
	{
		Point3D pos=projectFloater(new Point3D(p.x,p.y,1));
		namePos.x=pos.x;
		namePos.y=pos.y;
	}

	/**
	 * @see geofuchs.model.DrawableGeoObject
	 */
	public String getGeoXMLType()
	{
		return GeoXMLStrings.CIRCLE;
	}

}
