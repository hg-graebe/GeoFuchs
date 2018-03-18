/*
 * Created on 19.06.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package geofuchs.model;
import java.awt.geom.*;
import geofuchs.control.*;
import geoxml.*;
import geofuchs.mathtools.*;

/**
 * This is the algorithm class for intersection points, irrespective 
 * of the types of their directly defining objects (the intersected objects, 
 * that is). In contrast to other algorithm classes, instances of this class
 * may be attached to several points. For instance, both intersection points
 * of two circles share one intersection algorithm object(if both intersection)
 * points have been constructed). 
 * @author Andy Stock
 *
 */
public class IntersectionAlgorithm implements GeoAlgorithm 
{

	private Intersectable intObject1;	//The first directly defining intersectable object
	private Intersectable intObject2;	//The second directly defining intersectable object
	private DrawableGeoPoint intersection[]; //Array of the intersection points
	private Point3D lastRealPos[];		//The last real positions of the intersection points	
	private DrawableGeoPoint selectedPoint; //The currently selected intersection point, to which 
											//the next point-specific commands, e.g. createObject(), are applied
	private int age[];						//contains the number of times each intersection point couldn't be updated
	
	int coordsGiven=0;						//is used for giving back different coordinates every time the getFreeCoordinates method is called
	
	/**
	 * The constructor method.
	 * @param i1 The first directly defining intersectable object.
	 * @param i2 The second directly defining intersectable object.
	 */
	public IntersectionAlgorithm(Intersectable i1, Intersectable i2)
	{
		intObject1=i1;
		intObject2=i2;
		//get intersection set
		Point3D[] intersectionSet=i1.getIntersectionSet(i2);
		//create intersection points and initialize arrays
		intersection=new DrawableGeoPoint[intersectionSet.length];		
		lastRealPos=new Point3D[intersectionSet.length];
		age=new int[intersectionSet.length];
		for(int i=0; i<intersectionSet.length; i++)
		{
			if(intersectionSet[i].h!=0 && intersectionSet[i].x!=Double.NaN)lastRealPos[i]=intersectionSet[i];
			else lastRealPos[i]=new Point3D(Double.MAX_VALUE,Double.MAX_VALUE,1);
			//the name will be set by the createObject method
			intersection[i]=new DrawableGeoPoint(intersectionSet[i],null);
			age[i]=0;
		}
	}
	
	/**
	 * Selects one of the points which are contained in the algorithm's intersection set.
	 * The next point-specific commands, e.g. createObject(), are applied to this point.
	 * @param p A position in world coordinates. The closest intersection point will be selected.
	 */
	public void selectIntersectionPoint(Point2D.Double p)
	{
		int bestIndex=0;  //index of the nearest intersection point
		double bestDistance=Double.MAX_VALUE; //best distance found yet
		for(int i=0; i<intersection.length; i++)
		{
			//calculate the square distance and check if it's the best distance that's been found yet
			double distance=Calculator.getSquareDistance(new Point3D(p.x,p.y,1), intersection[i].coords);//(p.x-intersection[i].coords.getX())*(p.x-intersection[i].coords.getX())+(p.y-intersection[i].coords.getY())*(p.y-intersection[i].coords.getY());
			if(new Double(distance).equals(new Double(Double.NaN))) distance=Double.MAX_VALUE-1;
			if(distance<=bestDistance && intersection[i].getName()==null)
			{
				bestDistance=distance;
				bestIndex=i;
			}
		}
		selectedPoint=intersection[bestIndex];
	}
	
	
	/**
	 * @see geofuchs.model.GeoAlgorithm)
	 */
	public void calculate() 
	{

		
		//calculate new positions
		Point3D pos[]=intObject1.getIntersectionSet(intObject2);		
		
		//calculate proposed positions
		Point3D proposedPosition[] = new Point3D[intersection.length];
		for(int i=0; i<intersection.length; i++)			
			proposedPosition[i]=lastRealPos[i];
	
		for(int i=0; i<intersection.length; i++) intersection[i].setDrawable(true);
		
		//one of the directly defining objects isn't drawable --> the intersection points are neither 
		//the same goes if the directly defining objects are equal
		if(!intObject1.isDrawable() || !intObject2.isDrawable() || intObject1.equals(intObject2))
		{
			for(int i=0; i<intersection.length; i++) intersection[i].setDrawable(false);
			return;
		}
		
		
		//compute all distances
		double distance[][]=new double[intersection.length][intersection.length];
		double maxDistance=0;
		for(int i=0; i<intersection.length; i++)
			for(int j=0; j<intersection.length; j++)
			{
				if(pos[i].x==Double.NaN || pos[i].h==0) distance[j][i]=Double.MAX_VALUE;
				else 
				{
					distance[j][i]=Calculator.getDistanceRoot(proposedPosition[j],pos[i])+age[i]*age[i]; 		
					if(distance[j][i]>maxDistance) maxDistance=distance[j][i];
				}
			}
		for(int i=0; i<intersection.length; i++)
			for(int j=0; j<intersection.length; j++)
				if(distance[i][j]==Double.MAX_VALUE) distance[i][j]=maxDistance+1;
			
			//compute all permutations and find the one with the lowest distance sum
			
			//initalize permutations: identity
			int[] perm=new int[intersection.length];
			for(int i=0; i<intersection.length;i++)
				perm[i]=i;
			
			//get the next permutation in lexicographical order
			double bestDistance=Double.MAX_VALUE;
			int[] bestPerm=new int[intersection.length];
			int[] lastPerm=new int[intersection.length];
			//compute the distance sum
			while(true)
			{
				double dSum=0;
				for(int i=0; i<intersection.length; i++)
					dSum+=distance[i][perm[i]];
				if(dSum<bestDistance)
				{
					bestDistance=dSum;
					bestPerm=perm;
				}

				for(int i=0; i<intersection.length;i++)											
					lastPerm=perm;
				
				perm=Calculator.getNextPermutation(perm);
				//check if there have been changes
				boolean changed=false;
				for(int i=intersection.length-1; i>=0; i--)
					if(perm[i]!=lastPerm[i])
					{  
						changed=true;
						break;
					}
				if(!changed) break;
			}
			

			//set the intersection points' positions and update information like the age field
			for(int i=0; i<intersection.length; i++)
			{
				int bestIndex=bestPerm[i];
				intersection[i].move(pos[bestIndex].x, pos[bestIndex].y,pos[bestIndex].h);
				age[i]++;
				if(pos[bestIndex].x!=Double.NaN && pos[bestIndex].h!=0)
				{
					lastRealPos[i]=pos[bestIndex];
					age[i]=0;
				}
	
			}
						
		
	}

	
	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public Geometric createObject(String name) {
		//if the point has a name, it has been "given away" before.
		if(!(selectedPoint.getName()==null)) return null;
		else 
		{
			selectedPoint.setName(name);
			selectedPoint.setColor(GDefaults.intersectionColor);
			selectedPoint.setAlgorithm(this);
			calculate();
			return selectedPoint;
		} 
	}

		/** 
	   * @see geofuchs.model.GeoAlgorithm
	   */
	  public String getDescription() {
		  return new String(selectedPoint.getName()+": "+langStrings.s_intersection+" "+langStrings.s_of + " "+intObject1.getName()+" "+langStrings.s_and + " "+intObject2.getName());
	 	  
	  }

	  /**
	   * Intersection points are considered to be equal if they are intersections of the same object.
	   */
	  public boolean equals(GeoAlgorithm c) {
		  if(intObject1.getName().equals(intObject2.getName()))
		  	return true;
	  	  else return false;
	  }

	/**
	 * 
	 * @return An object that encapsulates information on the algorithm.
	 * @see geofuchs.model.AlgorithmInfo
	 */
	public static AlgorithmInfo getInfo() {
		
		ObjectExpectation[] obEx = new ObjectExpectation[]
		{
			new ObjectExpectation(GeoStrings.LINE,2),
			new ObjectExpectation(GeoStrings.CIRCLE,2)		
		};
	
		return new AlgorithmInfo(GeoStrings.INTERSECTION , GeoStrings.POINT, 2, obEx);

	}


	/**
	 * @param name1 The first object's name.
	 * @param name2 The second object's name.
	 * @return True, if the intersectable objects (their names, that is) of this algorithm are 
	 * 			equal to the given ones. Otherwise false.
	 */
	public boolean intObjectsAre(String name1, String name2)
	{

		if((intObject1.getName().equals(name1)&&intObject2.getName().equals(name2)) 
			|| (intObject1.getName().equals(name2)&&intObject2.getName().equals(name1))) return true;
		else return false;
	}

	/**
	 * Should be called before any of the intersection points is removed from the algorithm.
	 * This may for instance happen because of a change operation.
	 * @param name The object's name.
	 */
	void removeObjectFromAlgorithm(String name)
	{
		for(int i=0; i<intersection.length; i++)
			if(intersection[i].getName().equals(name))
			{	
				DrawableGeoPoint p = new DrawableGeoPoint(intersection[i].coords.getX(),intersection[i].coords.getY(), null);
				intersection[i]=p;
			}
	}

	/**
	 * Sets the intersection point that is closest to the given position to the 
	 * given Geometric object, but only if it's an instance of DrawableGeoPoint.
	 * Otherwise, or if the nearest intersection point is already part of the
	 * construction (in this case it has a name!), no changes are performed.
	 * @param g The geometric object.
	 * @param pos The position.
	 */
	public void setDependentObject(Geometric g, Point2D.Double pos)
	{
		//find closest intersection point
		double bestDistance=999999;
		int bestIndex=0; //index of the best distance.
		for(int i=0; i<intersection.length; i++)
		{
			//calculate the distance and check if it's the best distance that's been found yet
			double distance=(pos.x-intersection[i].coords.getX())*(pos.x-intersection[i].coords.getX())+(pos.y-intersection[i].coords.getY())*(pos.y-intersection[i].coords.getY());
			if(distance<bestDistance)
			{
				bestDistance=distance;
				bestIndex=i;
			}
		}
		if(intersection[bestIndex].getName()==null && g instanceof DrawableGeoPoint)
			intersection[bestIndex]=(DrawableGeoPoint) g;
	}
	/**
	 * @see geofuchs.model.GeoGeoAlgorithm
	 */
	public String[] getDefiningObjectNames() {
		String[] objects = new String[2];
		objects[0]=intObject1.getName();
		objects[1]=intObject2.getName();
		return objects;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getGeoXMLType() {
		return GeoXMLStrings.INTERSECTION;
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public double[] getFreeCoordinates() 
	{
		Point3D p=intersection[coordsGiven].coords;
		coordsGiven++;
		if(coordsGiven==intersection.length) coordsGiven=0;
		return new double[] {p.x, p.y, p.h};
	}

	/**
	 * @see geofuchs.model.GeoAlgorithm
	 */
	public String getType()
	{
		return GeoStrings.INTERSECTION;
	}

}
