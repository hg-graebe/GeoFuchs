/*
 * Created on 06.04.2004
 *
 */
package geofuchs.mathtools;

/**
 *
 * This class includes some static methods which are particularly 
 * useful for projective geometry, but also some other, computations.
 * @author Andy Stock
 */
public class Calculator {


	/**
	 * Calculates a line that is othogonal to a given line and passes through
	 * a given point.
	 * @param l The line a1*x+b1*y+c1*h as triple (a1,b1,c1).
	 * @param p The point (x,y,h).
	 * @return The orthogonal line as (a2,b2,c2).
	 */
	static public Point3D getOrthoLine(Point3D l, Point3D p)
	{ 
		double b = l.x*p.h;
		double c = l.y*p.x-l.x*p.y;
		double a = -p.h*l.y;
		return normalize(new Point3D(a,b,c));			
	}

	/**
	 * Calculates a line which is parallel to a given line and
	 * passes through a given point.
	 * @param l The line a1*x+b1*y+c1*h as triple (a1,b1,c1).
	 * @param p The point (x,y,h).
	 * @return A parallel line through the given point as triple (a2,b2,c2).
	 */
	static public Point3D getParaLine(Point3D l, Point3D p)
	{ 
		double b = l.y*p.h;
		double c = -l.y*p.y-p.x*l.x;
		double a = p.h*l.x;
		return normalize(new Point3D(a,b,c));			
	}

	/**
		 * Calculates the intersection of two lines.
		 * @param l1 The first line a1*x+b1*y+c1*h as triple (a1,b1,c1).
		 * @param l2 The second line a2*x+b2*y+c2*h as triple (a2,b2,c2).
		 * @return The intersection point (x,y,h).
	*/
	static public Point3D getLineLineIntersection(Point3D l1, Point3D l2)
	{ 
		if(Math.abs(l1.x/l1.h-l2.x/l2.h)<0.0000001 && Math.abs(l1.y/l1.h-l2.y/l2.h)<0.0000001) return new Point3D(0,0,0);
		double y = -l1.x*l2.h+l2.x*l1.h;
		double x = l1.y*l2.h-l1.h*l2.y;
		double h = l1.x*l2.y-l2.x*l1.y;
		return normalize(new Point3D(x,y,h));		
	}

	/**
	 * Calculates the line that passes through both given points.
	 * @param p1 The first point (x1,y1,h1).
	 * @param p2 The second point (x2,y2,h2).
	 * @return The line (a,b,c).
	 */
	public static Point3D getLineThrough(Point3D p1, Point3D p2)
	{
		double b=-p1.x*p2.h+p2.x*p1.h;    
		double a=p1.y*p2.h-p1.h*p2.y; 
		double c=-p2.x*p1.y+p1.x*p2.y; 
		return normalize(new Point3D(a,b,c));
	}	

	/**
	 * Calculates the intersection of a line and a circle.
	 * @param line The line (l,m,n).
	 * @param c_1 First parameter of the circle (c_1,c_2,c_3,c_4).
	 * @param c_2 Second parameter of the circle (c_1,c_2,c_3,c_4).
	 * @param c_3 Third parameter of the circle (c_1,c_2,c_3,c_4).
	 * @param c_4 Fourth parameter of the circle (c_1,c_2,c_3,c_4).
	 * @return The intersection points.
	 */
	public static Point3D[] getLineCircleIntersection(Point3D line, double c_1, double c_2, double c_3, double c_4)
	{
	
		double a=line.x;
		double b=line.y;
		double c=line.h;

		//degenerated circle
		if(c_1==0)
		{
			Point3D p1=getLineLineIntersection(normalize(line), normalize(new Point3D(c_2,c_3,c_4)));
			Point3D p2=new Point3D(10,10,10);
			return new Point3D[] {normalize(p1), normalize(p2)};
		}
		
		
		 if(a!=0)
		{
			double k=Math.sqrt(c_2 * c_2 * a * a * b * b - 0.2e1 * c_2 * Math.pow(a, 0.3e1) * b * c_3 + 0.4e1 * c * b * c_1 * c_3 * a * a + c_3 * c_3 * Math.pow(a, 0.4e1) - 0.4e1 * c_1 * b * b * c_4 * a * a - 0.4e1 * c_1 * c_1 * a * a * c * c + 0.4e1 * c_1 * Math.pow(a, 0.3e1) * c_2 * c - 0.4e1 * c_1 * Math.pow(a, 0.4e1) * c_4);
			Point3D p1=new Point3D(0,0,0);
			Point3D p2=new Point3D(0,0,0);
			p1.x= -b * (c_2 * a * b - 2 * c * b * c_1 - c_3 * a * a + k) - 2 * b * b * c_1 * c - 2 * c * c_1 * a * a;
			p2.x=-b * (c_2 * a * b - 2 * c * b * c_1 - c_3 * a * a - k) - 2 * b * b * c_1 * c - 2 * c * c_1 * a * a;
			p1.y=-(-c_2 * a * b + 2 * c * b * c_1 + c_3 * a * a - k) * a;
			p2.y=-(-c_2 * a * b + 2 * c * b * c_1 + c_3 * a * a + k) * a;
			p1.h= 2*a*c_1*b*b+2*c_1*a*a*a;
			p2.h=p1.h;
			return new Point3D[] {normalize(p1),normalize(p2)};
		}	
		else
		{

			Point3D p1=new Point3D(0,0,0);
			Point3D p2=new Point3D(0,0,0);
			p1.x = -(double) (b * c_2) + Math.sqrt((double) (c_2 * c_2 * b * b - 4 * c_1 * c_1 * c * c + 4 * c * b * c_1 * c_3 - 4 * c_1 * b * b * c_4));
			p2.x = -(double) (b * c_2) - Math.sqrt((double) (c_2 * c_2 * b * b - 4 * c_1 * c_1 * c * c + 4 * c * b * c_1 * c_3 - 4 * c_1 * b * b * c_4));
			p1.y = -2*c_1*c;
			p2.y=p1.y;
			p1.h = 2*b*c_1;
			p2.h=p1.h;
 
			return new Point3D[] {normalize(p1),normalize(p2)};
		}

	}



	/**
	 * Calculates two points' square distance.
	 * @param p1 A point (x1,y1,h1).
	 * @param p2 A point (x2,y1,h2).
	 * @return The square distance of both points or Double.MAX_VALUE if at 
	 * 			least one of the points is at infinity.
	 */
	public static double getSquareDistance(Point3D p1, Point3D p2)
	{
		if(p1.h==0 || p2.h==0) return Double.MAX_VALUE;
		else return (p1.x/p1.h-p2.x/p2.h)*(p1.x/p1.h-p2.x/p2.h)+(p1.y/p1.h-p2.y/p2.h)*(p1.y/p1.h-p2.y/p2.h);
	}

	/**
	 * Calculates two points' distance's root.
	 * @param p1 A point (x1,y1,h1).
	 * @param p2 A point (x2,y1,h2).
	 * @return The square root of the distance of both points or the square root of Double.MAX_VALUE if at 
	 * 			least one of the points is at infinity.
	 */
	public static double getDistanceRoot(Point3D p1, Point3D p2)
	{
		double distance=getSquareDistance(p1,p2);
		return Math.sqrt(Math.sqrt(distance));
	}

	/**
	 * Normalizes a point.
	 * @param p The point p=(x,y,h).
	 * @return (x/h,y/h,1) if h!=0, (x,y,0) otherwise.
	 */
	
	public static Point3D normalize(Point3D p)
	{
		if(p.h!=0) return new Point3D(p.x/p.h, p.y/p.h,1);
		else if(p.x!=0) return new Point3D(1, p.y/p.x,p.h/p.x);
		else if(p.y!=0) return new Point3D(0,1,0);
		else return p;  
	}

	/**
	 * Calculates the midpoint og two given points.
	 * @param p1 The first point.
	 * @param p2 The second point.
	 * @return The midpoint.
	 */
	public static Point3D getMidpoint(Point3D p1, Point3D p2)
	{
		return normalize(new Point3D(p2.h*p1.x+p1.h*p2.x, p2.h*p1.y+p1.h*p2.y, 2*p2.h*p1.h));
	}

	/**
	 * Computes the intersection of two circles.
	 * @param c1 The first circle's center.
	 * @param p1 A point which lies on the first circle's periphery.
	 * @param c2 The second circle's center.
	 * @param p2 A point which lies on the second circle's periphery.
	 * @return
	 */
	public static Point3D[] getCircleCircleIntersection(Point3D c1, Point3D p1, Point3D c2, Point3D p2)
	{
		
		//get the midpoint of both given periphery points
		Point3D mp=getMidpoint(p1, p2);
		//get lines through this point and the circles' centers
		Point3D cline1=getLineThrough(mp, c1);
		Point3D cline2=getLineThrough(mp, c2);
		//Get the radical axes of the circle with the midpoint mp and the point
		//p1 on the periphery with both circles. It's orthogonal to the line given through
		//mp and c1 or mp and c2, respectively, and they pass through p1 or p2.
		Point3D raxis1=getOrthoLine(cline1, p1);
		Point3D raxis2=getOrthoLine(cline2, p2);
		//get their intersection
		Point3D ipoint=getLineLineIntersection(raxis1, raxis2);
		//c1, c2 and mp shouldn't be colinear! 
		Point3D hline1=getLineThrough(c1,c2);
		Point3D hline2=getOrthoLine(hline1, mp);
		Point3D hp=getLineLineIntersection(hline1,hline2);
		double hdist=getSquareDistance(hp,mp);
		if(hdist<1) ipoint=mp;
		
		//the radical axis of the given circles is orthogonal to
		//the line through their centers and joins the other radical axes 
		//in a point (which has been computed yet)
		Point3D cline3=getLineThrough(c1,c2);
		Point3D raxis3=getOrthoLine(cline3, ipoint);
		//this radical axis' intersection with one of the circles is the
		//circles' intersection as well
		double a=p1.h*p1.h*c1.h; 
		double b=-2*c1.x*p1.h*p1.h; 
		double c=-2*c1.y*p1.h*p1.h;     
		double d=-c1.h*p1.x*p1.x-c1.h*p1.y*p1.y+2*c1.x*p1.x*p1.h+2*c1.y*p1.y*p1.h;
		Point3D[] iSet=getLineCircleIntersection(raxis3,a,b,c,d);
		return iSet;
	}

	/**
	 * Adds two points.
	 * @param p1
	 * @param p2
	 * @return p1+p2
	 */
	public static Point3D addPointVectors(Point3D p1, Point3D p2)
	{
		p1=normalize(p1);
		p2=normalize(p2);
		return new Point3D(p1.x+p2.x,p1.y+p2.y,1);
		
	}


	/**
	 * Computes the next permutation in lexicographical order.
	 * @param perm A permutation.
	 * @return The next permutation in lexicographical order (or perm, if it's the last one).
	 */	
	public static int[] getNextPermutation(int[] p)
	{
		int n=p.length;
		int k = n-2;

		int[] perm=new int[p.length];
		for(int i=0; i<p.length; i++)
			perm[i]=p[i];

		while (k>=0&&perm[k] > perm[k+1]) k--;
		if (k == -1) return perm;
		else
		{
			int j = n-1;
			while (perm[k] > perm[j]) j--;
			int temp=perm[j];
			perm[j]=perm[k];
			perm[k]=temp;
			int r = n-1; 
			int s = k+1;
			while (r>s)
			{
				temp=perm[r];
				perm[r]=perm[s];
				perm[s]=temp;
				r--; 
				s++;
			}
		}
		return perm; 
		
	}


}
