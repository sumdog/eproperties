package test.net.jmatrix.eproperties;

import java.io.*;
import java.net.URL;
import java.util.List;

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
   
   @Test 
   public void testListFromString() throws IOException {
      String props=
         "stringlist=a,b,c,d \n"+
         "sub=3,4\n"+
         "stringlistwithsub=1,2,${sub}\n";
      EProperties p=new EProperties();
      p.load(new StringBufferInputStream(props));
      
      List<String> stringlist=p.getList("stringlist");
      List<String> stringlistwithsub=p.getList("stringlistwithsub");
      
      
      System.out.println ("stringlist: "+stringlist);
      System.out.println ("stringlistwithsub: "+stringlistwithsub);
      Assert.assertTrue(stringlist.size()==4);
      Assert.assertTrue(stringlistwithsub.size()==4);
   }
   
   @Test
   public void testPutWithComplexKey1() throws IOException {
      
      EProperties p=new EProperties();
      p.put("nested->nested->foo", "bar");
      
      System.out.println(p.list());
      
      String val=p.getString("nested->nested->foo");
      Assert.assertTrue(val != null);
      Assert.assertTrue(val.equals("bar"));
   }
   
   @Test 
   public void testPutWithComplexKey2() throws IOException {
      String props=
         "nested={\n"+
         "   foo=bar\n"+
         "}\n";
      EProperties p=new EProperties();
      p.load(new StringBufferInputStream(props));
      
      System.out.println ("Before complex put: \n"+p.list());
      p.put("nested->foo", "baz");
      System.out.println ("After complex put: \n"+p.list());
      
      String newval=p.getString("nested->foo");
      Assert.assertNotNull(newval);
      Assert.assertTrue(newval.equals("baz"));
   }
   
   @Test 
   public void testFlatten() throws IOException {
      String props=
         "flatv1=X\n"+
         "nested={\n"+
         "   foo=bar\n"+
         "   nest2={\n"+
         "      baz=444\n"+
         "      biz=${flatv1}\n"+
         "   }\n"+
         "   list=1, 2, 3, 4\n"+
         "}\n"+
         "flatv2=Y\n";
      EProperties p=new EProperties();
      p.load(new StringBufferInputStream(props));
      
      EProperties flat=p.flatten();
      
      System.out.println ("Flat EProperties: \n"+flat.list());
      
      Assert.assertNotNull(flat);
      Assert.assertTrue(flat.size() == 6);
   }
}
