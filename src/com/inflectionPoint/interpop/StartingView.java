package com.inflectionPoint.interpop;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class StartingView extends GLSurfaceView {

	private StartingRenderer startingRenderer;
	
	private float touchedX;
	private float touchedY;
	
	public StartingView(Context context, final ScreenDetails screenDetails) {
		super(context);
		
		startingRenderer = new StartingRenderer(context, this, screenDetails);
		setRenderer(startingRenderer);
		
		this.touchedX = -100;
		this.touchedY = -100;
		
		this.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				
				switch(action){
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					touchedX = event.getX();
					touchedY = screenDetails.metrics.heightPixels - event.getY();
					return true;
				case MotionEvent.ACTION_UP:
					touchedX = -100;
					touchedY = -100;
					if(startingRenderer.isCompleted()){
						Intent mainMenu = new Intent(getContext(), MainMenuActivity.class);
						getContext().startActivity(mainMenu);
						return true;
					}
					else
						return false;
				default: 
					touchedX = -100;
					touchedY = -100;
					return false;
				}
			}
		});
	}
	
	public StartingView(Context context) {
		super(context);
	}
	
	public StartingView(Context context, AttributeSet set) {
		super(context);
	}
	
	public StartingView(Context context, AttributeSet set, int arg) {
		super(context);
	}
	
	public float getTouchedX() {
		return touchedX;
	}
	
	public float getTouchedY() {
		return touchedY;
	}
	
}
