package test.net.jmatrix.eproperties;

import java.io.*;
import java.util.logging.*;

public class TestUtils {
   
   public static final void enableDebug() {
      System.out.println("Setting log level to trace for parser.");

      Logger.getLogger("net.jmatrix").setLevel(Level.FINER);
      Handler handlers[] = Logger.getLogger("").getHandlers();
      for (Handler handler : handlers) {
         handler.setLevel(Level.FINEST);
      }
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
