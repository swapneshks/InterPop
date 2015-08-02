package com.inflectionPoint.interpop;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

public class ScoreRenderer implements GLSurfaceView.Renderer{

	private Context context;
	private ScoreActivity scoreActivity;
	private ScoreView scoreView;
	private ScreenDetails screenDetails;
	private DrawingUtilities utilities;
	private Paint textPaint;
	private Typeface font;
	
	private RectF rectScreen;
	private RectF backButton;
	private RectF fwdButton;
	private RectF backToMenuButton;
	
	private float touchedX;
	private float touchedY;
	
	private int gameMode;
	
	public ScoreRenderer(Context context, ScoreView scoreView, ScreenDetails screenDetails) {
		this.context = context;
		this.scoreActivity = (ScoreActivity) context;
		this.scoreView = scoreView;
		this.screenDetails = screenDetails;
		this.utilities = new DrawingUtilities(context, screenDetails, "background_main_512x1024_temp.png");
		this.textPaint = new Paint();
		
		this.rectScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 0);
		this.backButton = new RectF(10, screenDetails.cellDim * 0.6666F + 10, 
									screenDetails.cellDim * 0.6666F + 10, 10);
		this.fwdButton = new RectF(screenDetails.metrics.widthPixels - screenDetails.cellDim * 0.6666F - 10, 
								   screenDetails.cellDim * 0.6666F + 10, 
								   screenDetails.metrics.widthPixels - 10, 10);
		this.backToMenuButton = new RectF((screenDetails.metrics.widthPixels - (backButton.right - backButton.left)) / 2, 
				  					      backButton.top, 
				  					      (screenDetails.metrics.widthPixels + (backButton.right - backButton.left)) / 2, 
				  					      backButton.bottom);


		this.gameMode = GameRenderer.GAME_TIME_1M;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		Settings.firstRun = false;
		Settings.saveSettings(new FileManager(context));
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glViewport(0, 0, screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, screenDetails.metrics.widthPixels, 0, screenDetails.metrics.heightPixels, 1, -1);
	
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		utilities.createBitmap();
		
		utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);
		if(gameMode != GameRenderer.GAME_TIME_1M)
			utilities.drawTexture(gl, backButton, DrawingUtilities.TEXTURE_BACKBUTTON, 1);
		if(gameMode != GameRenderer.GAME_SCORE_POSITIVE)
			utilities.drawTexture(gl, fwdButton, DrawingUtilities.TEXTURE_FWDBUTTON, 1);
		utilities.drawTexture(gl, backToMenuButton, DrawingUtilities.TEXTURE_GAMEBACKBUTTON, 1);
		
		font = Typeface.createFromAsset(scoreActivity.getAssets(), "PWBubbles.ttf");
		
		textPaint.setColor(Color.WHITE);
		textPaint.setTypeface(font);
		textPaint.setTextSize((60 / 88.0f) * screenDetails.cellDim);
		textPaint.setTextAlign(Align.CENTER);
		
		textPaint.setARGB(255, 255, 255, 255);
		utilities.drawTextToBitmap("HIGHSCORES", screenDetails.metrics.widthPixels / 2, 
									screenDetails.cellDim * 0.75F, textPaint);
		
		font = Typeface.createFromAsset(context.getAssets(), "Emotion Engine.ttf");
		textPaint.setTypeface(font);
		textPaint.setTextSize((40 / 88.0f) * screenDetails.cellDim);
		if(gameMode == GameRenderer.GAME_TIME_1M) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("TIME - 1 MINUTE", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + Integer.toString(Settings.highscores1M[i]), 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		else if(gameMode == GameRenderer.GAME_TIME_2M) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("TIME - 2 MINUTES", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + Integer.toString(Settings.highscores2M[i]), 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		else if(gameMode == GameRenderer.GAME_TIME_3M) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("TIME - 3 MINUTES", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + Integer.toString(Settings.highscores3M[i]), 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		else if(gameMode == GameRenderer.GAME_SCORE_500) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("SCORE - 500", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + String.format("%.2f", Settings.highscores500S[i]) + " s", 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		else if(gameMode == GameRenderer.GAME_SCORE_1000) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("SCORE - 1000", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + String.format("%.2f", Settings.highscores1000S[i]) + " s", 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		else if(gameMode == GameRenderer.GAME_SCORE_2000) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("SCORE - 2000", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + String.format("%.2f", Settings.highscores2000S[i]) + " s", 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		if(gameMode == GameRenderer.GAME_SCORE_POSITIVE) {
			for(int i = 0; i < 5; i++) {
				textPaint.setARGB(255, 255, 255, 255);
				utilities.drawTextToBitmap("SCORE - POSITIVE", screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * 1.25F, textPaint);
				textPaint.setARGB(255, 0, 0, 0);
				utilities.drawTextToBitmap(Integer.toString(i + 1) + ". " + String.format("%.2f", Settings.highscoresScorePos[i]) + " s", 
										   screenDetails.metrics.widthPixels / 2, 
										   screenDetails.cellDim * (2*i + 3) / 4 + screenDetails.cellDim, textPaint);
			}
		}
		
		scoreView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				
				touchedX = event.getX();
				touchedY = screenDetails.metrics.heightPixels - event.getY();
				
				switch(action){
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					return true;
				case MotionEvent.ACTION_UP:
					if(utilities.checkTouch(touchedX, touchedY, backButton)) {
						if(gameMode != GameRenderer.GAME_TIME_1M)
							gameMode--;
					}
					else if(utilities.checkTouch(touchedX, touchedY, fwdButton)) {
						if(gameMode != GameRenderer.GAME_SCORE_POSITIVE)
							gameMode++;
					}
					if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), backToMenuButton))
						scoreActivity.finish();
					return true;
				}
				return false;
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

}
