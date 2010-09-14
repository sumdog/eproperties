package test.net.jmatrix.eproperties;


import java.io.*;
import java.util.logging.*;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class TestHTTPS_SVN {

   @Before
   public void setUp() throws Exception {
    System.out.println ("Setting log level to trace for parser.");
    
    Logger.getLogger("net.jmatrix").setLevel(Level.FINER);
    Handler handlers[]=Logger.getLogger("").getHandlers();
    for (Handler handler:handlers) {
       handler.setLevel(Level.FINEST);
    }
   }
   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testLoadFromHTTPS() throws IOException {
      EProperties p=new EProperties();
      
      String url="https://junit:tester@svn.jmatrix.net/unittest/simple.properties";
      
      p.load(url);
      
      System.out.println (p.list());
   }
}
