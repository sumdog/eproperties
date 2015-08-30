package net.jmatrix.eproperties.cache;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import net.jmatrix.eproperties.utils.URLUtil;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
 * 
 * System Properties: <br>
 * eproperties.cache:  If set to anything other than "true", the cache is 
 *    disabled for the life of the JVM.  Only read at the first call to 
 *    CacheManager.getInstance(), called internally before any EProperties load 
 *    operation.  Default: true<br>
 *    
 * eproperties.cache.root: Sets the cache root folder.  
 *    Default: ${user.home}/.eproperties/cache/
 */
public class CacheManager {
   static final Logger log=LoggerFactory.getLogger(CacheManager.class);
   
   static CacheManager instance=null;
   
   static final String CACHE_ROOT_PROPERTY="eproperties.cache.root";
   
   /** It is enabled by default, but will be disabled (for the life of the 
    * JVM) if this is set to "false".  
    * 
    * There is no lifecycle management.  At the time that CacheManager.getInstance() 
    * is called - if this _system_ property is set to false, then the 
    * CacheManager will be 'disabled' any you cannot re-enable it at runtime.
    * 
    * The CacheManager is either on or off for the life of the JVM.  Unless
    * someone can explain a need for a more complex behavior.  
    * 
    * Paul Bemowski, 30jul2010.
    */
   static final String EPROPERTIES_CACHE="eproperties.cache";
   
   File cacheRoot=null;
   
   /* 
    * This depends on the target system.  If the filesystem is NFS or otherwise
    * network based, then creating a local disk cache of file URLs makes sense.
    * However if the filesystem is local disk - cacheing such URLs is redundant, 
    * wasteful, and can lead to errors.  (Ie, FileNotFound may be more valid
    * than running with a cached file.)  
    */
   boolean CACHE_FILE_URLS=false;
   
   boolean online=true;
   
   String state="ONLINE";
   
   /** */
   private CacheManager() {
      String enabled=System.getProperty(EPROPERTIES_CACHE, "true");
      
      if (enabled.equals("true")) {
         if (System.getProperty(CACHE_ROOT_PROPERTY) != null) {
            cacheRoot=new File(System.getProperty(CACHE_ROOT_PROPERTY));
         } else {
            cacheRoot=new File(System.getProperty("user.home")+"/.eproperties/cache/");
         }
         log.debug("EProperties: CacheManager root at "+cacheRoot);
         
         if (!cacheRoot.exists()) {
            boolean success=cacheRoot.mkdirs();
            
            if (!success) {
               online=false;
               state="Cannot create cache root dir at "+
                     cacheRoot.getAbsolutePath();
               log.error(state);
               log.error("CacheManager will be unavilable for read/write property caching.");
            }
         }
         
         if (!cacheRoot.canWrite()) {
            online=false;
            state="Cannot write in cache dir.  CacheManager unavailable.";
            log.error(state);
            log.error("CacheManager will be unavilable for read/write property caching.");
         }
      } else {
         online=false;
         state="Disabled by system property (eproperties.cache != true)";
      }
      
      log.debug("EProperties: CacheManager state: "+state);
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
      
      if (!online) {
         URLConnection urlConn=URLUtil.getConnection(url);
         return urlConn.getInputStream();
      }
      
      InputStream pis=null;
      String surl=url.toExternalForm();
      
      File cacheFile=getCacheFileForURL(url);
      
      log.debug("CacheFile: "+cacheFile.getAbsolutePath());
      
      try {
         URLConnection urlConn=URLUtil.getConnection(url);
         
//         log.debug("URLConnection is a "+urlConn.getClass().getName());
//         if (urlConn instanceof HttpsURLConnection) {
//            log.debug("Connection is a javax.net.ssl.HttpsURLConnection");
//         }
         
         InputStream ris=urlConn.getInputStream();
         
         // Ok, we have an input stream, so lets create a cache stream
         // and return it.
         try {
            OutputStream cos=getCacheOutputStream(surl, cacheFile);
            CacheInputStream cis=new CacheInputStream(ris, cos);
            
            //cis.setLastModified(lastMod);
            pis=cis;
         } catch (Exception ex) {
            // Can't create cache file or stream.
            log.warn("Cannot create cache output stream with file "+cacheFile);
            log.debug("Continuing with remote input stream as properties stream. Uncached.");
            pis=ris;
         }
      } catch (IOException ex) {
         log.warn("Remote stream unavailable for URL "+url+" due to "+ex.toString());
         log.warn("Looking for cache file at "+cacheFile.getAbsolutePath());
         //log.debug("Remote stream stack: ", ex);

         if (cacheFile.exists() && cacheFile.canRead()) {
            log.info("Cache file "+cacheFile.getAbsolutePath()+" exists. Attempting to use.");
            DateFormat df=new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss");
            log.info("Cache file lastMod is "+df.format(new Date(cacheFile.lastModified())));
            FileInputStream fis=new FileInputStream(cacheFile);
            pis=new CacheInputStream(fis, null);
         } else {
            throw ex;
            //throw new IOException("Properties cache file does not exist at "+
            //      cacheFile.getAbsolutePath()+" for URL "+url);
         }
      }
      
      return pis;
   }
   
   /** MUST be called before getInstance().  
    * Sets the system property eproperties.cache to false.*/
   public static final void disable() {
      if (instance != null) {
         log.error("CacheManager: Call to disable() after CacheManager instance already created.  Disabling. Cannot re-enable.");
         
         // This was mostly added for unit tests.
         instance.online=false;
         instance.state="Offline, disabled by static method call.";
      } else {
         System.setProperty(EPROPERTIES_CACHE, "false");
      }
   }
   
   /** */
   public InputStream getInputStream(InputStream is, URL url) {
      return getInputStream(is, (url == null? null:url.toExternalForm()));
   }
   
   /** */
   public InputStream getInputStream(InputStream is, String surl) {
      if (!online)
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
      surl=surl.replace(":", "-");
      
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
