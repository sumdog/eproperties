package net.jmatrix.eproperties.include;

import java.lang.reflect.Method;
import java.util.*;

import net.jmatrix.eproperties.EProperties;

import org.apache.commons.logging.*;

/**
 *
 */
public class MethodPropertiesLoader implements PropertiesLoader {
   static Log log=LogFactory.getLog(MethodPropertiesLoader.class);
   
   // FIXME: Add method execution URL handler.  Like this:
   // [method://java.net.InetAddress.getLocalHost().getHostName()]
   // this would return a String rather than a nested properties
   // object.  Which could be difficult.  
   // Could make some implicit assumptions about the method:// processor
   //   - Strings would be single value
   //   - List or String[] would be converted to List<String> using toString?
   //   - Properties or Map or Hashtable would be convered to nested EProperties()
   //   - Object could be converted to an EProperties object by introspecting
   //     for public accessors and/or methods
   //     This could be interesting.  So if a method returned URL, 
   //     we could either use url.toString(), or 
   //     introspect into URL and getFile(), getHost(), getPort(), and getProtocol() would
   //     result in:
   //     foo={
   //        file=(result from getFile())
   //        host=(result from getHost())
   //        # etc, you get the picture.
   //     }
   
   /** */
   public boolean acceptsURL(String s) {
      if (s.toLowerCase().startsWith("method://")) {
         return true;
      }
      return false;
   }

   /** */
   public Object loadProperties(EProperties parent, String url, 
         EProperties options) {
      EProperties props=new EProperties(parent);
      //props.addAll(System.getProperties());
      
      // parse the URL: 
      // strip off method://
      
      url=url.substring("method://".length());
      
      // java.lang.System.getenv()
      
      // parse the classs name and method name.
      String className=url.substring(0, url.lastIndexOf("."));
      String methodName=url.substring(url.lastIndexOf(".")+1);
      
      if (methodName.endsWith("()"))
         methodName=methodName.substring(0, methodName.length()-2);
      
      try {
         Class clazz=Class.forName(className);
         
         Method method=clazz.getMethod(methodName);
         
         Object obj=method.invoke(null);
         
         if (obj == null) {
            log.error("Method include of "+className+"."+methodName+" returned null.");
         }
         
         if (obj instanceof Map) {
            Map map=(Map)obj;
            
            Set keys=map.keySet();
            int count=0;
            for(Object key:keys) {
               count++;
               Object value=map.get(key);
               
               props.put(key.toString(), value.toString());
            }
            
            log.debug("Loaded "+count+" properties with MethodLoader.");
         } else {
            log.error("Don't know how to process method include of object type "+obj.getClass().getName());
         }
      } catch (Exception ex) {
         throw new RuntimeException("Error processing method:// inclusion.", ex);
      }
      
      //if (true)
      //   throw new RuntimeException("Error method:// inclusion not implemented.");
      
      return props;
   }
}
