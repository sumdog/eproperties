package net.jmatrix.eproperties;

/** 
 * Represents a dynamic string value.
 *
 */
public class StringValue implements Value<String> {
   String persistentValue=null;
   EProperties owner=null;
   
   public StringValue(String s, EProperties p) {
      persistentValue=s;
      owner=p;
   }
   
   public String toString() {
      Object runtimeValue = getRuntimeValue();
      if (runtimeValue != null)
      {
         return runtimeValue.toString();
      }
      return null;
   }
   
   public String getPersistentValue() {
      return persistentValue;
   }

   public Object getRuntimeValue() {
      // FIXME: in the future, the choice of static vs. dynamic substitution
      //        should be statically configurable.
      
      // perform dynamic substitution.
      return SubstitutionProcessor.processSubstitution(persistentValue, owner, Object.class);
   }

   @Override
   public void setOwner(EProperties p) {
      owner=p;
   }
}
