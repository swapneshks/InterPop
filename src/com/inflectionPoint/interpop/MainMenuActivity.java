package com.inflectionPoint.interpop;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class MainMenuActivity extends Activity {

	private MainMenuView mainMenuView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Context context = getBaseContext();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mainMenuView = new MainMenuView(this, new ScreenDetails(metrics, 5));
		setContentView(mainMenuView);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mainMenuView.onResume();
	}

	@Override
	public void onBackPressed() {
		
	}
	
}
