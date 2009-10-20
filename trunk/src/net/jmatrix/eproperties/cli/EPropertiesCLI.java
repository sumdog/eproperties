package net.jmatrix.eproperties.cli;

import java.io.File;
import java.net.URL;
import java.util.logging.*;

import net.jmatrix.eproperties.EProperties;
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
         System.out.println ("Setting root logger to debug.");
         Logger logger=Logger.getLogger("");
         logger.setLevel(Level.FINEST);
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
      
      props.load(url);
      
      System.out.println ("---- listing ----");
      props.list(System.out);
      System.out.println ("---- end listing ----");
   }
}
