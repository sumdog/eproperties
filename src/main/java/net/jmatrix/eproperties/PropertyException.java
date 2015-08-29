package net.jmatrix.eproperties;

/**
 * Typically thrown when there is a syntax error when loading 
 * a properties stream.
 */
public class PropertyException extends Exception {

   /** */
   public PropertyException() {
      super();
   }

   /** */
   public PropertyException(String message) {
      super(message);
   }

   /** */
   public PropertyException(Throwable cause) {
      super(cause);
   }

   /** */
   public PropertyException(String message, Throwable cause) {
      super(message, cause);
   }
}
