package cn.com.gfa.ecma.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

public class WebView2Activity extends MaizeActivity{        



	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		super.init();

		


		

		String resultString="http://192.168.1.2:8080/bms/pages/news_info.jsp?infoid=768902&pagenum=1&moduleid=175805&firsttitle=%E5%8E%A6%E9%97%A8%E6%B5%B7%E5%85%B3%E9%AB%98%E6%B8%A9%E9%9C%B2%E5%A4%A9%E7%9B%98%E4%BB%93%E5%BF%99&title=%E4%BB%8A%E6%97%A5%E6%B5%B7%E5%85%B3";

		this.appView.loadUrl(resultString);


	}

	




 


	

  
    
    
    
    
}
