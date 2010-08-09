package test.net.jmatrix.eproperties;


import java.io.*;
import java.util.Properties;
import java.util.logging.*;

import net.jmatrix.eproperties.EProperties;

import org.junit.*;

public class TestBackslashEscaping {


   
   @Before
   public void setUp() throws Exception {
//      System.out.println ("Setting log level to trace for parser.");
//      
//      Logger.getLogger("net.jmatrix").setLevel(Level.FINER);
//      Handler handlers[]=Logger.getLogger("").getHandlers();
//      for (Handler handler:handlers) {
//         handler.setLevel(Level.FINEST);
//      }
   }
   

   @After
   public void tearDown() throws Exception {
   }
   
   ////////////////  Testing backslash escaping /////////////////////
   @Test
   public void testEscapedBackslashNonQuoted() throws IOException {
      String pstring=
         "prop=c:\\\\a\\\\b\\\\c";
      // This should parse as a property DIR = c:\a\b\c
      String expected="c:\\a\\b\\c";
      
      testScenario("Escaping backslash, non-quoted string", pstring, expected, true);
   }
   
   @Test
   public void testBackslashNonQuoted() throws IOException {
      String pstring=
         "prop=c:\\a\\b\\c";
      // This should parse as a property DIR = c:\a\b\c
      String expected="c:abc";
      
      testScenario("Non-escaped backslash, non-quoted string", pstring, expected, false);
   }
   
   @Test
   public void testDoubleBackslashQuoted() throws IOException {
      String pstring=
         "prop=\"c:\\\\a\\\\b\\\\c\"";
      // This should parse as a property DIR = c:\a\b\c
      String expected="c:\\\\a\\\\b\\\\c";
      
      testScenario("Quoted double backslash - escaped ", pstring, expected, false);
   }
   
   @Test
   public void testSingleBackslashQuoted() throws IOException {
      String pstring=
         "prop=\"c:\\a\\b\\c\"";
      // This should parse as a property DIR = c:\a\b\c
      String expected="c:\\a\\b\\c";
      
      testScenario("Escaping backslash, quoted string", pstring, expected, false);
   }
   ////////////////  Testing quote escaping /////////////////////
   @Test
   public void simpleQuotedString() throws IOException {
      String pstring=
         "prop=\"quoted string\"";
      // This should parse as a property DIR = c:\a\b\c
      String expected="quoted string";
      
      testScenario("Simple Quoted String", pstring, expected, false);
   }
   
   @Test
   public void testEscapedQuoteinQuoted() throws IOException {
      String pstring=
         "prop=\"The cow goes \\\"moo\\\"\"";
      // This should parse as a property DIR = c:\a\b\c
      String expected="The cow goes \"moo\"";
      
      testScenario("Escaped quotes in Quoted String", pstring, expected, false);
   }
   
   @Test
   public void testEscapedQuoteinNonQuoted() throws IOException {
      String pstring=
         "prop=The cow goes \\\"moo\\\"";
      // This should parse as a property DIR = c:\a\b\c
      String expected="The cow goes \"moo\"";
      
      testScenario("Un-escaped quotes in NON-Quoted String", pstring, expected, true);
   }
   
   @Test
   public void testUnescapedQuotesinQuotedError() throws IOException {
      String pstring=
         "prop=\"The cow goes \"moo\"\"";
      
      String expected="The cow goes \"moo\""; // error
      
      // ODDLY, this seems to work.
      Exception e=null;
      try {
         testScenario("Un-escaped quotes in Quoted String(error)", pstring, expected, false);
      } catch (Exception ex) {
         System.out.println ("Caught expected parser error");
         ex.printStackTrace();
      }
      //Assert.assertNotNull("Exception on bad qutoe syntax", e);
   }
   
   @Test
   public void testEscapeColon() throws IOException {
      String pstring=
         "prop=http\\://www.foo.com";
      
      String expected="http://www.foo.com"; 
      
      testScenario("Escaped colon in URL (seen in the wild)", pstring, expected, true);
   }
   
   @Test
   public void testEscapeEquals() throws IOException {
      String pstring=
         "prop=<foo bar\\=\"baz\">";
      
      String expected="<foo bar=\"baz\">"; 
      
      testScenario("Escaped equals (seen in the wild)", pstring, expected, true);
   }
   
   @Test
   public void testBackslashF() throws IOException {
      // This causes a problem.  \f is the 'form feed character.
      String pstring=
         "prop=c:\\file";
      
      String expected="c:file"; 
      
      testScenario("Backslash f - \\f (form feed in java.util.Properties)", pstring, expected, false);
   }
   
   //http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.6
   
//   \ b                       backspace BS                   
//   \ t                       horizontal tab HT              
//   \ n                       linefeed LF                    
//   \ f                       form feed FF           
//   \ r                       carriage return CR             
//   \ "                       double quote "             
//   \ '                       single quote '          
//   \ \                       backslash \                     
//   OctalEscape               \u0000 to \u00ff: from octal value   
// javadoc on load method:
   //http://download.oracle.com/javase/6/docs/api/java/util/Properties.html#load%28java.io.Reader%29
   
   
   public void testScenario(String desc, String pstring, String expected, boolean testJavaPropsToo) throws IOException {
      System.out.println ("Testing: "+desc);
      EProperties p=new EProperties(); 
      p.load(new StringBufferInputStream(pstring));
      String val=p.getString("prop");
      
      System.out.println ("Property file defined as: \n"+pstring);
      System.out.println ("Expected dir property: "+expected);
      System.out.println ("EProperties Result:    "+val);
      


      
      Properties jp=new Properties();
      jp.load(new StringBufferInputStream(pstring));
      String jpval=jp.getProperty("prop");
      
      System.out.println ("java.util.properties:  "+jpval);
      System.out.println ("-------------------------------------");
      
      Assert.assertEquals(desc, expected, val);
      
      if (testJavaPropsToo) {
         Assert.assertEquals("java.util.Properties - "+desc, expected, jpval);
      }

   }
}
