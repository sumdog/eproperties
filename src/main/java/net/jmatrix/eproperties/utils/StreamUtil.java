package net.jmatrix.eproperties.utils;

import java.io.*;

/**
 *
 */
public final class StreamUtil 
{
   static final int EOS=-1;

   /** 
    * Pumps one stream to another, with a buffer - both streams are
    * closed when the pump is complete.
    */
   public static final void pump(InputStream is, OutputStream os)
      throws IOException {
      byte buffer[]=new byte[8192];
      int bytes=is.read(buffer);
      while (bytes > 0) {
         os.write(buffer, 0, bytes);
         // SOLVED! -- See jet_net ChunkedInputStream, and 
         // Transfer-Encoding: chunked.  !!
         // pab, 24/7/2003
         bytes=is.read(buffer);
      }
      os.flush(); os.close();
      is.close();      
   }

   public static final void unbufferedPump(InputStream is, OutputStream os) 
      throws IOException {
      int b=is.read();
      while (b != EOS) {
         os.write(b);
         b=is.read();
      }
      os.flush(); os.close();
      is.close();
   }

  /** */
  public static final void pumpExactly(InputStream is, OutputStream os, 
                                       int bytes) 
    throws IOException {
    for (int i=0; i<bytes; i++) {
      os.write(is.read());
    }
    os.flush();
    os.close();
    is.close();
  }

   /** 
    * Reads all remaining bytes from a stream and returns it 
    * as a string. 
    */
   public static String readToString(InputStream is) 
      throws IOException {
      ByteArrayOutputStream baos=new ByteArrayOutputStream();
      pump(is, baos);
      return baos.toString();
   }

   public static byte[] readFully(InputStream is) 
      throws IOException {
      ByteArrayOutputStream baos=new ByteArrayOutputStream();
      int b=is.read();
      while (b != EOS) {
         baos.write(b);
         b=is.read();
      }
      return baos.toByteArray();
   }
}
