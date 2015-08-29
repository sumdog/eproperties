package net.jmatrix.eproperties.cli;


import java.io.FileReader;
import java.util.*;

import net.jmatrix.eproperties.EProperties;

/**
 * PDiff will create a diff of EProperties vs. java.util.properties.
 *
 */
public class PDiff {

   /**
    * @param args
    */
   public static void main(String[] args) {
      ArgParser ap=new ArgParser(args);
      
      String file=ap.getLastArg();
      
      try {
         diff(file, ap);
      } catch (Exception ex) {
         System.out.println ("Error diffing properties.");
         ex.printStackTrace();
      }
   }
   
   /** */
   public static void diff(String propsFile, ArgParser ap) throws Exception {
      
      String format=ap.getStringArg("-format", "20|30");
      if (format.indexOf("|") == -1) 
         throw new Exception("Invalid format '"+format+"'.  Format must be int|int.");
      
      String keysizeString=format.substring(0, format.indexOf("|"));
      String valuesizeString=format.substring(format.indexOf("|")+1);
      
      int keysize=Integer.parseInt(keysizeString);
      int valuesize=Integer.parseInt(valuesizeString);
      
      
      try {
         EProperties eProps=new EProperties(); // read(propsFile);
         eProps.load(propsFile);
         
         Properties juProps=new Properties(); // read(con);
         juProps.load(new FileReader(propsFile));
         
         List<String> allKeys=new ArrayList<String>();
         
         Iterator keys=eProps.keySet().iterator();
         while (keys.hasNext()) {
            allKeys.add(keys.next().toString());
         }
         
         keys=juProps.keySet().iterator();
         while (keys.hasNext()) {
            String key=keys.next().toString();
            if (!allKeys.contains(key))
               allKeys.add(key);
         }
         
         Collections.sort(allKeys);
         
         StringBuilder header=new StringBuilder();
         header.append(truncPad("   key   ", keysize, ' '));
         header.append("|");
         header.append(truncPad("       EProperties value", valuesize, ' '));
         header.append("|");
         header.append(truncPad("  java.util.Properties value", valuesize, ' '));
         System.out.println(header);
         
         for (int i=0; i<allKeys.size(); i++) {
            String key=allKeys.get(i);
            String epVal=eProps.getProperty(key);
            String juVal=juProps.getProperty(key);
            
            if (epVal == null)
               epVal="null";
            if (juVal == null)
               juVal="null";
            
            if (!epVal.equals(juVal))
               key="*"+key;
            
            StringBuilder line=new StringBuilder();
            line.append(truncPad(key, keysize, '-'));
            
            line.append("|");
            line.append(truncPad(epVal, valuesize, ' '));
            line.append("|");
            line.append(truncPad(juVal, valuesize, ' '));
            
            System.out.println (line);
         }
         
      } finally {
         
      }
   }
   
   /** */
   public static void diff(Properties a, Properties b) {
      
   }
   
   
   /** returns a string of the exact length, by either truncating or
    * padding.
    */
   static String truncPad(String s, int len, char padChar) {
      if (s.length() == len || len == -1)
         return s;
      else if (s.length() < len) {
         StringBuilder pad=new StringBuilder();
         for (int i=0; i<len-s.length(); i++) {
            pad.append(padChar);
         }
         return s + pad.toString();
      } else {
         return s.substring(0, len);
      }
   }
}
