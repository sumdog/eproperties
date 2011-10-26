package net.jmatrix.eproperties.utils;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

import net.jmatrix.eproperties.*;

import org.apache.commons.logging.*;


/**
 *
 */
public class JDK14LogConfig {
   public static boolean debug=false;
   
   static Log log=LogFactory.getLog(JDK14LogConfig.class);
   
   public static void startup() {
      System.out.println ("Log is "+log.getClass().getName());
      
      LogManager logManager=LogManager.getLogManager();
      
      Logger logger=Logger.getLogger("");
      logger.removeHandler(logger.getHandlers()[0]);
      
      ConsoleHandler consoleHandler=new ConsoleHandler();
      consoleHandler.setFormatter(new LocalLogFormatter());
      consoleHandler.setLevel(Level.FINEST);
      logger.addHandler(consoleHandler);
      
      logger.setLevel(Level.ALL);
      
      logger=Logger.getLogger(SubstitutionProcessor.class.getName());
      logger.setLevel(Level.INFO);
      
      logger=Logger.getLogger(EProperties.class.getName());
      logger.setLevel(Level.INFO);
      
      if (debug) {
         Enumeration loggers=logManager.getLoggerNames();
         
         while (loggers.hasMoreElements()) {
            String name=(String)loggers.nextElement();
            Logger l=Logger.getLogger(name);
            System.out.println ("   Loggger: "+name);
            System.out.println ("          level: "+l.getLevel());
            System.out.println ("       handlers: "+Arrays.asList(l.getHandlers()));
            System.out.println ("       use parent?: "+l.getUseParentHandlers());
         }
      }
   }
   
   public static void main(String args[]) {
      debug=true;
      startup();
      
      log.debug("debug");
      log.info("info");
      log.warn("warn");
      log.error("error");
   }
   
   /**
   *
   */
  static class LocalLogFormatter extends Formatter {
     
     DateFormat df=new SimpleDateFormat("HH:mm:ss.SSS");
     
     /** */
     @Override
     public String format(LogRecord record) {
        
        StringBuilder sb=new StringBuilder();
        
        synchronized(df) {
           sb.append(df.format(new Date(record.getMillis())) + " ");
        }
        
        sb.append(record.getLevel()+" ");
        sb.append(record.getThreadID()+":"+Thread.currentThread().getName()+" ");
        sb.append(shortLoggerName(record.getLoggerName()+" "));
        sb.append(record.getMessage());
        
        if (record.getThrown() != null) {
           ByteArrayOutputStream baos=new ByteArrayOutputStream();
           PrintWriter pw=new PrintWriter(new OutputStreamWriter(baos));
           record.getThrown().printStackTrace(pw);
           pw.flush();
           sb.append("\n"+baos.toString());
        } else {
           sb.append("\n");
        }
        return sb.toString();
     }
     
     private static final String shortLoggerName(String loggerName) {
        return loggerName.substring(loggerName.lastIndexOf(".")+1);
     }
  }

}
