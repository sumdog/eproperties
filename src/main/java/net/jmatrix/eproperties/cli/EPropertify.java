package net.jmatrix.eproperties.cli;

import java.io.File;
import java.util.List;

import net.jmatrix.eproperties.*;

public class EPropertify {

   /**
    * @param args
    */
   public static void main(String[] args) throws Exception  {
      ArgParser ap=new ArgParser(args);
      
      // should be
      // epropertify -i <infile/url> -o <outfile> -d <delimeter>
      
      String in=ap.getStringArg("-i");
      String out=ap.getStringArg("-o");
      String delim=ap.getStringArg("-d", ".");
      
      EProperties inProps=new EProperties();
      inProps.load(in);
      inProps=inProps.flatten(delim);
      
      EProperties outProps=new EProperties();
      File outFile=new File(out);
      
      
      List<Key> keys=inProps.getKeys();
      for (Key key:keys) {
         String skey=key.toString();
         String newkey=skey.replace(delim, "->");
         System.out.println (skey+" ->   "+newkey);
         
         outProps.put(newkey, inProps.get(skey));
      }
      
      outProps.save(outFile);
   }
}
