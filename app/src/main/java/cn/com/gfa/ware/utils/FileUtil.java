package cn.com.gfa.ware.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 文件工具类
 * 
 * 
 */
public class FileUtil {

	public static String TAG = "FileUtil";
	public static String cookieStr;
	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * 检验SDcard状态
	 * 
	 * @return boolean
	 */
	public static boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 从2013-02-01  12：21:23类型的字符串中取到日期和时间
	 * 
	 * @return boolean
	 */
	public static String getDate(String dateTime){
		String date=dateTime.substring(0, dateTime.indexOf(" "));
		return date;
	}
	public static String getTime(String dateTime){
		String time=dateTime.substring(dateTime.indexOf(" "));
		return time;
	}

	/**
	 * 保存文件文件到目录
	 * 
	 * @param context
	 * @return 文件保存的目录
	 */
	public static String setMkdir(Context context) {
		String filePath;
//		PackageManager pm = context.getPackageManager();
//		String appName = context.getApplicationInfo().loadLabel(pm).toString();//商务应用
		
		String dir = "customs";
		if (checkSDCard()) {
			filePath = Environment.getExternalStorageDirectory()
					+ File.separator + dir;
		} else {
			filePath = context.getCacheDir().getAbsolutePath() + File.separator
					+ dir;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			boolean b = file.mkdirs();
		} else {
		}

		return filePath;
	}

	/**
	 * 保存zip文件文件到目录
	 * 
	 * @param context
	 * @return 文件zip保存的目录
	 */
	public static String checkAndMkFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			boolean b = file.mkdirs();
		} else {
		}
		return filePath;
	}
	
	public static String checkAndMkParentDir(String filePath) {
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		} 
		return filePath;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * 
	 */
	public static boolean checkFileExist(String resID) {
		String filePath;
		filePath = Environment.getExternalStorageDirectory() + File.separator
				+ "Middleware" + File.separator + "apps/" + resID;
		File file = new File(filePath);
		boolean exists = file.exists();
		if (!file.exists()) {
			file.delete();
		}
		return file.exists();
	}

	public static String encryptDES(String encryptString, String encryptKey)
			throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return Base64.encode(encryptedData);
	}

	public static String decryptDES(String decryptString, String decryptKey)
			throws Exception {
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);
		return new String(decryptedData);
	}

	/**
	 * 得到文件的名称
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getFileName(String url) {
		String name = null;
		try {
			name = url.substring(url.lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * * 递归删除目录下的所有文件及子目录下所有文件 * @param dir 将要删除的文件目录 * @return boolean Returns
	 * "true" if all deletions were successful. * If a deletion fails, the
	 * method stops attempting to * delete and returns "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static void unZip(String unZipfileName, String mDestPath) {// unZipfileName需要解压的zip文件全路经
		FileOutputStream fileOut;
		ZipInputStream zipIn;
		ZipEntry zipEntry;
		File file;
		int readedBytes;
		byte buf[] = new byte[4096];
		try {
			zipIn = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(unZipfileName)));
			while ((zipEntry = zipIn.getNextEntry()) != null) {
				file = new File(mDestPath + File.separator + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					file.mkdirs();
				} else {
					// 如果指定文件的目录不存在,则创建之.
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					fileOut = new FileOutputStream(file);
					while ((readedBytes = zipIn.read(buf)) > 0) {
						fileOut.write(buf, 0, readedBytes);
					}
					fileOut.close();
				}
				zipIn.closeEntry();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// 解压完后把zip包删除
		File fileZip = new File(unZipfileName);
		if (fileZip.exists()) {
			fileZip.delete();
		}
	}

	// 得到json的内容
	public static String getStringJson(String strURL) {
		try {
			Log.i("getStringJson", strURL);
			URL url = new URL(strURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();

			con.setDoInput(true);
			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			
			String result = strURL +" http status code: "+ con.getResponseCode() + "\n";
			Log.i("getStringJson", "code=" + result);
			Log.i("getStringJson", "==" + con.getContentType());
			Log.i("getStringJson", "==" + con.getContentLength());
//			String content = IOUtils.toString(con.getInputStream());
//			Log.i("=============", con.getContentLength()+" = "+ result);
			
			Log.i("getStringJson", "cookie="
					+ con.getHeaderFields().get(
							"set-cookie"));
			// cookieStr=((HttpURLConnection)
			// con).getHeaderFields().get("set-cookie").toString();
			// HttpURLConnection.HTTP_OK
			// 判断服务端返回码是否正确
			if (HttpURLConnection.HTTP_OK ==con.getResponseCode()) {
				Log.i("getStringJson", con.getResponseCode()
						+ "");
				InputStream is = con.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer bab = new ByteArrayBuffer(32);
				int current = 0;
				while ((current = bis.read()) != -1) {
					bab.append((byte) current);
				}
				
				result = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
				bis.close();
				is.close();
				con.disconnect();
				return result;
			}else if(con.getResponseCode()==489){
				return "489";
			}else{
//				handler.sendEmptyMessage(489);
				return null;
			}
		} catch (Exception e) {
			Log.i("getStringJson", "数据获取失败！");
			e.printStackTrace();
		}
		return null;
	}

	// 得到json的内容
	public static String getStringJson(String strURL, Context context) {
		String result = null;
		BufferedInputStream bis = null;
		try {
			Log.i("getStringJson", strURL);
			URL url = new URL(strURL);
			URLConnection con = url.openConnection();
			con.setRequestProperty("cookie", getCookie(context));

			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			Log.i("getStringJson", "code=" + result);
			Log.i("getStringJson", "cookie="
					+ ((HttpURLConnection) con).getHeaderFields().get(
							"set-cookie"));

			// HttpURLConnection.HTTP_OK
			// 判断服务端返回码是否正确
			if (HttpURLConnection.HTTP_OK != ((HttpURLConnection) con)
					.getResponseCode()) {
				Log.i("getStringJson", ((HttpURLConnection) con)
						.getResponseCode()
						+ "");
				// return null;
			}
			bis = new BufferedInputStream(con.getInputStream());
			ByteArrayBuffer bab = new ByteArrayBuffer(32);
			int current = 0;
			while ((current = bis.read()) != -1) {
				bab.append((byte) current);
			}

			result = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
			bis.close();
			return result;
		} catch (Exception e) {
			Log.i("getStringJson", "数据获取失败！");
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
					bis = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String getCookie(Context context) {
		String cookie = cookieStr.substring(cookieStr.indexOf("GFASESSIONID"),
				cookieStr.indexOf(";"));
		Log.i(TAG, "cookie=" + cookie);
		return cookie;
	}

	public static boolean emailAdressFormat(String emailAdress) {

		String[] emails = emailAdress.split(",");
		for (int i = 0; i < emails.length; i++) {
			if (emails[i].endsWith(">")) {
				String email = emails[i].substring(emails[i].indexOf("<") + 1,
						emails[i].length() - 1);
				Log.i(TAG, "email=" + email);
				if (!emailFormat(email)) {
					return false;
				}
			} else {
				if (!emailFormat(emails[i])) {
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * 
	 * 验证输入的邮箱格式是否符合
	 * 
	 * @param email
	 * 
	 * @return 是否合法
	 */

	public static boolean emailFormat(String email) {
		Pattern pattern = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher mc = pattern.matcher(email);
		return mc.matches();
	}

	public static String changeUrl(String url, String str) {
		int index = url.indexOf('/', 8);
		String urlEnds = url.substring(index);
		url = "http://" + str + urlEnds;
		return url;
	}

	/**
	 * 判断字符串的编码
	 * 
	 * @param str
	 * @return
	 */
	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}

//	public static void attach_workedWithJson(String stringJson,
//			String downloadUrl, Handler handler, Activity context, String name) {
//		JSONObject jsonObject;
//		try {
//			jsonObject = new JSONObject(stringJson);
//			if (jsonObject.has("code")) {
//				// String code = jsonObject.getString("code");
//				String note = jsonObject.getString("note");
//				Message msg = new Message();
//				Bundle bundle = new Bundle();
//				msg.what = 0;
//				bundle.putString("code", jsonObject.getString("code"));
//				bundle.putString("note", note);
//				msg.setData(bundle);
//				handler.sendMessage(msg);
//				// if (code.equalsIgnoreCase("100201006")) {
//				// handler.sendEmptyMessage(502);
//				// return;
//				// }
//				return;
//			}
//			String fileType = jsonObject.getString("fileType");
//			if (fileType != null && !fileType.equalsIgnoreCase("")) {
//				if (!fileType.equalsIgnoreCase("zip")
//						&& !fileType.equalsIgnoreCase("rar")
//						&& !fileType.equalsIgnoreCase("txt")
//						&& !fileType.equalsIgnoreCase("log")
//						&& !FileUtil.isImageType(fileType)
//						&& !FileUtil.isPDFType(fileType)){
//					Message msg = new Message();
//					Bundle bundle = new Bundle();
//					msg.what = 0;
//					bundle.putString("code", "109000513");
//					bundle.putString("note", "不支持的文件格式");
//					msg.setData(bundle);
//					handler.sendMessage(msg);
//				}else if (fileType.equalsIgnoreCase("zip")
//						|| fileType.equalsIgnoreCase("rar")) {
//					Message msg = new Message();
//					Bundle bundle = new Bundle();
//					bundle.putString("json", stringJson);
//					msg.setData(bundle);
//					msg.what = 4;
//					handler.sendMessage(msg);
//				} else if (fileType.equalsIgnoreCase("pdf")) {
//					// pdf
//					String fileName = jsonObject.getString("fileName");
//					// fileName=new
//					// String(fileName.getBytes(getEncoding(fileName)));
//					String totalNum = jsonObject.getString("totalNum");
//					// String fileUrl=jsonObject.getString("fileUrl");
//					// 如果要下载的附件没有传过来名字，则用现在的名字代替；
//					if (name.equalsIgnoreCase("")) {
//						name = fileName;
//					}
//					File file = new File(FileUtil.checkAndMkFile(FileUtil
//							.setMkdir(context)
//							+ File.separator + "attaches/")
//							+ getMD5Str(getDownloadUrl(downloadUrl))
//							+ "."
//							+ fileType);
//					Log.i(TAG, "updateFlag="
//							+ jsonObject.getString("updateFlag"));
//					Log.i(TAG, "file.exists()=" + file.exists());
//					if (jsonObject.has("updateFlag")) {
//						if (jsonObject.getString("updateFlag")
//								.equalsIgnoreCase("")
//								&& file.exists() && file.length() != 0) {
//							Message msg = new Message();
//							Bundle bundle = new Bundle();
//							bundle.putString("fileUrl", file.getPath());
//							bundle.putString("name", name);
//							bundle.putString("type", fileType);
//							msg.setData(bundle);
//							msg.what = 3;
//							handler.sendMessage(msg);
//							return;
//						} else {
//							// 下载
//							new DownloaderThread(handler, downloadUrl
//									+ "&gfa_pageNo=1", FileUtil
//									.checkAndMkFile(FileUtil.setMkdir(context)
//											+ File.separator + "attaches/"),
//							// changeType(fileName, fileType)).start();
//									getMD5Str(getDownloadUrl(downloadUrl))
//											+ "." + fileType, name).start();
//						}
//					}
//				} else if (fileType.equalsIgnoreCase("cebx")) {
//					// pdf
//					String fileName = jsonObject.getString("fileName");
//					String totalNum = jsonObject.getString("totalNum");
//					// String fileUrl=jsonObject.getString("fileUrl");
//					if (name.equalsIgnoreCase("")) {
//						name = fileName;
//					}
//					File file = new File(FileUtil.checkAndMkFile(FileUtil
//							.setMkdir(context)
//							+ File.separator + "attaches/")
//							+ getMD5Str(getDownloadUrl(downloadUrl))
//							+ "."
//							+ fileType);
//					if (jsonObject.has("updateFlag")) {
//						if (jsonObject.getString("updateFlag")
//								.equalsIgnoreCase("")
//								&& file.exists() && file.length() != 0) {
//							Message msg = new Message();
//							Bundle bundle = new Bundle();
//							bundle.putString("fileUrl", file.getPath());
//							bundle.putString("name", name);
//							bundle.putString("type", fileType);
//							msg.setData(bundle);
//							msg.what = 3;
//							handler.sendMessage(msg);
//							return;
//						} else {
//							// 下载
//							new DownloaderThread(handler, downloadUrl
//									+ "&gfa_pageNo=1", FileUtil
//									.checkAndMkFile(FileUtil.setMkdir(context)
//											+ File.separator + "attaches/"),
//							// changeType(fileName, fileType)).start();
//									getMD5Str(getDownloadUrl(downloadUrl))
//											+ "." + fileType, name).start();
//						}
//					}
//				} else if (fileType.equalsIgnoreCase("txt")
//						|| fileType.equalsIgnoreCase("log")) {
//					// 文本
//					String fileName = jsonObject.getString("fileName");
//					String totalNum = jsonObject.getString("totalNum");
//					// String fileUrl=jsonObject.getString("fileUrl");
//					if (name.equalsIgnoreCase("")) {
//						name = fileName;
//					}
//					File file = new File(FileUtil.checkAndMkFile(FileUtil
//							.setMkdir(context)
//							+ File.separator + "attaches/")
//							+ getMD5Str(getDownloadUrl(downloadUrl))
//							+ "."
//							+ fileType);
//					if (jsonObject.has("updateFlag")) {
//						if (jsonObject.getString("updateFlag")
//								.equalsIgnoreCase("")
//								&& file.exists() && file.length() != 0) {
//							Message msg = new Message();
//							Bundle bundle = new Bundle();
//							bundle.putString("fileUrl", file.getPath());
//							bundle.putString("name", name);
//							bundle.putString("type", fileType);
//							msg.setData(bundle);
//							msg.what = 3;
//							handler.sendMessage(msg);
//							return;
//						} else {
//							// 下载
//							new DownloaderThread(handler, downloadUrl
//									+ "&gfa_pageNo=1",
//									checkAndMkFile(setMkdir(context)
//											+ File.separator + "attaches/"),
//									getMD5Str(getDownloadUrl(downloadUrl))
//											+ "." + fileType, name
//							// changeType(fileName, fileType)
//							).start();
//						}
//					}
//				} else {
//					// 图片
//					String fileName = jsonObject.getString("fileName");
//					String totalNum = jsonObject.getString("totalNum");
//					if (name.equalsIgnoreCase("")) {
//						name = fileName;
//					}
//					File file = new File(FileUtil.checkAndMkFile(FileUtil
//							.setMkdir(context)
//							+ File.separator + "attaches/")
//							+ getMD5Str(getDownloadUrl(downloadUrl))
//							+ "."
//							+ fileType);
//					if (jsonObject.has("updateFlag")) {
//						if (jsonObject.getString("updateFlag")
//								.equalsIgnoreCase("")
//								&& file.exists() && file.length() != 0) {
//							Message msg = new Message();
//							Bundle bundle = new Bundle();
//							bundle.putString("fileUrl", file.getPath());
//							bundle.putString("name", name);
//							bundle.putString("type", fileType);
//							msg.setData(bundle);
//							msg.what = 3;
//							handler.sendMessage(msg);
//							return;
//						} else {
//							// 下载
//							new DownloaderThread(handler, downloadUrl
//									+ "&gfa_pageNo=1",
//									checkAndMkFile(setMkdir(context)
//											+ File.separator + "attaches/"),
//									getMD5Str(getDownloadUrl(downloadUrl))
//											+ "." + fileType, name
//							// changeType(fileName, fileType)
//							).start();
//						}
//					}
//					// Message msg = new Message();
//					// Bundle bundle = new Bundle();
//					// bundle.putString("fileUrl", downloadUrl +
//					// "&gfa_pageNo=1");
//					// msg.setData(bundle);
//					// msg.what = 5;
//					// handler.sendMessage(msg);
//				}
//			} else {
//				Message msg = new Message();
//				Bundle bundle = new Bundle();
//				msg.what = 0;
//				bundle.putString("code", "109000513");
//				bundle.putString("note", "不支持的文件格式");
//				msg.setData(bundle);
//				handler.sendMessage(msg);
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			Message msg = new Message();
//			Bundle bundle = new Bundle();
//			msg.what = 0;
//			bundle.putString("code", "109000513");
//			bundle.putString("note", "不支持的文件格式");
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//			e.printStackTrace();
//		}
//	}

	public static boolean isImageType(String type) {
		if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("png")
				|| type.equalsIgnoreCase("jif")
				|| type.equalsIgnoreCase("jpeg")
				|| type.equalsIgnoreCase("bmp")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPDFType(String type) {
		if (type.equalsIgnoreCase("doc") || type.equalsIgnoreCase("docx")
				|| type.equalsIgnoreCase("docm")
				|| type.equalsIgnoreCase("pdf") || type.equalsIgnoreCase("ppt")
				|| type.equalsIgnoreCase("pps")
				|| type.equalsIgnoreCase("pptx")
				|| type.equalsIgnoreCase("xls")
				|| type.equalsIgnoreCase("xlsm")
				|| type.equalsIgnoreCase("xlsx")
				|| type.equalsIgnoreCase("ett") || type.equalsIgnoreCase("et")
				|| type.equalsIgnoreCase("wps") || type.equalsIgnoreCase("wpt")
				|| type.equalsIgnoreCase("xlt") || type.equalsIgnoreCase("dps")
				|| type.equalsIgnoreCase("dpt")) {
			return true;
		} else {
			return false;
		}
	}

	public static String changeType(String fileName, String type) {
		String name = fileName.substring(0, fileName.lastIndexOf("."));
		fileName = name + "." + type;
		return fileName;

	}

	public static String getDownloadUrl(String s) {
		String download = s.substring(s.indexOf("url=")).trim();
		return download;
	}

	/*
	 * MD5加密
	 */
	private static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		// 16位加密，从第9位到25位
		return md5StrBuff.substring(8, 24).toString().toUpperCase();
	}
	/*
	 * private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
	 * '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	 * 
	 * public static String toHexString(byte[] b) {
	 * 
	 * // String to byte
	 * 
	 * StringBuilder sb = new StringBuilder(b.length * 2); for (int i = 0; i <
	 * b.length; i++) { sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
	 * sb.append(HEX_DIGITS[b[i] & 0x0f]); } return sb.toString(); } /* MD5加密
	 */
	/*
	 * public static String getMD5Str(String s) { try { // Create MD5 Hash
	 * MessageDigest digest = java.security.MessageDigest .getInstance("MD5");
	 * digest.update(s.getBytes()); byte messageDigest[] = digest.digest();
	 * return toHexString(messageDigest); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); } return ""; }
	 */
}
