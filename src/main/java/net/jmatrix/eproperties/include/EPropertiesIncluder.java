package net.jmatrix.eproperties.include;

import java.util.*;

import net.jmatrix.eproperties.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/** 
 * Processes include URLs for the parser.  This contains a pluggable
 * loader implementation for various URL formats.  
 */
public class EPropertiesIncluder {
   static Logger log= LoggerFactory.getLogger(EPropertiesIncluder.class);
   List<PropertiesLoader> loaders=new ArrayList<PropertiesLoader>();
   
   PropertiesLoader urlPropsLoader=null;
   
   public static boolean debug=false;
   
   public EPropertiesIncluder() {
      urlPropsLoader=new URLPropertiesLoader(); 
      
      registerLoader(urlPropsLoader);
      //registerLoader(new SystemPropertiesLoader());
      registerLoader(new MethodPropertiesLoader());
      
      
   }
   
   /** */
   public void registerLoader(PropertiesLoader upl) {
      log.debug("Registering loader "+upl.getClass().getName());
      loaders.add(upl);
   }
   
   /**
    * Processes an include, typically on behalf of the parser.
    */
   public Object processInclude(EProperties parent, String url) {
      // Include URL format and options are the following:
      // [prefix://]path;[options]
      // if [prefix://] is omitted, then it is assumed that this
      // is a file based inclusion.  (this would be the most 
      // common).  If the file begins with a '/', then its path
      // is assumed to be absolute.  Otherwise, it is assumed
      // to be relative to its parent.  
      // file:// URLs can also be used. 
      
      
      // the URL string will come in as [include.properties]
      url=url.trim();
      if (url.startsWith("["))
         url=url.substring(1, url.length());
      if (url.endsWith("]"))
         url=url.substring(0, url.length()-1);
      
      // Here, we handle the potential for tokenization of the inclusion 
      // URL.  If the token cannot be found, this will throw a 
      // InvalidIndirectionException()
      // this is new as of 16 feb 2009.
      if (SubstitutionProcessor.containsTokens(url))
      url=SubstitutionProcessor.processSubstitution(url, parent);

      // if it still contains tokens, that's an error (substitution failed).
      if (SubstitutionProcessor.containsTokens(url)) {
         log.error("Warning: Include URL '"+url+"' defined "+
                   "in terms of un-resolvable substitution.");
      }
      
      // process options
      EProperties options=parseOptionsFromIncludeURL(url);
      if (options.size() > 0) {
         // trim the options off the end of that url
         url=url.substring(0, url.indexOf("|"));
      }
      
      
      int size=loaders.size();
      for (int i=0; i<size; i++) {
         PropertiesLoader upl=loaders.get(i);
         if (upl.acceptsURL(url)) {
            log.debug("Processing include with "+
                 upl.getClass().getName());
            
            Object result=upl.loadProperties(parent, url, options);
            //EProperties props=upl.loadProperties(parent, url, options);
            
            if (result instanceof EProperties) {
               ((EProperties)result).setIncludedURL(url);
            }
            return result;
         }
      }
      log.debug("Hmmm... No PropertiesLoader accepts url '"+url+"'");
      log.debug ("       Assuming URLPropertiesLoader.");
      // if no one else claims it, assume that this URL does not 
      // start with foo://, and assume that it is a relative URL
      //EProperties props=urlPropsLoader.loadProperties(parent, url, options);
      //props.setIncludedURL(url);
      
      Object result=urlPropsLoader.loadProperties(parent, url, options);
      
      if (result instanceof EProperties) {
         ((EProperties)result).setIncludedURL(url);
      }
      return result;
   }
   
   /** 
    * Here we are supporting an include options syntax.  All possible
    * options should have sensible default values in code.  It is up to 
    * each include processor to respect or ignore these options.
    * 
    * The 2 key options as this point are (defaults in bold):
    *    parse=[TRUE|false]
    *    failonerrror=[TRUE|false]
    *  
    * The syntax is simple: 
    * 
    * prebill.sql=[sql/prebill.sql|parse=false,failonerror=true]
    * 
    * Pipe is the delimiter, followed by a csv list of key=value options.
    * 
    * This could also be used to facilitate JDBC based includes - 
    * with the URL being something like:  
    *   [jdbc://.......|user=foo,pass=bar,keycol=key,valcol=value]
    */
   EProperties parseOptionsFromIncludeURL(String url) {
      // this can and very often will be an empty set of options.
      
      EProperties options=new EProperties();
      
      int pipeindex=url.indexOf('|');
      if (pipeindex > 0) {
         options.put("original.url", url);
         String opString=url.substring(pipeindex+1);
         
         StringTokenizer st=new StringTokenizer(opString, ",", false);
         
         while(st.hasMoreElements()) {
            String kvp=st.nextToken();
            
            int equalsindex=kvp.indexOf("=");
            if (equalsindex == -1) 
               options.putBoolean(kvp, true);
            else {
               String key=kvp.substring(0, equalsindex);
               String value=kvp.substring(equalsindex+1);
               options.put(key, value);
            }
         }
         log.info("Include options: \n"+options.list(2));
      }
      
      return options;
   }
}
