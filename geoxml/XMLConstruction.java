/*
 * Created on 11.07.2004

 */
package geoxml;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import java.io.*;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class can be used for reading or writing GeoXML documents.
 * @author Andy Stock
 *
 */
public class XMLConstruction 
{
	private ArrayList geoObjects=new ArrayList(); //the geometric objects list;
	private ArrayList varList=new ArrayList();	  //the element nodes for variables list
	private int position=0;  					  //iterator
	private DocumentBuilder builder=getDocumentBuilder();		//the document builder
	private Document document=builder.newDocument();			//the DOM 
	
	private int idCounter=0; //counts the IDs that have been written for creating new IDs
	private int varCounter=0; //counts the variables in the document
	
	/**
	 * Adds a geometric object. If there's no ID, but a name, the name will be used as ID.
	 * If there's no ID and no name the ID will be generated automatically. However,
	 * if the object's name or ID start with GeoXMLStrings.IDPREFIX, they will be changed.
	 * It's advisable for any application using GeoXML to take care of setting  the IDs properly itself.
	 * @param geoObject Information on the geometric object.
	 */
	public void addObject(GeoObjectDescription geoObject)
	{
		//make sure there can't be duplicate IDs
		if(geoObject.getID()!=null && geoObject.getID().startsWith(GeoXMLStrings.IDPREFIX)) geoObject.setID("x"+geoObject.getID());
		else
			if(geoObject.getName()!=null) 
			{	
				if(geoObject.getName().startsWith(GeoXMLStrings.IDPREFIX)) geoObject.setName("x"+geoObject.getName());
				geoObject.setID(geoObject.getName());				
			} 
			else 
			{	
				geoObject.setID(GeoXMLStrings.IDPREFIX+idCounter); 
				idCounter++;
			}
	
		//add the object
		geoObjects.add(geoObject);
	
		//if the object has variables, create the element node and add it to the variable list.
		if(geoObject.getVariables()!=null && geoObject.getVariables().length>0)
		{
			double[] variables=geoObject.getVariables();
		
			for(int i=0; i<variables.length; i++)
			{
				Element variable=document.createElement(/*GeoXMLStrings.NAMESPACEPREFIX+":"+*/GeoXMLStrings.TVARIABLE);
				String id=GeoXMLStrings.VARIDPREFIX+varCounter; //create variable ID
				variable.setAttribute(GeoXMLStrings.AID, id);
				Text content=document.createTextNode(new Double(variables[i]).toString());
				variable.appendChild(content);
				varCounter++;
				varList.add(variable);
		
				//now add a reference to the variable to the param list of the object
				String[] oldParams=geoObject.getDefObjects();
				String[] params=new String[oldParams.length+1];
				for(int j=0; j<oldParams.length; j++)
					params[j]=oldParams[j];
				params[oldParams.length]=id;
				geoObject.setDefObjects(params);
					
			}
		}
	}
	
	/**
	 * This method returns a geometric object.
	 * Successive calls will return successive geometric objects.
	 * @return A description of the geo object, or null if there are no more objects.
	 */
	public GeoObjectDescription getNextObject()
	{
		if(position<geoObjects.size())
		{	
			GeoObjectDescription o =  (GeoObjectDescription) geoObjects.get(position);
			position++;
			return o;
		}
		else return null;
	}
	
	/**
	 * Creates a DOM tree of the construction given by former addObject method calls
	 * and writes it to a xml file.
	 * @param file
	 */
	public void writeXML(String file)
	{
		
		//create root node
		Element construction = document.createElement(GeoXMLStrings.TCONSTRUCTION);
		document.appendChild(construction);
		construction.setAttribute(GeoXMLStrings.AFORMAT, GeoXMLStrings.FORMAT);
		construction.setAttribute(GeoXMLStrings.AVERSION,GeoXMLStrings.VERSION);
		
		//write the variables
		writeVariables(construction);
		
		//write the objects
		writeObjects(construction);
		
		//write display options
		writeDisplayOptions(construction);
		
		//normalize the DOM 
		document.getDocumentElement().normalize();
		
		//write the document to a file
		writeToFile(file);
	
		
	
	}

	/**
	 * Creates the display-options part of the document.
	 * @param construction The superordinate construction node.
	 */
	private void writeDisplayOptions(Element construction)
	{
		Element displayOptions=document.createElement(GeoXMLStrings.TDO);
		construction.appendChild(displayOptions);
		//iterate over objects and add display options
		for(int i=0; i<geoObjects.size();i++)
		{
			GeoObjectDescription o=(GeoObjectDescription)geoObjects.get(i);
			Element option=document.createElement(GeoXMLStrings.TOPTION);
			option.setAttribute(GeoXMLStrings.AREF, o.getID());
			option.setAttribute(GeoXMLStrings.ASHOWNAME, ""+o.getShowName());
			//write the colors
			double[] c=o.getColor();
			for(int j=0; j<c.length; j++)
			{
				Element color=document.createElement(GeoXMLStrings.TCOLOR);
				Text content=document.createTextNode(new Double(c[j]).toString());
				color.appendChild(content);
				option.appendChild(color);
			}
			//write emphasis factor
			Element emphFactor=document.createElement(GeoXMLStrings.TEMPH);
			double t=o.getEmphFactor();
			emphFactor.setNodeValue(new Double(t).toString());
			Text content=document.createTextNode(new Integer(o.getEmphFactor()).toString());
			emphFactor.appendChild(content);
			option.appendChild(emphFactor);
			displayOptions.appendChild(option);
		}
	}

	/**
	 * Creates the variables part of the document.
	 * @param construction The superordinate construction node.
	 */
	private void writeVariables(Element construction)
	{
		Element variables=document.createElement(GeoXMLStrings.TVARIABLES);
		construction.appendChild(variables);
		for(int i=0; i<varList.size(); i++)
		{
			Element var=(Element) varList.get(i);
			variables.appendChild(var);
			
		}
	}

	/**
		 * Creates the geometric objects part of the document.
		 * @param construction The superordinate construction node.
		 */
	private void writeObjects(Element construction)
	{
		//write objects element
		Element objects=document.createElement(GeoXMLStrings.TOBJECTS);
		construction.appendChild(objects);
		//add objects
		for(int i=0; i<geoObjects.size();i++)
		{
			GeoObjectDescription o=(GeoObjectDescription)geoObjects.get(i);
			Element object=document.createElement(o.getType());
			object.setAttribute(GeoXMLStrings.AID, o.getID());
			if(o.getName()!=null) object.setAttribute(GeoXMLStrings.ANAME, o.getName());
			object.setAttribute(GeoXMLStrings.AALGORITHM, o.getAlgorithm());
			writeParams(o, object);
			objects.appendChild(object);
		}	
	}

	/**
	 * Adds the parameters for an object to the document.
	 * @param object Information on the object, especially on the directly defining objects.
	 * @param objectNode The DOM node representing the object.
	 */
	private void writeParams(GeoObjectDescription object, Element objectNode)
	{
		String[] params=object.getDefObjects();
		if(params!=null && params.length!=0)
			for(int i=0; i<params.length; i++)
			{
				Element param=document.createElement(GeoXMLStrings.TPARAM);
				objectNode.appendChild(param);
				param.setAttribute(GeoXMLStrings.AREF, params[i]);	
			}
	}

	/**
	 * Writes the document to a file.
	 * @param file The file's name.
	 */	
	private void writeToFile(String file)
	{
		  try 
		  {
		
			  //Use a Transformer for output
			  TransformerFactory tFactory = TransformerFactory.newInstance();
			  Transformer transformer = tFactory.newTransformer();
			  DOMSource source = new DOMSource(document);
			  StreamResult result = new StreamResult(new FileOutputStream(file));
			  transformer.transform(source, result);
	
		  }
		  catch (TransformerConfigurationException tce) 
		  {
			  //Error generated by the parser
			  System.err.println ("* Transformer Factory error");				  System.err.println(" " + tce.getMessage() );
		  } 
		  catch (TransformerException te) 
		  {
			  //Error generated by the parser
			  System.err.println ("* Transformation error");
			  System.err.println(" " + te.getMessage() );		
		  }
		  catch(IOException ie)
		  {
			 //couldn't write file
			System.err.println ("Error while writing file");
			System.err.println(" " + ie.getMessage() );		
		  }		  
	}
	
	/**
	 * Creates a document from a xml file. The old DOM - if existing - will be deleted.
	 * The getObject() method can be used to get the results.
	 * @param file The file's name.
	 */
	public void parseFile(String file)
	{
		geoObjects=new ArrayList();
		varList=new ArrayList();
		
		//parse file
		document=getDOMFromFile(file);
		
		//normalize document
		document.getDocumentElement().normalize();
		
		//read the objects
		Node objects=document.getElementsByTagName(GeoXMLStrings.TOBJECTS).item(0);
		NodeList objectNodes=objects.getChildNodes();
		//iterate over the object nodes and add them to the object list 
		for(int i=0; i<objectNodes.getLength(); i++) parseObjectNode(objectNodes.item(i));
		//process the display options
		Node displayOptions=document.getElementsByTagName(GeoXMLStrings.TDO).item(0);
		NodeList displayOptionNodes=displayOptions.getChildNodes();
		for(int i=0; i<displayOptionNodes.getLength(); i++) parseDONode(displayOptionNodes.item(i));	
	}
	
	/**
	 * This method parses a display option node.
	 * @param node The abovementioned node.
	 */
	private void parseDONode(Node node)
	{
		if(!(node instanceof Element)) return;
		Element element=(Element) node;
		if(element.getTagName().equals(GeoXMLStrings.TOPTION))
		{
			
			int nrOfColorNodes=0;
			ArrayList colors=new ArrayList();
			String emphFactor=new String("1");
			
			//get attributes
			String ref=element.getAttribute(GeoXMLStrings.AREF);
			String showName=element.getAttribute(GeoXMLStrings.ASHOWNAME);
			NodeList children=element.getChildNodes();
			
			//get content of the child nodes
			for(int j=0; j<children.getLength();j++)
			{
				Node c=children.item(j);
				//color element
				if(c.getNodeName().equals(GeoXMLStrings.TCOLOR))
				{
					nrOfColorNodes++;
					//check the color node's child nodes for content
					NodeList children2=c.getChildNodes();
					for(int k=0; k<children2.getLength(); k++)
						if(children2.item(k) instanceof Text) colors.add(children2.item(k).getNodeValue());
				}	
				//emphasis element
				else if(c.getNodeName().equals(/*GeoXMLStrings.NAMESPACEPREFIX+":"+*/GeoXMLStrings.TEMPH))
				{	
					//check the emphasis node's child nodes for content
					NodeList children2=c.getChildNodes();
					for(int k=0; k<children2.getLength(); k++)
						if(children2.item(k) instanceof Text) emphFactor=children2.item(k).getNodeValue();
				}	
			}
				
			//find the corresponding object
			for(int i=0; i<geoObjects.size();i++)
			{
				Object o=geoObjects.get(i);
				if(o instanceof GeoObjectDescription)
				{
					
					GeoObjectDescription desc=(GeoObjectDescription) o;
					if(desc.getName().equals(ref))
					{
						//set the showName-property
						if(showName.equals(new String("true"))) desc.setShowName(true);
						else desc.setShowName(false);
						//set the emphasis factor
						desc.setEmphFactor(Integer.parseInt(emphFactor));
						//create color array
						double[] colorArray=new double[nrOfColorNodes];
						String[] array=(String[]) colors.toArray(new String[0]);
						for(int j=0; j<array.length; j++)
							colorArray[j]=Double.parseDouble(array[j]);
						desc.setColor(colorArray);
					}
				}
			}
		}
	}
	
	/**
	 * This method parses a geometric object node.
	 * @param node The abovementioned node.
	 */
	private void parseObjectNode(Node node)
	{
		ArrayList defObjects=new ArrayList();
		ArrayList variables=new ArrayList();
		
		//the given node has to be an element node
		if(!(node instanceof Element)) return;
		Element object=(Element) node;
		
		//get simple strings from attributes
		String id=object.getAttribute(GeoXMLStrings.AID);
		String name=object.getAttribute(GeoXMLStrings.ANAME);
		String type=object.getLocalName(); 
		
		String configuration=object.getAttribute(GeoXMLStrings.AALGORITHM);
		//get the child nodes
		NodeList children=object.getChildNodes();
		//iterate over the children and find param tags
		for(int i=0; i<children.getLength(); i++ )
		{
			//find out if it's a variable or directly defining object
			Node childnode=children.item(i);
			if(childnode instanceof Element)
			{				
				Element child = (Element) childnode;				
				if(child.getTagName().equals(GeoXMLStrings.TPARAM))
				{
					String ref=child.getAttribute(GeoXMLStrings.AREF);
					//search for objects with matching ID
					Node parent=document.getElementsByTagName(GeoXMLStrings.TOBJECTS).item(0);
					NodeList objects = parent.getChildNodes(); 
					for(int j=0; j<objects.getLength(); j++)
					{
						Element param = (Element)objects.item(j);
						if(param.getAttribute(GeoXMLStrings.AID).equals(ref))
							{
								defObjects.add(param.getAttribute(GeoXMLStrings.AID));
							}
					}
					//search for variables with matching ID
					objects = document.getElementsByTagName(GeoXMLStrings.TVARIABLE);
					for(int j=0; j<objects.getLength(); j++)
					{
						Element param = (Element) objects.item(j);
						if(param.getAttribute(GeoXMLStrings.AID).equals(ref))
						{
							//get children and check for a text node (the first children may be comments)
							NodeList pChildNodes = param.getChildNodes();
							for(int k=0; k<pChildNodes.getLength(); k++)
							{
								Node pChildNode = pChildNodes.item(k);
								if(pChildNode instanceof Text)
								variables.add(pChildNode.getNodeValue());
							}
						}
					}
				}
			}
		}
	
		//create Arrays from ArrayLists
		String[] definingObjects = (String[]) defObjects.toArray(new String[0]);
		double[] varList = new double[variables.size()];
		for(int i=0; i<varList.length; i++)
			varList[i]=new Double(((String)variables.get(i))).doubleValue();
		
		//create object description and add it to the geometric objects list
		GeoObjectDescription o = new GeoObjectDescription(id, name, type, configuration, definingObjects, varList);
		geoObjects.add(o);
	
	}
	
	/**
	* Find the named subnode in a node's sublist.
	* <li>Ignores comments and processing instructions.
	* <li>Ignores TEXT nodes (likely to exist and contain
	* ignorable whitespace, if not validating.
	* <li>Ignores CDATA nodes and EntityRef nodes.
	* <li>Examines element nodes to find one with
	* the specified name.
	* </ul>
	* @param name the tag name for the element to find
	* @param node the element node to start searching from
	* @return the Node found
	*/
	private Node findSubNode(String name, Node node) 
	{

		if (node.getNodeType() != Node.ELEMENT_NODE) 
		{
			System.err.println("Error: Search node not of element type");
		}
		if (!node.hasChildNodes()) return null;
		NodeList list = node.getChildNodes();
		for (int i=0; i < list.getLength(); i++) 
		{
			Node subnode = list.item(i);
			if (subnode.getNodeType() == Node.ELEMENT_NODE) 
			{
				if (subnode.getNodeName().equals(name)) return subnode;
			}
		}
		return null;
	}
	
	/**
	 * Returns a DOM from the given file.
	 * 
	 */
	private Document getDOMFromFile(String file)
	{
		//create parser and parse file
		try 
		{
			Document document = builder.parse( new File(file) );
			return document;
		} 
		catch (SAXParseException spe) 
		{
			//error while parsing
			System.out.println ("Error while parsing file");
			System.out.println(" " + spe.getMessage() );	
			return null;
		}
		catch (SAXException se) 
		{
			//error while parsing
			System.err.println ("Error while parsing file");
			System.err.println(" " + se.getMessage() );	
			return null;
		}
		catch (IOException ie) 
		{
			//couldn't open file
			System.err.println ("Error while trying to open file");
			System.err.println(" " + ie.getMessage() );	
			return null;
		}
	}
	
	/**
	 * Creates a new DocumentBuilder.
	 *
	 */
	private DocumentBuilder getDocumentBuilder()
	{
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try 
		{
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder;
		}
		catch (ParserConfigurationException pce) 
		{
			//Parser with specified options can't be built
			System.err.println("XML Parser can't be created!!!");
			return null;
		}
	
	}
		
}


