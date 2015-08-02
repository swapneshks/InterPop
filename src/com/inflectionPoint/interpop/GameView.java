package com.inflectionPoint.interpop;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GameView extends GLSurfaceView {

	public GameView(Context context, ScreenDetails screenDetails) {
		super(context);
		
		this.setRenderer(new GameRenderer(context, this, screenDetails));
	}

}
