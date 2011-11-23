package net.jmatrix.eproperties.cli;

import java.io.File;
import java.net.URL;
import java.util.logging.*;

import net.jmatrix.eproperties.*;
import net.jmatrix.eproperties.parser.EPropertiesParser;
import net.jmatrix.eproperties.utils.JDK14LogConfig;

import org.apache.commons.logging.*;

/**
 * A simple command line interface to the EProperties system. 
 *
 */
public class EPropertiesCLI {
   static Log log=LogFactory.getLog(EPropertiesCLI.class);
   static String usage=
      "EPropertiesCLI [-debug] <Props.URL> \n"+
      "  Props.URL: this is required. It can be a relatve file path or a full http/file URL.";
   
   /** */
   public static void main(String[] args) throws Exception {
      JDK14LogConfig.startup();
      ArgParser ap=new ArgParser(args);
      
      String surl=ap.getLastArg();
      
      if (surl == null) {
         System.out.println (usage);
         System.exit(1);
      }
      
      if (ap.getBooleanArg("-debug")) {
         EProperties.debug=true;
         System.out.println ("Setting root logger to debug.");
         Logger logger=Logger.getLogger("");
         logger.setLevel(Level.ALL);
         
         logger=Logger.getLogger(EProperties.class.getName());
         logger.setLevel(Level.FINE);
         
         logger=Logger.getLogger(SubstitutionProcessor.class.getName());
         logger.setLevel(Level.INFO);
         
         logger=Logger.getLogger(EPropertiesParser.class.getName());
         logger.setLevel(Level.ALL);
      } else {
         Logger logger=Logger.getLogger("");
         logger.setLevel(Level.INFO);
      }
      
      URL url=null;
      
      if (surl.indexOf("://") != -1) {
         System.out.println ("Constructing URL as URL");
         url=new URL(surl);
      } else {
         System.out.println ("Constructing URL as File");
         url=(new File(surl)).toURI().toURL();
      }
      System.out.println ("Loading with "+url);
      
      EProperties props=new EProperties();
      
      System.out.println ("\n********************  BEGIN LOAD  *******************");
      props.load(url);
      System.out.println ("********************   END LOAD  *******************\n");
      
      
      System.out.println ("size: "+props.size());
      System.out.println ("keys: "+props.getKeys().size());
      
//      System.out.println ("override_sub="+props.getString("OVERRIDE_SUB"));
//      System.out.println();
//      
//      System.out.println ("find override_sub="+props.findProperty("OVERRIDE_SUB"));
//      System.out.println();
//      
//      System.out.println ("override="+props.getString("OVERRIDE"));
      
      System.out.println ("---- listing ----");
      if (ap.getBooleanArg("-flatten", false))
         props.flatten(".").list(System.out);
      else
         props.list(System.out);
      System.out.println ("---- end listing ----");
      
      props.superList();
   }
}
