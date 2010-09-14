package test.net.jmatrix.eproperties.features;


import java.io.*;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

import test.net.jmatrix.eproperties.TestUtils;

public class TestSourceModified {
   
   File tempPropsFile=null;

   @Before
   public void setUp() throws Exception {
      TestUtils.enableDebug();
      tempPropsFile=File.createTempFile("testSourctModified.", ".eproperties");
      //tempPropsFile.deleteOnExit();
      
      System.out.println ("Created temp file: "+tempPropsFile.getAbsolutePath());
      
      StringBuilder sprops=new StringBuilder();
      sprops.append("foo=bar\n");

      TestUtils.write(tempPropsFile, sprops.toString());
   }
   
   @After
   public void tearDown() throws Exception {
   }

   
   @Test
   public void testNotModified() throws Exception {
      EProperties props=new EProperties();
      props.load(tempPropsFile);
      
      System.out.println(props.list());
      
      Assert.assertTrue("Props loaded.", props.getString("foo").equals("bar"));
      
      Assert.assertFalse("Props not modified", props.isSourceModified());
   }
   
   @Test
   public void testModified() throws Exception {
      EProperties props=new EProperties();
      props.load(tempPropsFile);
      
      System.out.println(props.list());
      Thread.sleep(2000);
      TestUtils.write(tempPropsFile, "foo=baz");
      
      Assert.assertTrue("Props are modified", props.isSourceModified());
   }
}
