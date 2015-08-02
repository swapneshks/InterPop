package com.inflectionPoint.interpop;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;

public class MainMenuView extends GLSurfaceView {

	MainMenuRenderer renderer;
	public MainMenuView(Context context, ScreenDetails screenDetails) {
		super(context);
		
		renderer = new MainMenuRenderer(context, this, screenDetails);
		this.setRenderer(renderer);
			
	}
	
	public MainMenuView(Context context) {
		super(context);
	}
	
	public void startIntent(int i) {
		if(i == 0) {
			Intent newGame = new Intent(getContext(), GameActivity.class);
			getContext().startActivity(newGame);
			renderer.setOptionOff();
		}
		else if(i == 1) {
			Intent settings = new Intent(getContext(), SettingsActivity.class);
			getContext().startActivity(settings);
			renderer.setOptionOff();
		}
		else if(i == 2) {
			Intent score = new Intent(getContext(), ScoreActivity.class);
			getContext().startActivity(score);
			renderer.setOptionOff();
		}
		else if(i == 3) {
			Intent help = new Intent(getContext(), HelpActivity.class);
			getContext().startActivity(help);
			renderer.setOptionOff();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		renderer.setOptionOn();
	}

}
