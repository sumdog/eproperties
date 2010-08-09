package net.jmatrix.eproperties.utils;

import java.net.URL;

import org.apache.commons.logging.*;

import net.jmatrix.eproperties.EProperties;
import net.jmatrix.eproperties.include.URLPropertiesLoader;

public class ClasspathURLUtil {
   public static Log log=LogFactory.getLog(ClasspathURLUtil.class);
   
   /** 
    * Converts a classpath:/ style URL to a real file/jar URL like:
    * 
    * jar:file:/home/bemo/svnroot/eproperties/jars/testproperties.jar!/testjar.properties
    * Or: 
    * file:/bla
    * 
    * */
   public static final String convertClasspathURL(String surl) {
      String lcurl=surl.toLowerCase();
      
      if (lcurl.startsWith("classpath:/")) {
         log.debug ("Found classpath URL: "+surl);
         // First strip off the stuff that I created - 'classpath://'
         String resourcepath=surl.substring("classpath:/".length());
         log.debug ("Resource path: "+resourcepath);
         
         //URL localurl=ClasspathURLUtil.class.
         
         URL sysurl=ClassLoader.getSystemResource(resourcepath);
         log.debug ("System URL: "+sysurl);
         
         if (sysurl == null) {
            throw new RuntimeException("Cannot find resource '"+resourcepath+"' in classpath for URL "+surl);
         }
         log.debug (surl+" -> "+sysurl.toExternalForm());
         surl=sysurl.toExternalForm(); // this will start with either file:// or jar:file:/
      }
      
      return surl;
   }
}
