package cn.com.gfa.ware.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;


/**
 * *************************************************************************
 * <pre>
 * 文件名称:  URLConnectionUtil.java
 * </pre>
 *<pre>
 * 类描述:  URLConnection工具类
 * Revision : V1.0
 * 创建人： wanghailong@ec.com.cn
 * 创建时间：2013-1-10 上午10:24:23  
 * 修改人：wanghailong@ec.com.cn  
 * 修改时间：2013-1-10 上午10:24:23  
 * </pre>
 *
 * <pre>
 *  1. 修改记录：
 *    -----------------------------------------------------------------------------------------------
 *              时间                      |       修改人            |         修改的方法                       |         修改描述                                                                
 *    -----------------------------------------------------------------------------------------------
 *                  		|                 |                           |                                       
 *    -----------------------------------------------------------------------------------------------
 * </pre>
**************************************************************************
 */
public class URLConnectionUtils {
	
	/**
	 * 
	 * @auther wanghailong
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static byte[] sendGet(String url){
		HttpURLConnection conn = null;
		Log.i("URLConnectionUtils", "url = " + url+ "====="+url.length());
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection)u.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(20000);
//			conn.setRequestProperty("Content-Type","text/html; charset=UTF-8");
			String sCookie = CookieManager.getInstance().getCookie(url);
			if(sCookie!=null && sCookie.length()>0){
                conn.setRequestProperty("Cookie", sCookie);                  
            }
			int code = conn.getResponseCode();
			Log.i("URLConnectionUtils", "ResponseCode = " + code);
			if(200 == code){
				return IOUtils.toByteArray(conn.getInputStream());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
	
	 public static byte[] sendPost(String url,String params){
		 OutputStream out = null;
		 HttpURLConnection conn = null;
             try{
            	 	URL u = new URL(url);
            	 	conn = (HttpURLConnection)u.openConnection();
                     conn.setDoOutput(true);
                     conn.setRequestMethod("POST");
                     conn.setUseCaches(false);
                     conn.setConnectTimeout(10000);
         			conn.setReadTimeout(20000);
//         			conn.setRequestProperty("Content-Type","text/html; charset=UTF-8");
         			String sCookie = CookieManager.getInstance().getCookie(url);
        			if(sCookie!=null && sCookie.length()>0){
                        conn.setRequestProperty("Cookie", sCookie);                  
                    }
         			out = conn.getOutputStream();  
                    out.write(params.getBytes());  
                    out.flush();
                    
                    int code = conn.getResponseCode();
        			Log.i("URLConnectionUtils", "ResponseCode = " + code);
        			if(200 == code){
        				return IOUtils.toByteArray(conn.getInputStream());
        			}

             }catch(Exception e){
            	 if(out!=null){
            		 try {
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
            	 }
            	 if (conn != null) {
     				conn.disconnect();
     			}   
             }
              return null;
     }
	 
	 public static byte[] upload(String url,Uri... uris){
		 Map files = new HashMap();
		 for(Uri uri : uris){
			 if(uri != null){
			 String filename = uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
			 files.put(filename,new File(uri.getPath()));
			 Log.i("URLConnectionUtils", "filename = " + filename+" , path="+uri.getPath());
			 }
		 }
		 Log.i("URLConnectionUtils", "url = " + url);
		 return upload(url, null, files);
	 }
	 
	 public static byte[] upload(String url,Map < String , String > params , Map < String , File > files){
		 DataOutputStream out = null;
		 HttpURLConnection conn = null;
		 String BOUNDARY = UUID.randomUUID().toString(); // 定义数据分隔线
		 String PREFIX = "--";
		 String LINEND  = "\r\n";
             try{
            	 	URL u = new URL(url);
            	 	conn = (HttpURLConnection)u.openConnection();
            	 	conn.setDoInput (true) ;
                     conn.setDoOutput(true);
                     conn.setRequestMethod("POST");
//                     conn.setChunkedStreamingMode(4096*1000);  
//                     conn.setUseCaches(false);
                     conn.setRequestProperty("connection", "Keep-Alive");
//                     conn.setRequestProperty("Charsert", "UTF-8");   
                     conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
                     conn.setConnectTimeout(10000);
         			conn.setReadTimeout(20000);
         			String sCookie = CookieManager.getInstance().getCookie(url);
        			if(sCookie!=null && sCookie.length()>0){
                        conn.setRequestProperty("Cookie", sCookie);                  
                    }
        			
        			
        			StringBuilder sb = new StringBuilder () ; 
        			if(params!=null)
		                for ( Map.Entry < String , String > entry : params.entrySet () )  
		                    {  
		                        sb.append ( PREFIX ) ;  
		                        sb.append ( BOUNDARY ) ;  
		                        sb.append ( LINEND ) ;  
		                        sb.append ( "Content-Disposition: form-data; name=\""  
		                                + entry.getKey ( ) + "\"" + LINEND ) ;  
		                        sb.append ( "Content-Type: text/plain; charset=UTF-8"   + LINEND ) ;  
		                        sb.append ( "Content-Transfer-Encoding: 8bit" + LINEND ) ;  
		                        sb.append ( LINEND ) ;  
		                        sb.append ( entry.getValue ( ) ) ;  
		                        sb.append ( LINEND ) ;  
		                    }  
        			
        			
        			
         			out = new DataOutputStream(conn.getOutputStream());  
//         			Log.i("URLConnectionUtils", sb.toString());
         			out.write (sb.toString().getBytes ()) ;  
         			if ( files != null )
         				for ( Map.Entry < String , File > file : files.entrySet ( ) )  
                        {  
                            sb = new StringBuilder( ) ;  
                            sb.append( PREFIX ) ;  
                            sb.append( BOUNDARY ) ;  
                            sb.append( LINEND ) ;  
                            String contentName = "upfile";
                            if(file.getKey ().endsWith("png"))
                            	contentName = "upfile";
                            else if(!file.getKey ().endsWith("pdf"))
                            	contentName = "upsound";
                            sb.append( "Content-Disposition: form-data; name=\""+contentName+"\"; filename=\""  
                                    + file.getKey () + "\"" + LINEND ) ;  
                            sb.append( "Content-Type: application/octet-stream" + LINEND ) ;  //octet-stream; charset=UTF-8
                            sb.append( LINEND ) ;  
//                            Log.i("URLConnectionUtils", sb.toString());
                            out.write( sb.toString().getBytes () ) ;  
  
//                            Log.i("URLConnectionUtils", "filestream");
                            out.write(FileUtils.readFileToByteArray(file.getValue()));  
//                            Log.i("URLConnectionUtils", LINEND);
                            out.write (LINEND.getBytes ( )) ;  
                        }  
//         			 Log.i("URLConnectionUtils", PREFIX + BOUNDARY + PREFIX + LINEND);
         			out.write((PREFIX + BOUNDARY + PREFIX + LINEND).getBytes());
                    out.flush();
                    
                    int code = conn.getResponseCode();
        			Log.i("URLConnectionUtils", "ResponseCode = " + code);
        			if(200 == code){
        				return IOUtils.toByteArray(conn.getInputStream());
        			}

             }catch(Exception e){
            	 e.printStackTrace();
            	 if(out!=null){
            		 try {
						out.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
            	 }
            	 if (conn != null) {
     				conn.disconnect();
     			}   
             }
              return null;
     }
	 
	
	
}
