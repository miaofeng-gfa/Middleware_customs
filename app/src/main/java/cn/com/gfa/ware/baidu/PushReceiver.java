package cn.com.gfa.ware.baidu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import cn.com.gfa.ecma.activity.WebViewActivity;
import cn.com.gfa.ware.CustomApplication;
import cn.com.gfa.ware.R;
import cn.com.gfa.ware.utils.URLConnectionUtils;

import com.baidu.android.pushservice.PushMessageReceiver;

public  class PushReceiver extends PushMessageReceiver {
	
	 public static final String TAG = PushReceiver.class
	            .getSimpleName();
	    public static String userId,channelId;
	    
	    public static ArrayList<INewMessageListener> msgListeners = new ArrayList<INewMessageListener>();
	    
	    public static int NOTIFY_ID = 1000;
	    
	    
	    public static interface INewMessageListener
		{
			public abstract void onNewMessage(String message);
		}

	/**
	* 调用 PushManager.startWork 后，sdk 将对 push server 发起绑定请求，这个过程是异步
	的。绑定请求的结果通过 onBind 返回。
	*/
	  
	@Override
	public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
		Log.d(TAG, responseString);
        if (errorCode == 0) { 
            // 绑定成功 	
        	final String regUrl = ((CustomApplication)context.getApplicationContext()).getServerUrl()+"/customsadmin/methodController.do?mobile&type=android&tokenid=&channelId="+channelId;
        	
        	new Thread() {
   				
   				@Override
   				public void run() {
   					
   					byte[] result = URLConnectionUtils.sendGet(regUrl);
//   		            Log.i(TAG, "----------"+new String(result));
   				}
   			}.start();
        	
            
        }
	}

	/**
	* delTags() 的回调函数。
	*/
	@Override
	public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

	}

	/**
	* listTags() 的回调函数。
	*/
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);


	}

	/**
	* 接收透传消息的函数。
	*/
	@Override
	public void onMessage(Context context, String message,
            String customContentString) { 
		String messageString = "透传消息 message=\"" + message
                + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);
        parseMessage(context, message);
        
       
	}
	
	
	private void parseMessage(Context context,String message)
	{
       
    	Log.d(TAG, "msgListeners.size() = "+msgListeners.size());
        	
        	if (msgListeners.size() > 0)
    		{// 有监听的时候，传递下去
    			for (int i = 0; i < msgListeners.size(); i++)
    				msgListeners.get(i).onNewMessage(message);
    		} else {
    			
    			showNotify(context, message);
    		}
        	

	}
	
	public static void cancelNotify(Context context){
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}
	
	private void showNotify(Context context,String message){
		
		String moduleid=null,url=null,infoid=null,title=null,description=null;
		
		if (!TextUtils.isEmpty(message)) {
            JSONObject customJson = null;
            try {
            	 customJson = new JSONObject(message);
            	moduleid = customJson.getString("moduleid");
            	url = customJson.getString("url");
            	infoid = customJson.getString("infoid");
            	title = customJson.getString("title");
            	description = customJson.getString("description");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle(title)//设置通知栏标题  
            .setContentText(description)//内容 
            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间  
            .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级  
            .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消    
            .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)  
            .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合  
            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission  
            .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON 
            Notification notification = mBuilder.build();    
            notification.flags = Notification.FLAG_AUTO_CANCEL;   
    		Intent intent = new Intent(context,WebViewActivity.class);  
    		intent.putExtra("moduleid", moduleid);
        	intent.putExtra("title", title); 
        	intent.putExtra("tourl", url); 
        	intent.putExtra("description", description);
        	intent.putExtra("infoid", infoid);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        	mBuilder.setContentIntent(pendingIntent);
        	WebViewActivity.isStart = true;
        	Notification notif = mBuilder.build();
        	mNotificationManager.notify(message,NOTIFY_ID, notif);
            
        }
		
    }

	

	@Override
	    public void onNotificationClicked(Context context, String title,
	            String description, String customContentString) {
		Log.e(TAG, "===onNotificationClicked消息点击:"+customContentString);
		
	    }

	/**
	* setTags() 的回调函数。
	*/
	@Override
	public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

	}

	/**
	* PushManager.stopWork() 的回调函数。
	*/
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 解绑定成功
        } 

	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
