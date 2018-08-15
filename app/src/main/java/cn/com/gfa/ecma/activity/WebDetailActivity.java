package cn.com.gfa.ecma.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.com.gfa.ware.R;

public class WebDetailActivity extends Activity implements OnTouchListener,OnGestureListener,MaizeI{        
	private final static String TAG = "WebDetailActivity";
	protected ValueCallback<Uri> mUploadMessage;
	protected MaizeWebView appView;
	
	protected GestureDetector gd;
	 protected LinearLayout root;
	 @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.layout_1);  
	        gd = new GestureDetector(WebDetailActivity.this,(OnGestureListener)this);  
	        root = (LinearLayout) findViewById(R.id.weather_layout);
	    	root.setOnTouchListener(this);  
	    	root.setLongClickable(true);
	    	
	appView = new MaizeWebView(this);
	appView.setId(101);
	appView.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0F));

	
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
        this.appView.setOverScrollMode(MaizeWebView.OVER_SCROLL_NEVER);
    }
	this.getIntent().putExtra("loadUrlTimeoutValue", 30000);
	if (android.os.Build.VERSION.SDK_INT < 19) {
			appView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
	}
	CookieSyncManager.createInstance(this);
	
	
	String url = this.getIntent().getStringExtra("tourl");
//	url = "http://192.168.1.2:8080/bms/pages/news_info.jsp?infoid=768902&pagenum=1&moduleid=175805&firsttitle=%E5%8E%A6%E9%97%A8%E6%B5%B7%E5%85%B3%E9%AB%98%E6%B8%A9%E9%9C%B2%E5%A4%A9%E7%9B%98%E4%BB%93%E5%BF%99&title=%E4%BB%8A%E6%97%A5%E6%B5%B7%E5%85%B3";
	Log.d(TAG, "tourl==="+url);
	appView.loadUrl(url);
	root.addView(appView);
	
	 }
	 
	 @Override
     public boolean dispatchTouchEvent(MotionEvent ev) {    
		 	gd.onTouchEvent(ev);
//		 	return appView.onTouchEvent(ev);//这几行代码也要执行，将webview载入MotionEvent对象一下，况且用载入把，不知道用什么表述合适
            return super.dispatchTouchEvent(ev);
     }
	 
	 
	public boolean onTouch(View v, MotionEvent event) {  
	        // TODO Auto-generated method stub  
//	        return gd.onTouchEvent(event);  
	        return super.onTouchEvent(event);
	    }      

	//e1  The first down motion event that started the fling.手势起点的移动事件    
	//e2  The move motion event that triggered the current onFling.当前手势点的移动事件    
	//velocityX   The velocity of this fling measured in pixels per second along the x axis.每秒x轴方向移动的像素    
	//velocityY   The velocity of this fling measured in pixels per second along the y axis.每秒y轴方向移动的像素    
	
	final int FLING_MIN_DISTANCE=200;
    final int FLING_MIN_VELOCITY=200;
    
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
	            float velocityY) {
//	    	Log.i(TAG, e2.getX()-e1.getX() +" = "+velocityX+" = "+velocityY+" = " +	(e2.getY()-e1.getY()));
	        // TODO Auto-generated method stub  
	        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE&&Math.abs(velocityX) > FLING_MIN_VELOCITY && Math.abs(e2.getY()-e1.getY()) <FLING_MIN_DISTANCE)  
	        {  
//	            Intent intent = new Intent(WeatherActivity.this,WebViewActivity.class);  
//	            startActivity(intent);  
//	            overridePendingTransition(R.anim.activity_left_in,
//							R.anim.activity_right_out); 
	  
	        }else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) >FLING_MIN_VELOCITY && Math.abs(e2.getY()-e1.getY()) <FLING_MIN_DISTANCE) {  
	            //切换Activity  
//	            Intent intent = new Intent(WebDetailActivity.this, WebViewActivity.class);  
//	            startActivity(intent);
	            finish();
	            overridePendingTransition(R.anim.activity_fixed,
							R.anim.activity_right_out);
	        }  
	          
	        return false;  
	    }
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.activity_fixed,
					R.anim.activity_right_out);
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void setUploadMessage(ValueCallback<Uri> uploadMsg) {
		mUploadMessage = uploadMsg;
	}
	@Override
	public ValueCallback<Uri> getUploadMessage() {
		return mUploadMessage;
	}
	@Override
	public WebView getAppView() {
		return appView;
	}
	@Override
	public void removeSplashScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		root.removeView(appView);
	    appView.removeAllViews();
		appView.destroy();
		
	}  
	
}