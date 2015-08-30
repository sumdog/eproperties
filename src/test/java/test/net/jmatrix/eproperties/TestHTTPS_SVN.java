package test.net.jmatrix.eproperties;


import java.io.*;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class TestHTTPS_SVN {

   @Before
   public void setUp() throws Exception {

   }
   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testLoadFromHTTPS() throws IOException {
      EProperties p=new EProperties();
      
      String url="https://raw.githubusercontent.com/sumdog/eproperties/master/config/simple.properties";
      
      p.load(url);
      
      System.out.println (p.list());
   }
}
