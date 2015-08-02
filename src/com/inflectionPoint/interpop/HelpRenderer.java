package com.inflectionPoint.interpop;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

public class HelpRenderer implements GLSurfaceView.Renderer{

	static final int FADE_IN = 1;
	static final int FADE_OUT = -1;
	
	private Context context;
	private HelpActivity helpActivity;
	private HelpView helpView;
	private ScreenDetails screenDetails;
	private DrawingUtilities utilities;
	private Animator animator;
	private Animator textureAnimator;
	private int animationType;
	private float color[];
	
	private float radius;
	private RectF rectScreen;
	private RectF backButton;
	private RectF fwdButton;
	private RectF backToMenuButton;
	
	private Paint textPaint;
	private Typeface font;
	private boolean isInter;
	private boolean isPop;
	
	private int screenNo;
	private int keyFrame;
	
	private long currentTime;
	private float currentTimeF;
	
	public HelpRenderer(Context context, HelpView helpView, ScreenDetails screenDetails) {
		this.context = context;
		this.helpActivity = (HelpActivity) context;
		this.helpView = helpView;
		this.screenDetails = screenDetails;
		this.utilities = new DrawingUtilities(context, screenDetails, "background_help_512x1024.png");
		this.animator = new Animator();
		this.textureAnimator = new Animator();
		animator.setStartTime(0);
		textureAnimator.setStartTime(0);
		this.animationType = FADE_IN;
		this.radius = 0;
		this.rectScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 0);
		this.backButton = new RectF((10 / 88.0f) * screenDetails.cellDim, screenDetails.cellDim * 0.6666F + (10 / 88.0f) * screenDetails.cellDim, 
									screenDetails.cellDim * 0.6666F + (10 / 88.0f) * screenDetails.cellDim, (10 / 88.0f) * screenDetails.cellDim);
		this.fwdButton = new RectF(screenDetails.metrics.widthPixels - screenDetails.cellDim * 0.6666F - (10 / 88.0f) * screenDetails.cellDim, 
								   screenDetails.cellDim * 0.6666F + (10 / 88.0f) * screenDetails.cellDim, 
								   screenDetails.metrics.widthPixels - (10 / 88.0f) * screenDetails.cellDim, (10 / 88.0f) * screenDetails.cellDim);
		this.backToMenuButton = new RectF(screenDetails.metrics.widthPixels - screenDetails.margin / 2 - (backButton.right - backButton.left), 
										  screenDetails.metrics.heightPixels - screenDetails.margin / 2, 
										  screenDetails.metrics.widthPixels - screenDetails.margin / 2, 
										  screenDetails.metrics.heightPixels - screenDetails.margin / 2 - (backButton.top - backButton.bottom));
		
		this.color = new float[4];		
		color[0] = color[1] = color[2] = 1f;
		color[3] = 0f;
		
		this.textPaint = new Paint();
		this.textPaint.setTextAlign(Align.CENTER);
		this.isInter = false;
		this.isPop = false;
		
		this.screenNo = 1;
		this.keyFrame = 1;
		
		System.nanoTime();
		this.currentTime = 0;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		Settings.firstRun = false;
		Settings.saveSettings(new FileManager(context));
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glViewport(0, 0, screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glOrthof(0, screenDetails.metrics.widthPixels, 0, screenDetails.metrics.heightPixels, 1, -1);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);
		if((screenNo == 1 && keyFrame > 1) || screenNo > 1)
			utilities.drawTexture(gl, backButton, DrawingUtilities.TEXTURE_BACKBUTTON, 1);
		if(screenNo < 8)
			utilities.drawTexture(gl, fwdButton, DrawingUtilities.TEXTURE_FWDBUTTON, 1);
		utilities.drawTexture(gl, backToMenuButton, DrawingUtilities.TEXTURE_GAMEBACKBUTTON, 1);
		utilities.createBitmap();
		
		font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
		textPaint.setTypeface(font);
		
		textPaint.setARGB((int) (color[3] * 255), (int) (color[0] * 255), 
							(int) (color[1] * 255), (int) (color[2] * 255));
		
		switch(screenNo) {
		case 1:
			switch(keyFrame){
			case 1:
				animate(gl, "Welcome to", screenDetails.metrics.widthPixels / 2, 
						screenDetails.cellDim, screenDetails.cellDim / 2, 4000);
				
				this.isInter = true;
				animate(gl, "INTER", (float) (screenDetails.metrics.widthPixels / 3), 
						screenDetails.cellDim * 2.5f, screenDetails.cellDim / 2, 4000);
				
				this.isInter = false;
				this.isPop = true;
				animate(gl, "POP", (float) (screenDetails.metrics.widthPixels * 3.0 / 4), 
						screenDetails.cellDim * 2.5f, screenDetails.cellDim / 2, 4000);
				this.isPop = false;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
				}
				
				break;
			case 2:
				animate(gl, "And this is the", screenDetails.metrics.widthPixels / 2, 
						screenDetails.cellDim * 7/2, 40, 4000);
				animate(gl, "world of bubbles...", screenDetails.metrics.widthPixels / 2,
						screenDetails.cellDim * 4, 40, 4000);
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
				}
				
				break;
			case 3:
				animate(gl, "Your goal is to", screenDetails.metrics.widthPixels / 2, 
						screenDetails.cellDim, 40, 4000);
				animate(gl, "pop bubbles and", screenDetails.metrics.widthPixels / 2, 
						screenDetails.cellDim * 3/2, 40, 4000);
				animate(gl, "set new highscore!", screenDetails.metrics.widthPixels / 2, 
						screenDetails.cellDim * 2, 40, 4000);
				
				if(color[3] == 0) {
					screenNo ++;
					keyFrame = 1;
					animator.setStartTime(0);
				}
				break;
			}  
			break;
		case 2:
			switch(keyFrame){
			case 1:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++)
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
								GameRenderer.UNTOUCHED_POINT, 0);
				
				animate(gl, "The points that you", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "see above are", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "pressure points...", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
				}
				
				break;
			case 2:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Touch one of them", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "and release to", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "create a wave.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 3:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Use them to", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "pop white and", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "black bubbles.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN) {
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 2.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_NORMAL, 1);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_BLACK, 1);
					textureAnimator.setStartTime(System.nanoTime());
				}
				else if(animationType == FADE_OUT) {
					radius = 0;
					float textAlpha = textureAnimator.fadeOut(currentTime, 4000);
					utilities.drawBubble(gl, 2.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
				}
					
				if(color[3] == 0) {
					keyFrame++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 4:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Normal bubble gives", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "10 and black bubble", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "gives -15 points.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				animate(gl, "10", 2.5F * screenDetails.cellDim + screenDetails.margin / 2, 
						screenDetails.metrics.heightPixels - (screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2), 
						40, 4000);
				animate(gl, "-15", 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
						screenDetails.metrics.heightPixels - (screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2), 
						40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN) {
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 2.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_NORMAL, 1);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_BLACK, 1);
					textureAnimator.setStartTime(System.nanoTime());
				}
				else if(animationType == FADE_OUT) {
					radius = 0;
					float textAlpha = textureAnimator.fadeOut(currentTime, 4000);
					utilities.drawBubble(gl, 2.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
				}
					
				if(color[3] == 0) {
					screenNo ++;
					keyFrame = 1;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			} 
			break;
		case 3:
			switch(keyFrame) {
			case 1:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "You will level", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "up after every", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "150 points.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 2:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
						   (i == 4 && j == 6) || (i == 4 && j == 5) || (i == 4 && j == 4) ||
						   (i == 3 && j == 4) || (i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Points in orange", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "are blocked points.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "New waves on these", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 3:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
						   (i == 4 && j == 6) || (i == 4 && j == 5) || (i == 4 && j == 4) ||
						   (i == 3 && j == 4) || (i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "points cannot be", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "created until the", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "wave finishes.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					screenNo++;
					keyFrame = 1;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			}
			break;
		case 4:
			switch(keyFrame) {
			case 1:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Waves are of 2 types.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "Up wave(red) and", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "down wave(black).", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
				}
				break;
			case 2:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Touch, drag and", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "release to select", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "adjacent points.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
				}
				break;
			case 3:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Adjacent points will", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "form waves of", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "opposite type.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
				}
				break;
			case 4:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Waves of opposite type", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "will interfere.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					screenNo++;
					keyFrame = 1;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			}
			break;
		case 5:
			switch(keyFrame) {
			case 1:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "These interference", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "waves interact with", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "bubbles differently.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN)
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				else if(animationType == FADE_OUT)
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				
				if(color[3] == 0) {
					keyFrame++;
					animator.setStartTime(0);
				}
				break;
			case 2:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Bubble pops from", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "interference waves are", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "called InterPop.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
			
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN) {
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 4.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_NORMAL, 1);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_BLACK, 1);
					textureAnimator.setStartTime(System.nanoTime());
				}
				else if(animationType == FADE_OUT) {
					radius = 0;
					float textAlpha = textureAnimator.fadeOut(currentTime, 4000);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 4.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
				}
					
				if(color[3] == 0) {
					keyFrame++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 3:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Normal InterPop gives", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "20 and Black InterPop", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "gives 0 points.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				animate(gl, "20", 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
						screenDetails.metrics.heightPixels - (screenDetails.extraHeight + 4.5F * screenDetails.cellDim + screenDetails.margin / 2), 
						40, 4000);
				animate(gl, "0", 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
						screenDetails.metrics.heightPixels - (screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2), 
						40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN) {
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 4.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_NORMAL, 1);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_BLACK, 1);
					textureAnimator.setStartTime(System.nanoTime());
				}
				else if(animationType == FADE_OUT) {
					radius = 0;
					float textAlpha = textureAnimator.fadeOut(currentTime, 4000);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 4.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
				}
					
				if(color[3] == 0) {
					keyFrame++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 4:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Use InterPop to", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "avoid black bubbles.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN) {
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_BLACK, 1);
					textureAnimator.setStartTime(System.nanoTime());
				}
				else if(animationType == FADE_OUT) {
					radius = 0;
					float textAlpha = textureAnimator.fadeOut(currentTime, 4000);
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 6, Bubble.BUBBLE_DROP, textAlpha);
				}
					
				if(color[3] == 0) {
					screenNo ++;
					keyFrame = 1;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			} 
			break;
		case 6:
			switch(keyFrame) {
			case 1:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "In the game,", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "boundary points", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "will be disabled.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 2:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "As you level up,", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "you will unlock orbs.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "For example, one of", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
			
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 3:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "the orbs is 'Enable", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "Boundary Orb'. Collect", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "it to enable", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
			
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
					animationType = FADE_IN; 
				}
				break;
			case 4:
				if(animationType == FADE_IN) {
					for(int i = 1; i < screenDetails.wCells; i++)
						for(int j = 2; j < screenDetails.hCells; j++) {
							if(i == 3 && j == 5)
								utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
										GameRenderer.UNTOUCHED_POINT, radius);
							else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
									   (i == 4 && j == 6) || (i == 4 && j == 5) || (i == 4 && j == 4) ||
									   (i == 3 && j == 4) || (i == 2 && j == 4))
										utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
												GameRenderer.UNTOUCHED_POINT, 0);
							else
								utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
										GameRenderer.UNTOUCHED_POINT, 0);		
						}
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 3, Bubble.POWERUP_BOUNDARY, 1);
				}
				else if(animationType == FADE_OUT) {
					for(int i = 0; i <= screenDetails.wCells; i++)
						for(int j = 1; j <= screenDetails.hCells; j++) {
							if(i == 3 && j == 5)
								utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
										GameRenderer.UNTOUCHED_POINT, radius);
							else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
									   (i == 4 && j == 6) || (i == 4 && j == 5) || (i == 4 && j == 4) ||
									   (i == 3 && j == 4) || (i == 2 && j == 4))
										utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
												GameRenderer.UNTOUCHED_POINT, 0);
							else
								utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
										GameRenderer.UNTOUCHED_POINT, 0);		
						}
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				}
				
				animate(gl, "boundary points", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "for 5 seconds. Some", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "orbs can be avoided", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
			
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 5:
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(i == 3 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_UP, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if(i == 4 && j == 5)
							utilities.drawGridPoint(gl, i, j, GameRenderer.DRAWING_POINT_WAVE_DOWN, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else if((i == 2 && j == 5) || (i == 2 && j == 6) || (i == 3 && j == 6) ||
								(i == 4 && j == 6) || (i == 5 && j == 6) || (i == 5 && j == 5) ||
								(i == 5 && j == 4) || (i == 4 && j == 4) || (i == 3 && j == 4) ||
								(i == 2 && j == 4))
							utilities.drawGridPoint(gl, i, j, GameRenderer.BLOCKED_POINT, 
									GameRenderer.UNTOUCHED_POINT, radius);
						else
							utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "using InterPop. For", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "example the 'Power down", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "black score' orb. As", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				if(animationType == FADE_IN) {
					radius = ((currentTimeF - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
					utilities.drawBubble(gl, 3.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.extraHeight + 5.5F * screenDetails.cellDim + screenDetails.margin / 2, 
							 screenDetails.cellDim / 3, Bubble.POWERDOWN_BLACK_SCORE, 1);
				}
				else if(animationType == FADE_OUT) {
					radius = ((currentTimeF - 4000 * 1000000l - animator.getStartTime()) / 4000 / 1000000f) * screenDetails.cellDim;
				}
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
			
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 6:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "you progress, you", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "will know more about", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "the different orbs. ", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
			
				if(color[3] == 0) {
					screenNo ++;
					keyFrame = 1;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			}
			break;
		case 7:
			switch(keyFrame) {
			case 1:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				animate(gl, "Some display", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "details...", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;		
			case 2:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("36.84s", 
						                   screenDetails.metrics.widthPixels / 2, 
						                   screenDetails.cellDim / 2, 
						                   textPaint);
				
				textPaint.setARGB((int) (color[3] * 255), 255, 255, 255);
				animate(gl, "First row shows", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "the game time.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;	
			case 3:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("N:" + 10 + "  NI:" + 20 + "  B:" + -15 + "  BI:" + 0, 
		                                   screenDetails.metrics.widthPixels / 2, 
		                                   screenDetails.cellDim * 1.5F + screenDetails.margin / 2, 
		                                   textPaint);
				
				textPaint.setARGB((int) (color[3] * 255), 255, 255, 255);
				animate(gl, "Second row shows points", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "awarded for different", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "pops. N for Normal pop,", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 4:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("N:" + 10 + "  NI:" + 20 + "  B:" + -15 + "  BI:" + 0, 
		                                   screenDetails.metrics.widthPixels / 2, 
		                                   screenDetails.cellDim * 1.5F + screenDetails.margin / 2, 
		                                   textPaint);
				
				textPaint.setARGB((int) (color[3] * 255), 255, 255, 255);
				animate(gl, "NI for Normal InterPop,", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "B for Black Pop and", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "BI for Black InterPop.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 5:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("Level:7", 
		   				                   screenDetails.metrics.widthPixels / 2, 
		   				                   screenDetails.cellDim * 2.5F + screenDetails.margin / 2, 
		   				                   textPaint);
				
				textPaint.setARGB((int) (color[3] * 255), 255, 255, 255);
				animate(gl, "Third row shows", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "the level.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 6:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("Boundary Points disable in: 3.86s", 
		   				                   screenDetails.metrics.widthPixels / 2, 
		   				                   screenDetails.cellDim * 3.5F + screenDetails.margin / 2, 
		   				                   textPaint);
				
				textPaint.setARGB((int) (color[3] * 255), 255, 255, 255);
				animate(gl, "Fourth row shows time", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "remaining before", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "boundary points", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					keyFrame ++;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			case 7:
				for(int i = 1; i < screenDetails.wCells; i++)
					for(int j = 2; j < screenDetails.hCells; j++) {
						utilities.drawGridPoint(gl, i, j, GameRenderer.UNTOUCHED_POINT, 
									GameRenderer.UNTOUCHED_POINT, 0);	
					}
				
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("Boundary Points disable in: 3.86s", 
		                   				   screenDetails.metrics.widthPixels / 2, 
		                   				   screenDetails.cellDim * 3.5F + screenDetails.margin / 2, 
		                   				   textPaint);

				textPaint.setARGB((int) (color[3] * 255), 255, 255, 255);
				animate(gl, "disable, when 'Enable", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim, 40, 4000);
				animate(gl, "Boundary Points' power", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim * 5/8, 40, 4000);
				animate(gl, "is active.", screenDetails.metrics.widthPixels / 2, 
						screenDetails.metrics.heightPixels - screenDetails.cellDim / 4, 40, 4000);
				
				currentTime = System.nanoTime();
				currentTimeF = currentTime;
				
				if(color[3] == 0) {
					screenNo++;
					keyFrame = 1;
					animator.setStartTime(0);
					textureAnimator.setStartTime(0);
					textureAnimator.setAlpha(1);
				}
				break;
			}
			break;
		case 8:
			utilities.drawTexture(gl, fwdButton, DrawingUtilities.TEXTURE_RESTARTBUTTON, 1);
			break;
		}
		
		helpView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				
				switch(action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					return true;
				case MotionEvent.ACTION_UP:
					if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), fwdButton)) {
						keyFrame ++;
						if((screenNo == 1 && keyFrame > 3) ||
						   (screenNo == 2 && keyFrame > 4) ||
						   (screenNo == 3 && keyFrame > 3) ||
						   (screenNo == 4 && keyFrame > 4) ||
						   (screenNo == 5 && keyFrame > 4) ||
						   (screenNo == 6 && keyFrame > 6) ||
						   (screenNo == 7 && keyFrame > 7)) {
							screenNo ++;
							keyFrame = 1;
						}
						if(screenNo == 8) {
							screenNo = 1;
							keyFrame = 1;
						}
						
						animator.setStartTime(0);
						animator.setAlpha(0);
						color[3] = 0;
						radius = 0;
					}
					if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), backButton)) {
						if((screenNo == 1 && keyFrame > 1) || screenNo > 1) {
							keyFrame --;
						}
						if(keyFrame < 1 && screenNo > 1) {
							screenNo --;
							if(screenNo == 1)
								keyFrame = 3;
							else if(screenNo == 2)
								keyFrame = 4;
							else if(screenNo == 3)
								keyFrame = 3;
							else if(screenNo == 4)
								keyFrame = 4;
							else if(screenNo == 5)
								keyFrame = 4;
							else if(screenNo == 6)
								keyFrame = 6;
							else if(screenNo == 7)
								keyFrame = 7;
						}
						if(screenNo < 1) {
							screenNo = 1;
							keyFrame = 1;
						}
						animator.setStartTime(0);
						animator.setAlpha(0);
						color[3] = 0;
						radius = 0;
					}
					if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), backToMenuButton))
						helpActivity.finish();
					return true;
				default:
					return false;
				}
			}
		});
		
		utilities.drawText(gl);
		
		utilities.recycleBitmap();
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.2f, 0.2f, 0.7f, 0f);
	}
	
	public void animate(GL10 gl, String string, float x, float y, float textSize, int animationTime) {
		
		if(animator.getStartTime() == 0){
			color[3] = 0.0000001f;
			animator.setStartTime(System.nanoTime());
			animator.setAlpha(0);
			animationType = FADE_IN;
		}
		currentTime = System.nanoTime();
		
		if(currentTime - animator.getStartTime() < (animationTime - 2000) * 1000000L / 2) {
			color[3] = animator.fadeIn(currentTime, (animationTime - 2000) / 2);
		}
		else if((currentTime - animator.getStartTime() > (animationTime - 2000) * 1000000L / 2) &&
				(currentTime - animator.getStartTime() < (animationTime - 2000) * 1000000L))
			color[3] = 1;
		else {
			color[3] = animator.fadeOut(currentTime - (animationTime - 2000) * 1000000l, (animationTime - 2000) / 2);
		}
		
		textPaint.setTextSize((textSize / 88.0f) * screenDetails.cellDim);
		
		if(isInter) {
			font = Typeface.createFromAsset(context.getAssets(), "The Perfect Wave.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextAlign(Align.CENTER);
			textPaint.setTextSize((120 / 88.0f) * screenDetails.cellDim);
		}
		
		if(isPop) {
			font = Typeface.createFromAsset(context.getAssets(), "Definitely Maybe.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextAlign(Align.CENTER);
			textPaint.setTextSize((100 / 88.0f) * screenDetails.cellDim);
		}

		utilities.drawTextToBitmap(string, x, y, textPaint);
		
	}

}
