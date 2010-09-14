package net.jmatrix.eproperties;

/** 
 * This interface is used internally, to return the representation of the 
 * object that should be saved, not the runtime value.  The difference
 * between runtime value and persistence value is exactly substitutions.
 *
 * This concept is best shown by example. Consider the following 
 * properties file:
 * <pre>
 * foo="world"
 * bar="hello ${foo}"
 * </pre>
 * 
 * Here we have 2 keys, and 2 values. The runtime and persistent values
 * represented by the key "foo" are identical.  This is not the case for
 * bar.  For bar, the following values are represented:
 * 
 * bar runtime value    = hello world
 * bar persistent value = hello ${foo}
 * 
 * We should never persist the runtime value.  <p>
 * 
 * Further, internally, the key bar must listen to the key foo for changes.
 * Substitutions can be done dynamically, however we also need to listen
 * to foo because others may be listening to us.  Ie, if I declare myself
 * to be a listener of the key "bar", and someone changes the value of
 * "foo" then I need to be notified that my runtime value has also changed.
 * 
 * Substitutions could be done dynamically (as in when getRuntimeValue() is 
 * called), or could be done statically - and listen for updates up the 
 * tree. Either mechanism may be used, however changes to "foo" must be
 * immediately reflected in "bar".  
 */
public interface Value<T> {
   // Value - concept and implementations coded 26 Nov 2008, B757 ATL-SEA
   public T getPersistentValue();
   public Object getRuntimeValue();
}
