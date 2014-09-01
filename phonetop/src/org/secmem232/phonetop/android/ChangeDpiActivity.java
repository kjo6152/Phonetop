package org.secmem232.phonetop.android;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.secmem232.phonetop.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeDpiActivity extends Activity implements OnItemClickListener {

	private static final int GET_ACTIVITIES = 0;
	PackageManager packageManager;
	 ListView apkList;
	 Button btn1;
	 TextView curDpi,changeDpi,result;
	 SeekBar seek;
	 int resultDpi;
	 SharedPreferences pref ;
   SharedPreferences.Editor editor;
   String cur;
   List<PackageInfo> installPackageList;
   List<PackageInfo> packageList;
   ApkAdapter adapter;
   Map<String,Double> appCountInfo; // packageName , count 담을 map 선언.
   TreeMap<String,Double> sortAppCountInfo;
   ArrayList<String> checkPackageInfo;
   
	String [] SystemApp = { "org.secmem232.phonetop","com.android.packageinstaller", "com.google.android.gms", "android", "com.lge.update","com.android.location.fused",
			"jp.co.omronsoft.openwnn","com.android.smspush","com.android.tag","com.android.externalstorage","com.redbend.vdmc",
			"com.android.systemui", "com.android.soundrecorder",	"com.android.voicedialer",
			"com.android.defcontainer", "com.android.inputmethod.latin","com.android.proxyhandler",
			"com.android.htmlviewer", "com.android.bluetooth",	"com.android.inputdevices",
			"com.android.wallpaper.holospiral","com.android.nfc","com.android.onetimeinitializer",
			"com.android.providers.userdictionary","com.android.sharedstoragebackup",
			"com.android.vpndialogs"	,"com.android.providers",	"com.android.provision",
			"com.android.pacprocessor",  	"com.android.certinstaller",	"com.android.keychain",
			"com.android.packageinstaller",		"com.svox.pico",		"com.android.shell",
			"com.android.keyguard",			"com.android.backupconfirm",	"com.android.wallpapercropper", 
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dpi_change_main);
		 try {
	            ////////////////////////////////////////////////////////////////
	            BufferedReader in = new BufferedReader(new FileReader("/data/data/countingPackage.txt"));
	            String s;
	            String []split=null;
	   
	            appCountInfo = new HashMap<String, Double>(); // 객체생성
	            while ((s = in.readLine()) != null) {
	              split = s.split("/"); // 파일에서 읽어온 문자열을 PackageName 과 count로 나눠
	              appCountInfo.put(split[0], Double.parseDouble(split[1]));// map 객체에 담아.
	              Log.d("test3","split[0]: " + split[0] + "split[1] :" +split[1]);
	           //check 배열에 packageName을 넣어서 실행한 어플리케이션이 아닌 설치된 어플리케이션을 구분.
	               
	            }
	            in.close();
	            ////////////////////////////////////////////////////////////////
	          } catch (IOException e) {
	        	   Log.d("test2",""+e);
	          }
		    
		    
	       //////////////////////map 객체를 count 에 따라 정렬하는 부분////////////////////////
		    	Log.d("test3" , "Nosort :" + appCountInfo.toString());
		    	ValueComparator bvc =  new ValueComparator(appCountInfo);
		    	sortAppCountInfo = new TreeMap<String,Double>(bvc);
		    	sortAppCountInfo.putAll(appCountInfo);
		    	Log.d("test3" , "Sort :" + sortAppCountInfo.toString());
		   ///////////////////////////////////////////////////////////////////////////////////
		    	
		    	 packageList = new ArrayList<PackageInfo>();
		    	 packageManager = getPackageManager();
		    	 PackageInfo p;
		    	 for (String appPackageName : sortAppCountInfo.keySet()) {
		    		try {
		    			if(!appPackageName.equals("org.secmem232.phonetop")){
		    				p=packageManager.getPackageInfo(appPackageName,GET_ACTIVITIES );
							packageList.add(p);	
		    			}
						
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
		    	 
		    	 
		         installPackageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		        /*To filter out System apps*/
		       
		        for(PackageInfo pi : installPackageList) {
		           boolean addPackage=true;
		           // boolean b = isSystemPackage(pi);
		            //if(!b) {
		                //packageList1.add(pi);
		            //}
		           //  정렬된 packageinfo에
		        	for (String pkName : sortAppCountInfo.keySet()) {
						if(pi.packageName.equals(pkName)){
							addPackage=false;
							break;
						}
					}
		        	
		        	for (String name : SystemApp) {
						if(pi.packageName.equals(name)){
							addPackage =false;
							break;
						}
					}
		        	
		        	if(addPackage) packageList.add(pi);
		        }
		        adapter = new ApkAdapter(this, packageList, packageManager);
		        apkList = (ListView) findViewById(R.id.applist);
		        apkList.setAdapter(adapter);
		        apkList.setOnItemClickListener(this);
		       
		    }

//		    private boolean isSystemPackage(PackageInfo pkgInfo) {
//		        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
//		                : false;
//		    }
//		     
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position,
		            long row) {
		        PackageInfo packageInfo = (PackageInfo) parent.getItemAtPosition(position);
		  
		        //AppData appData = (AppData) getApplicationContext();
		        //appData.setPackageInfo(packageInfo);
		        //Toast toast = Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT);
				//toast.show();
		    	String appLable;
				try {
					
					
					appLable = (String)getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageInfo.packageName, PackageManager.GET_UNINSTALLED_PACKAGES));
					ShowDialog(packageInfo.packageName,appLable);
					
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    
		    	
		    }
		
			public void ShowDialog(final String packageName, String appLable)
			{
		        AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		        
		        View Viewlayout = inflater.inflate(R.layout.custom_dialog,(ViewGroup) findViewById(R.id.layout_dialog));       

		        curDpi = (TextView)Viewlayout.findViewById(R.id.curDpi); // txtItem1
		        changeDpi = (TextView)Viewlayout.findViewById(R.id.changeDpi); // txtItem1
		        SharedPreferences pref1 = getSharedPreferences("PackageDpi", MODE_PRIVATE);
		        cur = pref1.getString(packageName, "");
		        
		        if(cur=="") 	        	curDpi.setText(" Cur Dpi : 480");
		        else curDpi.setText(" Cur Dpi : "+cur);

				changeDpi.setText(" Change Dpi : ");
				Drawable icon;
				try {
					icon = getPackageManager().getApplicationIcon(packageName);
					icon.setBounds(0, 0, 10, 10);
					popDialog.setIcon(icon);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				popDialog.setTitle(appLable);
			
				popDialog.setView(Viewlayout);
				if(cur.equals("")) cur="480";
				int currentDpi = Integer.parseInt(cur);
				//  seekBar
				seek = (SeekBar) Viewlayout.findViewById(R.id.seekBar);
				seek.setMax(480);
				seek.setProgress(currentDpi);
				resultDpi = currentDpi;
				seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				            //Do something here with new value
				        	
				        	if(progress < 160){
				        		seek.setProgress(160);
				        		changeDpi.setText(" Change Dpi : " + 160);
				        		resultDpi = 160;
				        	}else{
				        		
				        		changeDpi.setText(" Change Dpi : " + progress);
				        		resultDpi = progress;
				        	}
				        }

						public void onStartTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub
							
						}

						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							
						}
				    });
				
			
				// Button OK
				popDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
					
								pref = getSharedPreferences("PackageDpi", Context.MODE_WORLD_READABLE);
							    editor = pref.edit();
								editor.putString(packageName,""+resultDpi);
								editor.commit();
							
								ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
								am.killBackgroundProcesses (packageName);
								Log.d("test5" , packageName);
								
								Toast toast = Toast.makeText(getApplicationContext(),"변경", Toast.LENGTH_SHORT);
								
								toast.show();
								
								dialog.dismiss();
								adapter.notifyDataSetChanged();
							}

						});
				popDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast toast = Toast.makeText(getApplicationContext(),"취소", Toast.LENGTH_SHORT);
						
						toast.show();
						dialog.dismiss();
					
					}
				});
				popDialog.create();
				popDialog.show();
		        
			}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

//value 정렬...
class ValueComparator implements Comparator<String> {

 Map<String, Double> base;
 public ValueComparator(Map<String, Double> base) {
     this.base = base;
 }

 // Note: this comparator imposes orderings that are inconsistent with equals.    
 public int compare(String a, String b) {
     if (base.get(a) >= base.get(b)) {
         return -1;
     } else {
         return 1;
     } // returning 0 would merge keys
 }
}