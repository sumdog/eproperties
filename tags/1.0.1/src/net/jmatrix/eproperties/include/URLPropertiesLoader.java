package net.jmatrix.eproperties.include;

import java.io.*;
import java.net.URL;

import net.jmatrix.eproperties.EProperties;
import net.jmatrix.eproperties.cache.CacheManager;
import net.jmatrix.eproperties.utils.StreamUtil;

import org.apache.commons.logging.*;

/**
 * Loads properties from a file.  
 */
public class URLPropertiesLoader implements PropertiesLoader {
   static Log log=LogFactory.getLog(URLPropertiesLoader.class);
   static CacheManager cacheManager=CacheManager.getInstance();
   
   /** */
   public boolean acceptsURL(String s) {
      if (s == null)
         return false;
      
      if (s.toLowerCase().startsWith("file://") ||
          s.toLowerCase().startsWith("http://") ||
          s.toLowerCase().startsWith("https://")) {
         return true;
      }
      return false;
   }

   /** */
   public Object loadProperties(EProperties parent, String surl, 
         EProperties options) {
      
      //EProperties props=new EProperties();
      Object result=null;
            
      if (surl.startsWith("http://") || surl.startsWith("https://") ||
          surl.startsWith("file://")) {
         log.debug("Loading as absolute URL: "+surl); 
         result=loadFromURL(surl, options, parent);
      } else if (surl.startsWith("/")) { 
         log.debug("Loading as absolute file: "+surl);
         // ok, just try to open it as a local file.
         File f=new File(surl);
         if (f.exists()) {
            try {
               result=loadFromURL(f.toURI().toURL().toExternalForm(), options, parent);
               //props.load(f.toURI().toURL());
            } catch (IOException ex) {
               if (options.getBoolean("failonerror", true)) {
                  throw new 
                  RuntimeException("Error loading included properties from '"+
                        surl+"'", ex);
               } else {
                  log.warn("Cannot find file '"+surl+"', failonerror=false");
               }
            }
         } else {
            // the file does not exist.  
            if (options.getBoolean("failonerror", true)) {
               throw new RuntimeException("Cannot find file '"+surl+"' to include.");
            } else {
               log.warn("Cannot find file '"+surl+"' to include. failonerror=false");
            }
         }
      } else {
         // ok, the URL does not start with [http:// | https:// | file://], so
         // let's assume it is a relative reference to the most recent parent.
         // This means that if the parent is:
         //   file://../config/config.properties, and the include url is 'include.properties'
         // then the include should be processed from that relative location:
         //   file://../config/include.properties
         //
         // Similarly, if the parent URL is:
         //    http://jmatrix.net/properties/public/foo.properties 
         // and the url is:
         //    '../bar/baz.properties'
         // then the include should be
         //    http://jmatrix.net/properties/public/../bar/baz.properties
         
         URL parentURL=parent.findSourceURL();
         
         log.debug("Loading as relative URL, parent URL is '"+parentURL+"'");
         
         if (parentURL == null) {
            // ok, just try to open it as a local file.
            File f=new File(surl);
            if (f.exists()) {
               try {
                  result=loadFromURL(f.toURI().toURL().toExternalForm(), options, parent);
                  //props.load(f.toURI().toURL());
               } catch (IOException ex) {
                  if (options.getBoolean("failonerror", true)) {
                     throw new 
                     RuntimeException("Error loading included properties from '"+
                           surl+"'", ex);
                  } else {
                     log.warn("Cannot find file '"+surl+"', failonerror=false");
                  }
               }
            } else {
               // the file does not exist.  
               if (options.getBoolean("failonerror", true)) {
                  throw new RuntimeException("Cannot find file '"+surl+"' to include.");
               } else {
                  log.warn("Cannot find file '"+surl+"' to include. failonerror=false");
               }
            }
         } else {
            // create relative URL string
            String workingSURL=parentURL.toString();
            workingSURL=workingSURL.substring(0, workingSURL.lastIndexOf("/")+1)+surl;
            log.debug("Loading w/ rel URL: '"+workingSURL+"'");
            result=loadFromURL(workingSURL, options, parent);
//            try {
//               URL url=new URL(workingSURL);
//               
//               props.load(url);
//            } catch (IOException ex) {
//               throw new 
//               RuntimeException("Error loading included properties from '"+surl+"'", ex);
//            }
         }
      }
      
      return result;
   }

   
   /** */
   private Object loadFromURL(String surl, EProperties options, EProperties parent) {
      Object result=null;
      try {
         URL url=new URL(surl);
         if (options.getBoolean("parse", true)) {
            EProperties props=new EProperties(parent);
            props.load(url);
            result=props;
         } else {
            // load it as a string
            //InputStream is=url.openStream();
            InputStream is=cacheManager.getInputStream(url);
            try {
               String value=StreamUtil.readToString(is);
               result=value;
            } finally {
               if (is != null)
                  is.close();
            }
         }
      } catch (IOException ex) {
         
         if (options.getBoolean("failonerror", true)) {
            throw new 
            RuntimeException("Error loading included properties from '"+surl+"'", ex);
         } else {
            log.info("Cannot load properties from  '"+surl+"', failonerror=false");
         }
      }
      return result;
   }
}
