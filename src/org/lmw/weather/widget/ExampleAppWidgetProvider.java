package org.lmw.weather.widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lmw.weather.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/*
 * @author : skywang <wangkuiwu@gmail.com>
 * description : 提供App Widget
 */

public class ExampleAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "ExampleAppWidgetProvider";

	private boolean DEBUG = false; 
    // 启动ExampleAppWidgetService服务对应的action
    private final Intent EXAMPLE_SERVICE_INTENT = new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
    // 更新 widget 的广播对应的action
	private final String ACTION_UPDATE_ALL = "org.lmw.weather.UPDATE";
    // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
	private static Set idsSet = new HashSet();
	// 按钮信息
    private static final int BUTTON_SHOW = 1;
	// 图片数组
    private static final int[] ARR_IMAGES = { 
    	R.drawable.cloud_1, 
    	R.drawable.cloud_2, 
    	R.drawable.cloud_3, 
    	R.drawable.cloud_4, 
    	R.drawable.cloud_5, 
    	R.drawable.cloud_6, 
    	R.drawable.cloud_7,
    	R.drawable.cloud_8, 
    };
    private static String currWendu="99°";
	// onUpdate() 在更新 widget 时，被执行，
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(TAG, "onUpdate(): appWidgetIds.length="+appWidgetIds.length);

		// 每次 widget 被创建时，对应的将widget的id添加到set中
		for (int appWidgetId : appWidgetIds) {
			idsSet.add(Integer.valueOf(appWidgetId));
		}
		prtSet();
	}
    
    // widget被删除时调用  
    @Override  
    public void onDeleted(Context context, int[] appWidgetIds) {  
		Log.d(TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);

		// 当 widget 被删除时，对应的删除set中保存的widget的id
		for (int appWidgetId : appWidgetIds) {
			idsSet.remove(Integer.valueOf(appWidgetId));
		}
		prtSet();
		
        super.onDeleted(context, appWidgetIds);  
    }

    // 第一个widget被创建时调用  
    @Override  
    public void onEnabled(Context context) {  
    	Log.d(TAG, "onEnabled");
    	// 在第一个 widget 被创建时，开启服务
    	context.startService(EXAMPLE_SERVICE_INTENT);
    	
        super.onEnabled(context);  
    }  
    
    // 最后一个widget被删除时调用  
    @Override  
    public void onDisabled(Context context) {  
    	Log.d(TAG, "onDisabled");

    	// 在最后一个 widget 被删除时，终止服务
    	context.stopService(EXAMPLE_SERVICE_INTENT);

        super.onDisabled(context);  
    }
    
    
    // 接收广播的回调函数
    @Override  
    public void onReceive(Context context, Intent intent) {  
        final String action = intent.getAction();
        Log.d(TAG, "OnReceive:Action: " + action);
        if (ACTION_UPDATE_ALL.equals(action)) {
        	currWendu=intent.getExtras().getString("wendu");
        	// “更新”广播
        	updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
	    } else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
	    	// “按钮点击”广播
	        Uri data = intent.getData();
	        int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
	        if (buttonId == BUTTON_SHOW) {
	        	Log.d(TAG, "Button wifi clicked");
	        	Toast.makeText(context, "刷新天气", Toast.LENGTH_SHORT).show();
	        }
	    }
        
        super.onReceive(context, intent);  
    }  

    // 更新所有的 widget 
    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {

		Log.d(TAG, "updateAllAppWidgets(): size="+set.size());
		
		// widget 的id
    	int appID;
    	// 迭代器，用于遍历所有保存的widget的id
    	Iterator it = set.iterator();

    	while (it.hasNext()) {
    		appID = ((Integer)it.next()).intValue();    
    		// 随机获取一张图片
    		int index = (new java.util.Random().nextInt(ARR_IMAGES.length));
    		
    		if (DEBUG) Log.d(TAG, "onUpdate(): index="+index);    		
    		// 获取 example_appwidget.xml 对应的RemoteViews    		
    		RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.appwidget);
    		
    		// 设置显示图片
    		remoteView.setImageViewResource(R.id.iv_show, ARR_IMAGES[index]);
    		remoteView.setTextViewText(R.id.wendu, currWendu);
    		// 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
    		remoteView.setOnClickPendingIntent(R.id.btn_show, getPendingIntent(context, BUTTON_SHOW));

    		// 更新 widget
    		appWidgetManager.updateAppWidget(appID, remoteView);		
    	}    	
	}

    private PendingIntent getPendingIntent(Context context, int buttonId) {
        Intent intent = new Intent();
        intent.setClass(context, ExampleAppWidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("custom:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0 );
        return pi;
    }

    // 调试用：遍历set
    private void prtSet() {
    	if (DEBUG) {
	    	int index = 0;
	    	int size = idsSet.size();
	    	Iterator it = idsSet.iterator();
	    	Log.d(TAG, "total:"+size);
	    	while (it.hasNext()) {
	    		Log.d(TAG, index + " -- " + ((Integer)it.next()).intValue());
	    	}
    	}
    }
}
