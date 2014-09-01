package org.secmem232.phonetop.android;

import java.util.List;

import org.secmem232.phonetop.R;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class ApkAdapter extends BaseAdapter {
	private static SharedPreferences prefs;
	List<PackageInfo> packageList;
    Activity context;
    PackageManager packageManager;
    String packageName;
    String dpi;
    public ApkAdapter(Activity context, List<PackageInfo> packageList,
            PackageManager packageManager) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.packageManager = packageManager;
    }
 
    private class ViewHolder {
        TextView apkName;
        TextView packageName;
        TextView apkDpi;
    }
 
    public int getCount() {
        return packageList.size();
    }
 
    public Object getItem(int position) {
        return packageList.get(position);
    }
 
    public long getItemId(int position) {
        return 0;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
    		Log.d("test6" , "getViewCall");
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.apklist_item, null);
            holder = new ViewHolder();
            
            holder.apkName = (TextView) convertView.findViewById(R.id.appname);
            holder.packageName = (TextView) convertView.findViewById(R.id.pkName);
            holder.apkDpi = (TextView) convertView.findViewById(R.id.appdpi);
            
            convertView.setTag(holder);
            Log.d("test6" , "convertView1");
            
        } else {
            holder = (ViewHolder) convertView.getTag();
            Log.d("test6" , "convertView2");
        }
        PackageInfo packageInfo = (PackageInfo) getItem(position);
        Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        
		
        packageName = packageInfo.packageName;
        appIcon.setBounds(0, 0, 30, 30);
        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
        holder.apkName.setCompoundDrawablePadding(15);
        holder.apkName.setText(" "+appName);
        holder.packageName.setText(" "+packageName);
        prefs = context.getSharedPreferences("PackageDpi",Context.MODE_WORLD_READABLE);
        
     
        // sharedPrefernce 에서 읽어와서 DPI 를 setText 해야해 ..
        	
        if(prefs.getString(packageName,"")!=null){
        	dpi = prefs.getString(packageName,""); 
        	
         }
        if(prefs.getString(packageName,"")==""){
        	dpi = "480";
         }
        holder.apkDpi.setText("Dpi : "+dpi);
       
        //notifyDataSetChanged();
               
        return convertView;

        
    }
    
}