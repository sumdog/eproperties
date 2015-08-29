package test.net.jmatrix.eproperties;


import java.net.URL;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class TestCompleteSubstitution {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testCompleteSub() throws Exception {
      URL url=this.getClass().getResource("TestCompleteSubstitution.properties");
      
      System.out.println ("URL: "+url);
      
      EProperties props=new EProperties();
      props.load(url);
      
      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      
      String prodHost="prod.jmatrix.net";
      
      Assert.assertTrue("env->host is prod host.", props.getString("env->host").equals(prodHost));
      Assert.assertTrue("host is prod host", props.getString("host").equals(prodHost));
   }
}
