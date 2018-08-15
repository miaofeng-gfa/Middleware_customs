package cn.com.gfa.ecma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

public class MaizeWebChromeClient extends WebChromeClient {

	public final static int FILECHOOSER_RESULTCODE=1;  
	Activity maize;
	MaizeWebView appView;
	
	private ProgressDialog progressdialog = null;
	boolean isCancelProgressDialog;
	
	public MaizeWebChromeClient(Activity activity) {
        this.maize = activity;
        progressdialog = new ProgressDialog(maize);
    	progressdialog.setIndeterminate(true);
    	progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressdialog.setMessage("请稍等...");
    	progressdialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface dialog) {
//				Log.i("MaizeWebChromeClient", "===========onCancel");
				isCancelProgressDialog = true;
			}
    		
    	});
    }
	public MaizeWebChromeClient(Activity activity, MaizeWebView view) {
        this.maize = activity;
        this.appView = view;
        progressdialog = new ProgressDialog(maize);
    	progressdialog.setIndeterminate(true);
    	progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressdialog.setMessage("请稍等...");
    	progressdialog.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface dialog) {
//				Log.i("MaizeWebChromeClient", "===========onCancel");
				isCancelProgressDialog = true;
			}
    		
    	});
    }
	
	 public void setWebView(MaizeWebView view) {
	        this.appView = view;
	    }
	 
	 
	 
	 
	 /**
	     * Tell the client to display a javascript alert dialog.
	     *
	     * @param view
	     * @param url
	     * @param message
	     * @param result
	     */
	    @Override
	    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
	        AlertDialog.Builder dlg = new AlertDialog.Builder(maize);
	        dlg.setMessage(message);
	        dlg.setTitle("提示");
	        //Don't let alerts break the back button
	        dlg.setCancelable(true);
	        dlg.setPositiveButton(android.R.string.ok,
	                new AlertDialog.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        result.confirm();
	                    }
	                });
	        dlg.setOnCancelListener(
	                new DialogInterface.OnCancelListener() {
	                    public void onCancel(DialogInterface dialog) {
	                        result.cancel();
	                    }
	                });
	        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
	            //DO NOTHING
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	                if (keyCode == KeyEvent.KEYCODE_BACK)
	                {
	                    result.confirm();
	                    return false;
	                }
	                else
	                    return true;
	            }
	        });
	        dlg.create();
	        dlg.show();
	        return true;
	    }
	    
	    
	    
	    public void showMessage(String text) {
	    	Dialog dialog = new AlertDialog.Builder(maize)
			.setTitle("提示").setMessage(text)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create();
	    	dialog.show();
	    }
	    
	    
	    /**
	     * Tell the client to display a confirm dialog to the user.
	     *
	     * @param view
	     * @param url
	     * @param message
	     * @param result
	     */
	    @Override
	    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
	        AlertDialog.Builder dlg = new AlertDialog.Builder(maize);
	        dlg.setMessage(message);
	        dlg.setTitle("请确认");
	        dlg.setCancelable(true);
	        dlg.setPositiveButton(android.R.string.ok,
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        result.confirm();
	                    }
	                });
	        dlg.setNegativeButton(android.R.string.cancel,
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        result.cancel();
	                    }
	                });
	        dlg.setOnCancelListener(
	                new DialogInterface.OnCancelListener() {
	                    public void onCancel(DialogInterface dialog) {
	                        result.cancel();
	                    }
	                });
	        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
	            //DO NOTHING
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	                if (keyCode == KeyEvent.KEYCODE_BACK)
	                {
	                    result.cancel();
	                    return false;
	                }
	                else
	                    return true;
	            }
	        });
	        dlg.create();
	        dlg.show();
	        return true;
	    }

	    /**
	     * Tell the client to display a prompt dialog to the user.
	     * If the client returns true, WebView will assume that the client will
	     * handle the prompt dialog and call the appropriate JsPromptResult method.
	     *
	     * Since we are hacking prompts for our own purposes, we should not be using them for
	     * this purpose, perhaps we should hack console.log to do this instead!
	     *
	     * @param view
	     * @param url
	     * @param message
	     * @param defaultValue
	     * @param result
	     */
	    @Override
	    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

	        // Security check to make sure any requests are coming from the page initially
	        // loaded in webview and not another loaded in an iframe.
	        boolean reqOk = false;
	        if (url.startsWith("file://")) {
	            reqOk = true;
	        }

	        

	        // Show dialog
	        else {
	            final JsPromptResult res = result;
	            AlertDialog.Builder dlg = new AlertDialog.Builder(maize);
	            dlg.setMessage(message);
	            final EditText input = new EditText(maize);
	            if (defaultValue != null) {
	                input.setText(defaultValue);
	            }
	            dlg.setView(input);
	            dlg.setCancelable(false);
	            dlg.setPositiveButton(android.R.string.ok,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int which) {
	                            String usertext = input.getText().toString();
	                            res.confirm(usertext);
	                        }
	                    });
	            dlg.setNegativeButton(android.R.string.cancel,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int which) {
	                            res.cancel();
	                        }
	                    });
	            dlg.create();
	            dlg.show();
	        }
	        return true;
	    }
	 
	 
	 @Override
	    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
	        this.appView.showCustomView(view, callback);
	    }

		@Override
		public void onHideCustomView() {
	    	this.appView.hideCustomView();
		}
		
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
//			Log.i("aaa", "newProgress==================="+newProgress);
			if (newProgress == 100) {
//				 ((MaizeActivity)maize).progressbar.setProgress(newProgress);
				progressdialog.dismiss();
				isCancelProgressDialog = false;
            } else {
//                if (((MaizeActivity)maize).progressbar.getVisibility() == View.GONE)
//                	((MaizeActivity)maize).progressbar.setVisibility(View.VISIBLE);
//                ((MaizeActivity)maize).progressbar.setProgress(newProgress);
            	if(!isCancelProgressDialog){
            		progressdialog.setProgress(newProgress);
            		progressdialog.show();
            	}
            }
			
	        
			super.onProgressChanged(view, newProgress);
		}
		
	
//		ImageView iamgeView = null;//phonegap
//	public void onProgressChanged_(WebView view, int newProgress) {
//		if (newProgress == 100) {
//			if (iamgeView != null)
//				iamgeView.setVisibility(View.GONE);
//			// view.setVisibility(View.VISIBLE);
//
//			// DroidGap.this.root.addView(view);
//
//			System.out.println("加载完成");
//			Animation translate_in = AnimationUtils.loadAnimation(
//					maize, R.drawable.translate_in);
//
//			translate_in.setFillAfter(true);
//			translate_in.setDuration(1000);
//			translate_in.setDetachWallpaper(true);
//			// translate_in.
//			view.setAnimation(translate_in);
//
//			Animation translate_out = AnimationUtils.loadAnimation(
//					maize, R.drawable.translate_out);
//
//			translate_out.setAnimationListener(new AnimationListener() {
//
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					if (null != iamgeView) {
//						maize.root.removeView(iamgeView);
//						iamgeView = null;
//					}
//
//				}
//
//				@Override
//				public void onAnimationRepeat(Animation animation) {
//					// TODO Auto-generated method stub
//				}
//
//				@Override
//				public void onAnimationStart(Animation animation) {
//					// TODO Auto-generated method stub
//				}
//
//			});
//			translate_out.setFillAfter(true);
//			translate_out.setDuration(1000);
//			translate_out.setDetachWallpaper(true);
//			// translate_in.
//			if (null != iamgeView) {
//				iamgeView.setAnimation(translate_out);
//			}
//		} else {
//
//			if (null == iamgeView) {
//
//				iamgeView = new ImageView(maize);
//
//				view.setDrawingCacheEnabled(true);
//				Bitmap bitmap = view.getDrawingCache();
//				if (null != bitmap) {
//					Bitmap b = Bitmap.createBitmap(bitmap);
//					iamgeView.setImageBitmap(b);
//				}
//				maize.root.addView(iamgeView);
//			}
//		}
//		super.onProgressChanged(view, newProgress);
//	}
	
	
	 // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
    	((MaizeI)maize).setUploadMessage(uploadMsg);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        maize.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    // The undocumented magic method override
    // Eclipse will swear at you if you try to put @Override here
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
    	((MaizeI)maize).setUploadMessage(uploadMsg);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        maize.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }
    
    
  //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
    	((MaizeI)maize).setUploadMessage(uploadMsg);
      Intent i = new Intent(Intent.ACTION_GET_CONTENT);
      i.addCategory(Intent.CATEGORY_OPENABLE);
      i.setType("*/*");
      maize.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

    }
    
    public ValueCallback<Uri> getValueCallback() {
        return ((MaizeI)maize).getUploadMessage();
    }
}
