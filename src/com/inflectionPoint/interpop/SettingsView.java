package com.inflectionPoint.interpop;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class SettingsView extends GLSurfaceView {

	public SettingsView(Context context, ScreenDetails screenDetails) {
		super(context);
		
		this.setRenderer(new SettingsRenderer(context, this, screenDetails));
	}
	
	public SettingsView(Context context) {
		super(context);
	}

}
