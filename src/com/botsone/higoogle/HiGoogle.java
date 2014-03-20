package com.botsone.higoogle;


import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.WindowManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;


public class HiGoogle extends Application implements IXposedHookLoadPackage {

	
	
	public void handleLoadPackage(final LoadPackageParam lpparam)
			throws Throwable {

		
		
		if (!lpparam.packageName.equals("com.vlingo.midas"))
			return;

		
		XposedBridge.log("Loaded app: " + lpparam.packageName);

		
		findAndHookMethod("com.samsung.alwaysmicon.AlwaysMicOnService",
				lpparam.classLoader, "launchSVoice",
				new XC_MethodReplacement() {

			
			
			@SuppressLint("Wakelock")
			protected Object replaceHookedMethod(MethodHookParam param)
					throws Throwable {

				
				
				Context context = getContextHelper();

				
				createWakeLock(context);

				
				configureIntent(context);

				
				return null;

				
			}
			
			
			
			@SuppressLint("Wakelock")
			private void createWakeLock(Context context){
				
				
				
				PowerManager pm = (PowerManager) context
						.getSystemService(Context.POWER_SERVICE);

				
				@SuppressWarnings("deprecation")
				PowerManager.WakeLock wakeLock = pm.newWakeLock(
						PowerManager.SCREEN_BRIGHT_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP
						, "HiGoogle wakelock");

				
				wakeLock.acquire(10000);
				
				
				//TODO add stuff to turn screen off etc...
				
				
			}
			
			

			private void configureIntent(Context localContext) {

				
				
				Intent localIntent = new Intent(
						RecognizerIntent.ACTION_WEB_SEARCH);


				localIntent.putExtra(
						"android.speech.extra.LANGUAGE_MODEL", "en-US");

				
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				
				localIntent
				.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

				
				localContext.startActivity(localIntent); 
				
				
			}

			
			
			private Context getContextHelper() {
				
				

				Log.d("vlingo VLINGO_LOG",
						"vlingo Trying to get context from AndroidAppHelper");
				

				Context localContext = AndroidAppHelper
						.currentApplication().getApplicationContext();
				

				if (localContext == null) {
					
					

					Log.d("vlingo VLINGO_LOG",
							"vlingo Trying to get context from mSystemContext");
					

					Object localObject2 = XposedHelpers.getStaticObjectField(
							XposedHelpers.findClass(
									"android.app.ActivityThread", null),
							"mSystemContext");
					

					if (localObject2 != null)
						

						localContext = (Context) localObject2;
	

				}

				
				
				if (localContext == null) {

					
					
					Log.d("vlingo VLINGO_LOG",
							"vlingo Trying to get activityThread from systemMain");
					

					Object localObject1 = XposedHelpers.callStaticMethod(
							XposedHelpers.findClass(
									"android.app.ActivityThread", null),
									"systemMain", new Object[0]);
					

					if (localObject1 != null) {
						
						

						Log.d("vlingo VLINGO_LOG",
								"vlingo Trying to get context from getSystemContext");
						

						localContext = (Context) XposedHelpers
								.callMethod(localObject1,
										"getSystemContext",
										new Object[0]);

						
					}
				}
				return localContext;
				
				
			}
		});
	};
}
