package test.net.jmatrix.eproperties.deepmerge;


import java.util.List;

import net.jmatrix.eproperties.*;

import org.junit.*;

public class TestDeepMerge {


   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testDeepMerge() throws Exception {
      EProperties props=new EProperties();
      
      props.load(this.getClass().getResource("DeepMergeTest.properties"));
      
      
      System.out.println ("---list---");
      props.list(System.out, true);
      System.out.println ("---list---");
      
      String nestedKey1=props.getString("nested->key1");
      String nestedKey2=props.getString("nested->key2");
      
      Assert.assertNotNull("Nested key1 is null.", nestedKey1);
      Assert.assertNotNull("Nested key2 is null.", nestedKey2);
      
      Assert.assertTrue("Nested Key1 overwritten by deepMerge", nestedKey1.equals("Foo"));
      Assert.assertTrue("Nested Key2 remains after deepMerge", nestedKey2.equals("value2"));
      
      Assert.assertNotNull("Nested2 propertly merged.", props.get("nested2"));
   }
}
