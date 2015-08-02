package com.inflectionPoint.interpop;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.opengl.GLSurfaceView;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

public class MainMenuRenderer implements GLSurfaceView.Renderer{

	private Context context;
	private MainMenuView mainMenuView;
	private ScreenDetails screenDetails;
	
	private DrawingUtilities utilities;
	private SoundManager soundManager;
	
	private PointF point[];
	private PointF touchedPoint;
	private boolean isTouched;
	private int optionIndex;
	private boolean isDrawing;
	private int drawingRadius;
	private boolean option[];
	private boolean intentStarted;
	private float color[];
	
	private TextPaint textPaint;
	private Typeface font;
	
	private RectF rectScreen;
	private RectF rectOption;
	private RectF rectCredits;
	private RectF backToMenuButton;
	
	private boolean isCredits;
	
	public MainMenuRenderer(Context context, MainMenuView mainMenuView, ScreenDetails screenDetails) {
		this.context = context;
		this.mainMenuView = mainMenuView;
		this.screenDetails = screenDetails;
		
		this.utilities = new DrawingUtilities(context, screenDetails, "background_menu_512x1024.png");
		this.soundManager = new SoundManager(context, 5);
		
		point = new PointF[4];
		
		point[0] = new PointF(screenDetails.metrics.widthPixels - screenDetails.cellDim * 2, 
					(float) (screenDetails.metrics.heightPixels - screenDetails.cellDim * 1.5));
		point[1] = new PointF(screenDetails.cellDim * 2,
					(float) (screenDetails.metrics.heightPixels - screenDetails.cellDim * 3 - (10 / 88.0f) * screenDetails.cellDim));
		point[2] = new PointF(screenDetails.metrics.widthPixels - screenDetails.cellDim * 2, 
					(float) (screenDetails.metrics.heightPixels - screenDetails.cellDim * 4.5 - (20 / 88.0f) * screenDetails.cellDim));
		point[3] = new PointF(screenDetails.cellDim * 2,
					(float) (screenDetails.metrics.heightPixels - screenDetails.cellDim * 6 - (30 / 88.0f) * screenDetails.cellDim));
		touchedPoint = new PointF(-100, -100);
		
		isTouched = false;
		optionIndex = -1;
		isDrawing = false;
		drawingRadius = -1;
		this.color = new float[4];
		color[0] = color[1] = color[2] = color[3] = 1f;
		
		this.textPaint = new TextPaint();
		
		this.rectScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 0);
		this.rectOption = new RectF(0, 0, 0, 0);
		this.rectCredits = new RectF(screenDetails.metrics.widthPixels / 2 - screenDetails.cellDim, 
				 screenDetails.cellDim + screenDetails.cellDim / 4, 
				 screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim, 
				 screenDetails.cellDim / 4);
		this.backToMenuButton = new RectF(screenDetails.metrics.widthPixels - screenDetails.margin / 2 - screenDetails.cellDim * 0.6666F, 
										  screenDetails.metrics.heightPixels - screenDetails.margin / 2, 
										  screenDetails.metrics.widthPixels - screenDetails.margin / 2, 
										  screenDetails.metrics.heightPixels - screenDetails.margin / 2 - screenDetails.cellDim * 0.6666F);

		this.isCredits = false;
		
		this.option = new boolean[4];
		
		for(int i = 0; i < 4; i++)
			option[i] = false;
		
		this.intentStarted = false;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glViewport(0, 0, screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, screenDetails.metrics.widthPixels, 0, screenDetails.metrics.heightPixels, 1, -1);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);
		utilities.drawTexture(gl, rectCredits, DrawingUtilities.TEXTURE_CREDITSBUTTON, 0.6f);
		
		if(Settings.firstRun) {
			utilities.createBitmap();
			
			font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextAlign(Align.CENTER);
			textPaint.setTextSize(40);
			
			utilities.drawTextToBitmap("Tap on a", screenDetails.cellDim, screenDetails.metrics.heightPixels - point[3].y - (30 / 88.0f) * screenDetails.cellDim, textPaint);
			utilities.drawTextToBitmap("pressure", screenDetails.cellDim, screenDetails.metrics.heightPixels - point[3].y, textPaint);
			utilities.drawTextToBitmap("point...", screenDetails.cellDim, screenDetails.metrics.heightPixels - point[3].y + (30 / 88.0f) * screenDetails.cellDim, textPaint);
		}
			
		
		for(int i = 0; i < 4; i++){
			rectOption.set((float)(point[i].x + Math.pow(-1, i+1) * screenDetails.cellDim * 1.5 - screenDetails.cellDim), 
						point[i].y + screenDetails.cellDim, 
						(float)(point[i].x + Math.pow(-1, i+1) * screenDetails.cellDim * 1.5 + screenDetails.cellDim), 
						point[i].y - screenDetails.cellDim);
			
			utilities.drawTexture(gl, rectOption, DrawingUtilities.TEXTURE_NEWGAME + i, 1);
			
			utilities.drawCircularElement(gl, point[i].x, point[i].y, 2, InscribedPolygon.MODE_FILL, 1, color);
			utilities.drawCircularElement(gl, point[i].x, point[i].y, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		}
		
		for(int i = 0; i < 4; i++){
			color[0] = color[1] = color[2] = color[3] = 1f;
			utilities.drawCircularElement(gl, point[i].x, point[i].y, 2, InscribedPolygon.MODE_FILL, 1, color);
			utilities.drawCircularElement(gl, point[i].x, point[i].y, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		}
		
		mainMenuView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				
				switch(action){
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					PointF tPoint = new PointF(event.getX(), screenDetails.metrics.heightPixels - event.getY());
					
					for(int i = 0; i < 4; i++){
						if(utilities.touchedRegionCheck(tPoint, point[i], (40 / 88.0f) * screenDetails.cellDim)) {
									if(!isTouched && !isDrawing && !isCredits){
										isTouched = true;
										touchedPoint = point[i];
										optionIndex = i;
										break;
									}
						}
						else
							isTouched = false;
					}
					return true;
				case MotionEvent.ACTION_UP:
					if(!isDrawing && isTouched && !isCredits){
						isDrawing = true;
						drawingRadius = 0;
					}
					if(utilities.touchedRegionCheckRect(new PointF(event.getX(), screenDetails.metrics.heightPixels - event.getY()), 
														new PointF(rectCredits.centerX(), rectCredits.centerY()), 
														screenDetails.cellDim, screenDetails.cellDim / 2))
						isCredits = true;
					if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), backToMenuButton) && isCredits)
						isCredits = false;
					
					isTouched = false;
					return true;
				default:
					isTouched = false;
					return false;
				}
			}
		});
		
		if(isTouched && !isDrawing && !intentStarted) {
			color[0] = 0.9f;
			color[1] = 0.05f;
			color[2] = 0.05f;
			color[3] = 1f;
			utilities.drawCircularElement(gl, touchedPoint.x, touchedPoint.y, 2, 
									InscribedPolygon.MODE_FILL, 1, color);
			utilities.drawCircularElement(gl, touchedPoint.x, touchedPoint.y, 6, 
									InscribedPolygon.MODE_BOUNDARY, 1, color);
			color[0] = color[1] = color[2] = color[3] = 1f;
		}
			
		else if(!isTouched && isDrawing && !intentStarted) {
			color[0] = color[1] = color[2] = color[3] = 1f;
			
			utilities.drawCircularElement(gl, touchedPoint.x, touchedPoint.y, 
									drawingRadius, InscribedPolygon.MODE_BOUNDARY, 2, color);
			drawingRadius += 2.0;
		}
		
		if(drawingRadius >= screenDetails.cellDim / 2){
			isDrawing = false;
			drawingRadius = -1;
			if(optionIndex == 0 || optionIndex == 1) {
				if(!Settings.mute)
					soundManager.play(SoundManager.MENU2);
			}
			else if(optionIndex == 2 || optionIndex == 3) {
				if(!Settings.mute)
					soundManager.play(SoundManager.MENU1);
			}
			mainMenuView.startIntent(optionIndex);
		}
		
		if(isCredits) {
			utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_CREDITS, 1);
			utilities.drawTexture(gl, backToMenuButton, DrawingUtilities.TEXTURE_GAMEBACKBUTTON, 1);
		}
		
		if(Settings.firstRun) {
			utilities.drawText(gl);
			utilities.recycleBitmap();
		}
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.1f, 0.1f, 0.1f, 0f);
	}
	
	public void setOptionOff() {
		optionIndex = -1;
		intentStarted = true;
	}
	
	public void setOptionOn() {
		intentStarted = false;
	}

}
