package test.net.jmatrix.eproperties;

import java.io.*;

public class TestUtils {
   
   public static final void enableDebug() {

   }

   
   public static void write(File f, String s) throws IOException {
      FileWriter fw=null;
      try {
         System.out.println ("Writing to file: "+f.getAbsolutePath());
         
         fw=new FileWriter(f);
         fw.write(s);
      } finally {
         fw.close();
      }
   }
}
