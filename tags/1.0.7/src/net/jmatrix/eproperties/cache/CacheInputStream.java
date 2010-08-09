package net.jmatrix.eproperties.cache;

import java.io.*;

/**
 * The CacheInputStream stores the bytes read from the input stream to a
 * local file based output stream for later use if a remote resource
 * is unavailable.
 */
public class CacheInputStream extends InputStream {
   InputStream sourceStream=null;
   OutputStream cacheStream=null;
   
   long lastModified=0l;
   
   /** */
   public CacheInputStream(InputStream is, OutputStream os) {
      sourceStream=is;
      
      if (os != null) {
         if (os instanceof BufferedOutputStream)
            cacheStream=os;
         else
            cacheStream=new BufferedOutputStream(os);
      }
   }
   
   public long getLastModified() {
      return lastModified;
   }
   public void setLastModified(long lastModified) {
      this.lastModified = lastModified;
   }
   
   /** There is little buffering or efficiency in overriding only this 
    * method - but as a quick and dirty first go, it is a simple way
    * to start.  
    * FIXME: Update other read() methods and potentially use Buffering on 
    * the output stream to make this class more performant.
    **/
   @Override
   public int read() throws IOException {
      int b=sourceStream.read();
      
      if (cacheStream != null) {
         if (b != -1)
            cacheStream.write(b);
         else // b==-1 == EOF.  Flush at EOF to ensure we have a well formed file.
            cacheStream.flush();
      }
      
      return b;
   }
   
   @Override
   public void close() throws IOException {
      if (cacheStream != null) {
         try {
            cacheStream.close();
         } catch (IOException cex) {
            // log this, but continue.  It is relatively unlikely.
            cex.printStackTrace();
         }
      }
      if (sourceStream != null) {
         try {
            sourceStream.close();
         } catch (IOException sex) {
            sex.printStackTrace();
         }
      }
   }
}
