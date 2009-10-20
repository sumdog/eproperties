package net.jmatrix.eproperties.cli;

/**
 * Simple class for parsing command line arguments out of an argument 
 * list. <p>
 *
 * The arguments can be switches (boolean), or Strings.  For string 
 * arguments, either of the following 2 formats is valid:<br>
 *
 * -cargVal or<br>
 * -c argVal<p>
 * 
 * For either of these 2 cases, a call go getStringArg("-c") will return 
 * argVal.
 *
 * @author Paul Bemowski
 */
public class ArgParser
{
   String args[]=null;
   
   /**
    * The constructor requires the arguements that are passed to the 
    * main method.
    */
   public ArgParser(String a[]) {
      args=a;
   }

   public String getStringArg(String key){
      return getStringArg(key, null);
   }

   /**
    * Returns the string corresponding to the requested key.
    */
   public String getStringArg(String key, String def) {
      for (int i=0; i<args.length; i++) {
         String arg=args[i];
         
         if (arg.equalsIgnoreCase(key) &&
             args.length >= i+2)
            return args[i+1].trim();
         
         if (arg.startsWith(key))
            return arg.substring(key.length()).trim();
      }
      return def;
   }

   public int size() {return args.length;}
   public String getLastArg() {
      if (args.length == 0)
         return null;
      return args[args.length-1];
   }

   /**
    *
    */
   public Integer getIntegerArg(String key) {
      String str=getStringArg(key);
      if (str == null || str.trim().length() <= 0) return null;
      
      Integer val=null;
      try {
         int i=Integer.parseInt(str.trim());
         val=new Integer(i);
      }
      catch (NumberFormatException ex) {
         System.out.println ("NumberFormatException reading "+key+" from "+
                             "args.");
      }
      return val;
   }

   /** */
   public int getIntArg(String key, int def) {
      Integer i=getIntegerArg(key);
      if (i == null)
         return def;
      else
         return i.intValue();
   }

   /** */
   public String toString() {
      StringBuffer sb=new StringBuffer();
      for (int i=0; i<args.length; i++) {
         sb.append("  "+i+": '"+args[i]+"'\n");
      }
      return sb.toString();
   }

   /**
    * Returns a boolean indicating whether a command like switch was
    * present.
    */ 
   public boolean getBooleanArg(String key) {
      for (int i=0; i<args.length; i++) {
         String arg=args[i];
         if (arg.equalsIgnoreCase(key))
            return true;
      }
      return false;
   }

   /**
    * Returns a boolean indicating whether a command like switch was
    * present.
    */ 
   public boolean getBooleanArg(String key, boolean def) {
      for (int i=0; i<args.length; i++) {
         String arg=args[i];
         if (arg.equalsIgnoreCase(key))
            return true;
      }
      return def;
   }
}
