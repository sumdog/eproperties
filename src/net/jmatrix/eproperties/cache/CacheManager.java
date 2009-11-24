package net.jmatrix.eproperties.cache;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Date;

import org.apache.commons.logging.*;

/**
 * The CacheManager implements many simple functions necessary for caching
 * properties.  Properties are cached for safe failover to the most 
 * recently used values in the case of failure of network based resources.
 * Network based resources could include: http or NFS file URLs, or JDBC based
 * properties, or indeed any custom derrived properties includer.
 *
 * Properties loaded with non-stream based loading (such as Method based
 * proeprties inclusion) are not cached. 
 *
 * To maintain transparency, we will use a Tee type input stream bound to a
 * buffered writer to minimize performance impacts.
 */
public class CacheManager {
   static final Log log=LogFactory.getLog(CacheManager.class);
   
   static CacheManager instance=null;
   
   static final String CACHE_ROOT_PROPERTY="eproperties.cache.root";
   
   File cacheRoot=null;
   
   /* 
    * This depends on the target system.  If the filesystem is NFS or otherwise
    * network based, then creating a local disk cache of file URLs makes sense.
    * However if the filesystem is local disk - cacheing such URLs is redundant, 
    * wasteful, and can lead to errors.  (Ie, FileNotFound may be more valid
    * than running with a cached file.)  
    */
   boolean CACHE_FILE_URLS=false;
   
   boolean ONLINE=true;
   String state="ONLINE";
   
   /** */
   private CacheManager() {
      if (System.getProperty(CACHE_ROOT_PROPERTY) != null) {
         cacheRoot=new File(System.getProperty(CACHE_ROOT_PROPERTY));
      } else {
         cacheRoot=new File(System.getProperty("user.home")+"/.eproperties/cache/");
      }
      
      if (!cacheRoot.exists()) {
         boolean success=cacheRoot.mkdirs();
         
         if (!success) {
            ONLINE=false;
            state="Cannot create cache root dir at "+
                  cacheRoot.getAbsolutePath();
            log.error(state);
            log.error("CacheManager will be unavilable for read/write property caching.");
         }
      }
      
      if (!cacheRoot.canWrite()) {
         ONLINE=false;
         state="Cannot write in cache dir.  CacheManager unavailable.";
         log.error(state);
         log.error("CacheManager will be unavilable for read/write property caching.");
      }
      
      log.info("EProperties: CacheManager online at "+cacheRoot);
   }
   
   /** */
   public static CacheManager getInstance() {
      if (instance == null) {
         synchronized(CacheManager.class) {
            if (instance == null) {
               instance=new CacheManager();
            }
         }
      }
      return instance;
   }
   
   /**
    * This method will return an input stream based on a URL.
    * 
    * In the case where the stream IS available from the URL, the contents
    * of that stream will be cached.  In the case where the steram is NOT 
    * available, we will attempt to get the stream from the cache.
    * 
    * @param url
    * @return
    */
   public InputStream getInputStream(URL url) throws IOException {
      if (url == null) {
         throw new IOException ("Cannot open input stream for null URL.");
      }
      
      if (!ONLINE)
         return url.openStream();
      
      InputStream pis=null;
      String surl=url.toExternalForm();
      
      File cacheFile=getCacheFileForURL(url);
      
      log.debug("CacheFile: "+cacheFile.getAbsolutePath());
      
      try {
         //InputStream ris=url.openStream();
         URLConnection urlConn=url.openConnection();
         
         long lastMod=urlConn.getLastModified();
         
         InputStream ris=urlConn.getInputStream();
         
         // Ok, we have an input stream, so lets create a cache stream
         // and return it.
         try {
            OutputStream cos=getCacheOutputStream(surl, cacheFile);
            CacheInputStream cis=new CacheInputStream(ris, cos);
            
            cis.setLastModified(lastMod);
            pis=cis;
         } catch (Exception ex) {
            // Can't create cache file or stream.
            log.warn("Cannot create cache output stream with file "+cacheFile);
            log.debug("Continuing with remote input stream as properties stream. Uncached.");
            pis=ris;
         }
      } catch (Exception ex) {
         log.warn("Remote stream unavailable for URL "+url+" due to "+ex.toString());
         log.debug("Remote stream stack: ", ex);
         
         if (cacheFile.exists() && cacheFile.canRead()) {
            log.info("Cache file "+cacheFile.getAbsolutePath()+" exists. Attempting to use.");
            DateFormat df=new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss");
            log.info("Cache file lastMod is "+df.format(new Date(cacheFile.lastModified())));
            FileInputStream fis=new FileInputStream(cacheFile);
            pis=new CacheInputStream(fis, null);
         } else {
            throw new IOException("Properties cache file does not exist at "+
                  cacheFile.getAbsolutePath()+" for URL "+url);
         }
      }
      
      return pis;
   }
   
   /** */
   public InputStream getInputStream(InputStream is, URL url) {
      return getInputStream(is, (url == null? null:url.toExternalForm()));
   }
   
   /** */
   public InputStream getInputStream(InputStream is, String surl) {
      if (!ONLINE)
         return is;
      
      if (surl == null)
         return is;
      
      // This is already a cache stream.
      if (is instanceof CacheInputStream)
         return is;
      
      File cacheFile=getCacheFileForURLString(surl);
      log.debug("CacheFile: "+cacheFile.getAbsolutePath());
      
      InputStream pis=null;
      // Ok, we have an input stream, so lets create a cache stream
      // and return it.
      try {
         OutputStream cos=getCacheOutputStream(surl, cacheFile);
         CacheInputStream cis=new CacheInputStream(is, cos);
         pis=cis;
      } catch (Exception ex) {
         // Can't create cache file or stream.
         log.warn("Cannot create cache output stream with file "+cacheFile);
         log.debug("Continuing with remote input stream as properties stream. Uncached.");
         pis=is;
      }
      return pis;
   }
   
   /**
    * Here we create a .pcache file by translating the full URL into a 
    * directory structure.
    **/
   private File getCacheFileForURL(URL url) {
      String surl=url.toExternalForm();
      return getCacheFileForURLString(surl);
   }
   
   /** */
   private File getCacheFileForURLString(String surl) {
      surl=surl.replace("\\", "/");
      surl=surl.replace("//", "/");
      surl=surl.replace(":/", "/");
      
      String filename=null;
      String path=null;
      if (!surl.contains("/")) {
         filename=surl+".pcache";
         path="";
      }
      if (surl.endsWith("/")) {
         filename="index.pcache";
         path=surl;
      } else {
         filename=surl.substring(surl.lastIndexOf("/")+1)+".pcache"; 
         path=surl.substring(0, surl.lastIndexOf("/"));
      }
      
      File file=new File(cacheRoot, path+"/"+filename);
      try {
         file=file.getCanonicalFile();
      } catch (IOException ex) {
         // this should really never happen...
         log.warn("Cannot form canonical file for file: "+file.getAbsolutePath());
      }
      
      return file;
   }
   
   /** */
   private OutputStream getCacheOutputStream(String surl, File cacheFile) 
   throws IOException {
      FileOutputStream fos=null;
      if (surl.startsWith("file")) {
         if (CACHE_FILE_URLS) {
            createDirsForFile(cacheFile);
            fos=new FileOutputStream(cacheFile, false);
         }
      } else {
         createDirsForFile(cacheFile);
         fos=new FileOutputStream(cacheFile, false);
      }
      return fos;
   }
   
   /** */
   private void createDirsForFile(File f) {
      File dir=f.getParentFile();
      if (dir.exists()) 
         return;
      dir.mkdirs();
   }
}
