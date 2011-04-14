package net.jmatrix.eproperties;


/**
 *
 */
public class PropertyChangeEvent
{
   Key key;
   Object value;
   
   public PropertyChangeEvent(Key k, Object val) {
      key=k;
      value=val;
   }

   public String toString() {
      return key.toString()+":"+value.toString();
   }

   public Key getKey() {return key;};

   public String getKeyString() {return key.toString();}

   public Object getValue() {
      return value;
   }
}
