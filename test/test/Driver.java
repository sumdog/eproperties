package test;

import java.io.*;
import java.util.Arrays;

import net.jmatrix.eproperties.utils.JDK14LogConfig;

import org.junit.runner.*;
import org.junit.runner.notification.*;




/**
 * A simple JUnit driver class.
 *
 */
public class Driver {
   
   static TestListener listener=null;
   
   /**
    * @param args
    */
   public static void main(String[] args) throws Exception {
      JDK14LogConfig.startup();
      
      //TestRunner.run(LDAPImplTest.class);
     // JUnitCore.runClasses(LDAPImplTest.class);
      
      JUnitCore core = new JUnitCore();
      // use for categories special listener, give some statistics
      listener=new TestListener();
      core.addListener(listener);
      //core.run(UserLDAPImplTest.class, UserDAOTest.class);
      System.out.println ("Testing "+Arrays.asList(args));
      
      for (int i=0; i<args.length; i++) {
         System.out.println ("Testing "+args[i]);
         core.run(Class.forName(args[i]));
      }

      
      System.out.println ("=================================================");
      System.out.println (listener.getReport());
      System.exit(0);
   }
   
   public static void log(String s) {
      // When executed in ant, Listener will be null.
      if (listener != null)
         listener.write(s);
      else
         System.out.println (s);
   }
   
   /**
    * 
    */
   public static class TestListener extends RunListener {
      
      StringWriter sw=null;
      
      //Description currentTest=null;
      long start=-1;
      
      public String getReport() {
         return sw.toString();
      }
      
      public TestListener() {
         sw=new StringWriter();
      }
      
      @Override
      public void testAssumptionFailure(Failure failure) {
        write("Assumption failure: "+failure.getMessage());
      }

      @Override
      public void testFailure(Failure failure) throws Exception {
         write(failure.getDescription());
         if (failure.getException() != null)
            write(failure.getException());
      }

      @Override
      public void testStarted(Description description) throws Exception {
         write ("vvvvvvvvvvvvvvvvvvv  "+description.getDisplayName()+"vvvvvvvvvvvvvvvvvv");
         start=System.currentTimeMillis();
      }
      
      @Override
      public void testFinished(Description description) throws Exception {
         long et=System.currentTimeMillis() - start;
         start=-1;
         
         write("Test took "+et+"ms to execute.");
         write("^^^^^^^^^^^^^^^^^  "+ description.getDisplayName()+"^^^^^^^^^^^^^^^^^^^\n\n");
      }

      @Override
      public void testIgnored(Description description) throws Exception {
         
      }

      @Override
      public void testRunFinished(Result result) throws Exception {
         write("Test run finished.");
         
         write("Tests: "+result.getRunCount());
         write("Failures: "+result.getFailureCount());
         write("Runtime: "+result.getRunTime()+"ms");
      }

      @Override
      public void testRunStarted(Description description) throws Exception {
         write("Test run started..");
      }

      
      void write(Object o) {
         if (o != null)
            write(o.toString());
         else 
            write("null");
      }
      void write(String s) {
         System.out.println(s);
         sw.write(s+"\n");
      }
      
      void write(Throwable ex) {
         StringWriter sw=new StringWriter();
         PrintWriter pw=new PrintWriter(sw);
         ex.printStackTrace(pw);
         write(sw.toString());
      }
   }
}
