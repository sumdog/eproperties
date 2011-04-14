package net.jmatrix.util;


import java.util.*;
import java.lang.reflect.*;

import net.jmatrix.eproperties.EProperties;

import java.util.logging.*;
import static java.util.logging.Level.*;


/**
 * The InstanceFactory builds instances of classes based on 
 * properties files.  These can then be cast to appropriate types
 * as necessary.
 */
public final class InstanceFactory 
{
   static final Logger log=Logger.getLogger(InstanceFactory.class.getName());
   
   /** */
   static Map<String, Constructor> constructorCache=new HashMap();

   /** 
    * Builds a new instance of a class based on an EProperties file.
    * The properties MUST CONTAIN: 
    *  - 'class' which is the fully qualified (with package) class name 
    *    of the implementing class.
    * 
    * The class defined above MUST be available in the current JVM
    * CLASSPATH.  Ie, Class.forName(class) must resolve.
    * 
    * The Class defined above MUST have at least 1 constructor that 
    * take an EProperties object as a parameter.  These properties
    * are passed to the implementing class's constructor.
    *
    * Users of this class will always cast the resulting object 
    * to the appropriate type.
    *
    * @param EProperties The properties object which defines the class
    * and any parameters for its construction.
    * @return An Object, which is an instance of the class requested.
    */
   public static Object buildInstance(EProperties props) 
      throws InstantiationException, ClassNotFoundException, 
      NoSuchMethodException, IllegalAccessException, 
      InvocationTargetException {
      // This method makes 3 assumptions:
      // 1) The properies file contains a string property 
      //    called 'class' - which is the class name of the 
      //    implmementation class
      // 2) The class specified in 'class' exists in the current 
      //    classpath.
      // 3) The class has a public constructor that takes an 
      //    EProperties object.

      Object target=null;
      String className=props.getString("class");

      log.log(FINE, "InstanceFactory: building instance of "+className);
      if (className == null) {
         throw new RuntimeException("InstanceFactory: ERROR.  Property "+
                                    "'class' is required.  Inbound "+
                                    "properties does not contain 'class'");
      }

      if (className == null) {
         throw new 
         IllegalArgumentException("InstanceFactoryr: Required property "+
                                  "class which defines the "+
                                  "implementation class is null.  Props must "+
                                  "contain 'class' property.");
      }
      Constructor constructor=getConstructor(className);

      target=constructor.newInstance(props);

      return target;
   }
   
   /** */
   public static Object runtimeBuildInstance(EProperties p) {
      try {
         return buildInstance(p);
      } catch (Exception ex) {
         throw new RuntimeException("Error building instance!", ex);
      }
   }

   /** */
   private static final Constructor getConstructor(String className) 
      throws InstantiationException, ClassNotFoundException, 
      NoSuchMethodException {
      Constructor constructor=constructorCache.get(className);
      
      if (constructor == null) {
         log.log(FINE, "InstanceFactory: caching constructor for '"+className+"'");
         constructor=buildConstructor(className);
      }
      return constructor;
   }

   /** */
   private static final Constructor buildConstructor(String className) 
      throws InstantiationException, ClassNotFoundException, 
      NoSuchMethodException {
      Class clazz=Class.forName(className);
         
      // I apologize in advance for this obscure syntax.  Bemo.
      Constructor constructor=clazz.getConstructor(new Class[]{EProperties.class});
      constructorCache.put(className, constructor);
      return constructor;
   }
}