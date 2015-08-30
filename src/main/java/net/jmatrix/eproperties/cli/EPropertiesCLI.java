package net.jmatrix.eproperties.cli;

import java.io.File;
import java.net.URL;

import net.jmatrix.eproperties.*;
import net.jmatrix.eproperties.parser.EPropertiesParser;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;


/**
 * A simple command line interface to the EProperties system. 
 *
 */
public class EPropertiesCLI {
   static Logger log = (Logger)LoggerFactory.getLogger(EPropertiesCLI.class);
   static String usage=
      "EPropertiesCLI [-debug] <Props.URL> \n"+
      "  Props.URL: this is required. It can be a relatve file path or a full http/file URL.";
   
   /** */
   public static void main(String[] args) throws Exception {

      ArgParser ap=new ArgParser(args);
      
      String surl=ap.getLastArg();
      
      if (surl == null) {
         System.err.println(usage);
         System.exit(1);
      }
      
      if (ap.getBooleanArg("-debug")) {
         EProperties.debug=true;
         System.err.println ("Setting root logger to debug.");
         Logger logger=(Logger)LoggerFactory.getLogger("");
         logger.setLevel(Level.ALL);
         
         logger=(Logger)LoggerFactory.getLogger(EProperties.class.getName());
         logger.setLevel(Level.DEBUG);
         
         logger=(Logger)LoggerFactory.getLogger(SubstitutionProcessor.class.getName());
         logger.setLevel(Level.INFO);
         
         logger=(Logger)LoggerFactory.getLogger(EPropertiesParser.class.getName());
         logger.setLevel(Level.ALL);
      } else {
         Logger logger=(Logger)LoggerFactory.getLogger("");
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
      
      System.out.println ("---- listing ----");
      if (ap.getBooleanArg("-flatten", false))
         props.flatten(".").list(System.out);
      else
         props.list(System.out);
      System.out.println ("---- end listing ----");
      
      //props.superList();
   }
}
