package test.net.jmatrix.eproperties;

import java.io.*;
import java.net.URL;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class EPropertiesTest {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testLoadFromClasspathStream() throws IOException {
      EProperties p=new EProperties();
      
      InputStream is=this.getClass().getResourceAsStream("classpathtest.properties");
      System.out.println ("IS is "+is.getClass().getName());
      try {
         p.load(is);
         Assert.assertTrue("This should have thrown an exception", false);
      } catch (Throwable ex) {
         System.out.println ("Caught exception: "+ex);
         System.out.println ("this is expected.");
         Assert.assertTrue("Exception should be thrown when including via input stream.", ex!=null);
      }
      
//      System.out.println (p.list(2));
//      
//      Assert.assertNotNull(p.getString("foo"));
//      Assert.assertNotNull(p.getProperties("included"));
//      Assert.assertNotNull(p.getProperties("included").get("bar"));
   }
   
   @Test
   public void testLoadFromClasspathURL() throws IOException {
      EProperties p=new EProperties();
      
      URL url=this.getClass().getResource("classpathtest.properties");
      System.out.println ("URL is "+url);
      p.load(url);
      
      System.out.println (p.list(2));
      
      Assert.assertNotNull(p.getString("foo"));
      Assert.assertNotNull(p.getProperties("included"));
      Assert.assertNotNull(p.getProperties("included").get("bar"));
   }
}
