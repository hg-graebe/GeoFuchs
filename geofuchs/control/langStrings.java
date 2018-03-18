/* $Id: langStrings.java,v 1.2 2005/08/21 20:29:56 hg-graebe Exp $ */
package geofuchs.control;

import java.awt.event.*;


/** This class contains mainly static string constants which are
        used in dialogues etc.  */

public class langStrings {
        
    /* menu items */
    // File Menu
    public static final String mi_file = "Datei";       
    public static final int    mim_file = KeyEvent.VK_D;        
    public static final String mi_newConstruction = "Neue Konstruktion";        
    public static final int    mim_newConstruction = KeyEvent.VK_N;
    public static final String mi_saveConstruction = "Konstruktion speichern";  
    public static final int    mim_saveConstruction = KeyEvent.VK_S;
    public static final String mi_saveConstructionAs = "Konstruktion speichern unter";
    public static final int    mim_saveConstructionAs = KeyEvent.VK_A;
    public static final String mi_loadConstruction = "Konstruktion laden";      
    public static final int    mim_loadConstruction = KeyEvent.VK_S;
    public static final String mi_newChildFrame = "Neues Zeichenfenster";       
    public static final int    mim_newChildFrame = KeyEvent.VK_Z;
    public static final String mi_closeConstruction = "Aktuelle Konstruktion schließen";
    public static final int    mim_closeConstruction = KeyEvent.VK_K;
    public static final String mi_closeActiveWindow = "Aktuelles Zeichenfenster schließen";
    public static final int    mim_closeActiveWindow = KeyEvent.VK_Z;
    public static final String mi_exit = "Beenden";    
    public static final int    mim_exit = KeyEvent.VK_E;

    // Settings Menu
    public static final String mi_settings = "Einstellungen";  
    public static final int    mim_settings = KeyEvent.VK_E;

    // Edit Menu
    public static final String mi_edit = "Bearbeiten";  
    public static final int    mim_edit = KeyEvent.VK_A;
    public static final String mi_undo = "Undo";        
    public static final int    mim_undo = KeyEvent.VK_U;
    public static final String mi_redo = "Redo";        
    public static final int    mim_redo = KeyEvent.VK_R;

    // GeoTools Menu
    public static final String mi_tools = "Werkzeuge";  
    public static final int    mim_tools = KeyEvent.VK_W;
    public static final String mi_calcMidpoint = "Mittelpunkt bestimmen";       
    public static final int mim_calcMidpoint = KeyEvent.VK_M;
    public static final String mi_calcIntersection = "Schnittpunkt bestimmen";  
    public static final int mim_deleteElement = KeyEvent.VK_E;
    public static final String mi_insertFloater = "Gleiter";    
    public static final int     mim_insertFloater=KeyEvent.VK_G;
    public static final String mi_senk = "Senkrechte";  
    public static final int mim_senk=KeyEvent.VK_S;     
    public static final String mi_par = "Parallele";    
    public static final int mim_par=KeyEvent.VK_P;
    public static final String mi_insert = "Einfügen";  
    public static final int    mim_insert = KeyEvent.VK_E;
    public static final String mi_insertPoint = "freier Punkt";        
    public static final int    mim_insertPoint = KeyEvent.VK_P;
    public static final String mi_insertLine = "Gerade durch zwei Punkte";        
    public static final int    mim_insertLine = KeyEvent.VK_L;
    public static final String mi_circ= "Kreis";        
    public static final int    mim_circ = KeyEvent.VK_K;

    // View Menu
    public static final String mi_view = "Zeichenfenster";      
    public static final int    mim_view = KeyEvent.VK_Z;
    public static final String mi_switchCoords = "Koordinatenachsen ein-/ausblenden";   
    public static final int    mim_switchCoords = KeyEvent.VK_K;
    public static final String mi_switchGrid = "Gitter ein-/ausblenden";        
    public static final int    mim_switchGrid  = KeyEvent.VK_G;
    public static final String mi_zoomIn = "Vergrößern";        
    public static final int    mim_zoomIn = KeyEvent.VK_I;
    public static final String mi_zoomOut = "Verkleinern";      
    public static final int    mim_zoomOut = KeyEvent.VK_O;
    public static final String mi_resetView = "Default-Werte";  
    public static final int    mim_resetView  = KeyEvent.VK_R;

    // Info Menu
    public static final String mi_options= "Info";      
    public static final int    mim_options=  KeyEvent.VK_I;
    public static final String mi_about = "Über Geofuchs";
    public static final int mim_about = KeyEvent.VK_B;

    // Change Settings Menu
    public static final String mi_change = "Ändern";
    public static final int mim_change = KeyEvent.VK_C;
    public static final String mi_hide = "Verstecken";
    public static final String mi_normalsize = "Normale Größe";
    public static final String mi_emph1 = "Hervorheben";
    public static final String mi_emph2 = "Stark hervorheben";  
    public static final String mi_setname = "Umbenennen";
    public static final String mi_showname = "Namen anzeigen";
    public static final String mi_dontshowname = "Namen nicht anzeigen"; 
    public static final String mi_color = "Farbe auswählen";
    public static final String mi_setcolor = "Objektfarbe ändern";      
    public static final String mi_antialias = "Kantenglättung";

    //View Caption
    public static final String c_Construction = "Konstruktion"; 
    public static final String c_View = "Ansicht";      
    public static final String c_activeConstruction = "Aktive Konstruktion: ";
    public static final String c_none = "Keine";
    public static final String c_ChildWindow = "Zeichenfenster";
    public static final String c_currentTool = "Aktives Werkzeug: ";

    //Dialogues
    public static final String q_AskCloseConstruction = "Möchten Sie die folgende Konstruktion schliessen: ";
    public static final String q_AskCloseLastWindow = "Dies ist das letzte Fenster der Konstruktion. Möchten Sie die folgende Konstruktion schliessen: ";
    public static final String q_whichobject = "Welches Objekt meinen Sie?";
    public static final String q_chooseobject = "Bitte Objekt auswählen";
    public static final String q_info ="Geofuchs, Version 0.99: Auf Basis eines in dem SWT-Praktikum an der \n Universität Leipzig 2003 entstandenen Programms\n von A.Aderhold, J.Bachmann, T.Chiacos, \nY.Metzner, M.Todorov, A.Vollmer\n\n\n\n" +
                                                                            "Überarbeitet von A. Stock vom April 2004 bis März 2005. \n Betreung durch Prof. Dr. H.-G. Gräbe, Universität Leipzig.";
    public static final String q_whichconf = "Welchen Algorithmustyp soll das Objekt haben?";
    public static final String q_chooseconf = "Bitte neuen Algorithmustyp wählen";
    public static final String q_choosefile = "Datei auswählen";
    public static final String q_rename = "Umbenennen";
    public static final String q_newname="Bitte den neuen Namen eingeben: ";
    public static final String q_chooseColor="Bitte Farbe auswählen";

    //simples
    public static final String s_yes = "Ja";
    public static final String s_no = "Nein";
    public static final String s_none = "keine";
    public static final String s_of="von";
    public static final String s_and="und";
    public static final String s_on="auf";
    public static final String s_to="zu";
    public static final String s_through="durch";
    public static final String s_with="mit";

    public static final String s_line = "Gerade";
    public static final String s_point = "Punkt";
    public static final String s_midpoint = "Mittelpunkt";
    public static final String s_circle = "Kreis";
    public static final String s_nothing = "Nichts";
    public static final String s_intersection = "Schnittpunkt"; 
    public static final String s_floater="Gleiter";
    public static final String s_paraline="Parallele";
    public static final String s_ortholine="Senkrechte";
    public static final String s_center="Mittelpunkt";
    public static final String s_algorithm="Konfiguration";
    public static final String s_tool="Werkzeug";
    public static final String s_move="Bewege";
    public static final String s_noobject = "Kein Objekt gefunden";
    public static final String s_pointssmaller="Punkte kleiner";
    public static final String s_pointslarger="Punkte größer";
    public static final String s_matchingobjects=" passende Objekte";
    public static final String s_suretoquit="Wirklich beenden?";
}
