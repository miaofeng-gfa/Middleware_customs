package cn.com.gfa.ecma.activity;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.com.gfa.ware.baidu.PushReceiver;
import cn.com.gfa.ware.baidu.PushReceiver.INewMessageListener;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;


public class MaizeActivity extends Activity implements MaizeI,INewMessageListener {
	 public static String TAG = "MaizeActivity";

	    // The webview for our app
	    protected MaizeWebView appView;
	    protected String portalUrl;
	    protected MaizeWebViewClient webViewClient;
	    protected ValueCallback<Uri> mUploadMessage;

	    protected LinearLayout root;
	    protected boolean cancelLoadUrl = false;
	    protected ProgressDialog spinnerDialog = null;
	    private final ExecutorService threadPool = Executors.newCachedThreadPool();


	    // The initial URL for our app
	    // ie http://server/path/index.html#abc?query
	    //private String url = null;

	    private static int ACTIVITY_STARTING = 0;
	    private static int ACTIVITY_RUNNING = 1;
	    private static int ACTIVITY_EXITING = 2;
	    private int activityState = 0;  // 0=starting, 1=running (after 1st resume), 2=shutting down

	    // Plugin to call when activity result is received
	    protected boolean activityResultKeepRunning;

	    // Default background color for activity
	    // (this is not the color for the webview, which is set in HTML)
	    private int backgroundColor = Color.BLACK;

	    /*
	     * The variables below are used to cache some of the activity properties.
	     */

	    // Draw a splash screen using an image located in the drawable resource directory.
	    // This is not the same as calling super.loadSplashscreen(url)
	    protected int splashscreen = 0;
	    protected int splashscreenTime = 3000;

	    // LoadUrl timeout value in msec (default of 20 sec)
	    protected int loadUrlTimeoutValue = 20000;

	    // Keep app running when pause is received. (default = true)
	    // If true, then the JavaScript and native code continue to run in the background
	    // when another application (activity) is started.
	    protected boolean keepRunning = true;

	    private int lastRequestCode;

	    private Object responseCode;

	    private Intent lastIntent;

	    private Object lastResponseCode;

	    private String initCallbackClass;
	    
	    private Properties props;
	    
//	    ImageView homeView;
//	    ImageView scanView;
//	    ImageView sp0View; 
//	    ImageView pdfView;
//	    ImageView sp1View; 
//	    ImageView preView;
//	    ImageView sp2View; 
//	    ImageView nextView;
//	    ImageView sp3View; 
//	    ImageView subView;
//	    LinearLayout linearlayout;
	    ProgressBar progressbar;
	    String backUrl;
	    static boolean forcePortrait;

	    /**
	     * Called when the activity is first created.
	     *
	     * @param savedInstanceState
	     */
	    @SuppressWarnings("deprecation")
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
//	    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//	   	 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
	        Log.d(TAG, "MaizeActivity.onCreate()");
	        super.onCreate(savedInstanceState);

	        if(savedInstanceState != null)
	        {
	            initCallbackClass = savedInstanceState.getString("callbackClass");
	        }
	        
	        if(!this.getBooleanProperty("showTitle", false))
	        {
	            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	        }

	        if(this.getBooleanProperty("setFullscreen", false))
	        {
	            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        }
	        else
	        {
	            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
	                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	        }
	        // This builds the view.  We could probably get away with NOT having a LinearLayout, but I like having a bucket!
	        Display display = getWindowManager().getDefaultDisplay();
	        int width = display.getWidth();
	        int height = display.getHeight();

//	        root = new LinearLayout(this);
	        root = new LinearLayoutSoftKeyboardDetect(this, width, height);
	        root.setOrientation(LinearLayout.VERTICAL);
//	        root.setBackgroundColor(this.backgroundColor);
	        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
	                ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//	        root = (LinearLayout) inflater.inflate(R.layout.webview, null);

	        // Setup the hardware volume controls to handle volume control
	        setVolumeControlStream(AudioManager.STREAM_MUSIC);
	        
	        
	        PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,"Gxp1kTNYLdhLLphtFkjQTeTTDLYgsdjM");
	    }

	    /**
	     * Get the Android activity.
	     *
	     * @return
	     */
	    public Activity getActivity() {
	        return this;
	    }

	    /**
	     * Create and initialize web container with default web view objects.
	     */
	    public void init() {
	        MaizeWebView webView = new MaizeWebView(MaizeActivity.this);
	        MaizeWebViewClient webViewClient;
	        webViewClient = new MaizeWebViewClient(this, webView);
	        this.init(webView, webViewClient, new MaizeWebChromeClient(this, webView));
	    }

	    /**
	     * Initialize web container with web view objects.
	     *
	     * @param webView
	     * @param webViewClient
	     * @param webChromeClient
	     */
	    @SuppressLint("NewApi")
	    public void init(MaizeWebView webView, MaizeWebViewClient webViewClient, MaizeWebChromeClient webChromeClient) {
	        Log.d(TAG, "init()");

	        // Set up web container
	        this.appView = webView;
	        this.appView.setId(100);

	        this.appView.setWebViewClient(webViewClient);
	        this.appView.setWebChromeClient(webChromeClient);
	        webViewClient.setWebView(this.appView);
	        webChromeClient.setWebView(this.appView);

	        this.appView.setLayoutParams(new LinearLayout.LayoutParams(
	                ViewGroup.LayoutParams.MATCH_PARENT,
	                ViewGroup.LayoutParams.MATCH_PARENT,
	                1.0F));

	        if (this.getBooleanProperty("disallowOverscroll", false)) {
	            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
	                this.appView.setOverScrollMode(MaizeWebView.OVER_SCROLL_NEVER);
	            }
	        }

	        // Add web view but make it invisible while loading URL
	        this.appView.setVisibility(View.VISIBLE);
	        this.root.addView(this.appView);
	        

//	        progressbar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
//	        progressbar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
//	        this.root.addView(progressbar);
	       
	        WindowManager wm = this.getWindowManager();
	        int height = wm.getDefaultDisplay().getHeight();

//	        linearlayout = new LinearLayout(this);
//	        linearlayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//	        		height/16, 0.0F));
//	        linearlayout.setBackgroundResource(R.drawable.web_bg);
//	        linearlayout.setGravity(Gravity.CENTER_HORIZONTAL);
//	        linearlayout.setVisibility(0);//wwwwwww 0
//	        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	        
//	        homeView = new ImageView(this);
//	        homeView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        homeView.setImageResource(R.drawable.web_home);  
//	        linearlayout.addView(homeView);
//	        
//	        ImageView spView = new ImageView(this);
//	        spView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        spView.setImageResource(R.drawable.web_sp);  
//	        linearlayout.addView(spView);
//	        
//	        scanView = new ImageView(this);
//	        scanView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        scanView.setImageResource(R.drawable.web_pre);  
//	        linearlayout.addView(scanView);
//	        
//	        
//	       /** ChoosePDFActivity*/
//	        sp0View = new ImageView(this);
//	        sp0View.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        sp0View.setImageResource(R.drawable.web_sp);  
//	        linearlayout.addView(sp0View);
//	        
//	        pdfView = new ImageView(this);
//	        pdfView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        pdfView.setImageResource(R.drawable.scan_book);  
//	        linearlayout.addView(pdfView);
//	        
//	        
//	        /*********pre next sub*/
//	        sp1View = new ImageView(this);
//	        sp1View.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        sp1View.setImageResource(R.drawable.web_sp);  
//	        linearlayout.addView(sp1View);
//	        sp1View.setVisibility(8);
//	        
//	        preView = new ImageView(this);
//	        preView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        preView.setImageResource(R.drawable.page_pre);  
//	        linearlayout.addView(preView);
//	        preView.setVisibility(8);
//	        
//	        sp2View = new ImageView(this);
//	        sp2View.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        sp2View.setImageResource(R.drawable.web_sp);  
//	        linearlayout.addView(sp2View);
//	        sp2View.setVisibility(8);
//	        
//	        nextView = new ImageView(this);
//	        nextView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        nextView.setImageResource(R.drawable.page_next);  
//	        linearlayout.addView(nextView);
//	        nextView.setVisibility(8);
//	        
//	        sp3View = new ImageView(this);
//	        sp3View.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        sp3View.setImageResource(R.drawable.web_sp);  
//	        linearlayout.addView(sp3View);
//	        
//	        subView = new ImageView(this);
//	        subView.setLayoutParams(new LinearLayout.LayoutParams(height/20,
//	        		ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
//	        subView.setImageResource(R.drawable.page_submit);  
//	        linearlayout.addView(subView);
//	        
//	        linearlayout.setVisibility(8);
//	        this.root.addView(linearlayout);
	        		
	        setContentView(this.root);
//	        setContentView(R.layout.webview);

	        // Clear cancel flag
	        this.cancelLoadUrl = false;
	        
	    }

	    /**
	     * Load the url into the webview.
	     *
	     * @param url
	     */
	    public void loadUrl(String url) {

	        // Init web view if not already done
	        if (this.appView == null) {
	            this.init();
	        }

	        // Set backgroundColor
//	        this.backgroundColor = this.getIntegerProperty("backgroundColor", Color.BLACK);
//	        this.root.setBackgroundColor(this.backgroundColor);

	        // If keepRunning
	        this.keepRunning = this.getBooleanProperty("keepRunning", true);

	        // Then load the spinner
	        this.loadSpinner();

	        this.appView.loadUrl(url);
	    }

	    /*
	     * Load the spinner
	     */
	    void loadSpinner() {

	        // If loadingDialog property, then show the App loading dialog for first page of app
	        String loading = null;
	        if ((this.appView == null) || !this.appView.canGoBack()) {
	            loading = this.getStringProperty("loadingDialog", null);
	        }
	        else {
	            loading = this.getStringProperty("loadingPageDialog", null);
	        }
	        if (loading != null) {

	            String title = "";
	            String message = "Loading Application...";

	            if (loading.length() > 0) {
	                int comma = loading.indexOf(',');
	                if (comma > 0) {
	                    title = loading.substring(0, comma);
	                    message = loading.substring(comma + 1);
	                }
	                else {
	                    title = "";
	                    message = loading;
	                }
	            }
	            this.spinnerStart(title, message);
	        }
	    }

	    /**
	     * Load the url into the webview after waiting for period of time.
	     * This is used to display the splashscreen for certain amount of time.
	     *
	     * @param url
	     * @param time              The number of ms to wait before loading webview
	     */
	    public void loadUrl(final String url, int time) {

	        // Init web view if not already done
	        if (this.appView == null) {
	            this.init();
	        }

	        this.splashscreenTime = time;
	        this.splashscreen = this.getIntegerProperty("splashscreen", 0);
	        this.showSplashScreen(this.splashscreenTime);
	        this.appView.loadUrl(url, time);
	    }

	    /**
	     * Cancel loadUrl before it has been loaded.
	     */
	    // TODO NO-OP
	    @Deprecated
	    public void cancelLoadUrl() {
	        this.cancelLoadUrl = true;
	    }

	    /**
	     * Clear the resource cache.
	     */
	    public void clearCache() {
	        if (this.appView == null) {
	            this.init();
	        }
	        this.appView.clearCache(true);
	    }

	    /**
	     * Clear web history in this web view.
	     */
	    public void clearHistory() {
	        this.appView.clearHistory();
	    }

	    /**
	     * Go to previous page in history.  (We manage our own history)
	     *
	     * @return true if we went back, false if we are already at top
	     */
	    public boolean backHistory() {
	        if (this.appView != null) {
	            return appView.backHistory();
	        }
	        return false;
	    }

	    @Override
	    /**
	     * Called by the system when the device configuration changes while your activity is running.
	     *
	     * @param Configuration newConfig
	     */
	    public void onConfigurationChanged(Configuration newConfig) {
	        //don't reload the current page when the orientation is changed
	        
	    	super.onConfigurationChanged(newConfig);
//	        Log.i(TAG, (this.getResources().getConfiguration().orientation!=1)+"==============="+forcePortrait);
	        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				Log.v(TAG, "ORIENTATION_LANDSCAPE");
//				root.setOrientation(LinearLayout.HORIZONTAL);
			}
			if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				Log.v(TAG, "ORIENTATION_PORTRAIT");
//				setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);//又被固定了
				
			}
			
	    }

	    /**
	     * Get boolean property for activity.
	     *
	     * @param name
	     * @param defaultValue
	     * @return
	     */
	    public boolean getBooleanProperty(String name, boolean defaultValue) {
	        Bundle bundle = this.getIntent().getExtras();
	        if (bundle == null) {
	            return defaultValue;
	        }
	        Boolean p;
	        try {
	            p = (Boolean) bundle.get(name);
	        } catch (ClassCastException e) {
	            String s = bundle.get(name).toString();
	            if ("true".equals(s)) {
	                p = true;
	            }
	            else {
	                p = false;
	            }
	        }
	        if (p == null) {
	            return defaultValue;
	        }
	        return p.booleanValue();
	    }

	    /**
	     * Get int property for activity.
	     *
	     * @param name
	     * @param defaultValue
	     * @return
	     */
	    public int getIntegerProperty(String name, int defaultValue) {
	        Bundle bundle = this.getIntent().getExtras();
	        if (bundle == null) {
	            return defaultValue;
	        }
	        Integer p;
	        try {
	            p = (Integer) bundle.get(name);
	        } catch (ClassCastException e) {
	            p = Integer.parseInt(bundle.get(name).toString());
	        }
	        if (p == null) {
	            return defaultValue;
	        }
	        return p.intValue();
	    }

	    /**
	     * Get string property for activity.
	     *
	     * @param name
	     * @param defaultValue
	     * @return
	     */
	    public String getStringProperty(String name, String defaultValue) {
	        Bundle bundle = this.getIntent().getExtras();
	        if (bundle == null) {
	            return defaultValue;
	        }
	        String p = bundle.getString(name);
	        if (p == null) {
	            return defaultValue;
	        }
	        return p;
	    }

	    /**
	     * Get double property for activity.
	     *
	     * @param name
	     * @param defaultValue
	     * @return
	     */
	    public double getDoubleProperty(String name, double defaultValue) {
	        Bundle bundle = this.getIntent().getExtras();
	        if (bundle == null) {
	            return defaultValue;
	        }
	        Double p;
	        try {
	            p = (Double) bundle.get(name);
	        } catch (ClassCastException e) {
	            p = Double.parseDouble(bundle.get(name).toString());
	        }
	        if (p == null) {
	            return defaultValue;
	        }
	        return p.doubleValue();
	    }

	    /**
	     * Set boolean property on activity.
	     *
	     * @param name
	     * @param value
	     */
	    public void setBooleanProperty(String name, boolean value) {
	        this.getIntent().putExtra(name, value);
	    }

	    /**
	     * Set int property on activity.
	     *
	     * @param name
	     * @param value
	     */
	    public void setIntegerProperty(String name, int value) {
	        Log.d(TAG, "Setting integer properties in Maize will be deprecated in 3.1 on August 2013, please use config.xml");
	        this.getIntent().putExtra(name, value);
	    }

	    /**
	     * Set string property on activity.
	     *
	     * @param name
	     * @param value
	     */
	    public void setStringProperty(String name, String value) {
	        Log.d(TAG, "Setting string properties in Maize will be deprecated in 3.0 on July 2013, please use config.xml");
	        this.getIntent().putExtra(name, value);
	    }

	    /**
	     * Set double property on activity.
	     *
	     * @param name
	     * @param value
	     */
	    public void setDoubleProperty(String name, double value) {
	        Log.d(TAG, "Setting double properties in Maize will be deprecated in 3.0 on July 2013, please use config.xml");
	        this.getIntent().putExtra(name, value);
	    }








	    /**
	     * Show the spinner.  Must be called from the UI thread.
	     *
	     * @param title         Title of the dialog
	     * @param message       The message of the dialog
	     */
	    public void spinnerStart(final String title, final String message) {
	        if (this.spinnerDialog != null) {
	            this.spinnerDialog.dismiss();
	            this.spinnerDialog = null;
	        }
	        final MaizeActivity me = this;
	        this.spinnerDialog = ProgressDialog.show(MaizeActivity.this, title, message, true, true,
	                new DialogInterface.OnCancelListener() {
	                    public void onCancel(DialogInterface dialog) {
	                        me.spinnerDialog = null;
	                    }
	                });
	    }

	    /**
	     * Stop spinner - Must be called from UI thread
	     */
	    public void spinnerStop() {
	        if (this.spinnerDialog != null && this.spinnerDialog.isShowing()) {
	            this.spinnerDialog.dismiss();
	            this.spinnerDialog = null;
	        }
	    }

	    /**
	     * End this activity by calling finish for activity
	     */
	    public void endActivity() {
	        this.activityState = ACTIVITY_EXITING;
	        super.finish();
	    }
	    
	    
	    public void startActivityForResult(Intent intent, int requestCode) {
	        this.activityResultKeepRunning = this.keepRunning;
	        // Start activity
	        super.startActivityForResult(intent, requestCode);
	    }
	    
	    
	    
	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	        Log.d(TAG, "Incoming Result");
	        super.onActivityResult(requestCode, resultCode, intent);
	        Log.d(TAG, "Request code = " + requestCode);
	        mUploadMessage = this.appView.getWebChromeClient().getValueCallback();
	        if (requestCode == MaizeWebChromeClient.FILECHOOSER_RESULTCODE) {
	            Log.d(TAG, "did we get here?");
	            if (null == mUploadMessage)
	                return;
	            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
	            Log.d(TAG, "result = " + result);
//	            Uri filepath = Uri.parse("file://" + FileUtils.getRealPathFromURI(result, this));
//	            Log.d(TAG, "result = " + filepath);
	            mUploadMessage.onReceiveValue(result);
	            mUploadMessage = null;
	        }else if(requestCode == 0 && resultCode==this.RESULT_OK){
	        	//"TMP_ID,WRITE_PDF"
	        	Uri uri = Uri.parse(backUrl);
	        	String query = uri.getEncodedQuery().substring(uri.getEncodedQuery().indexOf("&"));
	        	String baseUrl = uri.getScheme()+"://"+uri.getEncodedAuthority()+"/"+uri.getPathSegments().get(0)+"/pdfEvidenceController.do?save";
	        	String url = baseUrl+query;
	        	try {
//		        	byte[] data = URLConnectionUtils.upload(url, intent.getData());
//		        	if (data  == null) {
//						appView.setProcess(false);
//						appView.getWebChromeClient().showMessage(
//								"请求的服务器出现问题,请稍后重试.");
//					}else{
////						appView.backHistory();
//					}
//		        	FileUtil.delete(new File(uri.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
				} 
	        	
	        }
//	            callback.onActivityResult(requestCode, resultCode, intent);
	    }


	    /**
	     * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable).
	     * The errorCode parameter corresponds to one of the ERROR_* constants.
	     *
	     * @param errorCode    The error code corresponding to an ERROR_* value.
	     * @param description  A String describing the error.
	     * @param failingUrl   The url that failed to load.
	     */
	    public void onReceivedError(final int errorCode, final String description, final String failingUrl) {
	        final MaizeActivity me = this;

	        // If errorUrl specified, then load it
	        final String errorUrl = me.getStringProperty("errorUrl", null);
	        if ((errorUrl != null) && (errorUrl.startsWith("file://") && (!failingUrl.equals(errorUrl)))) {

	            // Load URL on UI thread
	            me.runOnUiThread(new Runnable() {
	                public void run() {
	                    // Stop "app loading" spinner if showing
	                    me.spinnerStop();
	                    me.appView.showWebPage(errorUrl, false, true, null);
	                }
	            });
	        }
	        // If not, then display error dialog
	        else {
	            final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
	            me.runOnUiThread(new Runnable() {
	                public void run() {
	                    if (exit) {
	                        me.appView.setVisibility(View.GONE);
	                        me.displayError("Application Error", description + " (" + failingUrl + ")", "OK", exit);
	                    }
	                }
	            });
	        }
	    }

	    /**
	     * Display an error dialog and optionally exit application.
	     *
	     * @param title
	     * @param message
	     * @param button
	     * @param exit
	     */
	    public void displayError(final String title, final String message, final String button, final boolean exit) {
	        final MaizeActivity me = this;
	        me.runOnUiThread(new Runnable() {
	            public void run() {
	                try {
	                    AlertDialog.Builder dlg = new AlertDialog.Builder(me);
	                    dlg.setMessage(message);
	                    dlg.setTitle(title);
	                    dlg.setCancelable(false);
	                    dlg.setPositiveButton(button,
	                            new AlertDialog.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int which) {
	                                    dialog.dismiss();
	                                    if (exit) {
	                                        me.endActivity();
	                                    }
	                                }
	                            });
	                    dlg.create();
	                    dlg.show();
	                } catch (Exception e) {
	                    finish();
	                }
	            }
	        });
	    }

	    /**
	     * Get Activity context.
	     *
	     * @return
	     */
	    public Context getContext() {
	        return this;
	    }

	    /**
	     * Load the specified URL in the Maize webview or a new browser instance.
	     *
	     * NOTE: If openExternal is false, only URLs listed in whitelist can be loaded.
	     *
	     * @param url           The url to load.
	     * @param openExternal  Load url in browser instead of Maize webview.
	     * @param clearHistory  Clear the history stack, so new page becomes top of history
	     * @param params        Maize parameters for new app
	     */
	    public void showWebPage(String url, boolean openExternal, boolean clearHistory, HashMap<String, Object> params) {
	        if (this.appView != null) {
	            appView.showWebPage(url, openExternal, clearHistory, params);
	        }
	    }

	    protected Dialog splashDialog;

	    /**
	     * Removes the Dialog that displays the splash screen
	     */
	    public void removeSplashScreen() {
	        if (splashDialog != null && splashDialog.isShowing()) {
	            splashDialog.dismiss();
	            splashDialog = null;
	        }
	    }

	    /**
	     * Shows the splash screen over the full Activity
	     */
	    @SuppressWarnings("deprecation")
	    protected void showSplashScreen(final int time) {
	        final MaizeActivity that = this;

	        Runnable runnable = new Runnable() {
	            public void run() {
	                // Get reference to display
	                Display display = getWindowManager().getDefaultDisplay();

	                // Create the layout for the dialog
	                LinearLayout root = new LinearLayout(that.getActivity());
	                root.setMinimumHeight(display.getHeight());
	                root.setMinimumWidth(display.getWidth());
	                root.setOrientation(LinearLayout.VERTICAL);
//	                root.setBackgroundColor(that.getIntegerProperty("backgroundColor", Color.BLACK));
	                root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
	                        ViewGroup.LayoutParams.FILL_PARENT, 0.0F));
	                root.setBackgroundResource(that.splashscreen);
	                
	                // Create and show the dialog
	                splashDialog = new Dialog(that, android.R.style.Theme_Translucent_NoTitleBar);
	                // check to see if the splash screen should be full screen
	                if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
	                        == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
	                    splashDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	                }
	                splashDialog.setContentView(root);
	                splashDialog.setCancelable(false);
	                splashDialog.show();

	                // Set Runnable to remove splash screen just in case
	                final Handler handler = new Handler();
	                handler.postDelayed(new Runnable() {
	                    public void run() {
	                        removeSplashScreen();
	                    }
	                }, time);
	            }
	        };
	        this.runOnUiThread(runnable);
	    }

	    @Override
	    public boolean onKeyUp(int keyCode, KeyEvent event)
	    {
	        //Get whatever has focus!
	        View childView = appView.getFocusedChild();
	        if ((childView != null ) &&
	                (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)) {
	            return appView.onKeyUp(keyCode, event);
	        } else {
	            return super.onKeyUp(keyCode, event);
	    	}
	    }
	    
	    /*
	     * Android 2.x needs to be able to check where the cursor is.  Android 4.x does not
	     * 
	     * (non-Javadoc)
	     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	     */
	    
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event)
	    {
	        //Get whatever has focus!
	        View childView = appView.getFocusedChild();
	        //Determine if the focus is on the current view or not
	        if (childView != null && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)) {
	                    return appView.onKeyDown(keyCode, event);
	        }
	        else if(keyCode == KeyEvent.KEYCODE_BACK && appView.canGoBack()){
	        	appView.goBack();
	        	return true;
	        }
	        return false;
	    }
	    
	    
	    /**
	     * Called when a message is sent to plugin.
	     *
	     * @param id            The message id
	     * @param data          The message data
	     * @return              Object or null
	     */
	    public Object onMessage(String id, Object data) {
	        Log.d(TAG, "onMessage(" + id + "," + data + ")");
	        if ("splashscreen".equals(id)) {
	            if ("hide".equals(data.toString())) {
	                this.removeSplashScreen();
	            }
	            else {
	                // If the splash dialog is showing don't try to show it again
	                if (this.splashDialog == null || !this.splashDialog.isShowing()) {
	                    this.splashscreen = this.getIntegerProperty("splashscreen", 0);
	                    this.showSplashScreen(this.splashscreenTime);
	                }
	            }
	        }
	        else if ("spinner".equals(id)) {
	            if ("stop".equals(data.toString())) {
	                this.spinnerStop();
	                this.appView.setVisibility(View.VISIBLE);
	            }
	        }
	        else if ("onReceivedError".equals(id)) {
	            JSONObject d = (JSONObject) data;
	            try {
	                this.onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	        }
	        else if ("exit".equals(id)) {
	            this.endActivity();
	        }
	        return null;
	    }

	    public ExecutorService getThreadPool() {
	        return threadPool;
	    }
	    
	    private Properties loadProperty() {
	    	if(props == null){
		        props = new Properties();
		        try {
//		            int id = this.getResources().getIdentifier("commerce", "raw",
//		            		this.getPackageName());
//		            props.load(getResources().openRawResource(R.raw.commerce));
		        } catch (Exception e) {
		            Log.e(TAG, "Could not find the properties file.", e);
		        }
	    	}
	    	return props;
	    }
	    
	   
	    
	    public String getProperty(String name){
	    	String val = loadProperty().getProperty(name);
	    	if(val==null || "".equals(val))
	    		return null;
	    	else
	    		return val;
	    }
	    
	    public Integer getIntegerProperty(String name){
	    	String val = getProperty(name);
	    	if(val == null)
	    		return null;
	    	else
	    		return Integer.parseInt(val);
	    }

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			return super.onTouchEvent(event);
		}

		@Override
		protected void onResume() {
//			Log.i(TAG, (this.getResources().getConfiguration().orientation!=1)+"==============="+forcePortrait);
//			if(forcePortrait && getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){ 
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//又被固定不动了
//				root.setOrientation(LinearLayout.VERTICAL);
//			}
			Log.i(TAG,"onResume()");
			super.onResume();
			PushReceiver.msgListeners.add(this);
		}
		
		

		@Override
		protected void onDestroy() {
			Log.i(TAG,"onDestroy()");
			appView.destroy();
			super.onDestroy();
			PushReceiver.msgListeners.remove(this);
		}

		@Override
		protected void onPause() {
			Log.i(TAG,"onPause()");
			super.onPause();
			PushReceiver.msgListeners.remove(this);
		}

		@Override
		public void setUploadMessage(ValueCallback<Uri> uploadMsg) {
			mUploadMessage = uploadMsg;
		}

		@Override
		public WebView getAppView() {
			return appView;
		}

		@Override
		public ValueCallback<Uri> getUploadMessage() {
			return mUploadMessage;
		}

		@Override
		public void onNewMessage(String message) {
			Log.i(TAG, "javascript: onNewMessage : " + message );
			appView.loadUrl("javascript:onNewMessage("+message+")");
		}
	    
	    
	    
	    
}
