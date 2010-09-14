package test.net.jmatrix.eproperties;


import java.util.*;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class TestPropertiesInterface {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testStringKeyPropertyNames() {
      EProperties p=new EProperties();
      
      p.put("hello", "world");
      
      Enumeration keys=p.propertyNames();
      
      while (keys.hasMoreElements()) {
         String key=(String)keys.nextElement();
         System.out.println ("key="+key);
      }
      
   }
   
   @Test
   public void testPropertiesPutAll() {
      EProperties ep=new EProperties();
      
      ep.put("hello", "world");
      
      Properties p=new Properties();
      p.putAll(ep);
      
      Enumeration keys=p.propertyNames();
      while (keys.hasMoreElements()) {
         String key=(String)keys.nextElement();
         System.out.println ("key="+key);
      }
   }
   
   @Test
   public void testPropertiesDefaults() {
      EProperties ep=new EProperties();
      
      ep.put("hello", "world");
      
      Properties p=new Properties(ep);
      //p.putAll(ep);
      
      Enumeration keys=p.propertyNames();
      while (keys.hasMoreElements()) {
         String key=(String)keys.nextElement();
         System.out.println ("key="+key);
      }
   }
   
   @Test
   public void testPropertiesPutAllFlatten() {
      EProperties ep=new EProperties();
      
      ep.put("hello->key", "world");
      
      Properties p=new Properties();
      p.putAll(ep.flatten());
      
      Enumeration keys=p.propertyNames();
      while (keys.hasMoreElements()) {
         String key=(String)keys.nextElement();
         System.out.println ("key="+key);
      }
   }
}
