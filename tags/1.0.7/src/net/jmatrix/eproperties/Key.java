package net.jmatrix.eproperties;

import java.util.*;

/**
 * Represents a key in the EProperties class.  Key's behave like strings
 * in hashtables and vectors, but they have extended attributes 
 * useful for maintaining properties files, and notifying property 
 * observers.
 *
 * @author Paul Bemowski
 */
public class Key
{
   String comments=null;
   String key=null;
   
   int hitcount=0;

   List<PropertyListener> listeners=null;

   /** If a property is transient, it is not stored back to the data file. */
   boolean tranzient=false;
   
   /**
    *
    */
   public Key(String k, String c) {
      if (k == null)
         throw new Error ("null key");
      key=k;
      
      setComments(c);
   }

   /**
    *
    */
   public Key(String k) {
      this(k, null);
   }
   
   public String getComments() {
      return comments;
   }
   
   public synchronized void hit() {
      hitcount++;
   }
   
   public synchronized void removeHit(){
      hitcount--;
   }
   
   public int getHitCount() {
      return hitcount;
   }
   
   /** */
   public String keyString()
   {
      if (comments != null) {
         String commentString=comments.toString();
         if (!commentString.endsWith("\n"))
            commentString=commentString+"\n";

         return commentString+key;
      }
      else 
         return key;
   }

   /** */
   public void setComments(String com) {
      if (com != null) {
         com=com.trim();
         if (com.length() == 0)
            return;
         else
            comments=com.trim()+"\n";
      }
   }
   
   /** */
   public String toString() {
      return key;
   }

   /**
    *
    */
   public int hashCode() {
      return key.hashCode();
   }

   /**
    *
    */
   public boolean equals(Object obj)
   {
      String type="unk";
      boolean ret=false;
      if (obj instanceof String) {
         type="String";
         String s=(String)obj;
         if (s.equals(key))
            ret=true;
         else 
            ret=false;
      } else if (obj instanceof Key) {
         type="key";
         Key k=(Key)obj;
         if (k.key.equals(key))
            ret=true;
         else
            ret=false;
      }
      else
         ret=false;
      
      //System.out.println ("Key.equals ("+key+") == "+obj.toString()+" type="+type+" ? "+ret);
      return ret;
   }

   /**  */
   public boolean isTransient() {return tranzient;}

   /** */
   public void setTransient(boolean b) {tranzient=b;}

   /** */
   void addListener(PropertyListener obs) {
      if (listeners == null)
         listeners=new ArrayList<PropertyListener>();
      listeners.remove(obs); // prevent duplicates
      listeners.add(obs);
   }

   /**
    *
    */
   void removeListener(PropertyListener obs) {
      if (listeners == null) return;
      else
         listeners.remove(obs);
   }

   /**
    *
    */
   void notifyListeners(Object value) {
      if (listeners == null) return;  
      
      // generate the event
      PropertyChangeEvent evt=new PropertyChangeEvent(this, value);

      for (int i=0; i<listeners.size(); i++) {
         PropertyListener listener=listeners.get(i);
         listener.propertyChange(evt);
      }
   }
}
