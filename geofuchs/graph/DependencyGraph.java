/*
 * Created on 08.07.2004
*/
package geofuchs.graph;

import java.util.ArrayList;
import geofuchs.model.DrawableGeoObject;

/**
 *  Instances of this class represent a construction's dependency graph.
 * 	When using it, it's your own responsibility to avoid cycles. If there are
 *  any, some of this class's methods won't terminate!
 *  @author Andy Stock
 *
 */
public class DependencyGraph 
{

	//inner class for representation of nodes
	private class Node
	{
		protected String name; //the node's name
		protected ArrayList definingObjects; //list of child nodes
		
		/**
		 * The constructor method.
		 * @param name The node's name.
		 */
		public Node(String name)
		{
			this.name=name;
			definingObjects=new ArrayList();
		}
		
		//adds the node and (recursively) its child nodes to the dependent objects list
		protected void addToDependencyList()
		{
			descendantOrSelfNodes.add(name);
			for(int i=0; i<definingObjects.size(); i++)
			{
				((Node)definingObjects.get(i)).addToDependencyList();		
			}
		}
	}

	ArrayList nodes=new ArrayList();	//the nodes; they implicitly contain edge information 
	ArrayList descendantOrSelfNodes=new ArrayList(); //all dependent objects, set by getDependingObjects method

	/**
	 * The constructor method.
	 * @param constructionList An ArrayList containing geometric objects (of type DrawableGeoObject) of the construction
	 * to be represented.
	 */
	public DependencyGraph(ArrayList constructionList)
	{
		//iterate over the construction list and add a node for each contained object.
		for(int i=0; i<constructionList.size(); i++)
		{
			Object o = constructionList.get(i);
			if(o instanceof DrawableGeoObject)
				addNode(((DrawableGeoObject)o).getName());
		}
	
		//iterate over the list another time, and this time add the edges 
		for(int i=0; i<constructionList.size(); i++)
		{
			Object o = constructionList.get(i);
			if(o instanceof DrawableGeoObject)
			addEdge(((DrawableGeoObject)o).getName(), ((DrawableGeoObject)o).getDefiningObjectNames());
		}
		
	
	}
	
	/**
	 * This method adds a node to the node list.
	 * @param name The node's name.
	 */
	private void addNode(String name)
	{
		nodes.add(new Node(name));
	}

	

	/**
	 * 	Adds an edge.
	 * @param name The depending object's name.
	 * @param defObjects The directly defining objects' names.
	 */
	private void addEdge(String name, String[] defObjects)
	{
		//get the indices of the named object and the defining objects
		
		int obIndex=0;
		while(obIndex<nodes.size())
		{
			if(((Node)nodes.get(obIndex)).name.equals(name)) break;
			obIndex++;
		}
		
		for(int i=0; i<defObjects.length; i++)
		{		
			int defIndex=0; //index of a defining object
			while(defIndex<nodes.size()-1)
			{
				if(((Node)nodes.get(defIndex)).name.equals(defObjects[i])) break;
				defIndex++;
			}

			//add the given object's node to the child node list of the defining object's node 
			((Node) nodes.get(defIndex)).definingObjects.add(nodes.get(obIndex));
						
		}
	}

	/**
	 * Calculates all depending objects of the object given by name.
	 * @param name The defining object's name.
	 * @return The names the given object's depending objects, and the given name
	 * as well.
	 */
	public String[] getDependingOrSelfObjects(String name)
	{

		descendantOrSelfNodes=new ArrayList();

		//find the object given by name
		int obIndex=0;
		while(obIndex<nodes.size())
		{
			if(((Node)nodes.get(obIndex)).name.equals(name)) break;
			obIndex++;
		}
	
		//find the depending objects
		Node startNode=(Node) nodes.get(obIndex); 
		startNode.addToDependencyList();
		
		String[] dependentObjectNames=new String[descendantOrSelfNodes.size()];
		for(int i=0; i<dependentObjectNames.length; i++)
		dependentObjectNames[i]=(String)descendantOrSelfNodes.get(i);

		return dependentObjectNames;
	
	}

}
