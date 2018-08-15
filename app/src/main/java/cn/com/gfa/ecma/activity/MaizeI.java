package cn.com.gfa.ecma.activity;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;

public interface MaizeI {
	void setUploadMessage(ValueCallback<Uri> uploadMsg);
	ValueCallback<Uri> getUploadMessage();
	WebView getAppView();
	void removeSplashScreen();
}
