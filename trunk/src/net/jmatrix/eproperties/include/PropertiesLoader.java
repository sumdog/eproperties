package net.jmatrix.eproperties.include;

import net.jmatrix.eproperties.EProperties;

/**
 * When include syntax is encountered, the URLPropertiesLoaders are 
 * consulted to load said properties.
 */
public interface PropertiesLoader {
   /** */
   public boolean acceptsURL(String s);
   
   /** This method returns Object - but that object MUST be either
    * a String or an EProperties object.  */
   public Object loadProperties(EProperties parent, String url, 
         EProperties options);
}
