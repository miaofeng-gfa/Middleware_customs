package cn.com.gfa.ecma.activity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

public class LinearLayoutSoftKeyboardDetect extends LinearLayout{
	private static final String TAG = "SoftKeyboardDetect";

    private int oldHeight = 0;  // Need to save the old height as not to send redundant events
    private int oldWidth = 0; // Need to save old width for orientation change
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Activity app = null;

    public LinearLayoutSoftKeyboardDetect(Context context, int width, int height) {
        super(context);
        screenWidth = width;
        screenHeight = height;
        app = (Activity) context;
    }

    @Override
    /**
     * Start listening to new measurement events.  Fire events when the height
     * gets smaller fire a show keyboard event and when height gets bigger fire
     * a hide keyboard event.
     *
     * Note: We are using app.postMessage so that this is more compatible with the API
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.v(TAG, "We are in our onMeasure method");

        // Get the current height of the visible part of the screen.
        // This height will not included the status bar.\
        int width, height;

        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        // If the oldHeight = 0 then this is the first measure event as the app starts up.
        // If oldHeight == height then we got a measurement change that doesn't affect us.
        if (oldHeight == 0 || oldHeight == height) {
        	Log.d(TAG, "Ignore this event");
        }
        // Account for orientation change and ignore this event/Fire orientation change
        else if (screenHeight == width)
        {
            int tmp_var = screenHeight;
            screenHeight = screenWidth;
            screenWidth = tmp_var;
            Log.v(TAG, "Orientation Change");
        }
        // If the height as gotten bigger then we will assume the soft keyboard has
        // gone away.
//        else if (height > oldHeight) {
//            if (app != null)
//                app.appView.sendJavascript("cordova.fireDocumentEvent('hidekeyboard');");
//        }
//        // If the height as gotten smaller then we will assume the soft keyboard has 
//        // been displayed.
//        else if (height < oldHeight) {
//            if (app != null)
//                app.appView.sendJavascript("cordova.fireDocumentEvent('showkeyboard');");
//        }

        // Update the old height for the next event
        oldHeight = height;
        oldWidth = width;
    }
}
