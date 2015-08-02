package com.inflectionPoint.interpop;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class SettingsRenderer implements GLSurfaceView.Renderer{

	static final int NONE = 0;
	static final int SOUND_OFF = 1;
	static final int SOUND_ON = 2;
	static final int RESET = 5;
	
	static final int AMPLITUDE_INCREASING = 10;
	static final int AMPLITUDE_DECREASING = -10;
	
	private Context context;
	private SettingsView settingsView;
	private ScreenDetails screenDetails;
	private DrawingUtilities utilities;
	private SoundManager soundManager;
	
	private RectF soundOnOff;
	private PointF soundToggleOn;
	private PointF soundToggleOff;
	private RectF reset;
	private PointF resetToggle;

	private RectF rectFullScreen;
	private RectF rectReset;
	private RectF backToMenuButton;
	
	private int touchedPointIndex;
	private PointF touchedPoint;
	private boolean isTouched;
	private boolean isDrawing;
	private int drawingRadius;
	private float color[];
	
	private Paint textPaint;
	private Typeface font;
	
	private float amplitude;
	private int amplitudeType;
	
	private boolean isResetSelected;
	private boolean isReset;
	
	private long startTime;
	private long elapsedTime;
	
	public SettingsRenderer(Context context, SettingsView settingsView, ScreenDetails screenDetails) {
		this.context = context;
		this.settingsView = settingsView;
		this.screenDetails = screenDetails;
		this.utilities = new DrawingUtilities(context, screenDetails, "background_help_512x1024.png");
		this.soundManager = new SoundManager(context, 5);
		
		this.soundOnOff = new RectF(screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim * 0.75f, 
									screenDetails.metrics.heightPixels - screenDetails.cellDim * 2 / 3f, 
									screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim * 0.75f + screenDetails.cellDim * 2 / 3f, 
									screenDetails.metrics.heightPixels - screenDetails.cellDim * 4 / 3f);
		this.soundToggleOn = new PointF(soundOnOff.right + screenDetails.cellDim / 4, 
										screenDetails.metrics.heightPixels - screenDetails.cellDim);
		this.soundToggleOff = new PointF(soundOnOff.left - screenDetails.cellDim / 4, 
										 screenDetails.metrics.heightPixels - screenDetails.cellDim);
		this.reset = new RectF(screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim * 0.75f, 
							   screenDetails.metrics.heightPixels - screenDetails.cellDim - screenDetails.cellDim * 2 / 3f, 
							   screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim * 0.75f + screenDetails.cellDim * 2 / 3f, 
							   screenDetails.metrics.heightPixels - screenDetails.cellDim - screenDetails.cellDim * 4 / 3f);
		this.resetToggle = new PointF (reset.right + screenDetails.cellDim / 4,
									   screenDetails.metrics.heightPixels - 2 * screenDetails.cellDim);
		
		this.rectFullScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 0);
		this.rectReset = new RectF(screenDetails.margin,
				   				   screenDetails.metrics.widthPixels - screenDetails.margin,
				   				   screenDetails.metrics.widthPixels - screenDetails.margin,
				   				   screenDetails.margin);
		this.backToMenuButton = new RectF(screenDetails.metrics.widthPixels / 2 - screenDetails.cellDim * 0.3333F, 
										  screenDetails.cellDim * 0.6666F + 10, 
			      						  screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim * 0.3333F, 
			      						  10);

        this.touchedPoint = new PointF(-100, -100);
        this.touchedPointIndex = NONE;
        this.isTouched = false;
    	this.isDrawing = false;
    	this.drawingRadius = -1;        
        this.color = new float[4];
		color[0] = color[1] = color[2] = color[3] = 1f;
		
		this.textPaint = new Paint();
		this.amplitude = 0;
		this.amplitudeType = AMPLITUDE_INCREASING;
		
		this.isResetSelected = false;
		this.isReset = false;
		
		this.startTime = this.elapsedTime = 0;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		this.startTime = System.nanoTime();
		Settings.firstRun = false;
		Settings.saveSettings(new FileManager(context));
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glViewport(0, 0, screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, screenDetails.metrics.widthPixels, 0, screenDetails.metrics.heightPixels, 1, -1);
		
		color[0] = color[1] = color[2] = color[3] = 1f;
		
		utilities.drawTexture(gl, rectFullScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);
		if(!isResetSelected)
			utilities.drawTexture(gl, backToMenuButton, DrawingUtilities.TEXTURE_GAMEBACKBUTTON, 1);
		utilities.createBitmap();
		
		font = Typeface.createFromAsset(context.getAssets(), "FREEDOM.ttf");
		
		textPaint.setColor(Color.YELLOW);
		textPaint.setTypeface(font);
		textPaint.setTextSize((50 / 88.0f) * screenDetails.cellDim);
		textPaint.setTextAlign(Align.CENTER);
		
		utilities.drawTextToBitmap("SETTINGS", screenDetails.metrics.widthPixels / 2, screenDetails.cellDim / 2, textPaint);
				
		textPaint.setTextSize((32 / 88.0f) * screenDetails.cellDim);
		textPaint.setTextAlign(Align.CENTER);
		
		utilities.drawTextToBitmap("Sound:", screenDetails.cellDim * 2, screenDetails.metrics.heightPixels - soundToggleOn.y + 10 + amplitude, textPaint);
		utilities.drawTextToBitmap("Reset Scores:", screenDetails.cellDim * 3/2, screenDetails.metrics.heightPixels - resetToggle.y + amplitude * 2 + 10, textPaint);
		utilities.drawText(gl);

		utilities.drawCircularElement(gl, soundToggleOn.x , soundToggleOn.y, 2, InscribedPolygon.MODE_FILL, 1, color);
		utilities.drawCircularElement(gl, soundToggleOn.x , soundToggleOn.y, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		utilities.drawCircularElement(gl, soundToggleOff.x , soundToggleOff.y, 2, InscribedPolygon.MODE_FILL, 1, color);
		utilities.drawCircularElement(gl, soundToggleOff.x , soundToggleOff.y, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		if(Settings.mute  == false) {
			utilities.drawTexture(gl, soundOnOff, DrawingUtilities.TEXTURE_SOUNDON, 1);
		}
		else {
			utilities.drawTexture(gl, soundOnOff, DrawingUtilities.TEXTURE_SOUNDOFF, 1);
		}
		
		utilities.drawCircularElement(gl, resetToggle.x, resetToggle.y, 2, InscribedPolygon.MODE_FILL, 1, color);
		utilities.drawCircularElement(gl, resetToggle.x, resetToggle.y, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		utilities.drawTexture(gl, reset, DrawingUtilities.TEXTURE_RESET, 1);
		
		settingsView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				
				switch(action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					PointF tPoint = new PointF(event.getX(), screenDetails.metrics.heightPixels - event.getY());
					
					if(utilities.touchedRegionCheck(tPoint, soundToggleOn, 20)) {
									if(!isTouched && !isDrawing && Settings.mute == true){
										isTouched = true;
										touchedPoint = soundToggleOn;
										touchedPointIndex = SOUND_ON;
									}
					}
				    else if(utilities.touchedRegionCheck(tPoint, soundToggleOff, 20)) {
									if(!isTouched && !isDrawing && Settings.mute == false){
										isTouched = true;
										touchedPoint = soundToggleOff;
										touchedPointIndex = SOUND_OFF;
									}
				    }
				    else if(utilities.touchedRegionCheck(tPoint, resetToggle, 20)) {
				    				if(!isTouched && !isDrawing){
				    					isTouched = true;
				    					touchedPoint = resetToggle;
				    					touchedPointIndex = RESET;
				    				}
				    }
				    else
			    		isTouched = false;
					return true;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE:
					if(!isDrawing && isTouched && !isResetSelected){
						isDrawing = true;
						drawingRadius = 0;
					}
					if(isResetSelected) {
						if(utilities.touchedRegionCheck(new PointF(event.getX(), screenDetails.metrics.heightPixels - event.getY()),
													    new PointF(((120f / 512) * (screenDetails.metrics.widthPixels - 2 * screenDetails.margin)) + rectReset.left, 
													    		   ((230f / 512) * (screenDetails.metrics.widthPixels - 2 * screenDetails.margin)) + rectReset.bottom), 
													    (90f / 512) * (screenDetails.metrics.widthPixels - 2 * screenDetails.margin))) {
							isReset = true;
							isResetSelected = false;
						}
						if(utilities.touchedRegionCheck(new PointF(event.getX(), screenDetails.metrics.heightPixels - event.getY()),
							    						new PointF(((390f / 512) * (screenDetails.metrics.widthPixels - 2 * screenDetails.margin)) + rectReset.left, 
							    								   ((230f / 512) * (screenDetails.metrics.widthPixels - 2 * screenDetails.margin)) + rectReset.bottom), 
							    					    (90f / 512) * (screenDetails.metrics.widthPixels - 2 * screenDetails.margin))) {
							isReset = false;
							isResetSelected = false;
						}
					}
					if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), backToMenuButton) && !isResetSelected)
						((SettingsActivity) context).finish();
					isTouched = false;
					return true;
				default:
					isTouched = false;
					return false;
				}
			}
		});
		
		if(isTouched && !isDrawing){
			color[0] = 0.9f;
			color[1] = 0.05f;
			color[2] = 0.05f;
			color[3] = 1f;
			utilities.drawCircularElement(gl, touchedPoint.x, touchedPoint.y, 2, 
									InscribedPolygon.MODE_FILL, 1, color);
			utilities.drawCircularElement(gl, touchedPoint.x, touchedPoint.y, 6, 
									InscribedPolygon.MODE_BOUNDARY, 1, color);
		}
		else if(!isTouched && isDrawing && !isResetSelected) {
			color[0] = color[1] = color[2] = color[3] = 1f;
			utilities.drawCircularElement(gl, touchedPoint.x, touchedPoint.y, 
									drawingRadius, InscribedPolygon.MODE_BOUNDARY, 1, color);
			drawingRadius += 2.0 / 33333333 * elapsedTime;
		}
		
		if(isResetSelected)
			utilities.drawTexture(gl, rectReset, DrawingUtilities.TEXTURE_RESETPRESSED, 1);
			
		if(isReset) {
			Settings.saveDefSettings(new FileManager(context));
			Settings.loadSettings(new FileManager(context));
			
			((SettingsActivity) context).runOnUiThread(new Runnable() {
			    public void run() {
			    	Toast.makeText(context, "Scores reset.", Toast.LENGTH_SHORT).show();
			    }
			});
			
			if(!Settings.mute)
				soundManager.play(SoundManager.RESET);
			isReset = false;
		}
		
		if(drawingRadius >= screenDetails.cellDim / 4){
			isDrawing = false;
			drawingRadius = -1;
			switch(touchedPointIndex){
			case SOUND_ON:
				Settings.mute = false;
				break;
			case SOUND_OFF:
				Settings.mute = true;
				break;
	/*		case DIFFICULTY_DECREASE:
				if(Settings.startingDifficulty > 1)
					Settings.startingDifficulty--;
				else
					Settings.startingDifficulty = 1;
				break;
			case DIFFICULTY_INCREASE:
				if(Settings.startingDifficulty < 10)
					Settings.startingDifficulty++;
				else 
					Settings.startingDifficulty = 10;
				break;            */
			case RESET:
				isResetSelected = true;
				break;
			default:
				break;
			}
			
		Settings.saveSettings(new FileManager(context));
		}
		
		if(amplitudeType == AMPLITUDE_INCREASING) {
			amplitude += elapsedTime / 250000000.0;
			if(amplitude >= 5)
				amplitudeType = -amplitudeType;
		}
		else if(amplitudeType == AMPLITUDE_DECREASING) {
			amplitude -= elapsedTime / 250000000.0;
			if(amplitude <= -5)
				amplitudeType = -amplitudeType;
		}
		
		utilities.recycleBitmap();
		
		this.elapsedTime = System.nanoTime() - startTime;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.1f, 0.1f, 0.1f, 0f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

}
