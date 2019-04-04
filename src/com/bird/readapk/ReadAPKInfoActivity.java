package com.bird.readapk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.TextView;

public class ReadAPKInfoActivity extends Activity {
	private static final String FILE_PATH = "/mnt/sdcard/apk_info.txt";
	private TextView mTips;
	private TextView mDDMS;
	private TextView mSdcard;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
	        mTips = (TextView) findViewById(R.id.tips);
	        mDDMS = (TextView) findViewById(R.id.ddms);
	        mSdcard = (TextView) findViewById(R.id.sdcard);
			init();
		} catch (IOException e) {
			Log.e("bird", "Read APK info failed.", e);
		}
    }
    
    private void init() throws IOException{
    	boolean sdNormal = checkSDcard();
    	BufferedWriter writer = null;
    	if(sdNormal){
    		writer = new BufferedWriter(new FileWriter(FILE_PATH));
    	}
    	
        Log.e("bird", "-------------Apps Info begin-------------");
        if(sdNormal){
        	writer.write("-------------Apps Info begin-------------\n");
        }
        PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
        
        if(apps != null){
        	for(int i = 0; i < apps.size(); i++){
        		ResolveInfo app = apps.get(i);
        		String title = app.loadLabel(manager).toString();
        		String packageName = app.activityInfo.packageName;
        		String className = app.activityInfo.name;
        		Time today = new Time(Time.getCurrentTimezone());
        		if(sdNormal){
					writer.write("title = " + title + ", packageName = " 
	        				+ packageName + ", className = " + className + "\n");
        		}
        		Log.e("bird", "title = " + title + ", packageName = " 
        				+ packageName + ", className = " + className);
        	}
        }
        Log.e("bird", "-------------Apps Info end-------------");
        
        Log.e("bird", "-------------Widgets Info begin-------------");
        if(sdNormal){
        	writer.write("-------------Widgets Info begin-------------\n");
        }
        List<AppWidgetProviderInfo> widgets =
                AppWidgetManager.getInstance(this).getInstalledProviders();
        
        if(widgets != null){
            for (AppWidgetProviderInfo widget : widgets) {
                if (widget.minWidth > 0 && widget.minHeight > 0) {
            		if(sdNormal){
						writer.write("title = " + widget.label + ", packageName = " 
	            				+ widget.provider.getPackageName() + ", className = " + widget.provider.getClassName() + "\n");
            		}
            		Log.e("bird", "title = " + widget.label + ", packageName = " 
            				+ widget.provider.getPackageName() + ", className = " + widget.provider.getClassName());
                } 
            }
        }
        Log.e("bird", "-------------Widgets Info end-------------");
        
        Log.e("bird", "-------------Shortcuts Info begin-------------");
        if(sdNormal){
        	writer.write("-------------Shortcuts Info begin-------------\n");
        }
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        List<ResolveInfo> shortcuts = manager.queryIntentActivities(shortcutsIntent, 0);
        Collections.sort(shortcuts, new ResolveInfo.DisplayNameComparator(manager));
        
        if(shortcuts != null){
        	for(int i = 0; i < shortcuts.size(); i++){
        		ResolveInfo shortcut = shortcuts.get(i);
        		String title = shortcut.loadLabel(manager).toString();
        		String packageName = shortcut.activityInfo.packageName;
        		String className = shortcut.activityInfo.name;
        		
        		if(sdNormal){
					writer.write("title = " + title + ", packageName = " 
	        				+ packageName + ", className = " + className + "\n");
        		}
        		Log.e("bird", "title = " + title + ", packageName = " 
        				+ packageName + ", className = " + className);
        	}
        }
        Log.e("bird", "-------------Shortcuts Info end-------------");
        
        mTips.setText("读取APK信息 成功!!!");
        mDDMS.setText("可通过DDMS查看,tag为 bird,类型 为 E");
        if(sdNormal){
        	mSdcard.setText("也可查看该路径下的文件:" + FILE_PATH);
        	writer.close();
        }
    }
    
    private boolean checkSDcard(){
    	if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
    		mSdcard.setText("T卡不存在.");
    		return false;
    	}else{
    		File file = new File("/mnt/sdcard");
    		if(!file.exists()){
    			mSdcard.setText("T卡不存在.");
    			return false;
    		}
    		
    		file = new File(FILE_PATH);
    		if(file.exists()){
    			file.delete();
    		}
    		try {
				file.createNewFile();
				if(!file.canWrite()){
					mSdcard.setText("T卡不可写.");
					return false;
				}
			} catch (IOException e) {
				Log.e("bird", "create file failed.", e);
			}
    		
    	}
    	
    	return true;
    }
}