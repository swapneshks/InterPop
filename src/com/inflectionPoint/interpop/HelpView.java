package com.inflectionPoint.interpop;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class HelpView extends GLSurfaceView {

	public HelpView(Context context, ScreenDetails screenDetails) {
		super(context);
		
		this.setRenderer(new HelpRenderer(context, this, screenDetails));
	}
	
	public HelpView(Context context){
		super(context);
	}

}
