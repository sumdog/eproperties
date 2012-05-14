package net.jmatrix.eproperties.monitor;

import java.util.*;

import net.jmatrix.eproperties.EProperties;

/**
 * This class monitors URLs for changes (via URL dependent methods) and will
 * reload the properties if the URLs have changed.  <p>
 * 
 * This is a single monitor thread.  This thread monitors all properties
 * files in a single thread - as there may be many files.  The thread
 * is initialized internally if a single properties object registers
 * itself.  Its delay time is globally configurable.
 *
 */
public class EPropertiesMonitor extends Thread {
   
   /** Singleton instance. */
   private static EPropertiesMonitor instance=null;
   
   /** The delay time between checking files. */
   private long delay=20000; // 20 s
   
   /** */
   private volatile boolean shutdown=false;
   
   /** */
   private List<EProperties> monitoredProperties=new ArrayList<EProperties>();
   
   /** Singleton constructor.  */
   private EPropertiesMonitor() {
      setName("EPropertiesMonitor");
   }
   
   /** Execution method. */
   public void run() {
      try {
         while (!shutdown) {
            // 1) sleep a bit.
            try {
               sleep(delay);
            } catch (InterruptedException ex) {
               // this is expected at times.  and does not hurt.  no log.
            }
            
            // 2) check reload
            try {
               synchronized (monitoredProperties) {
                  int size=monitoredProperties.size();
                  
                  log("Monitoring "+size+" EProperties objects.");
                  
                  for (int i=0; i<size; i++) {
                     EProperties props=monitoredProperties.get(i);
                     if (props.isSourceModified())
                        props.reload();
                  }
               }
            } catch (Exception ex) {
               log("Error reloading properties.", ex);
            }
         }
      } catch (Throwable t) {
         log("Error in EPropertiesMonitor!!  Thread is exiting.", t);
      }
   }
   
   static final void log(String s) {
      System.out.println (s);
   }
   
   static final void log(String s, Throwable t) {
      System.out.println (s);
      t.printStackTrace();
   }
   
   public static final EPropertiesMonitor getInstance() {
      synchronized(EPropertiesMonitor.class) {
         if (instance == null) {
            synchronized(EPropertiesMonitor.class) {
               instance=new EPropertiesMonitor();
               instance.start();
            }
         }
      }
      return instance;
   }
   
   private void internalAdd(EProperties pr) {
      monitoredProperties.add(pr);
   }
   
   ////////////// Public, Static API //////////////
   public synchronized static void add(EProperties props) {
      // getInstance().add(props);
      EPropertiesMonitor monitor=getInstance();
      monitor.internalAdd(props);
   }
   
   public synchronized static void setMonitorDelay(long millis) {
      getInstance().delay=millis;
   }
   
   public synchronized static void shutdown() {
      if (instance != null) {
         instance.shutdown=true;
         instance.interrupt();
         instance=null;
      }
   }
}
