/*
 * Created on 28.03.2003
 */
package geofuchs.model;

import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.*;
import geofuchs.control.GeoStrings;
import geofuchs.mathtools.*;
import geoxml.*;

/**
 *  This class represents lines.
 *  @author Andy Stock
 */
public class DrawableGeoLine extends DrawableGeoObject implements Floatable, Intersectable 
{

	//The points on the line will be set by the calculate-Method of the algorithm.
	 protected Point3D p1 = new Point3D(0,0,1);
	 protected Point3D p2 = new Point3D(0,0,1);
	
	//The last coordinates of the points are used for
	//several decisions.


	//The parameters defining the line ax+by+cz=0.
	//They are computed and set set by the setPoints-method.
	protected double a,b,c;
	
	//the name's display position 
	protected Point2D.Double namePos=new Point2D.Double(0,0); 
	//the name's display position's offset from the nearest point on the line
	protected Point2D.Double nameVector=new Point2D.Double(0,0); 
	
	/**
	 * The constructor.
	 * @param algorithm The line's algorithm.
	 * @param name The line's name.
	 */
	public DrawableGeoLine(GeoAlgorithm algorithm, String name)
	{
		this.algorithm=algorithm;
		this.name=name;
	}
	
	/** 
	 * @see geofuchs.model.Drawable
	 */
	public void draw(Graphics2D g, Point2D.Double uL, Point2D.Double lR) 
	{
		
		double size=1.5*emph;

		if(!isDrawable || (emph==0 && !highlight)) return; 

		//Get two points on the line		
		Point3D hp1, hp2;
		if(a!=0)
		{
			hp1 = new Point3D(-(b+c)/a,1,1);
			hp2 = new Point3D(-(2*b+c)/a,2,1);	
		}
		else if(b!=0)
		{
			hp1 = new Point3D(1,-(a+c)/b,1);
			hp2 = new Point3D(2,-(2*a+c)/b,1);
		}
		else return; //far line
		
		Point2D.Double p1=new Point2D.Double(hp1.x/hp1.h, hp1.y/hp1.h);
		Point2D.Double p2=new Point2D.Double(hp2.x/hp2.h, hp2.y/hp2.h);
		
		g.setPaint(color);

		if(highlight)
		{
			g.setPaint(color.brighter());
			size*=2;
		}
		g.setStroke(new BasicStroke((float)size));
 		
		//get an equation y=m*x+n
		double h = p1.x-p2.x;
		if(h!=0)
		{
			double m = (p1.y-p2.y)/h;
			double n = p1.y-p1.x*m;
		
			//computation for relatively small |m|
			if(m<=10 || m>=-10)
			{
				p1 = new Point2D.Double(uL.x, m * uL.x + n);
				p2 = new Point2D.Double(lR.x, m * lR.x + n);
			}
			//90-degree-rotation for numeric stability
			else
			{	
				m=1/m;
				n=p1.getX()-m*p1.getY();
				p1 = new Point2D.Double(m*uL.y+n, uL.y);
								  p2 = new Point2D.Double(m*lR.y+n, lR.y);	
			}
		}
		//the y coordinates of the points are equal
		else 
		{
			p1.y=uL.y; 
			p2.y=lR.y;	
		}

		Line2D.Double line=new Line2D.Double(p1.x,p1.y,p2.x,p2.y);		
		g.draw(line);
		g.setStroke(new BasicStroke(1));
		
		//draw the line's name
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
	 * Defines the line by two points.
	 * @param p1 A point on the line. 
	 * @param p2 Another point on the line.
	 */
	public void setPoints(Point3D p1, Point3D p2)
	{
		this.p1=p1;
		this.p2=p2;

		//compute the parameters	
		Point3D l=Calculator.getLineThrough(p1,p2);
		a=l.x;
		b=l.y;
		c=l.h;
	
		setNamePosition(namePos);
	}
	
	/**
	 * Sets the line equation to a*x+b*y+c*h=0
	 * @param params The parameters (a,b,c).
	 */
	public void setParams(Point3D params)
	{

		a=params.x;
		b=params.y;
		c=params.h;

		//Compute points on the line
		if(a!=0)
		{
			p1 = new Point3D(-(b+c)/a,1,1);
			p2 = new Point3D(-(2*b+c)/a,2,1);	
		}
		else if(b!=0)
		{
			p1 = new Point3D(1,-(a+c)/b,1);
			p2 = new Point3D(2,-(2*a+c)/b,1);
		}
		else 
		{
			p1= new Point3D(1,0,0);
			p2= new Point3D(0,1,0);
		}
	
	}
	
	/**
	 * @see geofuchs.model.Drawable
	 */
	public String getType() {
		return new String(GeoStrings.LINE);
	}


	/**
	 * @see geofuchs.model.Geometric
	 */
	public boolean isAt(Point2D.Double p, double tolerance) {
		
		//get orthogonal line through the given position
		Point3D oline=Calculator.getOrthoLine(new Point3D(a,b,c), new Point3D(p.x, p.y, 1));
		//get the lines' intersection point (exactly one result)
		Point3D point=Calculator.getLineLineIntersection(new Point3D(a,b,c), oline);
		//compute the square distance to the given position
		double dist=Calculator.getSquareDistance(new Point3D(p.x, p.y, 1), point);
		return (dist<=tolerance);
	}

	/**
	 * @see geofuchs.model.Geometric
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see geofuchs.model.Floatable
	 */
	public Point3D projectFloater(Point3D p)
	{
		//Don't change anything if the line isn't drawable
		if(!isDrawable) return p;
		//calculate orthogonal line a2*x+b2*y+c2*h=0 through the given point's position
		Point3D l = Calculator.getOrthoLine(new Point3D(a,b,c), p);
		//calculate the two lines' intersection
		p=Calculator.getLineLineIntersection(new Point3D(a,b,c), l);
		return p;
	}

	/**
	 * Calculates a point on a parallel through the given point. 
	 * @param p Point on the parallel line.
	 * @return Another point on the parallel line.
	 */
	Point2D.Double getPointOnParallelThrough(Point2D.Double p)
	{
		Point2D.Double point=new Point2D.Double();
		if(p1.x-p2.x != 0)
		{
			double m = (p1.y-p2.y)/(p1.x-p2.x);
			double n = p.y-m*p.x;
			point.x=10+p.x;
			point.y=m*point.x+n;
		}
		else
		{
			point.x=p.x;
			point.y=p.y+10;
		}
		return point;
	}

	/**
	 * Calculates a point on an orthogonal line through the given point. 
	 * @param p Point on the orthogonal line.
	 * @return Another point on the orthogonal line.
	 */
	Point2D.Double getPointOnOrthoLineThrough(Point2D.Double p)
	{
		Point2D.Double point=new Point2D.Double();

		if(p1.y-p2.y!=0)
		{
			double m = -(p1.x-p2.x)/(p1.y-p2.y);
			double n = p.y-m*p.x;
			point.x=10+p.x;
			point.y=m*point.x+n;
			return point;		
		}
		else
		{
			point.y=p.y+10;
			point.x=p.x;
		}
		return point;
	}



	/**
	 * This method is handling line-line-intersections itself and forwarding others to the given other intersectable object.
	 * @see geofuchs.model.Intersectable
	 */
	public Point3D[] getIntersectionSet(Intersectable intObject) 
	{

		Point3D[] points;
		//line-line-intersection
		if(intObject instanceof DrawableGeoLine)
		{
			//there can be only one intersection...
			points=new Point3D[1];
			DrawableGeoLine l=(DrawableGeoLine)intObject;
			if(l.equals(this)) return new Point3D[] {new Point3D(0,0,0)};
			points[0]=Calculator.getLineLineIntersection(new Point3D(a,b,c), new Point3D(l.a, l.b, l.c));
			return points;
		}

		else return intObject.getIntersectionSet(this); 
	}

	/** 
	 * @see geofuchs.model.Geometric
	 */
	public String getObjectType() 
	{
	
		return new String(GeoStrings.LINE);
	}

	/** 
	 * @see geofuchs.model.Drawable
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
		return GeoXMLStrings.LINE;
	}

}
