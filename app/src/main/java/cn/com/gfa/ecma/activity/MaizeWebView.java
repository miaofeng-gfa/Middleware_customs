package cn.com.gfa.ecma.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Proxy;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.com.gfa.ware.R;
import cn.com.gfa.ware.baidu.LocationActivity;
import cn.com.gfa.ware.utils.DataCleanManager;
import cn.com.gfa.ware.utils.DeviceUtil;
import cn.com.gfa.ware.utils.FileUtil;
import cn.com.gfa.ware.utils.URLConnectionUtils;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;


public class MaizeWebView extends WebView {

	public static final String TAG = "MaizeWebView";
	private String url; 
	int loadUrlTimeout = 0;
	MaizeWebViewClient viewClient;
	MaizeWebChromeClient chromeClient;
	Activity maize;
	private Handler mHandler = new Handler();
	
	private BroadcastReceiver receiver;
	
	private boolean isProcess = true;
	
	DownloadManager mDownloadManager = null;
	long downloadId = 0;
	

	@Override
	public void destroy() {
		Log.i(TAG, "==destroy()==");
		maize.unregisterReceiver(downReceiver);
		super.destroy();
		
	}

	public boolean isProcess() {
		return isProcess;
	}

	public void setProcess(boolean isProcess) {
		this.isProcess = isProcess;
	}


	private View mCustomView;
	    private WebChromeClient.CustomViewCallback mCustomViewCallback;
	    
	    static final FrameLayout.LayoutParams COVER_SCREEN_GRAVITY_CENTER =
	            new FrameLayout.LayoutParams(
	            ViewGroup.LayoutParams.MATCH_PARENT,
	            ViewGroup.LayoutParams.MATCH_PARENT,
	            Gravity.CENTER);


	public MaizeWebView(Context context) {
		super(context);
		maize = (Activity)context;
		this.setWebViewClient(new MaizeWebViewClient(maize, this));
		this.setWebChromeClient(new MaizeWebChromeClient(maize, this));
		this.setup();
	}
	
//	public Activity getMaize() {
//		return maize;
//	}
//
//
//
//	public void setMaize(Activity maize) {
//		this.maize = maize;
//	}
	@SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
	private void setup() {
        this.setInitialScale(0);
        this.setVerticalScrollBarEnabled(false);
        this.requestFocusFromTouch();
        this.addJavascriptInterface(new OpenApp(), "openApp");
        this.addJavascriptInterface(new DataHandler(), "handler");
        this.addJavascriptInterface(new App(), "App");

        // Enable JavaScript
        WebSettings settings = this.getSettings();
        settings.setJavaScriptEnabled(true);
        
//        settings.setPluginsEnabled(true);  
        settings.setAllowFileAccess(true);  
//        settings.setAllowFileAccessFromFileURLs(true);
//        settings.setPluginState(PluginState.ON);
        
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        settings.setLoadWithOverviewMode(true);
//        this.clearCache(true);
        
        boolean isNetwork = DeviceUtil.isConnect(maize);
      if(isNetwork)
      	settings.setCacheMode(WebSettings.LOAD_DEFAULT);
      else
      	settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        
        
        settings.setSupportZoom(true);
//        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true); 
//只有API版本大于等于11的时候才有以下方法来隐藏按钮
        settings.setDisplayZoomControls(false);//隐藏缩放按钮
        settings.setSupportMultipleWindows(true);
        
        //We don't save any form data in the application
//        settings.setSaveFormData(false);
//        settings.setSavePassword(false);
        
        // Jellybean rightfully tried to lock this down. Too bad they didn't give us a whitelist
        // while we do this
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            Level16Apis.enableUniversalAccess(settings);
        // Enable database
        // We keep this disabled because we use or shim to get around DOM_EXCEPTION_ERROR_16
        String databasePath = maize.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databasePath);
        
        settings.setGeolocationDatabasePath(databasePath);

        // Enable DOM storage
        settings.setDomStorageEnabled(true);

        // Enable built-in geolocation
        settings.setGeolocationEnabled(true);
        
        // Enable AppCache
        // Fix for CB-2282
        settings.setAppCacheMaxSize(5 * 1048576);
//        settings.setAllowFileAccess(true);
        String pathToCache = maize.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(pathToCache);
        settings.setAppCacheEnabled(true);
//        settings.getUserAgent();//0
//        settings.getUserAgentString();//Mozilla/5.0 (Linux; U; Android 4.0.4; zh-cn; MediaPad 10 FHD Build/HuaweiMediaPad) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30
//       setProxy(this, maize.getProperty("proxy.ip"), maize.getIntegerProperty("proxy.port"));
        
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
//        if (this.receiver == null) {
//            this.receiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    updateUserAgentString();
//                }
//            };
//            maize.registerReceiver(this.receiver, intentFilter);
//        }
        
        this.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                if (url != null && url.startsWith("http://"))
//                	WebViewActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            	request.setDestinationInExternalPublicDir("download", Uri.parse(url).getLastPathSegment());
            	Log.i(TAG,"download = "+Uri.parse(url).getLastPathSegment());
//            	request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI); 
            	request.setAllowedOverRoaming(false);
            	request.setDescription(maize.getString(R.string.app_name));
//            	request.setMimeType("application/vnd.android.package-archive");
            	// 设置UI是否可见 
            	request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); 
            	request.setVisibleInDownloadsUi(true);
            	request.allowScanningByMediaScanner();
            	
            	if(mDownloadManager == null){
            		mDownloadManager = (DownloadManager) maize.getSystemService(Context.DOWNLOAD_SERVICE);
            	}
            	downloadId = mDownloadManager.enqueue(request);  
            }
        });
        
        maize.registerReceiver(downReceiver, filter);
    }
	
	IntentFilter filter =  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);  
    
	BroadcastReceiver downReceiver =  new  BroadcastReceiver() {  
	   @Override   
	   public   void  onReceive(Context context, Intent intent) {  
	     long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
	     Log.i(TAG,"downloadId = "+downloadId +" , reference="+reference);
	     if  (downloadId == reference) {
	    	 	downloadId = 0;
	    	 	String serviceString = Context.DOWNLOAD_SERVICE;
	            DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
	            Intent install = new Intent(Intent.ACTION_VIEW);
	            Uri downloadFileUri = dManager.getUriForDownloadedFile(reference);
	            Log.i(TAG,"downloadFileUri = "+(downloadFileUri==null? "null":downloadFileUri.getPath()));
	            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//	            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(install);
	    }  
	  }  
	}; 
	
	
	public void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // This code is adapted from the original Android Browser code, licensed under the Apache License, Version 2.0
        Log.d(TAG, "showing Custom View");
        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        
        // Store the view and its callback for later (to kill it properly)
        mCustomView = view;
        mCustomViewCallback = callback;
        
        // Add the custom view to its container.
        ViewGroup parent = (ViewGroup) this.getParent();
        parent.addView(view, COVER_SCREEN_GRAVITY_CENTER);
        
        // Hide the content view.
        this.setVisibility(View.GONE);
        
        // Finally show the custom view container.
        parent.setVisibility(View.VISIBLE);
        parent.bringToFront();
    }
	
	public void hideCustomView() {
        // This code is adapted from the original Android Browser code, licensed under the Apache License, Version 2.0
        Log.d(TAG, "Hidding Custom View");
        if (mCustomView == null) return;

        // Hide the custom view.
        mCustomView.setVisibility(View.GONE);
        
        // Remove the custom view from its container.
        ViewGroup parent = (ViewGroup) this.getParent();
        parent.removeView(mCustomView);
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        
        // Show the content view.
        this.setVisibility(View.VISIBLE);
    }
	
	@TargetApi(16)
    private static class Level16Apis {
        static void enableUniversalAccess(WebSettings settings) {
//            settings.setAllowUniversalAccessFromFileURLs(true);
        }
    }
    
    private void updateUserAgentString() {
        this.getSettings().getUserAgentString();
    }
    
    
    public void showWebPage(String url, boolean openExternal, boolean clearHistory, HashMap<String, Object> params) {

        // If clearing history
        if (clearHistory) {
            this.clearHistory();
        }

        // If loading into our webview
        if (!openExternal) {

            // Make sure url is in whitelist
            if (url.startsWith("file://")) {
                // TODO: What about params?
                // Load new URL
                this.loadUrl(url);
            }
            // Load in default viewer if not
            else {
                Log.w(TAG, "showWebPage: Cannot load URL into webview since it is not in white list.  Loading into browser instead. (URL=" + url + ")");
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    maize.startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Log.e(TAG, "Error loading url " + url, e);
                }
            }
        }

        // Load in default view intent
        else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                maize.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.e(TAG, "Error loading url " + url, e);
            }
        }
    }
    
    
    
    
    

    /**
     * Set the WebViewClient.
     *
     * @param client
     */
    public void setWebViewClient(MaizeWebViewClient client) {
        this.viewClient = client;
        super.setWebViewClient(client);
    }
    
    public MaizeWebViewClient getWebViewClient() {
        return this.viewClient;
    }

    /**
     * Set the WebChromeClient.
     *
     * @param client
     */
    public void setWebChromeClient(MaizeWebChromeClient client) {
        this.chromeClient = client;
        super.setWebChromeClient(client);
    }
    
    public MaizeWebChromeClient getWebChromeClient() {
        return this.chromeClient;
    }


    
    
    public String getProperty(String name, String defaultValue) {
        Bundle bundle = maize.getIntent().getExtras();
        if (bundle == null) {
            return defaultValue;
        }
        Object p = bundle.get(name);
        if (p == null) {
            return defaultValue;
        }
        return p.toString();
    }
    
    void loadUrlNow(String url) {
    	Log.i(TAG, "loadUrlNow: " + url);
//        if (url.startsWith("file://") || url.startsWith("javascript:")) {
    		try{
    			super.loadUrl(url);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
            
//        }
    }
    
    @Override
    public void loadUrl(String url) {
        if (url.equals("about:blank") || url.startsWith("javascript:")) {
            this.loadUrlNow(url);
        }
        else {

            String initUrl = this.getProperty("url", null);

            // If first page of app, then set URL to load to be the one passed in
            if (initUrl == null) {
                this.loadUrlIntoView(url);
            }
            // Otherwise use the URL specified in the activity's extras bundle
            else {
                this.loadUrlIntoView(initUrl);
            }
        }
    }
    
    public void loadUrlIntoView(final String url, final int time) {

        // If not first page of app, then load immediately
        // Add support for browser history if we use it.
        if ((url.startsWith("javascript:")) || this.canGoBack()) {
        }


        // Load url
        this.loadUrlIntoView(url);
    }
    
    
    public void loadUrlIntoView(final String url) {
        Log.d(TAG, ">>> loadUrl(" + url + ")");

        this.url = url;


        // Create a timeout timer for loadUrl
        final  MaizeWebView me = MaizeWebView.this;
        final int currentLoadUrlTimeout = me.loadUrlTimeout;
        final int loadUrlTimeoutValue = Integer.parseInt(this.getProperty("loadUrlTimeoutValue", "20000"));

        // Timeout error method
        final Runnable loadError = new Runnable() {
            public void run() {
            	if(MaizeWebView.this!=null){
            		try{
	            		me.stopLoading();
		                Log.e(TAG, "MaizeWebView: TIMEOUT ERROR!");
		                if (viewClient != null) {
		                    viewClient.onReceivedError(me, -6, "The connection to the server was unsuccessful.", url);
		                }
            		}catch(Exception e){
            			e.printStackTrace();
            		}
            	}
            }
        };

        // Timeout timer method
        final Runnable timeoutCheck = new Runnable() {
            public void run() {
                try {
                    synchronized (this) {
                        wait(loadUrlTimeoutValue);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // If timeout, then stop loading and handle error
                if (me.loadUrlTimeout == currentLoadUrlTimeout) {
                    maize.runOnUiThread(loadError);
                }
            }
        };

        // Load url
        maize.runOnUiThread(new Runnable() {
            public void run() {
                Thread thread = new Thread(timeoutCheck);
                thread.start();
                me.loadUrlNow(url);
            }
        });
    }

    /**
     * Load the url into the webview after waiting for period of time.
     * This is used to display the splashscreen for certain amount of time.
     *
     * @param url
     * @param time              The number of ms to wait before loading webview
     */
    public void loadUrl(final String url, int time) {
    	Log.i(TAG, "loadUrl: " + url);
        String initUrl = this.getProperty("url", null);

        // If first page of app, then set URL to load to be the one passed in
        if (initUrl == null) {
            this.loadUrlIntoView(url, time);
        }
        // Otherwise use the URL specified in the activity's extras bundle
        else {
            this.loadUrlIntoView(initUrl);
        }
    }    
    
    

	public boolean backHistory() {
        // Check webview first to see if there is a history
        // This is needed to support curPage#diffLink, since they are added to appView's history, but not our history url array (JQMobile behavior)
        if (super.canGoBack()) {
            super.goBack();
            
            return true;
        }
        return false;
    }
	
	class App
	{
		@JavascriptInterface
		public String getVersion() {
			String version = "";
			try {
				PackageManager manager = maize.getPackageManager();
				PackageInfo info = manager.getPackageInfo(
						maize.getPackageName(), 0);
				version = info.versionName;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return version;
		}
		
		@JavascriptInterface
		public void close() { 
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					maize.finish();
					maize.overridePendingTransition(R.anim.activity_fixed,
   							R.anim.activity_right_out);
				}
			});
		}
		
		@JavascriptInterface
		public void goBack() { 
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (((MaizeI) maize).getAppView().canGoBack()) {
						((MaizeI) maize).getAppView().goBack();
					}else{
						if(maize instanceof MaizeActivity)
							((MaizeI) maize).getAppView().loadUrl(((MaizeActivity) maize).portalUrl);
					}
				}
			});
		}
		
		@JavascriptInterface
		public void clearCache(final String callback) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						((MaizeI)maize).getAppView().clearCache(true);
						DataCleanManager.cleanApplicationData(maize);
//						DataCleanManager.clearAppUserData(maize.getPackageName());
						((MaizeI)maize).getAppView().loadUrl("javascript:"+callback+"(" + "0" +")");
					}

				});
				
		}
		
		@JavascriptInterface
		public void openExplore(final String url) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						Intent intent = new Intent();        
				        intent.setAction("android.intent.action.VIEW");    
				        Uri content_url = Uri.parse(url);   
				        intent.setData(content_url);  
				        maize.startActivity(intent);
					}
				});
		}
		
		@JavascriptInterface
		public void sendGet(final String url, final String callback) {
			System.out.println("sendGet==============================="+url);
			new Thread(){
					@Override
					public void run() {
						final byte[] data = URLConnectionUtils.sendGet(url);
						
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if(data!=null)
									((MaizeI)maize).getAppView().loadUrl("javascript:"+callback+"('" + new String(data).replaceAll("[\r\n\t]", "").replace("'", "\"") +"')");
								else
									((MaizeI)maize).getAppView().loadUrl("javascript:"+callback+"('')");
							}
						}
						);
					}
			}.start();
		}
		
		@JavascriptInterface
		public void newTab(final String url) {
			
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
//						MaizeWebView webView = new MaizeWebView(maize);
//						((MaizeActivity)maize).getAppView().loadUrl("file:///android_asset/customs/tgdh/one/page.html");
						
						Intent it = new Intent();
	   					it.setClass(maize, WebDetailActivity.class);
	   					it.putExtra("tourl", url);
	   					maize.startActivityForResult(it, 0);
	   					maize.overridePendingTransition(R.anim.activity_left_in,
	   							R.anim.activity_fixed);
					}
				});
		}
		
		
		@JavascriptInterface
		public void newTabWithTop(final String url) {
			
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						Intent it = new Intent();
	   					it.setClass(maize, WebDetail2Activity.class);
	   					it.putExtra("tourl", url);
	   					maize.startActivityForResult(it, 0);
	   					maize.overridePendingTransition(R.anim.activity_left_in,
	   							R.anim.activity_fixed);
					}
				});
		}
		
		
		@JavascriptInterface
   		public void openBaiduLBS() { 
   			mHandler.post(new Runnable() {
   				
   				@Override
   				public void run() {
   					Intent it = new Intent();
   					it.setClass(maize, LocationActivity.class);
   					maize.startActivityForResult(it, 0);
   					maize.overridePendingTransition(R.anim.activity_left_in,
   							R.anim.activity_fixed);
   				}
   			});
   		}
		
		
		@JavascriptInterface
   		public void openBaiduLBS(final String name, final String region) { 
			System.out.println("sdkShare================ "+name+" , "+region);
   			mHandler.post(new Runnable() {
   				
   				@Override
   				public void run() {
   					Intent it = new Intent();
   					it.setClass(maize, LocationActivity.class);
   					it.putExtra("name", name);
   					it.putExtra("region", region);
   					maize.startActivityForResult(it, 0);
   					maize.overridePendingTransition(R.anim.activity_left_in,
   							R.anim.activity_fixed);
   				}
   			});
   		}
		
		
		
		
		@JavascriptInterface
   		public void sdkShare(final String subject, final String text, final String imageUrl, final String platform, final String url, final String callback) { 
			System.out.println("sdkShare================ "+subject+" , "+text+" , " + imageUrl+" , "+platform+" , "+url+" , "+callback);
   			mHandler.post(new Runnable() {
   				
   				@Override
   				public void run() {
   					
//   					showShare(maize,true);
   					showShare();
   				}
   				
   				
   				
   				private final String APP_ID = "wxd33ddbf48620ee0d";
   			    private IWXAPI api = null;
   			    public void wxShare(Activity htmlActivity, boolean isFriendQ){
   			    	try {
   			    		api = WXAPIFactory.createWXAPI(htmlActivity, APP_ID,false);
   			    		api.registerApp(APP_ID);
   			    		
//   						String url = "http://www.customs.gov.cn/";
   						
   						WXWebpageObject localWXWebpageObject = new WXWebpageObject();
   						localWXWebpageObject.webpageUrl = url;
   						WXMediaMessage localWXMediaMessage = new WXMediaMessage(
   						localWXWebpageObject);
   						localWXMediaMessage.title = subject;//
   						localWXMediaMessage.description = text;
   						Bitmap bmp = BitmapFactory.decodeResource(maize.getResources(), R.drawable.ic_launcher);
   						localWXMediaMessage.thumbData = getBitmapBytes(bmp, false);
   						SendMessageToWX.Req localReq = new SendMessageToWX.Req();
   						localReq.transaction = System.currentTimeMillis() + "";
   						localReq.message = localWXMediaMessage;
//   						localReq.scene = SendMessageToWX.Req.WXSceneChooseByUser ; WP
   						localReq.scene = isFriendQ ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
   						api.sendReq(localReq);
   					} catch (Exception e) {
   						e.printStackTrace();
   						Toast.makeText(htmlActivity.getApplicationContext(), e.getMessage(),
   								Toast.LENGTH_SHORT).show();
   					}
   			    }
   			    
   			 private byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
   				Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
   				Canvas localCanvas = new Canvas(localBitmap);
   				int i;
   				int j;
   				if (bitmap.getHeight() > bitmap.getWidth()) {
   					i = bitmap.getWidth();
   					j = bitmap.getWidth();
   				} else {
   					i = bitmap.getHeight();
   					j = bitmap.getHeight();
   				}
   				while (true) {
   					localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0,
   					80, 80), null);
   					if (paramBoolean)
   					bitmap.recycle();
   					ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
   					localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
   					localByteArrayOutputStream);
   					localBitmap.recycle();
   					byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
   					try {
   						localByteArrayOutputStream.close();
   						return arrayOfByte;
   					} catch (Exception e) {
   						e.printStackTrace();
   					}
   					i = bitmap.getHeight();
   					j = bitmap.getHeight();
   				}
   			}
   				
   				
				private void showShare() {
					ShareSDK.initSDK(maize);
					OnekeyShare oks = new OnekeyShare();
					// 关闭sso授权
					oks.disableSSOWhenAuthorize();

					// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
					// oks.setNotification(R.drawable.ic_launcher,
					// getString(R.string.app_name));
					// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
					oks.setTitle(subject);
					// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
					oks.setTitleUrl(url);
					// text是分享文本，所有平台都需要这个字段
					oks.setText(text);
					// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
					String fileName =FileUtil.checkAndMkParentDir(FileUtil.setMkdir(maize)+"/customslogo.png");
					File file = new File(fileName);
					if(!file.exists()){
						System.out.println("======="+fileName);
//						Bitmap bmp = BitmapFactory.decodeResource(maize.getResources(), R.drawable.ic_launcher);
//						FileUtils.writeByteArrayToFile(file, data);
						URL logo = getClass().getResource("/assets/logo.png");
						try {
							FileUtils.copyURLToFile(logo, file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					oks.setImagePath(fileName);//"/sdcard/testSignature.jpg"
					if(false && imageUrl != null){
						byte[] data = URLConnectionUtils.sendGet(imageUrl);
						if(data != null){
							String pathName = FileUtil.checkAndMkParentDir(FileUtil.setMkdir(maize)+Uri.parse(imageUrl).getPath());
							try {
								FileUtils.writeByteArrayToFile(new File(pathName), data);
							} catch (IOException e) {
								e.printStackTrace();
							}
							oks.setImagePath(pathName);// 确保SDcard下面存在此张图片
						}
					}
					// url仅在微信（包括好友和朋友圈）中使用
					oks.setUrl(url);
					// comment是我对这条分享的评论，仅在人人网和QQ空间使用
					oks.setComment(url);
					// site是分享此内容的网站名称，仅在QQ空间使用
					oks.setSite(subject);
					// siteUrl是分享此内容的网站地址，仅在QQ空间使用
					oks.setSiteUrl(url);
					
					
					if (platform != null && !"".equals(platform)) {
						System.out.println("======="+platform);
			            oks.setPlatform(platform);
			        }
//					Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
//					platform.setPlatformActionListener(paListener);
//					platform.share(sp);
//
			        oks.setCallback(new PlatformActionListener(){

						@Override
						public void onCancel(Platform arg0, int arg1) {
							System.out.println("onCancel=============="+arg0);
							
						}

						@Override
						public void onComplete(Platform arg0, int arg1,
								HashMap<String, Object> arg2) {
							System.out.println("onComplete=============="+arg0.getName()+","+arg1);
							((MaizeI)maize).getAppView().loadUrl("javascript:"+callback+"('" + arg0.getName() +"')");
							System.out.println("onComplete=============="+callback+"('"+arg0.getName()+"')");
						}

						@Override
						public void onError(Platform arg0, int arg1,
								Throwable arg2) {
							System.out.println("onError=============="+arg0);
							
						}
			        	
			        });
//			        oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
					// 启动分享GUI
					oks.show(maize);
				}
   				
   			});
   		}
		

		
		@JavascriptInterface
   		public void systemShare(final String subject , final String text, final String imgUrl, final String type) { 
			System.out.println("systemShare================ "+subject+" , "+text+" , " + imgUrl+" , "+type);
   			mHandler.post(new Runnable() {
   				
   				@Override
   				public void run() {
   					
   					try {
   							Intent intent = new Intent(Intent.ACTION_SEND);//图片,文本,文件
   							intent.setType("image/*");
   							String pac = type;
	   						if(type != null){
	   	   						if("QQWEIBO".equalsIgnoreCase(type))
	   	   							pac = "com.tencent.wblog";
	   	   						else if("WEIXIN".equalsIgnoreCase(type))
	   	   							pac = "com.tencent.mm";
	   	   						else if("SINAWEIBO".equalsIgnoreCase(type))
	   	   							pac = "com.sina.weibo";
	   	   						else if("QQ".equalsIgnoreCase(type))
	   	   							pac = "com.tencent.mobileqq";
	   	   						else if("MAIL".equalsIgnoreCase(type)){
	   	   							pac = null;
//	   	   							intent=new Intent(Intent.ACTION_SEND,Uri.parse("mailto:"));
	   	   							//二者只有其一生效
//	   	   							intent.setData(Uri.parse("mailto:1213244340@qq.com"));
//	   	   							intent.setType("application/octet-stream");
//	   	   							intent.setType("message/rfc882");
	   	   							intent.setType("plain/text");
	   	   							intent.putExtra(android.content.Intent.EXTRA_EMAIL, ""); 
	   	   							intent.putExtra(android.content.Intent.EXTRA_CC, "");  
	   	   						}else if("SMS".equalsIgnoreCase(type)){
	   	   							pac = null;
	   	   							intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"));
	   	   							intent.setType("vnd.android-dir/mms-sms"); 
		   	   						intent.putExtra("sms_body", text);  
		   	   						 
	   	   						}
	   	   					}
//   						Uri imageUri = Uri.parse("android.resource://" + maize.getPackageName()+ "/drawable/" + "ic_launcher");

							
							intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 分享的内容
							intent.putExtra(Intent.EXTRA_TEXT, text);
//							URL logo = getClass().getResource("/assets/logo.png");
//							System.out.println("======="+logo.getPath());
//							intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(logo.getPath()));
							if(false && imgUrl!=null){
								byte[] data = URLConnectionUtils.sendGet(imgUrl);
								if(data != null){
									String pathName = FileUtil.checkAndMkParentDir(FileUtil.setMkdir(maize)+Uri.parse(imgUrl).getPath());
									try{
										FileUtils.writeByteArrayToFile(new File(pathName), data);
									} catch (IOException e) {
										e.printStackTrace();
									}
									intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(pathName)); // 分享的内容
								}
								
							}
							if(pac!=null)intent.setPackage(pac);//com.sina.weibo,com.tencent.wblog,evernote,renren,tencent.mobileqq
//							intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
							intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
							maize.startActivity(Intent.createChooser(intent,
									"选择分享"));// 目标应用选择对话框的标题

					} catch (Exception e) {
						e.printStackTrace();
					}

   					
   					
   				}
   			});
   		}

		
	}
    
    
	public List<ResolveInfo> getShareTargets(){
        List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
        Intent intent=new Intent(Intent.ACTION_SEND,null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("image/*");
        PackageManager pm=maize.getPackageManager();
        mApps=pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        return mApps;
    } 
	
    class OpenApp
	{
		public void open(final String pkgName) { 
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					Log.i("WebViewActivity", "++++++++++++++"+pkgName);
					Intent intent = maize.getPackageManager().getLaunchIntentForPackage(pkgName);      
					maize.startActivity(intent);
				}
			});
		}
	}
    
    class DataHandler
	{
		public void show(String data) { 
			new AlertDialog.Builder(maize).setMessage(data).create().show();
		}
	}
    
    
    
    
    
    public static boolean setProxy(Context ctx, String host, int port) {  
	    boolean ret = false;  
	    try {  
	        Log.d(TAG, "setProxy defaulthost="+host+" ,port="+port);  
	        Object requestQueueObject = getRequestQueue(ctx);  
	        if (requestQueueObject != null) {  
	            //Create Proxy config object and set it into request Q  
	            HttpHost httpHost = new HttpHost(host, port, "http");
	            Object out = getDeclaredField(requestQueueObject, "mProxyHost");
	            System.out.println("out=== "+out);
	            setDeclaredField(requestQueueObject, "mProxyHost", httpHost);  
	            ret = true;  
	        }  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	    return ret;  
	}
    String proxyHost = "";
    int proxyPort = 0;
    public boolean setProxy(String host, int port) {  
	//webview明明有代理设置API，为什么不开放呢
//	this.getApplicationContext().registerReceiver(receiver, filter)
    	
    	
//    HttpHost cmproxy =new HttpHost("211.88.18.33", 9999);
//	HttpParams params=new BasicHttpParams();
//	 HttpClient client=new DefaultHttpClient(params);
//	 client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, cmproxy);
//	 appView.enablePlatformNotifications();
    	
    	Proxy proxy = new Proxy();
    	proxyHost = android.net.Proxy.getDefaultHost();
    	proxyPort = proxy.getDefaultPort();
    	System.getProperties();
		
    	if(host!=null && port!=0){
		System.setProperty("http.proxyHost",host);
		System.setProperty("http.proxyPort",port+"");
		System.setProperty("http.nonProxyHosts","");
		
		
//        this.enablePlatformNotifications();
    		
    		
//    	com.android.settings.ProxySelector
//    		new ProxySelectorImpl();
//    	ProxySelector.setDefault(selector)
    		
		
		return true;
    	}
    	return false;
    }
    
    
    public boolean setProxy(HttpHost proxyServer) {
        Class networkClass = null;
        Object network = null;
        try {
            networkClass = Class.forName("android.webkit.Network");
            Field networkField = networkClass.getDeclaredField("sNetwork");
            network = getFieldValueSafely(networkField, null);
        } catch (Exception ex) {
            Log.e(TAG, "error getting network");
            return false;
        }
        if (network == null) {
            Log.e(TAG, "error getting network : null");
            return false;
        }
        Object requestQueue = null;
        try {
            Field requestQueueField = networkClass
                    .getDeclaredField("mRequestQueue");
            requestQueue = getFieldValueSafely(requestQueueField, network);
        } catch (Exception ex) {
            Log.e(TAG, "error getting field value");
            return false;
        }
        if (requestQueue == null) {
            Log.e(TAG, "Request queue is null");
            return false;
        }
        Field proxyHostField = null;
        try {
            Class requestQueueClass = Class.forName("android.net.http.RequestQueue");
            proxyHostField = requestQueueClass
                    .getDeclaredField("mProxyHost");
        } catch (Exception ex) {
            Log.e(TAG, "error getting proxy host field");
            return false;
        }       
        
            boolean temp = proxyHostField.isAccessible();
            try {
                proxyHostField.setAccessible(true);
                proxyHostField.set(requestQueue, proxyServer);
            } catch (Exception ex) {
                Log.e(TAG, "error setting proxy host");
            } finally {
                proxyHostField.setAccessible(temp);
            }
        
        return true;
    }


    
    public boolean setProxy(WebView webview, String host, Integer port) {
        try
        {
        	if(host!=null && port!=null){
        	//BrowserFrame.sJavaBridge.updateProxy((ProxyProperties)msg.obj);
        	Log.d(TAG, "setProxy defaulthost="+host+" ,port="+port);  

        	Object mWebView = webview;
            Class wcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            Class classz = Class.forName("android.net.ProxyProperties");
            params[0] = classz;
            Method updateProxyObject = wcjb.getDeclaredMethod("updateProxy", params);

            Class wv = Class.forName("android.webkit.WebView");
            try{
            	Class wvcclass = Class.forName("android.webkit.WebViewClassic");
                Method fromWebView = wvcclass.getDeclaredMethod("fromWebView", new Class[]{wv});
                mWebView = fromWebView.invoke(null, webview);
                wv = wvcclass;
            }catch(Exception ex){
            	Log.e(TAG, "not exist WebViewClassic : " + ex);
            }
            
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreObject = getFieldValueSafely(mWebViewCoreField, mWebView);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreObject);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field field = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(field, mBrowserFrame);

            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppconts = classz.getConstructor(pparams);

            updateProxyObject.invoke(sJavaBridge, ppconts.newInstance(host, port, null));

            Log.d(TAG, "Setting proxy successful!");
            return true;
        	}
        }
        catch (Exception ex)
        {
            Log.e(TAG, "failed to set HTTP proxy: " + ex);
        }
        return false;
    }


    
    private Object getFieldValueSafely(Field field, Object classInstance) throws IllegalArgumentException, IllegalAccessException {
        boolean oldAccessibleValue = field.isAccessible();
        field.setAccessible(true);
        Object result = field.get(classInstance);
        field.setAccessible(oldAccessibleValue);
        return result;      
    }

    
    
    public void cancelProxy(){
    	System.setProperty("http.proxyHost",proxyHost);
		System.setProperty("http.proxyPort",proxyPort+"");
    }
    
    
	public static String getProxyHostname(Context ctx){  
	    String res = null;  
	    try {  
	        Object requestQueueObject = getRequestQueue(ctx);  
	        if (requestQueueObject != null) {  
	            Object fild = getDeclaredField(requestQueueObject,"mProxyHost");  
	            if(fild!=null){  
	                HttpHost host = (HttpHost)fild;  
	                res = host.getHostName();  
	            }  
	        }  
	     } catch (Exception e) {  
	         e.printStackTrace();  
	     }  
	    return res;  
	}  
	  
	public static void cancelProxy(Context ctx){  
	    try {  
	    Object requestQueueObject = getRequestQueue(ctx);  
	    if (requestQueueObject != null) {  
	        setDeclaredField(requestQueueObject, "mProxyHost", null);  
	    }  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	}  
	  
	public static Object getRequestQueue(Context ctx) throws Exception {  
	    Object ret = null;  
	    Class networkClass = Class.forName("android.webkit.Network");  
	    if (networkClass != null) {  
	        Object networkObj = invokeMethod(networkClass, "getInstance", new Object[]{ctx}, Context.class);  
	        if (networkObj != null) {  
	            ret = getDeclaredField(networkObj, "mRequestQueue");  
	        }  
	    }  
	    return ret;  
	}  
	  
	private static Object getDeclaredField(Object obj, String name)  
	        throws SecurityException, NoSuchFieldException,  
	        IllegalArgumentException, IllegalAccessException {  
	    Field f = obj.getClass().getDeclaredField(name);  
	    f.setAccessible(true);  
	    Object out = f.get(obj);  
	    //System.out.println(obj.getClass().getName() + "." + name + " = "+ out);  
	    return out;  
	}  
	  
	private static void setDeclaredField(Object obj, String name, Object value)  
	        throws SecurityException, NoSuchFieldException,  
	        IllegalArgumentException, IllegalAccessException {  
	    Field f = obj.getClass().getDeclaredField(name);  
	    f.setAccessible(true);  
	    f.set(obj, value);  
	}  
	  
	private static Object invokeMethod(Object object, String methodName, Object[] params, Class... types) throws Exception {  
	    Object out = null;  
	    Class c = object instanceof Class ? (Class) object : object.getClass();  
	    if (types != null && types.length>0) {  
	        Method method = c.getMethod(methodName, types);  
	        out = method.invoke(object, params);  
	    } else {  
	        Method method = c.getMethod(methodName);  
	        out = method.invoke(object);  
	    }  
	    //System.out.println(object.getClass().getName() + "." + methodName + "() = "+ out);  
	    return out;  
	}
}
