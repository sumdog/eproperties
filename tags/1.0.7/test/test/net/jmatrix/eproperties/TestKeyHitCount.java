package test.net.jmatrix.eproperties;


import java.util.List;

import net.jmatrix.eproperties.*;

import org.junit.*;

public class TestKeyHitCount {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testHitCounts() throws Exception {
      EProperties props=new EProperties();
      
      props.load(this.getClass().getResource("TestKeyHitCount.properties"));
      
      props.get("nested->prop");
      
      System.out.println ("---list---");
      props.list(System.out, true);
      System.out.println ("---list---");
      
      List<Key> keys=props.getKeys();
      for (Key key:keys)
         System.out.println ("   "+key+": "+key.getHitCount());
      
      Key foo=getKey(keys, "foo");
      System.out.println ("foo hits: "+foo.getHitCount());
      Assert.assertTrue("Hit Count on foo is 3", foo.getHitCount()==3);
      
      Key nested=getKey(keys, "nested");
      Assert.assertTrue("Hit Count on nested is 1", nested.getHitCount()==1);
   }
   
   static Key getKey(List<Key> keys, String name) {
      for (Key key:keys) {
         if (key.toString().equals(name))
            return key;
      }
      return null;
   }
}
