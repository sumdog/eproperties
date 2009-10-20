package net.jmatrix.eproperties;

/** 
 * Represents a dynamic string value.
 *
 */
class StringValue implements Value<String> {
   String persistentValue=null;
   EProperties owner=null;
   
   public StringValue(String s, EProperties p) {
      persistentValue=s;
      owner=p;
   }
   
   public String toString() {
      return getRuntimeValue().toString();
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
}
