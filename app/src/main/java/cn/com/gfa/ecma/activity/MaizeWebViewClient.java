package cn.com.gfa.ecma.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLDecoder;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MaizeWebViewClient extends WebViewClient {
	
	private static final String TAG = "MaizeWebViewClient";
	Activity maize;
	MaizeWebView appView;
	String currURL = null;
	byte[] currData = null;
	
	public MaizeWebViewClient(Activity activity) {
        this.maize = activity;
    }
	public MaizeWebViewClient(Activity activity, MaizeWebView view) {
        this.maize = activity;
        this.appView = view;
    }
	
	
	 public void setWebView(MaizeWebView view) {
	        this.appView = view;
	    }
//	 public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//		 Log.i(TAG, "shouldOverrideKeyEvent: "+view);
//		 return true;
//	 }
	 
	 
//	 MaizeWebView mErrorView;
	 static boolean mIsErrorPage = false;
//	 protected void showErrorPage() {  
//		     LinearLayout webParentView = (LinearLayout)appView.getParent();  
//		       
//		     initErrorPage();  
//		     while (webParentView.getChildCount() > 1) {  
//		         webParentView.removeViewAt(0);  
//		     }  
//		     LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
//		     webParentView.addView(mErrorView, 0, lp);  
//		     mIsErrorPage = true;  
//		 }  
//		 protected void hideErrorPage() {  
//		     LinearLayout webParentView = (LinearLayout)appView.getParent();  
//		       
//		     mIsErrorPage = false;  
//		     while (webParentView.getChildCount() > 1) {  
//		         webParentView.removeViewAt(0);  
//		     }  
//		 }  
//		   
//		   
//		    protected void initErrorPage() {  
//		     if (mErrorView == null) {  
//		         mErrorView = View.inflate(this, R.layout.online_error, null);  
//		         Button button = (Button)mErrorView.findViewById(R.id.online_error_btn_retry);  
//		         button.setOnClickListener(new OnClickListener() {  
//		             public void onClick(View v) {  
//		            	 appView.reload();  
//		             }  
//		         });  
//		         mErrorView.setOnClickListener(null);  
//		     }  
//		 }
	 
	 
	 protected Handler handler = new Handler(){
	         public void handleMessage(Message message){
	             switch (message.what) {
	             case 0:
	                 break;
	             case 1:
//	            	 try{
////	            	 appView.getWebViewClient().shouldOverrideUrlLoading(appView, "file:///android_asset/app/ec/error.html");
//	            	int index = appView.copyBackForwardList().getSize();
//	            	Log.i(TAG, "size1=="+index);
	            	appView.loadUrl("file:///android_asset/customs/error.html");
//	                 
//	                 Class wv = Class.forName("android.webkit.WebView");
//	                 Field mCallbackProxyField = wv.getDeclaredField("mCallbackProxy");
//	                 Object mCallbackProxy = getFieldValueSafely(mCallbackProxyField, appView);
//	                 
//	                 Class wbfl = Class.forName("android.webkit.CallbackProxy");
//	                 Field mBackForwardListFild = wbfl.getDeclaredField("mBackForwardList");
//	                 Object mBackForwardList = getFieldValueSafely(mBackForwardListFild, mCallbackProxy);
//
//	                 
//	                 Method removeHistoryItem = mBackForwardList.getClass().getDeclaredMethod("removeHistoryItem", int.class);
//	                
//	                 removeHistoryItem.setAccessible(true);
//	                 
//	                 removeHistoryItem.invoke(mBackForwardList, index);
//	                 Log.i(TAG, "size2=="+appView.copyBackForwardList().getSize());
	                 mIsErrorPage = true;
////	                 new WebBackForwardListClient();
////	                 WebHistoryItem a= list.getCurrentItem();
////	                 WebHistoryItem b= list.getItemAtIndex(list.getSize()-1);
////	                 System.out.println(a.getUrl()+" === "+b.getUrl());
////	            	 appView.showWebPage("file:///android_asset/app/ec/error.html", false, false, null);
////	            	 String data = null;
////					try {
////						data = IOUtils.toString(maize.openFileInput("file:///android_asset/app/ec/error.html"));
////					} catch (FileNotFoundException e) {
////						e.printStackTrace();
////					} catch (IOException e) {
////						e.printStackTrace();
////					}
////	            	 Log.i(TAG, "===="+message.obj);
////	            	 appView.loadData("<a href='#' onclick='location.reload()'>aaaaa</a>", "text/html","utf-8");
//	                 break;
//	            	 } catch (ClassNotFoundException e){
//	            		 e.printStackTrace();
//	            	 } catch (NoSuchFieldException e) {
//						e.printStackTrace();
//					} catch (IllegalArgumentException e) {
//						e.printStackTrace();
//					} catch (IllegalAccessException e) {
//						e.printStackTrace();
//					} catch (NoSuchMethodException e) {
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						e.printStackTrace();
//					}
	                 break;
	             case 2:
	                 break;
	             }
	         }
	     };
	 
	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		Log.i(TAG, "onReceivedHttpAuthRequest: "+view.getUrl());
		super.onReceivedHttpAuthRequest(view, handler, host, realm);
	}
	@Override
	public void onLoadResource(WebView view, String url) {
//		Log.i(TAG, "onLoadResource: "+url);
		super.onLoadResource(view, url);
	}
	@Override
	public void onPageFinished(WebView view, String url) {
//		Log.i(TAG, "onPageFinished: "+url);
//		view.loadUrl("javascript:window.handler.show(document.body.innerHTML);");
//		if(url.startsWith("file:")){
////			this.maize.linearlayout.setVisibility(0);
//			this.maize.pdfView.setVisibility(8);
//			this.maize.sp0View.setVisibility(0);
//			this.maize.sp1View.setVisibility(8);
//			this.maize.preView.setVisibility(8);
//			this.maize.sp2View.setVisibility(8);
//			this.maize.nextView.setVisibility(8);
//			this.maize.sp3View.setVisibility(8);
//			this.maize.subView.setVisibility(8);
//			this.maize.forcePortrait = true;
//			this.maize.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		}else{
//			this.maize.linearlayout.setVisibility(8);
////			this.maize.backUrl = url;
//			this.maize.pdfView.setVisibility(8);
//			this.maize.sp0View.setVisibility(8);
//			this.maize.sp1View.setVisibility(8);
//			this.maize.preView.setVisibility(8);
//			this.maize.sp2View.setVisibility(8);
//			this.maize.nextView.setVisibility(8);
//			this.maize.sp3View.setVisibility(0);
//			this.maize.subView.setVisibility(0);
//			this.maize.forcePortrait = false;
//			this.maize.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//SCREEN_ORIENTATION_UNSPECIFIED
//		}
//		if(url.startsWith("file:") || !url.contains("/eps/")){
//			this.maize.linearlayout.setVisibility(8);
//		}else{
//			this.maize.linearlayout.setVisibility(0);
//			this.maize.pdfView.setVisibility(8);
//			this.maize.sp0View.setVisibility(8);
//			this.maize.sp1View.setVisibility(8);
//			this.maize.preView.setVisibility(8);
//			this.maize.sp2View.setVisibility(8);
//			this.maize.nextView.setVisibility(8);
//			this.maize.sp3View.setVisibility(0);
//			this.maize.subView.setVisibility(0);
//		}
		((MaizeI)maize).removeSplashScreen();
//		if(!appView.getSettings().getLoadsImagesAutomatically()) {
//			appView.getSettings().setLoadsImagesAutomatically(true);
//	    }
		super.onPageFinished(view, url); 
		this.appView.loadUrlTimeout++;
	}
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
//		Log.i(TAG, "onPageStarted: "+url);
		super.onPageStarted(view, url, favicon);
		if(!mIsErrorPage && url.endsWith("error.html")){
//			view.stopLoading();
			mIsErrorPage = false;
			Log.i(TAG, "onPageStarted==================error.html: "+url);
			view.goBackOrForward(-1);
		}
	}
	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		Log.i(TAG, "onReceivedError: "+failingUrl+" , "+description+" , "+errorCode);
		super.onReceivedError(view, errorCode, description, failingUrl);
//		view.stopLoading();
//		view.clearView();
//		if(errorCode!=-2){
			Message msg=handler.obtainMessage();//发送通知，加入线程
			msg.what=1;
			msg.obj=failingUrl;
			handler.sendMessage(msg);
//		}

	}
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//		Log.i(TAG, "shouldInterceptRequest: "+url);
		WebResourceResponse ret = super.shouldInterceptRequest(view, url);
        return ret;
	}
	
	
	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		Log.i(TAG, "onFormResubmission: "+view);
		super.onFormResubmission(view, dontResend, resend);
	}
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.i(TAG, "shouldOverrideUrlLoading: "+url);
		mIsErrorPage = false;
//		maize.backUrl = url;
		if (url.startsWith(WebView.SCHEME_TEL)) {
			return true;
		} else if (url.startsWith("newtab:")) {
			String realUrl=url.substring(7,url.length());
			Intent it = new Intent(Intent.ACTION_VIEW);
			it.setData(Uri.parse(realUrl));
			maize.startActivity(it);
		}else if (url.startsWith("geo:")) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				maize.startActivity(intent);
			} catch (android.content.ActivityNotFoundException e) {
				Log.e(TAG, "Error showing map " + url + ": " + e.toString());
			}
		} else if (url.startsWith(WebView.SCHEME_MAILTO)) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				maize.startActivity(intent);
			} catch (android.content.ActivityNotFoundException e) {
				Log.e(TAG, "Error sending email " + url + ": " + e.toString());
			}
		} else if (url.startsWith("file://") || url.startsWith("data:")) {
			return false;
		}/* else if (url.endsWith(".pdf") || url.indexOf("templatetype=1")>0) {
			
			url = URLDecoder.decode(url);
			String path = FileUtil.checkAndMkFile(FileUtil.setMkdir(maize)+File.separator);
			String name = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+".pdf";
			if(url.indexOf("templatename=")>0){
				name = url.substring(url.indexOf("templatename=")+ 13)+".pdf";
			}
			File file = new File(path+name);
			
			
			byte[] data = null;
			try {
//				data = URLConnectionUtils.sendGet(url);
				
				if (data  == null) {
					appView.setProcess(false);
					appView.getWebChromeClient().showMessage(
							"请求的服务器出现问题,请稍后重试.");
					return true;
				}
				FileUtils.writeByteArrayToFile(file, data);
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			if(data.length>5 &&data[0]==0x25 &&data[1]==0x50 &&data[2]==0x44 &&data[3]==0x46 &&data[4]==0x2D){
				url= file.getAbsolutePath();
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(maize,MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				maize.startActivityForResult(intent,0);
				return true;
				
			}
			view.loadDataWithBaseURL(url, new String(data), "html/text", "UTF-8", url);
//			view.loadUrl(url);
		} */else {
				
//			final boolean isNetworkAvailable = CheckNetsUtil
//					.isNetworkAvailable(maize);
//			if (!isNetworkAvailable) {
//				appView.getWebChromeClient().showMessage("你的设备没有连接网络,请检查网络设置.");
//				appView.setProcess(false);
//				return true;
//			}
//
//			HttpURLConnection conn = null;
//			InputStream input = null;
//			byte[] data = null;
//			try {
//				String ip = maize.getProperty("proxy.ip");
//				Integer port = maize.getIntegerProperty("proxy.port");
//				if(ip!=null && port!=null){
//					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
//					conn = (HttpURLConnection) new URL(url).openConnection(proxy);
//				}else{
//					conn = (HttpURLConnection) new URL(url).openConnection();
//				}
//				conn.setDoInput(true);
//				conn.setConnectTimeout(10000);
//				conn.setReadTimeout(10000);
//				int code = conn.getResponseCode();
//				Log.i("MaizeWebViewClient", "ResponseCode = " + code);
//				if (code != 200) {
//					appView.setProcess(false);
//					appView.getWebChromeClient().showMessage(
//							"请求的服务器出现问题,请稍后重试.");
//					return true;
//				}
//				input = conn.getInputStream();
//				data = IOUtils.toByteArray(input);
//				String temp = url.substring(url.length()-6);
//				if(!temp.endsWith("/") && temp.indexOf(".")==-1){
//					url +="/";
//				}
////				System.out.println("url= "+url);
//				view.loadDataWithBaseURL(url, new String(data), "text/html",
//						"utf-8", url);
//				appView.setProcess(true);
//			} catch (MalformedURLException e) {
//				appView.setProcess(false);
//				appView.getWebChromeClient().showMessage("您的访问地址不合法.");
//			} catch (SocketTimeoutException e) {
//				appView.setProcess(false);
//				appView.getWebChromeClient().showMessage(
//						"无线网络信号弱或中断,请您检查网络或稍后再试");
//			} catch (IOException e) {
//				appView.setProcess(false);
//				e.printStackTrace();
//				appView.getWebChromeClient().showMessage(
//						"无线网络信号弱或中断,请您检查网络或稍后再试.");
//			} finally {
//				if (input != null) {
//					try {
//						input.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				if (conn != null) {
//					conn.disconnect();
//				}
//			}
		}
		 view.loadUrl(url);
		return true;
	}
	
	
	
	@Override
	public void onReceivedSslError(WebView view,
			SslErrorHandler handler, SslError error) {
		// TODO Auto-generated method stub
		handler.proceed();
		super.onReceivedSslError(view, handler, error);
	}
	
	 private static boolean needsIceCreamSpaceInAssetUrlFix(String url) {
	        if (!url.contains("%20")){
	            return false;
	        }

	        switch(android.os.Build.VERSION.SDK_INT){
	            case android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH:
	            case android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
	                return true;
	            default:
	                return false;
	        }
	    }
	 
	 
	 private Object getFieldValueSafely(Field field, Object classInstance) throws IllegalArgumentException, IllegalAccessException {
	        boolean oldAccessibleValue = field.isAccessible();
	        field.setAccessible(true);
	        Object result = field.get(classInstance);
	        field.setAccessible(oldAccessibleValue);
	        return result;      
	    }
}
