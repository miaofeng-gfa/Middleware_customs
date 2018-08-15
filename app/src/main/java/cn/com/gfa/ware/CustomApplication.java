package cn.com.gfa.ware;

import java.util.Properties;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

public class CustomApplication extends Application {

	private static Properties props;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
	}
	
	public String getServerUrl() {
		return getProperties("server.url");
	}
	
	
	public String getProperties(String key){
		return loadProperties().getProperty(key);
	}
	
    private Properties loadProperties() {

    	if(props == null){
	        props = new Properties();
	        try {
	            int id = getResources().getIdentifier("config", "raw",
	                    getPackageName());
	            props.load(getResources().openRawResource(id));
	        } catch (Exception e) {
	            Log.e("MyApp", "Could not find the properties file.", e);
	        }
    	}
        return props;
    }

}