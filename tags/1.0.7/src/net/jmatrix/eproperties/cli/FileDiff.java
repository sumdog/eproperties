package net.jmatrix.eproperties.cli;

import java.io.FileReader;
import java.util.*;

import net.jmatrix.eproperties.EProperties;

/**
 * Diff's 2 URLs.  If not specified, the URLs are assumed to be 
 * file URLs - either relative or absolute.  But you can diff a file
 * against an http URL if you want.
 * 
 * Properties are 'flattened' before diffing, which deals with all
 * nesting issues.   They are also fully materialized - include URLs are 
 * resolved.  
 *
 */
public class FileDiff {

   /**
    * @param args
    */
   public static void main(String[] args) throws Exception {
      ArgParser ap=new ArgParser(args);
      
      String format=ap.getStringArg("-format", "20|30");
      if (format.indexOf("|") == -1) 
         throw new Exception("Invalid format '"+format+"'.  Format must be int|int.");
      
      String keysizeString=format.substring(0, format.indexOf("|"));
      String valuesizeString=format.substring(format.indexOf("|")+1);
      
      int keysize=Integer.parseInt(keysizeString);
      int valuesize=Integer.parseInt(valuesizeString);
      
      String urlA=args[args.length-2];
      String urlB=args[args.length-1];
      
      diff(urlA, urlB, keysize, valuesize);
   }
   
   /** */
   public static void diff(String urlA, String urlB, int keysize, int valuesize) throws Exception {

      try {
         EProperties propsA=new EProperties(); // read(propsFile);
         propsA.load(urlA);
         propsA=propsA.flatten(".");
         
         EProperties propsB=new EProperties(); // read(con);
         propsB.load(urlB);
         propsB=propsB.flatten(".");
         
         List<String> allKeys=new ArrayList<String>();
         
         Iterator keys=propsA.keySet().iterator();
         while (keys.hasNext()) {
            allKeys.add(keys.next().toString());
         }
         
         keys=propsB.keySet().iterator();
         while (keys.hasNext()) {
            String key=keys.next().toString();
            if (!allKeys.contains(key))
               allKeys.add(key);
         }
         
         Collections.sort(allKeys);
         
         StringBuilder header=new StringBuilder();
         header.append(PDiff.truncPad("   key   ", keysize, ' '));
         header.append("|");
         header.append(PDiff.truncPad("       First File", valuesize, ' '));
         header.append("|");
         header.append(PDiff.truncPad("      Second File", valuesize, ' '));
         System.out.println(header);
         
         for (int i=0; i<allKeys.size(); i++) {
            String key=allKeys.get(i);
            String aVal=propsA.getProperty(key);
            String bVal=propsB.getProperty(key);
            
            if (aVal == null)
               aVal="null";
            if (bVal == null)
               bVal="null";
            
            if (!aVal.equals(bVal))
               key="*"+key;
            
            StringBuilder line=new StringBuilder();
            line.append(PDiff.truncPad(key, keysize, '-'));
            
            line.append("|");
            line.append(PDiff.truncPad(aVal, valuesize, ' '));
            line.append("|");
            line.append(PDiff.truncPad(bVal, valuesize, ' '));
            
            System.out.println (line);
         }
         
      } finally {
         
      }
   }
}
