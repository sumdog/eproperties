package test.net.jmatrix.eproperties;


import java.net.URL;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class TestLoadFromClasspathJar {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testLoadFromClasspathJar() throws Exception {
      URL local=EProperties.class.getClassLoader().getResource("testjar.properties");
      System.out.println ("LocalURL: "+local);
      
      URL url=ClassLoader.getSystemResource("testjar.properties");
      System.out.println ("URL:      "+url);
      
      EProperties props=new EProperties();
      
      props.load(url);
      
      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      
      System.out.println ("========================================");
   }
   
   @Test
   public void testClasspathURLFromJar() throws Exception {
      EProperties props=new EProperties();
      
      props.load("classpath:/testjar.properties");
      
      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      System.out.println ("========================================");
   }
   
   @Test
   public void testClasspathURLFromFile() throws Exception {
      EProperties props=new EProperties();
      
      props.load("classpath:/classpath.relative.include.properties");
      
      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      System.out.println ("========================================");
   }
   
   @Test
   public void testRelativeIncludeFromJar() throws Exception {
      EProperties props=new EProperties();
      
      props.load("classpath:/classpath.relative.include.properties");
      
      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      System.out.println ("========================================");
   }
   
   @Test
   public void testClasspathIncludeFromFile() throws Exception {
      URL url=this.getClass().getResource("TestLoadFromClasspathJar-include.properties");
      
      EProperties props=new EProperties();
      
      props.load(url);
      
      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      System.out.println ("========================================");
   }
}
