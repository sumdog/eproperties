package test.net.jmatrix.eproperties.syntax;


import java.io.*;
import java.util.List;

import net.jmatrix.eproperties.*;

import org.junit.*;


public class TestComments {

   @Before
   public void setUp() throws Exception {

   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testPointerInComment() throws IOException {
      String pstring=
         "# This is a comment with a pointer -> ha!\n"+
         "foo=bar";
      
      EProperties props=new EProperties();
      props.load(new StringBufferInputStream(pstring));
      
      List<Key> keys=props.getKeys();
      
      Key key=keys.get(0);
      
      System.out.println ("keystring(0): \n'"+key.keyString()+"'");
      
      System.out.println ("--- list ---");
      props.list(System.out);

      Assert.assertTrue("Properties has only 1 element.", props.size()==1);
      Assert.assertTrue("props.get(\"foo\")=bar", props.getString("foo").equals("bar"));
   }

   @Test
   public void testCommentInList() throws IOException {
      String pstring=
         "list=(\"one\",\n"+
         "      \"two\",\n"+
         "      #\"three\",\n"+
         "      \"four\")\n";

      EProperties props=new EProperties();


      System.out.println ("---Props file---");
      System.out.println (pstring);
      System.out.println ("---Props file---");

      props.load(new StringBufferInputStream(pstring));

      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");

      Assert.assertTrue("3 items in list with 1 commented.", props.getList("list").size() == 3);
   }

   @Test
   public void testCommentInList2() throws IOException {
      String pstring=
         "list=(\"one\",\n"+
         "      #\"two\",\n"+
         "      #\"three\",\n"+
         "      \"four\")\n";

      EProperties props=new EProperties();


      System.out.println ("---Props file---");
      System.out.println (pstring);
      System.out.println ("---Props file---");

      props.load(new StringBufferInputStream(pstring));

      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      Assert.assertTrue("2 items in list with 2 commented.", props.getList("list").size() == 2);
   }

   @Test
   public void testCommentInListWithHashInListValue() throws IOException {
      String pstring=
         "list=(\"one\",\n"+
         "      \"#two\",\n"+
         "      #\"three\",\n"+
         "      \"##four\")\n";

      EProperties props=new EProperties();


      System.out.println ("---Props file---");
      System.out.println (pstring);
      System.out.println ("---Props file---");

      props.load(new StringBufferInputStream(pstring));

      System.out.println ("---list---");
      props.list(System.out);
      System.out.println ("---list---");
      Assert.assertTrue("3 items in list with 1 commented.", props.getList("list").size() == 3);
      Assert.assertTrue("List item 2 has correct value", props.getList("list").get(1).equals("#two"));
   }
}
