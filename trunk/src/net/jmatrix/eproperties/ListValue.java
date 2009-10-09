package net.jmatrix.eproperties;

import java.util.*;

/** 
 * Represents a dynamic list value.
 *
 */
class ListValue implements Value<List<String>> {
   List<String> persistentValue=null;
   EProperties owner=null;
   
   public ListValue(List<String> l, EProperties p) {
      persistentValue=l;
      owner=p;
   }
   
   public String toString() {
      return getRuntimeValue().toString();
   }
   
   /** Returns a List<String> that has not been passed through the 
    * SubstitutionProcessor. */
   public List<String> getPersistentValue() {
      return persistentValue;
   }

   public List<String> getRuntimeValue() {
      // FIXME: in the future, the choice of static vs. dynamic substitution
      //        should be statically configurable.
      
      // perform dynamic substitution.
      int size=persistentValue.size();
      List<String> newList=new ArrayList<String>(size);
      
      for (int i=0; i<size; i++) {
         newList.add(SubstitutionProcessor.processSubstitution(persistentValue.get(i), owner));
      }
      
      return newList;
   }
}
