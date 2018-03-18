/*****************************MODIFICATION HISTORY/GESCHICHTE VON VERÄNDERUNGEN***********
Bei veränderungen, bitte Datum, Name des Arbeiters sowie vorgenommene veränderungen angeben.

12 Juni 2003	Marin Todorov
	Stable status, interactives Testing


********************************************************************************************/

package geofuchs.control;


import java.awt.*;

/** defaults für das Projekt; könnten später von einen Options Dialog leicht modifiziert werden*/
public class GDefaults{

	public static double 		pointRadius = 5;
	public static final Color	pointColor=new Color(0,0,250);
	public static final Color	lineColor=new Color(250,0,0);
	public static final Color	circColor=new Color(50,150,0);
	public static final Color	midPointColor=new Color(0,0,130);
	public static final Color	orthoLineColor=new Color(170,0,0) ;
	public static final Color	parLineColor=new Color(170,0,0) ;
	public static final Color	floaterColor=new Color(0,0,200) ;
	public static final Color	intersectionColor=new Color(0,0,130);
	public static final Color	backColor=new Color(255,253,250) ;
	public static final int 	compositeDefaultSize=100;

	public static final int 	gridSpacing = 10;
	public static final int 	gridPan		= 10;
	public static final int 	maxFracDigits	=2;
	

	public static final double 	zoomfactor = 1;		//the original zoomfactor
	public static final double 	zoomstep = 0.1;		//the amount of zoomchange when the buttons are pressed

}
