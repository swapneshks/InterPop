package com.inflectionPoint.interpop;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class ScoreView extends GLSurfaceView {
	
	public ScoreView(Context context, ScreenDetails screenDetails) {
		super(context);
		
		this.setRenderer(new ScoreRenderer(context, this, screenDetails));
	}

	public ScoreView(Context context) {
		super(context);
	}

}
