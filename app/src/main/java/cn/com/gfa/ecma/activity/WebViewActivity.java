package cn.com.gfa.ecma.activity;


import java.io.File;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import cn.com.gfa.ware.CustomApplication;
import cn.com.gfa.ware.R;
import cn.com.gfa.ware.baidu.PushReceiver;
import cn.com.gfa.ware.utils.DeviceUtil;
import cn.com.gfa.ware.utils.FileUtil;

public class WebViewActivity extends MaizeActivity {
	private MaizeWebView mWebView;
//	private ImageView home;
//	private ImageView pre;
//	private ImageView exit;
//	private ValueCallback<Uri> mUploadMessage; 
	public static boolean isStart = false;
	private String baseUrl = "";

	private final static int FILECHOOSER_RESULTCODE=1;  
//	private String portalUrl="http://127.0.0.1:"+Global.portal_port+"/portal/index.html";//?f="+new Random().nextFloat();
//	private String portalUrl="http://172.18.102.31:8090/portal/index.html";
//	private String portalUrl="file:///android_asset/app/index.html";
	
//	private String portalUrl="http://211.88.18.198:8080/bms/pages/main_1.jsp";
//	private String portalUrl="http://211.88.18.198:7444/eps/businessController.do?find&id=ff8080814a4cf1f1014a565c82da000e";
//	private String portalUrl="http://211.88.18.202:53/eps/businessController.do?find&id=ff8080814a4cf1f1014a565c82da000e";

	@SuppressLint("JavascriptInterface")
	@Override
	public void onCreate(Bundle savedInstanceState) {
//	 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//   	 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.init();
		super.setIntegerProperty("splashscreen", R.drawable.loading1096);
		super.setIntegerProperty("loadUrlTimeoutValue", 30000);
		mWebView = this.appView;
		if (android.os.Build.VERSION.SDK_INT < 19) {
//			   root.setLayerType(View.LAYER_TYPE_HARDWARE,null);
				appView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
      }
		
		CookieSyncManager.createInstance(this);
		
		Log.i("WebViewActivity", "onCreate");
		if (savedInstanceState != null) {
//			Global.middle_port = savedInstanceState.getString("port");
		}
//		setContentView(R.layout.webview);
		
//		mWebView = (MaizeWebView) findViewById(R.id.webview);
//		mWebView.setMaize(this);
		
		
//		home = (ImageView) this.findViewById(R.id.imageView1);
//		pre = (ImageView) this.findViewById(R.id.imageView3);
//		exit = (ImageView) this.findViewById(R.id.imageView5);
		
//		homeView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
////				mWebView.loadUrl(portalUrl);
//				finish();
////				PinDialog dialog = new PinDialog(
////						WebViewActivity.this);
////				dialog.show();
//			}
//		});
//		
//		scanView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent it = new Intent();
//				it.setClass(WebViewActivity.this, CaptureActivity.class);
//				WebViewActivity.this.startActivityForResult(it, 0);
////				WebViewActivity.this.finish();
////					if (mWebView.canGoBack()) {
////						mWebView.goBack();
////					}else{
////						showLogoutDialog("确定退出程序？");
////					}
//			}
//		});
//		
//		pdfView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent it = new Intent();
//				it.setClass(WebViewActivity.this, ChoosePDFActivity.class);
//				WebViewActivity.this.startActivityForResult(it, 0);
//			}
//		});
//		
//		preView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				mWebView.loadUrl("javascript:testprevious()");
//			}
//		});
//		nextView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				mWebView.loadUrl("javascript:testnext()");
//			}
//		});
//		subView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				mWebView.loadUrl("javascript:savename_first()");
//			}
//		});
		
		
//		exit.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				showLogoutDialog("确定退出程序？");
//			}
//		});
//		appView.setOnTouchListener(new View.OnTouchListener() {  
//	            @Override  
//	            public boolean onTouch(View v, MotionEvent event) {  
//	  
//	                switch (event.getAction()) {  
//	                case MotionEvent.ACTION_DOWN:  
//	                case MotionEvent.ACTION_UP:  
//	                    v.requestFocusFromTouch();  
//	                    break;  
//	                case MotionEvent.ACTION_MOVE:  
//	                    break;  
//	                case MotionEvent.ACTION_CANCEL:  
//	                    break;  
//	                }  
//	                return false;  
//	            }  
//	        }); 

		
		

		
//		mWebView.loadUrl("http://127.0.0.1:"+Global.middle_port+"/MOFCOM/manage.action");
//		mWebView.loadUrl("file:///android_asset/app/ec/index.html");
//		super.appView.loadUrl(portalUrl);//?f="+new Random().nextFloat()

//		super.setProxyICS(super.appView,"211.88.18.33", 9999);
//		portalUrl=this.getProperty("portal.url");
		Bundle bundle = this.getIntent().getExtras();
//		barcodeBitmap = bundle.getParcelable("bitmap");
//		barcodeFormat = bundle.getString("barcodeFormat");
//		decodeDate = bundle.getString("decodeDate");
//		portalUrl="http://219.237.194.233:8080/customs/pages/main_1.jsp";
//		portalUrl="http://211.88.18.198:8080/customs/pages/main_1.jsp";
//		portalUrl="file:///android_asset/customs/index.html";
//		portalUrl="http://211.88.18.198:8080/bms/pages/main_1.jsp";
		baseUrl = ((CustomApplication)getApplication()).getServerUrl();
		portalUrl=baseUrl+"/customs/pages/main_1.jsp";
		String resultString = bundle.getString("resultString");
		Log.i("WebViewActivity", "resultString url====="+resultString);
		if(resultString != null)
			portalUrl = resultString;
		super.loadUrl(portalUrl,40000);
//		if(!appView.isProcess()){
//			super.appView.loadUrl("file:///android_asset/app/ec/alert.html");
//		}
//		Log.i("webviewActivity",portalUrl);
//		mWebView.loadUrl("http://211.88.18.139/woem");
//		mWebView.loadUrl("https://172.24.24.207:8080");
		PushReceiver.cancelNotify(this);
		isWebDetailActivity();
		new GetNewVersionThread().start();
	}
	
	
	 
	
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				new GetNewVersionThread().start();
				break;
			case 2:
//				callbackContext.error("VPN连接失败，请重试!\n" + "(错误码：109010112)");
				break;
			case 3:
//				callbackContext.error("网络请求失败，请重试!\n" + "(错误码：109011000)");
				break;
			case 4:
				// 客户端是最新版本，不用更新，开始检查OA包是否需要更新
//				new GetRescourseThread().start();
				break;
			case 5:
				// 客户端版本需要更新
				// loadingDialog.hideDialog();
//				showUpdateDialog("重要提示：", "         尊敬的用户您好，新服务地址上线啦，为了避免影响您的使用，请马上更新我们的2.0版本，感谢您的使用！");
				showUpdateDialog();
				break;
			case 6:
				// 客户端版本暂不更新
				// loadingDialog.showDialog("加载中请稍候...");
				handler.sendEmptyMessage(4);
				break;
			case 7:
				// OA包是最新版本，不用更新，开始登陆门户
//				callbackContext.success();
				break;
			case 8:
				// OA包需要更新
				// loadingDialog.hideDialog();
//				showUpdateOADialog("提示", "业务包有可更新版本，是否马上更新？");
				break;
			case 9:
				// OA包下载
//				downLoadOA();
				break;
			case 10:
				// VPN连接失败，报原因
//				String reason = msg.getData().getString("reason");
//				callbackContext.error(reason);
				break;
			case 4101:
//				callbackContext.error("用户名密码错误，请重试!\n" + "(错误码：109010112)");
				break;
			case 4096:
//				callbackContext.error("网络连接失败，请重试!\n" + "(错误码：109010112)");
				break;
			}
		}
	};
	
		// 检查客户端是否需要更新
		class GetNewVersionThread extends Thread {
			public void run() {
				String returnJSON = FileUtil
						.getStringJson(baseUrl + "/customsadmin/methodController.do?updateInfo&type=android");
				Log.i(TAG, "得到的json字符串为===" + returnJSON);
				if (returnJSON != null && !"".endsWith(returnJSON)) {
					// 处理json数据
					if ("489".equalsIgnoreCase(returnJSON)) {
						handler.sendEmptyMessage(3);
					} else {
						workedWithMiddleWareVisionJson(returnJSON);
					}
				} else {
					handler.sendEmptyMessage(3);
				}
			}
		}
		
		String downLoadURL = "";
		// 处理检查程序版本返回的json数据
		private void workedWithMiddleWareVisionJson(String stringJson) {
//			byte[] json1 = Base64.decode(stringJson, Base64.NO_WRAP);
			String json = stringJson.substring(1, stringJson.length()-1).replace("\\", "");//new String(json1);
//			String json = "{\"des\":\"第一版\",\"size\":\"7\",\"url\":\"http://211.88.18.198:8080/customs/apps/1/xx.apk\",\"version\":\"1\"}";
			Log.i(TAG, "得到的json字符串为=" + json);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(json);
				String newVersion = jsonObject.getString("version");
				String size = jsonObject.getString("size");
				Double double1 = Double.valueOf(size);
				double double2 = double1/1024.0/1024.0;
				DecimalFormat df = new DecimalFormat("####0.00");
				String size1 = df.format(double2);
				String size2 = size1+"M";
				String des = jsonObject.getString("des");
				String des2 = des.replaceAll("rn", "\n");
				apkInfo = new ApkInfo(newVersion,size2,des2);
				downLoadURL = jsonObject.getString("url");
				if (newVersion == null || newVersion.equalsIgnoreCase("")) {
					handler.sendEmptyMessage(4);
				} else {
					int newVersionNum = Integer.parseInt(newVersion);
					int curVersionNum = Integer.parseInt(DeviceUtil
							.getVersionCode(WebViewActivity.this));
					if (curVersionNum >= newVersionNum) {
						handler.sendEmptyMessage(4);
					} else {
						handler.sendEmptyMessage(5);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handler.sendEmptyMessage(3);
			}
		}
		
		
		
		private void showUpdateDialog() {
			// TODO Auto-generated method stub
			Runnable runnable = new Runnable() {
				public void run() {
					
					/*Dialog altertDialog = new AlertDialog.Builder(WebViewActivity.this,R.style.Dialog)
							.setTitle(title).setMessage(text).setPositiveButton(
									"更新", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
//											downLoadMiddleWare();
											WebViewActivity.this.loadUrl(downLoadURL);
										}
									}).setNegativeButton("暂不更新",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											// 开始检查OA包是否需要更新
											handler.sendEmptyMessage(4);
										}
									}).create();
					altertDialog.setCanceledOnTouchOutside(false);
					altertDialog.show();*/
					StringBuffer sb = new StringBuffer();  
					System.out.println("aaaaaaaaaa =========== " + apkInfo.toString());
					 sb.append("最新版本："+apkInfo.getVersion()+"\n")
					 	.append("更新版本大小："+apkInfo.getSize()+"\n")
					 	.append("更新内容："+"\n"+apkInfo.getDes()+"\n");
					CustomDialog.Builder builder = new CustomDialog.Builder(WebViewActivity.this);  
			        builder.setMessage(sb.toString());  
			        builder.setTitle("发现新版本");  
			        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() { 
			        	@Override
			            public void onClick(DialogInterface dialog, int which) { 
			        	
			            	   dialog.dismiss(); 
			            	   WebViewActivity.this.loadUrl(downLoadURL);
			              
			            }  
			        });  
			      
			        builder.create().show();
				}
			};
			this.getActivity().runOnUiThread(runnable);
		}
		
		
//		public void downLoadMiddleWare() {
//			// 下载apk文件
//			loadingDialog.setText("客户端下载中...");
//			DownLoadOAURL=FileUtil.changeUrl(DownLoadOAURL, m_ip+":"+((MyApp) context.getApplication()).init_m_port);
//			new DownloaderThread(downLoadHandler, DownLoadOAURL, FileUtil
//					.checkAndMkFile(FileUtil.setMkdir(context) + File.separator),
//					MyApp.getApp()+".apk", "").start();
//		}

//	@Override
//	public void onAttachedToWindow() {
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
//		super.onAttachedToWindow();
//	}
	
//	@Override  
//    protected void onUserLeaveHint() {  
//        Log.d("aeon","onUserLeaveHint");  
//        mWebView.loadUrl("http://127.0.0.1:"+Global.portal_port+"/portal/index.html");
//        super.onUserLeaveHint();  
//    }
	

	@Override
	public Object onMessage(String id, Object data) {
		// TODO Auto-generated method stub
//		 WebSettings settings = this.appView.getSettings();
		mWebView = this.appView;
		// webview.getZoomControls();
		// settings.setJavaScriptEnabled(true);// JavaScript可用
		// // this.appView.setInitialScale(100);//初始显示比例100%
//		 settings.setSupportZoom(true);
//		 settings.setBuiltInZoomControls(true);
		// settings.setUseWideViewPort(true);
		// if (DeviceUtil.getSDKVersionNumber() >= 11) {
		// // 只有API版本大于等于11的时候才有以下方法来隐藏按钮
		// settings.setDisplayZoomControls(false);// 隐藏缩放按钮
		// }

		if ("onPageFinished".equals(id)) {
			this.appView.setVisibility(View.VISIBLE);
		}
		return super.onMessage(id, data);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("webviewActivity",keyCode+"===="+event.getAction());
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			final boolean isNetworkAvailable = CheckNetsUtil
//					.isNetworkAvailable(this);
//			if (!isNetworkAvailable) {
//				appView.getWebChromeClient().showMessage("你的设备没有连接网络,请检查网络设置.");
//				return true;
//			}

			exitBy2Click();
			if (keyCode == KeyEvent.KEYCODE_BACK && appView.canGoBack()) {
				return super.onKeyDown(keyCode, event);
//				appView.goBack();
//				return true;
			}
//			else{
//				showLogoutDialog("确定退出程序?");
//				return true;
//			}
		}
		return false;
	}
	
	private static Boolean isExit = false;
	private ApkInfo apkInfo;  
	  
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        //Toast.makeText(this, "双击可退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 1500); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	  
	    } else {  
	    	showLogoutDialog("确定退出程序?");
	    }  
	}  
	
//	@Override  
//	 protected void onActivityResult(int requestCode, int resultCode,  
//	                                    Intent intent) {  
//		ValueCallback<Uri> mUploadMessage = this.appView.getWebChromeClient().getValueCallback();
//	  if(requestCode==FILECHOOSER_RESULTCODE)  
//	  {  
//	   if (null == mUploadMessage) return;  
//	            Uri result = intent == null || resultCode != RESULT_OK ? null  
//	                    : intent.getData();  
//	            mUploadMessage.onReceiveValue(result);  
//	            mUploadMessage = null;  
//	  }
//	  }  
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
//		outState.putString("port", Global.middle_port);
		super.onSaveInstanceState(outState);
	}
	
	private void showLogoutDialog(String text) {
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("提示").setMessage(text)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(){
							public void run(){
								delete();
							}
						}.start();
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		dialog.show();
	}
	

    public void delete() {  
    	File file=new File(Environment.getExternalStorageDirectory()
				+ File.separator+"KingsoftOffice/file/download/net");
    		File[] childFiles=file.listFiles();
    		if(childFiles!=null&&childFiles.length!=0){
    			for (int i = 0; i < childFiles.length; i++) {  
    				childFiles[i].delete();  
    			}  
    		}
    } 
    
    public boolean isWebDetailActivity(){
    	if(isStart){
    		isStart = false;
    		String moduleid = getIntent().getStringExtra("moduleid");
    		String url = getIntent().getStringExtra("tourl");
    		String infoid = getIntent().getStringExtra("infoid");
    		String title = getIntent().getStringExtra("title");
    		String description = getIntent().getStringExtra("description");
    		
    		Intent intent = new Intent(this,WebDetailActivity.class);
        	intent.putExtra("moduleid", moduleid);
        	intent.putExtra("title", title);
        	intent.putExtra("tourl", url);
        	intent.putExtra("description", description);
        	intent.putExtra("infoid", infoid);
        	startActivity(intent);
        	overridePendingTransition(R.anim.activity_left_in,
						R.anim.activity_fixed);
        	return true;
    	}
    	return false;
    }
    
    
}
