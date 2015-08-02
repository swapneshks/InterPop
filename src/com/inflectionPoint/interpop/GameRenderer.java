package com.inflectionPoint.interpop;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameRenderer implements GLSurfaceView.Renderer {

	static final int GAME_TIME_1M = 2300;
	static final int GAME_TIME_2M = 2301;
	static final int GAME_TIME_3M = 2302;
	static final int GAME_SCORE_500 = 2303;
	static final int GAME_SCORE_1000 = 2304;
	static final int GAME_SCORE_2000 = 2305;
	static final int GAME_SCORE_POSITIVE = 2306;
	static final int GAME_NO_ORBS = 2307;
	static final int GAME_NO_LIMITS = 2308;
	
	static final int UNTOUCHED_POINT = 0;
	static final int TOUCHED_POINT_WAVE_UP = 1;
	static final int TOUCHED_POINT_WAVE_DOWN = -1;
	static final int DRAWING_POINT_WAVE_UP = 2;
	static final int DRAWING_POINT_WAVE_DOWN = -2;
	static final int BOUNDARY_POINT_ON = 3;
	static final int BOUNDARY_POINT_OFF = -3;
	static final int BLOCKED_POINT = 4;
	
	static final int SCORE_NONE = 10000;
	static final int SCORE_NORMAL_NORMAL = 10001;
	static final int SCORE_NORMAL_INTERPOP = 10002;
	static final int SCORE_BLACK_NORMAL = 10003;
	static final int SCORE_BLACK_INTERPOP = 10004;
	
	static final int POINT_UP = 50001;
	static final int POINT_DOWN = 50002;
	static final int POINT_LEFT = 50003;
	static final int POINT_RIGHT = 50004;
	
	private Context context;
	private GameActivity gameActivity;
	private GameView gameView;
	private ScreenDetails screenDetails;
	private DrawingUtilities utilities;
	private SoundManager soundManager;
	
	private int gameMode;
	private boolean isPaused;
	private boolean isRestartPressed;
	private boolean isBackPressed;
	private boolean isGameOver;
	private boolean isHighScoreSet;
	
	private RectF rectGame1M;
	private RectF rectGame2M;
	private RectF rectGame5M;
	private RectF rectGame1000S;
	private RectF rectGame2000S;
	private RectF rectGame5000S;
	private RectF rectGameNoOrbs;
	private RectF rectGameNoLimits;
	private RectF rectGameScorePos;
	
	private int gridPointType[][];
	private int originalPointType[][];
	private boolean boundaryPoints;
	private int latestIndexX;
	private int latestIndexY;
	private int latestPointType;
	private float radiusAtPoint[][];
	private float rate;
	private float color[];
	
	private RectF rectFullScreen;
	private RectF rectGameScreen;
	private RectF rectBottom;
	private RectF headsDownDisplay;
	private RectF playPause;
	private RectF restart;
	private RectF back;
	private RectF rectRestartBack;
	private RectF restartBackYes;
	private RectF restartBackNo;
	private RectF orbUnlockedBack;
	private RectF orbUnlockedFwd;
	private RectF orbTickCross;
	private RectF rectViewOrb;
	private RectF viewOrbTickCross;
	private Rect textBounds;
	
	private int score;
	private int scoreType[][];
	private int scoreNormalNormal;
	private int scoreBlackNormal;
	private int scoreNormalInterpop;
	private int scoreBlackInterpop;
	private int bubbleType[][];
	private float bubbleFade[][];
	private Random random;
	private int bubbleRandomX;
	private int bubbleRandomY;
	
	private float touchedX;
	private float touchedY;
	
	private int difficultyLevel;
	
	private int orbUnlocked;
	private float orbUnlockedX;
	private float orbUnlockedY;
	private float orbUnlockedRadius;
	
	private boolean[] orbInterPop; 
	private String orbLine1[];
	private String orbLine2[];
	
	private float orbLine1Y;
	private float orbLine2Y;
	private float orbLine3Y;
	
	private boolean isUnlocked;
	private boolean isPowerUpNormal;
	private boolean isPowerUpBlack;
	private boolean isPowerDownNormal;
	private boolean isPowerDownBlack;
	private boolean isSuperpopBlack;
	private boolean isSuperpopBlackNegative;
	private boolean isInvertBubble;
	private boolean isConvertNormal;
	private boolean isConvertBlack;

	private float probOPUBoundary;
	private float probOPUNormal;
	private float probOPUBlack;
	private float probOPDNormal;
	private float probOPDBlack;
	
	private float probIndividual[];
	private float probCumulative[];
	
	private int countOrbs;
	
	private Paint textPaint;
	private Animator animator;
	private float textAlpha;
	private Typeface font;
	
	private int keyFrame;
	
	private long startingTime;
	private long elapsedTime;
	private long keyFrameTime;
	private long totalGameTime;
	private long powerUpBoundaryTime;
	private long powerUpDownNormalTime;
	private long powerUpDownBlackTime;
	
	private long powerUpBoundaryTimeC;   //C - Compare
	private long powerUpDownNormalTimeC;
	private long powerUpDownBlackTimeC;
	
	
	public GameRenderer(Context context, GameView gameView, ScreenDetails screenDetails) {
		this.context = context;
		this.gameActivity = (GameActivity) context;
		this.gameView = gameView;
		this.screenDetails = screenDetails;
		this.utilities = new DrawingUtilities(context, screenDetails, "background_game_512x1024.png");
		this.soundManager = new SoundManager(context, 20);
		
		this.gameMode = GAME_NO_LIMITS;
		this.isPaused = false;
		this.isRestartPressed = false;
		this.isGameOver = false;
		this.isHighScoreSet = false;
		
		this.rectGame1M = new RectF(44.0F / 1080 * screenDetails.metrics.widthPixels, 
									1532.0F / 1920 * screenDetails.metrics.heightPixels, 
									240.0F / 1080 * screenDetails.metrics.widthPixels, 
									1240.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGame2M = new RectF(392.0F / 1080 * screenDetails.metrics.widthPixels, 
									1532.0F / 1920 * screenDetails.metrics.heightPixels, 
									690.0F / 1080 * screenDetails.metrics.widthPixels, 
									1240.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGame5M = new RectF(736.0F / 1080 * screenDetails.metrics.widthPixels, 
									1532.0F / 1920 * screenDetails.metrics.heightPixels, 
									1032.0F / 1080 * screenDetails.metrics.widthPixels, 
									1240.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGame1000S = new RectF(44.0F / 1080 * screenDetails.metrics.widthPixels, 
									   1016.0F / 1920 * screenDetails.metrics.heightPixels, 
									   240.0F / 1080 * screenDetails.metrics.widthPixels, 
									   720.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGame2000S = new RectF(392.0F / 1080 * screenDetails.metrics.widthPixels, 
									   1016.0F / 1920 * screenDetails.metrics.heightPixels, 
									   690.0F / 1080 * screenDetails.metrics.widthPixels, 
									   720.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGame5000S = new RectF(736.0F / 1080 * screenDetails.metrics.widthPixels, 
									   1016.0F / 1920 * screenDetails.metrics.heightPixels, 
									   1032.0F / 1080 * screenDetails.metrics.widthPixels, 
									   720.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGameNoOrbs = new RectF(44.0F / 1080 * screenDetails.metrics.widthPixels, 
				   					    492.0F / 1920 * screenDetails.metrics.heightPixels, 
				   					    240.0F / 1080 * screenDetails.metrics.widthPixels, 
				   					    196.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGameNoLimits = new RectF(392.0F / 1080 * screenDetails.metrics.widthPixels, 
				   					      492.0F / 1920 * screenDetails.metrics.heightPixels, 
				   					      690.0F / 1080 * screenDetails.metrics.widthPixels, 
				   					      196.0F / 1920 * screenDetails.metrics.heightPixels);
		this.rectGameScorePos = new RectF(736.0F / 1080 * screenDetails.metrics.widthPixels, 
				   					   	  492.0F / 1920 * screenDetails.metrics.heightPixels, 
				   					   	  1032.0F / 1080 * screenDetails.metrics.widthPixels, 
				   					   	  196.0F / 1920 * screenDetails.metrics.heightPixels);

		
		this.gridPointType = new int[(int) screenDetails.wCells + 1][(int) screenDetails.hCells + 1];
		this.originalPointType = new int[(int) screenDetails.wCells + 1][(int) screenDetails.hCells + 1];

		for(int i = 0; i <= screenDetails.wCells; i++)
			for(int j = 1; j <= screenDetails.hCells; j++) {
				gridPointType[i][j] = UNTOUCHED_POINT;
				originalPointType[i][j] = UNTOUCHED_POINT;
			}
					
		this.boundaryPoints = false;
		
		if(boundaryPoints) {
			for(int i = 0; i <= screenDetails.wCells; i++) {
				gridPointType[i][1] = gridPointType[i][(int) screenDetails.hCells] = BOUNDARY_POINT_ON;
				originalPointType[0][1] = originalPointType[i][(int) screenDetails.hCells] = BOUNDARY_POINT_ON;
			}
			for(int j = 1; j <= screenDetails.hCells; j++) {
				gridPointType[0][j] = gridPointType[(int) screenDetails.wCells][j] = BOUNDARY_POINT_ON;
				originalPointType[0][j] = originalPointType[(int) screenDetails.wCells][j] = BOUNDARY_POINT_ON;
			}
		}
		else {
			for(int i = 0; i <= screenDetails.wCells; i++) {
				gridPointType[i][1] = gridPointType[i][(int) screenDetails.hCells] = BOUNDARY_POINT_OFF;		
				originalPointType[i][1] = originalPointType[i][(int) screenDetails.hCells] = BOUNDARY_POINT_OFF;	
			}
			for(int j = 1; j <= screenDetails.hCells; j++) {
				gridPointType[0][j] = gridPointType[(int) screenDetails.wCells][j] = BOUNDARY_POINT_OFF;				
				originalPointType[0][j] = originalPointType[(int) screenDetails.wCells][j] = BOUNDARY_POINT_OFF;	
			}
		}
		
		this.latestIndexX = -100;
		this.latestIndexY = -100;
		this.latestPointType = TOUCHED_POINT_WAVE_UP;
		this.radiusAtPoint = new float[(int) screenDetails.wCells + 1][(int) screenDetails.hCells + 1];
		this.color = new float[4];
		color[0] = color[1] = color[2] = color[3] = 1f;
		this.rectFullScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 0);
		this.rectGameScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 
									screenDetails.cellDim + screenDetails.extraHeight);
		this.rectBottom = new RectF(screenDetails.cellDim * (screenDetails.wCells - 2) + screenDetails.margin * 0.75F, 
				                    screenDetails.cellDim + screenDetails.extraHeight, 
				                    screenDetails.metrics.widthPixels - screenDetails.margin * 0.25F, 0);
		this.headsDownDisplay = new RectF(0, screenDetails.cellDim + screenDetails.extraHeight, 
										  screenDetails.metrics.widthPixels, 0);
		this.playPause = new RectF(screenDetails.margin * 0.75F, 
								   (screenDetails.cellDim + screenDetails.extraHeight) / 2 + (screenDetails.cellDim - screenDetails.margin / 2) / 2, 
								   screenDetails.cellDim + screenDetails.margin * 0.25F, 
								   (screenDetails.cellDim + screenDetails.extraHeight) / 2 - (screenDetails.cellDim - screenDetails.margin / 2) / 2);
		this.restart = new RectF(screenDetails.cellDim + screenDetails.margin * 0.75F, 
								 (screenDetails.cellDim + screenDetails.extraHeight) / 2 + (screenDetails.cellDim - screenDetails.margin / 2) / 2, 
								 screenDetails.cellDim * 2 + screenDetails.margin * 0.25F, 
								 (screenDetails.cellDim + screenDetails.extraHeight) / 2 - (screenDetails.cellDim - screenDetails.margin / 2) / 2);
		this.back = new RectF(screenDetails.cellDim * 2 + screenDetails.margin * 0.75F, 
				 				 (screenDetails.cellDim + screenDetails.extraHeight) / 2 + (screenDetails.cellDim - screenDetails.margin / 2) / 2, 
				 				 screenDetails.cellDim * 3 + screenDetails.margin * 0.25F, 
				 				 (screenDetails.cellDim + screenDetails.extraHeight) / 2 - (screenDetails.cellDim - screenDetails.margin / 2) / 2);
		this.rectRestartBack = new RectF(screenDetails.margin,
										 ((rectGameScreen.bottom + rectGameScreen.top) / 2) + (screenDetails.metrics.widthPixels / 2 - screenDetails.margin),
										 screenDetails.metrics.widthPixels - screenDetails.margin,
										 ((rectGameScreen.bottom + rectGameScreen.top) / 2) - (screenDetails.metrics.widthPixels / 2 - screenDetails.margin));
		this.restartBackYes = new RectF(rectRestartBack.left + (30.0F / 512 * (rectRestartBack.right - rectRestartBack.left)), 
										rectRestartBack.bottom + (312.0F / 512 * (rectRestartBack.top - rectRestartBack.bottom)), 
										rectRestartBack.left + (210.0F / 512 * (rectRestartBack.right - rectRestartBack.left)), 
										rectRestartBack.bottom + 142.0F / 512 * (rectRestartBack.top - rectRestartBack.bottom));
		this.restartBackNo = new RectF( rectRestartBack.left + (300.0F / 512 * (rectRestartBack.right - rectRestartBack.left)), 
										rectRestartBack.bottom + 312.0F / 512 * (rectRestartBack.top - rectRestartBack.bottom), 
										rectRestartBack.left + (480.0F / 512 * (rectRestartBack.right - rectRestartBack.left)), 
										rectRestartBack.bottom + 142.0F / 512 * (rectRestartBack.top - rectRestartBack.bottom));
		this.orbUnlockedBack = new RectF(10, 
										 screenDetails.cellDim * 1.6666F + 10  + screenDetails.extraHeight, 
										 screenDetails.cellDim * 0.6666F + 10, 
										 10 + screenDetails.extraHeight + screenDetails.cellDim);
		this.orbUnlockedFwd = new RectF(screenDetails.metrics.widthPixels - screenDetails.cellDim * 0.6666F - 10, 
								   		screenDetails.cellDim * 1.6666F + 10 + screenDetails.extraHeight, 
								   		screenDetails.metrics.widthPixels - 10, 
								   		10 + screenDetails.extraHeight + screenDetails.cellDim);
		this.orbTickCross = new RectF((930.0f / 1024) * (rectRestartBack.right - rectRestartBack.left) + rectRestartBack.left - screenDetails.cellDim / 6, 
									  (200.0f / 1024) * (rectRestartBack.top - rectRestartBack.bottom) + rectRestartBack.bottom + screenDetails.cellDim / 6, 
									  (930.0f / 1024) * (rectRestartBack.right - rectRestartBack.left) + rectRestartBack.left + screenDetails.cellDim / 6, 
									  (200.0f / 1024) * (rectRestartBack.top - rectRestartBack.bottom) + rectRestartBack.bottom - screenDetails.cellDim / 6);
		this.rectViewOrb = new RectF(screenDetails.metrics.widthPixels / 2 - screenDetails.cellDim, 
								     screenDetails.extraHeight + screenDetails.margin / 2 + screenDetails.cellDim * 3, 
								     screenDetails.metrics.widthPixels / 2 + screenDetails.cellDim, 
								     screenDetails.extraHeight + screenDetails.margin / 2 + screenDetails.cellDim);
		this.viewOrbTickCross = new RectF((472.0f / 512) * screenDetails.metrics.widthPixels - screenDetails.cellDim / 6, 
				  					      (310.0f / 1024) * (rectGameScreen.top - rectGameScreen.bottom) + rectGameScreen.bottom + screenDetails.cellDim / 6, 
				  					      (472.0f / 512) * screenDetails.metrics.widthPixels + screenDetails.cellDim / 6, 
				  					      (310.0f / 1024) * (rectGameScreen.top - rectGameScreen.bottom) + rectGameScreen.bottom - screenDetails.cellDim / 6);
		this.textBounds = new Rect(0, 0, 0, 0);
		
		this.score = 0;
		this.scoreType = new int[(int) screenDetails.wCells][(int) screenDetails.hCells];
		this.scoreNormalNormal = 10;
		this.scoreBlackNormal = -15;
		this.scoreNormalInterpop = 20;
		this.scoreBlackInterpop = 0;
		this.bubbleType = new int[(int) screenDetails.wCells][(int) screenDetails.hCells];
		this.bubbleFade = new float[(int) screenDetails.wCells][(int) screenDetails.hCells];
		this.random = new Random();
		this.bubbleRandomX = this.bubbleRandomY = -100;
		
		this.difficultyLevel = 1;
		
		for(int i = 0; i <= screenDetails.wCells; i++)
			for(int j = 0; j <= screenDetails.hCells; j++) 
				radiusAtPoint[i][j] = -100;
		
		this.rate = 1.5f;
		
		for(int i = 0; i < screenDetails.wCells; i++)
			for(int j = 0; j < screenDetails.hCells; j++) {
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
				bubbleFade[i][j] = 1.0f;
				scoreType[i][j] = SCORE_NONE;
			}
		
		this.orbUnlocked = Bubble.BUBBLE_NONE;
		this.orbUnlockedX = screenDetails.metrics.widthPixels / 2;
		this.orbUnlockedY = rectRestartBack.top - screenDetails.cellDim * 1.25f;
		this.orbUnlockedRadius = screenDetails.cellDim / 2;
		
		this.orbInterPop = new boolean[11];
		this.orbLine1 = new String[11];
		this.orbLine2 = new String[11];
		
		this.orbInterPop[0] = false;
		this.orbLine1[0] = "Activate Boundary Points";
		this.orbLine2[0] = "for 5 seconds.";
		
		this.orbInterPop[1] = false;
		this.orbLine1[1] = "N : 20, NI : 30";
		this.orbLine2[1] = "for 5 seconds.";
		
		this.orbInterPop[2] = false;
		this.orbLine1[2] = "B : -5, BI : 10";
		this.orbLine2[2] = "for 5 seconds.";
		
		this.orbInterPop[3] = true;
		this.orbLine1[3] = "N : 0, NI : 10";
		this.orbLine2[3] = "for 5 seconds.";
		
		this.orbInterPop[4] = true;
		this.orbLine1[4] = "B : -20, BI : -10";
		this.orbLine2[4] = "for 5 seconds.";
		
		this.orbInterPop[5] = false;
		this.orbLine1[5] = "InterPop all";
		this.orbLine2[5] = "normal bubbles.";
		
		this.orbInterPop[6] = false;
		this.orbLine1[6] = "InterPop all";
		this.orbLine2[6] = "black bubbles.";
		
		this.orbInterPop[7] = true;
		this.orbLine1[7] = "Pop all black bubbles";
		this.orbLine2[7] = "(Negative score!!)";
		
		this.orbInterPop[8] = false;
		this.orbLine1[8] = "Invert all bubbles.";
		this.orbLine2[8] = "";
		
		this.orbInterPop[9] = false;
		this.orbLine1[9] = "Convert all black";
		this.orbLine2[9] = "bubbles to normal.";
		
		this.orbInterPop[10] = true;
		this.orbLine1[10] = "Convert all normal";
		this.orbLine2[10] = "bubbles to black.";	
		
		this.orbLine1Y = screenDetails.metrics.heightPixels - rectRestartBack.top - rectRestartBack.height() / 2;
		this.orbLine2Y = screenDetails.metrics.heightPixels - rectRestartBack.top - rectRestartBack.height() / 2 + screenDetails.cellDim / 2;
		this.orbLine3Y = orbLine2Y + screenDetails.cellDim / 2;
		
		this.isUnlocked = false;
		this.isPowerUpNormal = false;
		this.isPowerUpBlack = false;
		this.isPowerDownNormal = false;
		this.isPowerDownBlack = false;
		this.isSuperpopBlack = false;
		this.isSuperpopBlackNegative = false;
		this.isInvertBubble = false;
		this.isConvertNormal = false;
		this.isConvertBlack = false;
		
		this.probOPUBoundary = 0f;
		this.probOPUNormal = 0f;
		this.probOPUBlack = 0f;
		this.probOPDNormal = 0f;
		this.probOPDBlack = 0f;
		
		this.probIndividual = new float[13];
	
		setProb();
		
		this.probCumulative = new float[13];
	
		normalizeProb();
		
		this.countOrbs = 0;
		
		this.textPaint = new Paint();
		this.animator = new Animator();
		this.animator.setStartTime(0);
		this.animator.setAlpha(1);
		this.textAlpha = 0;
		
		this.keyFrame = 0;
		
		this.startingTime = System.nanoTime();
		this.elapsedTime = 0;
		this.keyFrameTime = 0;
		this.powerUpBoundaryTime = 0;
		this.powerUpDownBlackTime = 0;
		this.powerUpDownNormalTime = 0;
		
		this.powerUpBoundaryTimeC = 0;
		this.powerUpDownBlackTimeC = 0;
		this.powerUpDownNormalTimeC = 0;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {	
		this.startingTime = System.nanoTime();
		Settings.firstRun = false;
		Settings.saveSettings(new FileManager(context));
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glViewport(0, 0, screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, screenDetails.metrics.widthPixels, 0, screenDetails.metrics.heightPixels, 1, -1);
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	
		utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);
		
		utilities.createBitmap();
		
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize((60 / 88.0f) * screenDetails.cellDim);
		textPaint.setTextAlign(Align.CENTER);
		
		gameView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				
				touchedX = event.getX();
				touchedY = screenDetails.metrics.heightPixels - event.getY();
				if((screenDetails.metrics.heightPixels - event.getY() >= screenDetails.cellDim + screenDetails.extraHeight) && (keyFrame == 2 || keyFrame == 4 || keyFrame == 5)) {
					touchedX = utilities.setXToGrid(event.getX(), boundaryPoints);
					touchedY = utilities.setYToGrid(screenDetails.metrics.heightPixels - event.getY(), boundaryPoints);
				}
				int indexX = utilities.getIndexX(touchedX);
				int indexY = utilities.getIndexY(touchedY);
				if(indexX < 0)
					indexX = 0;
				if(indexY < 0)
					indexY = 0;
				
				switch(action) {
				case MotionEvent.ACTION_DOWN:
					if((touchedY >= screenDetails.cellDim + screenDetails.extraHeight) && !isPaused && (keyFrame == 2 || keyFrame == 4 || keyFrame == 5)) {
						if(gridPointType[indexX][indexY] == UNTOUCHED_POINT || gridPointType[indexX][indexY] == BOUNDARY_POINT_ON) {
							gridPointType[indexX][indexY] = TOUCHED_POINT_WAVE_UP;
							latestIndexX = indexX;
							latestIndexY = indexY;
							latestPointType = TOUCHED_POINT_WAVE_UP;
						}
					}
					return true;
				case MotionEvent.ACTION_MOVE:
					if((touchedY >= screenDetails.cellDim + screenDetails.extraHeight) && !isPaused && (keyFrame == 2 || keyFrame == 4 || keyFrame == 5)) {
						if ((gridPointType[indexX][indexY] == UNTOUCHED_POINT || gridPointType[indexX][indexY] == BOUNDARY_POINT_ON) &&
								((indexX == latestIndexX - 1 && indexY == latestIndexY) ||
								(indexX == latestIndexX + 1 && indexY == latestIndexY) ||
								(indexX == latestIndexX && indexY == latestIndexY - 1) ||
								(indexX == latestIndexX && indexY == latestIndexY + 1))) {
								gridPointType[indexX][indexY] = -1 * latestPointType;
								latestIndexX = indexX;
								latestIndexY = indexY;
								latestPointType = -1 * latestPointType;
						}
					}
					return true;
				case MotionEvent.ACTION_UP:
					if((touchedY >= screenDetails.cellDim + screenDetails.extraHeight) && !isPaused && keyFrame == 2 ) {
						for(int i = 0; i <= screenDetails.wCells; i++) {
							for(int j = 0; j <= screenDetails.hCells; j++) {
								if(gridPointType[i][j] == TOUCHED_POINT_WAVE_UP) {
									radiusAtPoint[i][j] = 0;
									gridPointType[i][j] = DRAWING_POINT_WAVE_UP;
								}
								else if(gridPointType[i][j] == TOUCHED_POINT_WAVE_DOWN) {
									radiusAtPoint[i][j] = 0;
									gridPointType[i][j] = DRAWING_POINT_WAVE_DOWN;
								}
							}
						}							
						
						for(int i = 0; i <= screenDetails.wCells; i++)
							for(int j = 1; j <= screenDetails.hCells; j++) 
								blockPoints(i, j);
					}
					else {
						for(int i = 0; i <= screenDetails.wCells; i++) {
							for(int j = 0; j <= screenDetails.hCells; j++) {
								if(gridPointType[i][j] == TOUCHED_POINT_WAVE_UP) {
									radiusAtPoint[i][j] = 0;
									gridPointType[i][j] = DRAWING_POINT_WAVE_UP;
								}
								else if(gridPointType[i][j] == TOUCHED_POINT_WAVE_DOWN) {
									radiusAtPoint[i][j] = 0;
									gridPointType[i][j] = DRAWING_POINT_WAVE_DOWN;
								}
							}
						}	
						
						if(utilities.checkTouch(touchedX, touchedY, playPause) && !isRestartPressed && !isBackPressed && (keyFrame == 2 || keyFrame == 4 || keyFrame == 5)) {
							isPaused = !isPaused;
							if(keyFrame == 4 || keyFrame == 5) {
								isUnlocked = false;
								keyFrame = 2;
								orbUnlocked = Bubble.BUBBLE_NONE;
								for(int i = 0; i <= screenDetails.wCells; i++)
									for(int j = 1; j <= screenDetails.hCells; j++)
										gridPointType[i][j] = originalPointType[i][j];
							}
						}
						
						if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), rectViewOrb) && keyFrame == 2 && isPaused) {
							if(difficultyLevel > 1 && gameMode != GAME_NO_ORBS) {
								keyFrame = 5;
								orbUnlocked = Bubble.POWERUP_BOUNDARY;
							}
						}
						
						if(keyFrame == 4) {
							if(difficultyLevel == 3 && orbUnlocked == Bubble.POWERUP_NORMAL_SCORE) {
								if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), orbUnlockedFwd))
									orbUnlocked = Bubble.POWERUP_BLACK_SCORE;
							}
							else if(difficultyLevel == 3 && orbUnlocked == Bubble.POWERUP_BLACK_SCORE) {
								if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), orbUnlockedBack))
									orbUnlocked = Bubble.POWERUP_NORMAL_SCORE;
							}
							else if(difficultyLevel == 4 && orbUnlocked == Bubble.POWERDOWN_NORMAL_SCORE) {
								if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), orbUnlockedFwd))
									orbUnlocked = Bubble.POWERDOWN_BLACK_SCORE;
							}
							else if(difficultyLevel == 4 && orbUnlocked == Bubble.POWERDOWN_BLACK_SCORE) {
								if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), orbUnlockedBack))
									orbUnlocked = Bubble.POWERDOWN_NORMAL_SCORE;
							}
						}
						
						if(keyFrame == 5) {
							if(difficultyLevel > 2 && difficultyLevel < 10) {
								if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), orbUnlockedFwd)) {
									if(difficultyLevel == 3 && orbUnlocked < Bubble.POWERUP_BLACK_SCORE ||
									   difficultyLevel == 4 && orbUnlocked < Bubble.POWERDOWN_BLACK_SCORE ||
									   difficultyLevel == 5 && orbUnlocked < Bubble.SUPERPOP_NORMAL ||
									   difficultyLevel == 6 && orbUnlocked < Bubble.SUPERPOP_BLACK ||
									   difficultyLevel == 7 && orbUnlocked < Bubble.SUPERPOP_BLACK_NEGATIVE ||
									   difficultyLevel == 8 && orbUnlocked < Bubble.INVERT_BUBBLE ||
									   difficultyLevel == 9 && orbUnlocked < Bubble.CONVERT_ALL_TO_NORMAL)
										orbUnlocked += 100;
									if(orbUnlocked > Bubble.CONVERT_ALL_TO_BLACK)
										orbUnlocked = Bubble.CONVERT_ALL_TO_BLACK;
								}
							}
							if(difficultyLevel > 2 && difficultyLevel <= 10) {
								if(utilities.checkTouch(event.getX(), screenDetails.metrics.heightPixels - event.getY(), orbUnlockedBack)) {
									orbUnlocked -= 100;
									if(orbUnlocked < Bubble.POWERUP_BOUNDARY)
										orbUnlocked = Bubble.POWERUP_BOUNDARY;
								}
							}
						}
						
						if(utilities.checkTouch(touchedX, touchedY, restart) && !isBackPressed && keyFrame == 2){
							isRestartPressed = true;
							isPaused = true;
						}
						
						if(utilities.checkTouch(touchedX, touchedY, back) && !isRestartPressed && keyFrame == 2) {
							isBackPressed = true;
							isPaused = true;
						}
					}
					return true;
				}
				return false;
			}
		});
		
		switch(keyFrame) {
		case 0:
			utilities.drawTexture(gl, rectFullScreen, DrawingUtilities.TEXTURE_GAMEMODE, 1);
			if(setScoreAndTime()) {
				keyFrame ++;
				keyFrameTime = 0;
			} 
			break;
		case 1:
			if(!isPaused) 
				keyFrameTime += elapsedTime;
			
			font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextSize((70 / 88.0f) * screenDetails.cellDim);
					
			if(animator.getStartTime() == 0) {
				animator.setAlpha(0);
				animator.setStartTime(startingTime);
			}
			else if(keyFrameTime <= 1000000000L) {
				textAlpha = animator.fadeIn(System.nanoTime(), 1000);
				textPaint.setARGB((int) (textAlpha * 255), 255, 255, 255);
				utilities.drawTextToBitmap("Game starts in...", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 1000000000L && keyFrameTime < 2000000000L) {
				animator.setAlpha(1);
				textAlpha = animator.fadeOut(System.nanoTime() - 1000000000L, 1000);
				textPaint.setARGB((int) (textAlpha * 255), 255, 255, 255);
				utilities.drawTextToBitmap("Game starts in...", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 2000000000L && keyFrameTime < 2500000000L) {
				animator.setAlpha(1);
				utilities.drawTextToBitmap("3", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 2500000000L && keyFrameTime < 3500000000L) {
				animator.setAlpha(1);
				textAlpha = animator.fadeOut(System.nanoTime() - 2500000000L, 1000);
				textPaint.setARGB((int) (textAlpha * 255), 255, 255, 255);
				utilities.drawTextToBitmap("3", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 3500000000L && keyFrameTime < 4000000000L) {
				animator.setAlpha(1);
				utilities.drawTextToBitmap("2", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 4000000000L && keyFrameTime < 5000000000L) {
				animator.setAlpha(1);
				textAlpha = animator.fadeOut(System.nanoTime() - 4000000000L, 1000);
				textPaint.setARGB((int) (textAlpha * 255), 255, 255, 255);
				utilities.drawTextToBitmap("2", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 5000000000L && keyFrameTime < 5500000000L) {
				animator.setAlpha(1);
				utilities.drawTextToBitmap("1", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else if(keyFrameTime >= 5500000000L && keyFrameTime < 6500000000L) {
				animator.setAlpha(1);
				textAlpha = animator.fadeOut(System.nanoTime() - 5500000000L, 1000);
				textPaint.setARGB((int) (textAlpha * 255), 255, 255, 255);
				utilities.drawTextToBitmap("1", screenDetails.metrics.widthPixels / 2, 
						(rectGameScreen.top - rectGameScreen.bottom) / 2, textPaint);
			}
			else {
				animator.setAlpha(0);
				animator.setStartTime(0);
				keyFrame++;
				keyFrameTime = 0;
				totalGameTime = 0;
			}
			break;
		case 2:
			if(!isPaused) {
				keyFrameTime += elapsedTime;
				totalGameTime += elapsedTime;
			}
			
			font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextSize((55 / 88.0f) * screenDetails.cellDim);
			
			if(!isBackPressed && !isRestartPressed) {
				textPaint.setARGB(225, 255, 255, 255);
				if(gameMode == GAME_TIME_1M) {
					utilities.drawTextToBitmap(String.format("%.2f", (60000000000L - totalGameTime) / 1000000000F) + "s", 
											   screenDetails.metrics.widthPixels / 2, 
											   screenDetails.cellDim / 2, 
											   textPaint);
				}
				else if(gameMode == GAME_TIME_2M) {
					utilities.drawTextToBitmap(String.format("%.2f", (120000000000L - totalGameTime) / 1000000000F) + "s", 
											   screenDetails.metrics.widthPixels / 2, 
											   screenDetails.cellDim / 2, 
											   textPaint);
				}
				else if(gameMode == GAME_TIME_3M) {
					utilities.drawTextToBitmap(String.format("%.2f", (180000000000L - totalGameTime) / 1000000000F) + "s", 
											   screenDetails.metrics.widthPixels / 2, 
											   screenDetails.cellDim / 2, 
											   textPaint);
				}
				else {
					utilities.drawTextToBitmap(String.format("%.2f", totalGameTime / 1000000000F) + "s", 
							   				   screenDetails.metrics.widthPixels / 2, 
							   				   screenDetails.cellDim / 2, 
							   				   textPaint);
				}
				
				textPaint.getTextBounds("N:", 0, 2, textBounds);
				utilities.drawTextToBitmap("N:" + scoreNormalNormal + "  NI:" + scoreNormalInterpop + "  B:" + scoreBlackNormal + "  BI:" + scoreBlackInterpop, 
		   				                   screenDetails.metrics.widthPixels / 2, 
		   				                   screenDetails.cellDim * 1.5F + screenDetails.margin / 2 + textBounds.height() / 2, 
		   				                   textPaint);
				
				textPaint.getTextBounds("Level:", 0, 2, textBounds);
				utilities.drawTextToBitmap("Level:" + difficultyLevel, 
		   				   				   screenDetails.metrics.widthPixels / 2, 
		   				   				   screenDetails.cellDim * 2.5F + screenDetails.margin / 2 + textBounds.height() / 2, 
		   				   				   textPaint);
				textPaint.setARGB(255, 255, 255, 255);
			}
			
			if((gameMode == GAME_TIME_1M && totalGameTime >= 60000000000L)  ||
			   (gameMode == GAME_TIME_2M && totalGameTime >= 120000000000L) ||
			   (gameMode == GAME_TIME_3M && totalGameTime >= 180000000000L) ||
			   (gameMode == GAME_SCORE_500 && score >= 500) ||
			   (gameMode == GAME_SCORE_1000 && score >= 1000) ||
			   (gameMode == GAME_SCORE_2000 && score >= 2000) ||
			   (gameMode == GAME_SCORE_POSITIVE && score < 0))
				isGameOver = true;
			
			if((score + 150) / 150 > difficultyLevel) {
				textPaint.setTextAlign(Align.CENTER);
				if(gameMode != GAME_NO_ORBS) {
					if(difficultyLevel == 1 && Settings.isBoundaryUnlocked == false) {
						orbUnlocked = Bubble.POWERUP_BOUNDARY;
						Settings.isBoundaryUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 2 && Settings.isPowerUpUnlocked == false) {
						orbUnlocked = Bubble.POWERUP_NORMAL_SCORE;
						Settings.isPowerUpUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 3 && Settings.isPowerDownUnlocked == false) {
						orbUnlocked = Bubble.POWERDOWN_NORMAL_SCORE;
						Settings.isPowerDownUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 4 && Settings.isSPNormalUnlocked == false) {
						orbUnlocked = Bubble.SUPERPOP_NORMAL;
						Settings.isSPNormalUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 5 && Settings.isSPBlackUnlocked == false) {
						orbUnlocked = Bubble.SUPERPOP_BLACK;
						Settings.isSPBlackUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 6 && Settings.isSPBlackNegUnlocked == false) {
						orbUnlocked = Bubble.SUPERPOP_BLACK_NEGATIVE;
						Settings.isSPBlackNegUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 7 && Settings.isInvertUnlocked == false) {
						orbUnlocked = Bubble.INVERT_BUBBLE;
						Settings.isInvertUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 8 && Settings.isConvertNormalUnlocked == false) {
						orbUnlocked = Bubble.CONVERT_ALL_TO_NORMAL;
						Settings.isConvertNormalUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
					else if(difficultyLevel == 9 && Settings.isConvertBlackUnlocked == false) {
						orbUnlocked = Bubble.CONVERT_ALL_TO_BLACK;
						Settings.isConvertBlackUnlocked = true;
						Settings.saveSettings(new FileManager(gameActivity));
						isPaused = true;
						isUnlocked = true;
						keyFrame = 4;
					}
				}
				if(difficultyLevel < 10){
					if(!Settings.mute)
						soundManager.play(SoundManager.LEVELUP);
					difficultyLevel = (int) ((score + 150) / 150);
					rate += 0.1f;
				}
				if(difficultyLevel > 10)
					difficultyLevel = 10;
			}
			
			if(!isPowerUpNormal && !isPowerUpBlack && !isPowerDownNormal && !isPowerDownBlack)	
				setProb();
			
			if(gameMode == GAME_NO_ORBS || countOrbs > 3)
				setProbNoOrbs();
			
			if(gameMode == GAME_SCORE_POSITIVE) {
				probIndividual[0] = 0.35f;
				probIndividual[1] = 0.65f;
			}
			
			normalizeProb();
			
			if(boundaryPoints) {
				for(int i = 0; i <= screenDetails.wCells; i++) {
					if(gridPointType[i][1] != TOUCHED_POINT_WAVE_DOWN && gridPointType[i][1] != TOUCHED_POINT_WAVE_UP &&
					   gridPointType[i][1] != DRAWING_POINT_WAVE_DOWN && gridPointType[i][1] != DRAWING_POINT_WAVE_UP && 
					   gridPointType[i][1] != BLOCKED_POINT)
						gridPointType[i][1] = BOUNDARY_POINT_ON;
					if(gridPointType[i][(int) screenDetails.hCells] != TOUCHED_POINT_WAVE_DOWN && 
					   gridPointType[i][(int) screenDetails.hCells] != TOUCHED_POINT_WAVE_UP &&
					   gridPointType[i][(int) screenDetails.hCells] != DRAWING_POINT_WAVE_DOWN && 
					   gridPointType[i][(int) screenDetails.hCells] != DRAWING_POINT_WAVE_UP && 
					   gridPointType[i][(int) screenDetails.hCells] != BLOCKED_POINT)
						gridPointType[i][(int) screenDetails.hCells] = BOUNDARY_POINT_ON;
				}
				for(int j = 1; j <= screenDetails.hCells; j++) {
					if(gridPointType[0][j] != TOUCHED_POINT_WAVE_DOWN && gridPointType[0][j] != TOUCHED_POINT_WAVE_UP &&
					   gridPointType[0][j] != DRAWING_POINT_WAVE_DOWN && gridPointType[0][j] != DRAWING_POINT_WAVE_UP && 
					   gridPointType[0][j] != BLOCKED_POINT)
						gridPointType[0][j] = BOUNDARY_POINT_ON;
					if(gridPointType[(int) screenDetails.wCells][j] != TOUCHED_POINT_WAVE_DOWN && 
					   gridPointType[(int) screenDetails.wCells][j] != TOUCHED_POINT_WAVE_UP &&
					   gridPointType[(int) screenDetails.wCells][j] != DRAWING_POINT_WAVE_DOWN && 
					   gridPointType[(int) screenDetails.wCells][j] != DRAWING_POINT_WAVE_UP && 
					   gridPointType[(int) screenDetails.wCells][j] != BLOCKED_POINT)
						gridPointType[(int) screenDetails.wCells][j] = BOUNDARY_POINT_ON;
				}
			}
			else {
				for(int i = 0; i <= screenDetails.wCells; i++)
					gridPointType[i][1] = gridPointType[i][(int) screenDetails.hCells] = BOUNDARY_POINT_OFF;		
				for(int j = 1; j <= screenDetails.hCells; j++)
					gridPointType[0][j] = gridPointType[(int) screenDetails.wCells][j] = BOUNDARY_POINT_OFF;
			}
			
			for(int i = 0; i <= screenDetails.wCells; i++)
				for(int j = 1; j <= screenDetails.hCells; j++)
					utilities.drawGridPoint(gl, i, j, gridPointType[i][j], originalPointType[i][j], radiusAtPoint[i][j]);
			
			if(!isPaused) {
				
				for(int i = 0; i <= screenDetails.wCells; i++) {
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(gridPointType[i][j] == DRAWING_POINT_WAVE_UP || 
								gridPointType[i][j] == DRAWING_POINT_WAVE_DOWN)
							radiusAtPoint[i][j] += rate / 33333333 * elapsedTime;
					}
				}
				
				for(int i = 0; i <= screenDetails.wCells; i++)
					for(int j = 1; j <= screenDetails.hCells; j++)
						if(radiusAtPoint[i][j] >= screenDetails.cellDim)
							checkBubble(i, j);
				
				for(int i = 0; i <= screenDetails.wCells; i++) {
					for(int j = 1; j <= screenDetails.hCells; j++) {
						if(radiusAtPoint[i][j] >= screenDetails.cellDim && !isPaused) {
							unblockPoints(i, j);
							gridPointType[i][j] = originalPointType[i][j];
							radiusAtPoint[i][j] = -100;
							for(int h = 0; h <= screenDetails.wCells; h++)
								for(int k = 1; k <= screenDetails.hCells; k++)
									blockPoints(h, k);
						}
					}
				}
			}
			
			if(powerUpBoundaryTime > 0 && powerUpBoundaryTime <= powerUpBoundaryTimeC && !isPaused && !isRestartPressed && !isBackPressed) {
				powerUpBoundaryTime += elapsedTime;
				textPaint.setTextSize((43 / 88.0f) * screenDetails.cellDim);
				textPaint.setARGB(225, 255, 255, 255);
				if(powerUpBoundaryTime < powerUpBoundaryTimeC)
					utilities.drawTextToBitmap("Boundary Points disable in:" + String.format("%.1f", (powerUpBoundaryTimeC - powerUpBoundaryTime) / 1000000000.0f) + "s", 
						                   		screenDetails.metrics.widthPixels / 2, 
						                   		screenDetails.cellDim * 3.5f + screenDetails.margin / 2, 
						                   		textPaint);
				textPaint.setARGB(255, 255, 255, 255);
				textPaint.setTextSize((55 / 88.0f) * screenDetails.cellDim);
			}
			if(powerUpBoundaryTime > powerUpBoundaryTimeC) {
				powerUpBoundaryTime = 0;
				deactivatePower(Bubble.POWERUP_BOUNDARY);
			}
			
			if(powerUpDownNormalTime > 0 && powerUpDownNormalTime <= powerUpDownNormalTimeC && !isPaused && !isRestartPressed && !isBackPressed) {
				powerUpDownNormalTime += elapsedTime;
			}
			if(powerUpDownNormalTime > powerUpDownNormalTimeC) {
				powerUpDownNormalTime = 0;
				if(isPowerUpNormal) {
					deactivatePower(Bubble.POWERUP_NORMAL_SCORE);
					isPowerUpNormal = false;
				}
				else if(isPowerDownNormal) {
					deactivatePower(Bubble.POWERDOWN_NORMAL_SCORE);
					isPowerDownNormal = false;
				}
			}
			
			if(powerUpDownBlackTime > 0 && powerUpDownBlackTime <= powerUpDownBlackTimeC && !isPaused && !isRestartPressed && !isBackPressed) {
				powerUpDownBlackTime += elapsedTime;
			}
			if(powerUpDownBlackTime > powerUpDownBlackTimeC) {
				powerUpDownBlackTime = 0;
				if(isPowerUpBlack) {
					deactivatePower(Bubble.POWERUP_BLACK_SCORE);
					isPowerUpBlack = false;
				}
				else if(isPowerDownBlack) {
					deactivatePower(Bubble.POWERDOWN_BLACK_SCORE);
					isPowerDownBlack = false;
				}
			}			
			
			if(keyFrameTime > 1033333333L - (difficultyLevel * 33333333L) && !isPaused) {
				bubbleRandomX = (int) (random.nextFloat() * screenDetails.wCells);
				bubbleRandomY = (int) (random.nextFloat() * screenDetails.hCells);
				keyFrameTime = 0;
			}
			else {
				bubbleRandomX = -100;
				bubbleRandomY = -100;
			}
				
			if(bubbleRandomX >= 0 && bubbleRandomY >= 1)
				if(bubbleType[bubbleRandomX][bubbleRandomY] != Bubble.BUBBLE_NONE)
						bubbleRandomX = bubbleRandomY = -100;
					
			if(bubbleRandomX >= 0 && bubbleRandomY >= 1) {
				if(bubbleType[bubbleRandomX][bubbleRandomY] == Bubble.BUBBLE_NONE 
						&& checkWave(bubbleRandomX, bubbleRandomY) == false) {
					random.setSeed((new Random()).nextLong());
					float randomBubble = random.nextFloat();
					if(randomBubble <= probCumulative[0])
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.BUBBLE_NORMAL;
					else if(randomBubble > probCumulative[0] && randomBubble < probCumulative[1])
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.BUBBLE_BLACK;
					else if(randomBubble > probCumulative[1] && randomBubble < probCumulative[2]) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.POWERUP_BOUNDARY; 
						countOrbs++;
					}
					else if(randomBubble > probCumulative[2] && randomBubble < probCumulative[3] && !isPowerUpNormal && !isPowerDownNormal) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.POWERUP_NORMAL_SCORE;
						countOrbs++;
						isPowerUpNormal = true;
					}
					else if(randomBubble > probCumulative[3] && randomBubble < probCumulative[4] && !isPowerUpBlack && !isPowerDownBlack) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.POWERUP_BLACK_SCORE;
						countOrbs++;
						isPowerUpBlack = true;
					}
					else if(randomBubble > probCumulative[4] && randomBubble < probCumulative[5] && !isPowerUpNormal && !isPowerDownNormal) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.POWERDOWN_NORMAL_SCORE;
						countOrbs++;
						isPowerDownNormal = true;
					}
					else if(randomBubble > probCumulative[5] && randomBubble < probCumulative[6] && !isPowerUpBlack && !isPowerDownBlack) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.POWERDOWN_BLACK_SCORE;
						countOrbs++;
						isPowerDownBlack = true;
					}
					else if(randomBubble > probCumulative[6] && randomBubble < probCumulative[7]) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.SUPERPOP_NORMAL;
						countOrbs++;
					}
					else if(randomBubble > probCumulative[7] && randomBubble < probCumulative[8] && !isSuperpopBlackNegative) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.SUPERPOP_BLACK;
						countOrbs++;
						isSuperpopBlack = true;
					}
					else if(randomBubble > probCumulative[8] && randomBubble < probCumulative[9] && !isSuperpopBlack) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.SUPERPOP_BLACK_NEGATIVE;
						countOrbs++;
						isSuperpopBlackNegative = true;
					}
					else if(randomBubble > probCumulative[9] && randomBubble < probCumulative[10] && !isConvertNormal && !isConvertBlack) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.INVERT_BUBBLE;
						countOrbs++;
						isInvertBubble = true;
					}
					else if(randomBubble > probCumulative[10] && randomBubble < probCumulative[11] && !isInvertBubble && !isConvertBlack) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.CONVERT_ALL_TO_NORMAL;
						countOrbs++;
						isConvertNormal = true;
					}
					else if(randomBubble > probCumulative[11] && randomBubble < probCumulative[12] && !isConvertNormal && !isConvertBlack) {
						bubbleType[bubbleRandomX][bubbleRandomY] = Bubble.CONVERT_ALL_TO_BLACK;
						countOrbs++;
						isConvertBlack = true;
					}
				}
			}
			
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 1; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] != Bubble.BUBBLE_NONE) {
						if(bubbleType[i][j] <= Bubble.BUBBLE_DROP) {
							if(bubbleType[i][j] == Bubble.BUBBLE_BLACK)
								utilities.drawBubble(gl, (i + 0.5F) * screenDetails.cellDim + screenDetails.margin / 2, 
										 screenDetails.extraHeight + (j + 0.5F) * screenDetails.cellDim + screenDetails.margin / 2, 
										 screenDetails.cellDim / 6, bubbleType[i][j], 0.8f);
							else
								utilities.drawBubble(gl, (i + 0.5F) * screenDetails.cellDim + screenDetails.margin / 2, 
										 screenDetails.extraHeight + (j + 0.5F) * screenDetails.cellDim + screenDetails.margin / 2, 
										 screenDetails.cellDim / 6, bubbleType[i][j], bubbleFade[i][j]);
						}
						else
							utilities.drawBubble(gl, (i + 0.5F) * screenDetails.cellDim + screenDetails.margin / 2, 
									 screenDetails.extraHeight + (j + 0.5F) * screenDetails.cellDim + screenDetails.margin / 2, 
									 screenDetails.cellDim / 3, bubbleType[i][j], bubbleFade[i][j]);
						if(bubbleType[i][j] == Bubble.BUBBLE_DROP) {
							bubbleFade[i][j] -= elapsedTime / 1000000000.0;
						}
						if(bubbleFade[i][j] <= 0) {
							bubbleFade[i][j] = 1.0f;
							bubbleType[i][j] = Bubble.BUBBLE_NONE;
						}
					}
					if(scoreType[i][j] == SCORE_NORMAL_NORMAL)
						score += scoreNormalNormal;
					else if(scoreType[i][j] == SCORE_NORMAL_INTERPOP)
						score += scoreNormalInterpop;
					else if(scoreType[i][j] == SCORE_BLACK_NORMAL)
						score += scoreBlackNormal;
					else if(scoreType[i][j] == SCORE_BLACK_INTERPOP)
						score += scoreBlackInterpop;	
					scoreType[i][j] = SCORE_NONE;
				}
			
			if(isRestartPressed) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_RESTARTPRESSED, 1);
				if(utilities.checkTouch(touchedX, touchedY, restartBackYes)) {
					this.startingTime = System.nanoTime();
					this.elapsedTime = 0;
					this.keyFrameTime = 0;
					this.powerUpBoundaryTime = 0;
					this.powerUpDownBlackTime = 0;
					this.powerUpDownNormalTime = 0;
					
					this.powerUpBoundaryTimeC = 0;
					this.powerUpDownBlackTimeC = 0;
					this.powerUpDownNormalTimeC = 0;
					
					for(int i = 0; i <= screenDetails.wCells; i++)
						for(int j = 1; j <= screenDetails.hCells; j++)
							gridPointType[i][j] = originalPointType[i][j];
						
					this.latestIndexX = -100;
					this.latestIndexY = -100;
					this.latestPointType = TOUCHED_POINT_WAVE_UP;
					
					this.score = 0;
					this.scoreNormalNormal = 10;
					this.scoreBlackNormal = -15;
					this.scoreNormalInterpop = 20;
					this.scoreBlackInterpop = 0;
					this.bubbleRandomX = this.bubbleRandomY = -100;
					
					this.difficultyLevel = 1;
					
					for(int i = 0; i <= screenDetails.wCells; i++)
						for(int j = 0; j <= screenDetails.hCells; j++) 
							radiusAtPoint[i][j] = -100;
					
					this.rate = 1.5f;
					
					for(int i = 0; i < screenDetails.wCells; i++)
						for(int j = 0; j < screenDetails.hCells; j++) {
							bubbleType[i][j] = Bubble.BUBBLE_NONE;
							bubbleFade[i][j] = 1.0f;
							scoreType[i][j] = SCORE_NONE;
						}
					
					this.orbUnlocked = Bubble.BUBBLE_NONE;
					
					this.isUnlocked = false;
					this.isPowerUpNormal = false;
					this.isPowerUpBlack = false;
					this.isPowerDownNormal = false;
					this.isPowerDownBlack = false;
					this.isSuperpopBlack = false;
					this.isSuperpopBlackNegative = false;
					this.isInvertBubble = false;
					this.isConvertNormal = false;
					this.isConvertBlack = false;
					
					setProb();				
					normalizeProb();
					
					this.countOrbs = 0;
					
					this.animator.setStartTime(0);
					this.animator.setAlpha(1);
					this.textAlpha = 0;
					
					isRestartPressed = false;
					isPaused = false;
					keyFrame = 1;
					score = 0;
					totalGameTime = 0;
				}
				if(utilities.checkTouch(touchedX, touchedY, restartBackNo)) {
					isRestartPressed = false;
					isPaused = false;
				}
			}
			
			if(isBackPressed) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_BACKPRESSED, 1);
				if(utilities.checkTouch(touchedX, touchedY, restartBackYes)) {
					gameActivity.finish();
					gameActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
				if(utilities.checkTouch(touchedX, touchedY, restartBackNo)) {
					isBackPressed = false;
					isPaused = false;
				}
			}
			
			if(isGameOver) {
				animator.setAlpha(0);
				animator.setStartTime(0);
				keyFrame++;
				keyFrameTime = 0;
				if(!Settings.mute)
					soundManager.play(SoundManager.GAMEOVER);
			}
			
			if(isPaused && !isRestartPressed && !isBackPressed && !isUnlocked) {
				if(difficultyLevel > 1 && gameMode != GAME_NO_ORBS)
					utilities.drawTexture(gl, rectViewOrb, DrawingUtilities.TEXTURE_VIEWORB, 1);
			}
			
			font = Typeface.createFromAsset(context.getAssets(), "Emotion Engine.ttf");
			textPaint.setTypeface(font);
			
			textPaint.setTextSize(55.0f * (screenDetails.extraHeight + screenDetails.cellDim) / (55.0f + 88.0f));
			textPaint.getTextBounds("SCORE", 0, 5, textBounds);
			utilities.drawTextToBitmap("SCORE", rectBottom.centerX(), screenDetails.metrics.heightPixels - rectBottom.centerY() * 1.5f + textBounds.height() / 2, textPaint);
			utilities.drawTextToBitmap(Integer.toString(score), rectBottom.centerX(), screenDetails.metrics.heightPixels - rectBottom.centerY() / 2 + textBounds.height() / 2, textPaint);
			
			break;
		case 3:
			if(keyFrameTime == 0) {
				if(gameMode == GAME_TIME_1M || gameMode == GAME_TIME_2M || gameMode == GAME_TIME_3M) {
					isHighScoreSet = Settings.appendScore(gameMode, score);
					Settings.saveSettings(new FileManager(gameActivity));
				}
				else {
					isHighScoreSet = Settings.appendTime(gameMode, totalGameTime / 1000000000.0F);
					Settings.saveSettings(new FileManager(gameActivity));
				}
				animator.setAlpha(0);
				animator.setStartTime(System.nanoTime());
			}
			
			keyFrameTime += elapsedTime;
			
			
			
			if(keyFrameTime <= 1000000000L) {
				float alpha = animator.fadeIn(System.nanoTime(), 1000); 
				utilities.drawTexture(gl, rectFullScreen, DrawingUtilities.TEXTURE_GAMEOVER, alpha);
				if(isHighScoreSet) {
					textPaint.setARGB((int) alpha * 255, 255, 255, 255);
					utilities.drawTextToBitmap("New highscore set!", screenDetails.metrics.widthPixels / 2, 
											   screenDetails.metrics.heightPixels - screenDetails.cellDim, textPaint);
				}
			}
			else if(keyFrameTime > 1000000000L && keyFrameTime <= 3000000000L) {
				utilities.drawTexture(gl, rectFullScreen, DrawingUtilities.TEXTURE_GAMEOVER, 1);
				if(isHighScoreSet) {
					textPaint.setARGB(255, 255, 255, 255);
					utilities.drawTextToBitmap("New highscore set!", screenDetails.metrics.widthPixels / 2, 
											   screenDetails.cellDim, textPaint);
				}
			}
			else if(keyFrameTime > 3000000000L) {
				utilities.drawTexture(gl, rectFullScreen, DrawingUtilities.TEXTURE_GAMEOVER, 1);
				gameActivity.finish();
			}
			break;
		case 4:
			boolean tickCross = false;
			font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextSize((43 / 88.0f) * screenDetails.cellDim);
			
			if(difficultyLevel == 2) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERUP_BOUNDARY, 1);
				utilities.drawTextToBitmap(orbLine1[0], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[0], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				tickCross = orbInterPop[0];
			}
			else if(difficultyLevel == 3) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				
				if(orbUnlocked == Bubble.POWERUP_NORMAL_SCORE) {
					utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERUP_NORMAL_SCORE, 1);
					utilities.drawTexture(gl, orbUnlockedFwd, DrawingUtilities.TEXTURE_FWDBUTTON, 1);
					utilities.drawTextToBitmap(orbLine1[1], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
					utilities.drawTextToBitmap(orbLine2[1], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
					tickCross = orbInterPop[1];
				}
				else if(orbUnlocked == Bubble.POWERUP_BLACK_SCORE) {
					utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERUP_BLACK_SCORE, 1);
					utilities.drawTexture(gl, orbUnlockedBack, DrawingUtilities.TEXTURE_BACKBUTTON, 1);
					utilities.drawTextToBitmap(orbLine1[2], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
					utilities.drawTextToBitmap(orbLine2[2], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
					tickCross = orbInterPop[2];
				}
			}
			else if(difficultyLevel == 4) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				if(orbUnlocked == Bubble.POWERDOWN_NORMAL_SCORE) {
					utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERDOWN_NORMAL_SCORE, 1);
					utilities.drawTexture(gl, orbUnlockedFwd, DrawingUtilities.TEXTURE_FWDBUTTON, 1);
					utilities.drawTextToBitmap(orbLine1[3], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
					utilities.drawTextToBitmap(orbLine2[3], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
					tickCross = orbInterPop[3];
				}
				else if(orbUnlocked == Bubble.POWERDOWN_BLACK_SCORE) {
					utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERDOWN_BLACK_SCORE, 1);
					utilities.drawTexture(gl, orbUnlockedBack, DrawingUtilities.TEXTURE_BACKBUTTON, 1);
					utilities.drawTextToBitmap(orbLine1[4], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
					utilities.drawTextToBitmap(orbLine2[4], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
					tickCross = orbInterPop[4];
				}
			}
			else if(difficultyLevel == 5) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.SUPERPOP_NORMAL, 1);
				utilities.drawTextToBitmap(orbLine1[5], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[5], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				utilities.drawTextToBitmap("(SUPERPOP)", screenDetails.metrics.widthPixels / 2, orbLine3Y, textPaint);
				tickCross = orbInterPop[5];
			}
			else if(difficultyLevel == 6) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.SUPERPOP_BLACK, 1);
				utilities.drawTextToBitmap(orbLine1[6], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[6], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				utilities.drawTextToBitmap("(SUPERPOP)", screenDetails.metrics.widthPixels / 2, orbLine3Y, textPaint);
				tickCross = orbInterPop[6];
			}
			else if(difficultyLevel == 7) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.SUPERPOP_BLACK_NEGATIVE, 1);
				utilities.drawTextToBitmap(orbLine1[7], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[7], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				utilities.drawTextToBitmap("(SUPERPOP)", screenDetails.metrics.widthPixels / 2, orbLine3Y, textPaint);
				tickCross = orbInterPop[7];
			}
			else if(difficultyLevel == 8) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.INVERT_BUBBLE, 1);
				utilities.drawTextToBitmap(orbLine1[8], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[8], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				tickCross = orbInterPop[8];
			}
			else if(difficultyLevel == 9) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.CONVERT_ALL_TO_NORMAL, 1);
				utilities.drawTextToBitmap(orbLine1[9], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[9], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				tickCross = orbInterPop[9];
			}
			else if(difficultyLevel == 10) {
				utilities.drawTexture(gl, rectRestartBack, DrawingUtilities.TEXTURE_ORBUNLOCKED, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.CONVERT_ALL_TO_BLACK, 1);
				utilities.drawTextToBitmap(orbLine1[10], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[10], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				tickCross = orbInterPop[10];
			}
			if(tickCross)
				utilities.drawTexture(gl, orbTickCross, DrawingUtilities.TEXTURE_TICK, 1);
			else
				utilities.drawTexture(gl, orbTickCross, DrawingUtilities.TEXTURE_CROSS, 1);
			break;
		case 5:
			boolean viewTickCross = false;
			font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
			textPaint.setTypeface(font);
			textPaint.setTextSize((43 / 88.0f) * screenDetails.cellDim);
			textPaint.setARGB(255, 0, 0, 0);
			
			if(orbUnlocked == Bubble.POWERUP_BOUNDARY) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERUP_BOUNDARY, 1);
				utilities.drawTextToBitmap(orbLine1[0], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[0], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[0];
			}
			else if(orbUnlocked == Bubble.POWERUP_NORMAL_SCORE) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERUP_NORMAL_SCORE, 1);
				utilities.drawTextToBitmap(orbLine1[1], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[1], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[1];
			}
			else if(orbUnlocked == Bubble.POWERUP_BLACK_SCORE) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERUP_BLACK_SCORE, 1);
				utilities.drawTextToBitmap(orbLine1[2], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[2], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[2];
			}	
			else if(orbUnlocked == Bubble.POWERDOWN_NORMAL_SCORE) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERDOWN_NORMAL_SCORE, 1);
				utilities.drawTextToBitmap(orbLine1[3], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[3], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[3];
			}
			else if(orbUnlocked == Bubble.POWERDOWN_BLACK_SCORE) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.POWERDOWN_BLACK_SCORE, 1);
				utilities.drawTextToBitmap(orbLine1[4], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[4], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[4];
			}
			else if(orbUnlocked == Bubble.SUPERPOP_NORMAL) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.SUPERPOP_NORMAL, 1);
				utilities.drawTextToBitmap(orbLine1[5], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[5], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[5];
			}
			else if(orbUnlocked == Bubble.SUPERPOP_BLACK) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.SUPERPOP_BLACK, 1);
				utilities.drawTextToBitmap(orbLine1[6], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[6], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[6];
			}
			else if(orbUnlocked == Bubble.SUPERPOP_BLACK_NEGATIVE) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.SUPERPOP_BLACK_NEGATIVE, 1);
				utilities.drawTextToBitmap(orbLine1[7], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[7], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[7];
			}
			else if(orbUnlocked == Bubble.INVERT_BUBBLE) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.INVERT_BUBBLE, 1);
				utilities.drawTextToBitmap(orbLine1[8], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[8], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[8];
			}
			else if(orbUnlocked == Bubble.CONVERT_ALL_TO_NORMAL) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.CONVERT_ALL_TO_NORMAL, 1);
				utilities.drawTextToBitmap(orbLine1[9], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[9], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[9];
			}
			else if(orbUnlocked == Bubble.CONVERT_ALL_TO_BLACK) {
				utilities.drawTexture(gl, rectGameScreen, DrawingUtilities.TEXTURE_ORBAVAILABLE, 1);
				utilities.drawBubble(gl, orbUnlockedX, orbUnlockedY, orbUnlockedRadius, Bubble.CONVERT_ALL_TO_BLACK, 1);
				utilities.drawTextToBitmap(orbLine1[10], screenDetails.metrics.widthPixels / 2, orbLine1Y, textPaint);
				utilities.drawTextToBitmap(orbLine2[10], screenDetails.metrics.widthPixels / 2, orbLine2Y, textPaint);
				viewTickCross = orbInterPop[10];
			}
			if(viewTickCross)
				utilities.drawTexture(gl, viewOrbTickCross, DrawingUtilities.TEXTURE_TICK, 1);
			else
				utilities.drawTexture(gl, viewOrbTickCross, DrawingUtilities.TEXTURE_CROSS, 1);
			
			if(difficultyLevel > 2 && orbUnlocked != Bubble.POWERUP_BOUNDARY)
				utilities.drawTexture(gl, orbUnlockedBack, DrawingUtilities.TEXTURE_BACKBUTTON, 1);
			if(difficultyLevel == 2 || 
			   (difficultyLevel == 3 && orbUnlocked == Bubble.POWERUP_BLACK_SCORE) ||
			   (difficultyLevel == 4 && orbUnlocked == Bubble.POWERDOWN_BLACK_SCORE) ||
			   (difficultyLevel == 5 && orbUnlocked == Bubble.SUPERPOP_NORMAL) ||
			   (difficultyLevel == 6 && orbUnlocked == Bubble.SUPERPOP_BLACK) ||
			   (difficultyLevel == 7 && orbUnlocked == Bubble.SUPERPOP_BLACK_NEGATIVE) ||
			   (difficultyLevel == 8 && orbUnlocked == Bubble.INVERT_BUBBLE) ||
			   (difficultyLevel == 9 && orbUnlocked == Bubble.CONVERT_ALL_TO_NORMAL) ||
			   (difficultyLevel == 10 && orbUnlocked == Bubble.CONVERT_ALL_TO_BLACK)) {}
			else
				utilities.drawTexture(gl, orbUnlockedFwd, DrawingUtilities.TEXTURE_FWDBUTTON, 1);
			
			textPaint.setARGB(255, 255, 255, 255);
			break;
		}
		
		if(keyFrame == 1 || keyFrame == 2 || keyFrame == 4 || keyFrame == 5) {
			utilities.drawTexture(gl, headsDownDisplay, DrawingUtilities.TEXTURE_HEADSDOWN, 1);
		}
		
		if(keyFrame == 2) {
			utilities.drawTexture(gl, restart, DrawingUtilities.TEXTURE_RESTARTBUTTON, 1);
			utilities.drawTexture(gl, back, DrawingUtilities.TEXTURE_GAMEBACKBUTTON, 1);
			utilities.drawTexture(gl, rectBottom, DrawingUtilities.TEXTURE_GAMEBUTTON, 1);
		}
		
		if(keyFrame == 2 || keyFrame == 4 || keyFrame == 5) {
			if(this.isPaused == false)
				utilities.drawTexture(gl, playPause, DrawingUtilities.TEXTURE_PAUSEBUTTON, 1);
			else
				utilities.drawTexture(gl, playPause, DrawingUtilities.TEXTURE_PLAYBUTTON, 1);
		}
		
		utilities.drawText(gl);
		utilities.recycleBitmap();
		
/*		Log.i("check", "isPowerUpNormal" + isPowerUpNormal);
		Log.i("check", "isPowerUpBlack" + isPowerUpBlack);
		Log.i("check", "isPowerDownNormal" + isPowerDownNormal);
		Log.i("check", "isPowerDownBlack" + isPowerDownNormal);
		Log.i("check", "isSuperpopBlack" + isSuperpopBlack);
		Log.i("check", "isSuperpopBlackNegative" + isSuperpopBlackNegative);
		Log.i("check", "isInvertBubble" + isInvertBubble);
		Log.i("check", "isConvertNormal" + isConvertNormal);
		Log.i("check", "isConvertBlack" + isConvertBlack);
*/		
		elapsedTime = System.nanoTime() - startingTime;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0f, 0f, 0f, 0.4f);
	}
	
	public void blockPoints(int i, int j) {
		if(gridPointType[i][j] == DRAWING_POINT_WAVE_UP || gridPointType[i][j] == DRAWING_POINT_WAVE_DOWN) {
			if((i - 1 >= 0) && 
			   (gridPointType[i - 1][j] == UNTOUCHED_POINT || gridPointType[i - 1][j] == BOUNDARY_POINT_ON))
					gridPointType[i - 1][j] = BLOCKED_POINT;
			if((i + 1 <= screenDetails.wCells) && 
			   (gridPointType[i + 1][j] == UNTOUCHED_POINT || gridPointType[i + 1][j] == BOUNDARY_POINT_ON))
					gridPointType[i + 1][j] = BLOCKED_POINT;
			if((j - 1 >= 0) && 
			   (gridPointType[i][j - 1] == UNTOUCHED_POINT || gridPointType[i][j - 1] == BOUNDARY_POINT_ON))
					gridPointType[i][j - 1] = BLOCKED_POINT;
			if((j + 1 <= screenDetails.hCells) && 
			   (gridPointType[i][j + 1] == UNTOUCHED_POINT || gridPointType[i][j + 1] == BOUNDARY_POINT_ON))
					gridPointType[i][j + 1] = BLOCKED_POINT;	
			if((i + 1 <= screenDetails.wCells) && (j + 1 <= screenDetails.hCells) && 
					   (gridPointType[i + 1][j + 1] == UNTOUCHED_POINT || gridPointType[i + 1][j + 1] == BOUNDARY_POINT_ON))
					gridPointType[i + 1][j + 1] = BLOCKED_POINT;	
			if((i - 1 >= 0) && (j + 1 <= screenDetails.hCells) && 
					   (gridPointType[i - 1][j + 1] == UNTOUCHED_POINT || gridPointType[i - 1][j + 1] == BOUNDARY_POINT_ON))
					gridPointType[i - 1][j + 1] = BLOCKED_POINT;	
			if((i - 1 >= 0) && (j - 1 >= 0) && 
					   (gridPointType[i - 1][j - 1] == UNTOUCHED_POINT || gridPointType[i - 1][j - 1] == BOUNDARY_POINT_ON))
					gridPointType[i - 1][j - 1] = BLOCKED_POINT;	
			if((i + 1 <= screenDetails.wCells) && (j - 1 >= 0) && 
					   (gridPointType[i + 1][j - 1] == UNTOUCHED_POINT || gridPointType[i + 1][j - 1] == BOUNDARY_POINT_ON))
					gridPointType[i + 1][j - 1] = BLOCKED_POINT;	
			}
	}
	
	public void unblockPoints(int i, int j) {
		if(gridPointType[i][j] == DRAWING_POINT_WAVE_UP || gridPointType[i][j] == DRAWING_POINT_WAVE_DOWN) {
			if((i - 1 >= 0) && gridPointType[i - 1][j] == BLOCKED_POINT)
				gridPointType[i - 1][j] = originalPointType[i - 1][j];
			if((i + 1 <= screenDetails.wCells) && gridPointType[i + 1][j] == BLOCKED_POINT)
				gridPointType[i + 1][j] = originalPointType[i + 1][j];
			if((j - 1 >= 0) && gridPointType[i][j - 1] == BLOCKED_POINT)
				gridPointType[i][j - 1] = originalPointType[i][j - 1];
			if((j + 1 <= screenDetails.hCells) && gridPointType[i][j + 1] == BLOCKED_POINT)
				gridPointType[i][j + 1] = originalPointType[i][j + 1];
			if((i + 1 <= screenDetails.wCells) && (j + 1 <= screenDetails.hCells) && 
					   (gridPointType[i + 1][j + 1] == BLOCKED_POINT))
				gridPointType[i + 1][j + 1] = originalPointType[i + 1][j + 1];	
			if((i - 1 >= 0) && (j + 1 <= screenDetails.hCells) && 
					   (gridPointType[i - 1][j + 1] == BLOCKED_POINT))
				gridPointType[i - 1][j + 1] = originalPointType[i - 1][j + 1];	
			if((i - 1 >= 0) && (j - 1 >= 0) && 
					   (gridPointType[i - 1][j - 1] == BLOCKED_POINT))
				gridPointType[i - 1][j - 1] = originalPointType[i - 1][j - 1];	
			if((i + 1 <= screenDetails.wCells) && (j - 1 >= 0) && 
					   (gridPointType[i + 1][j - 1] == BLOCKED_POINT))
				gridPointType[i + 1][j - 1] = originalPointType[i + 1][j - 1];	
			}
	}
	
	public void checkBubble(int i, int j) {
		if(i == 0 && j == screenDetails.hCells) {
			if(checkPoint(i, j, POINT_DOWN) || checkPoint(i, j, POINT_RIGHT))
				setScore(i, j - 1, 1);
			else
				setScore(i, j - 1, 0);
		}
		else if(i == screenDetails.wCells && j == screenDetails.hCells) {
			if(checkPoint(i, j, POINT_DOWN) || checkPoint(i, j, POINT_LEFT))
				setScore(i - 1, j - 1, 1);
			else
				setScore(i - 1, j - 1, 0);
		}
		else if(i == screenDetails.wCells && j == 1) {
			if(checkPoint(i, j, POINT_UP) || checkPoint(i, j, POINT_LEFT)) 
				setScore(i - 1, j, 1);
			else
				setScore(i - 1, j, 0);
		}
		else if(i == 0 && j == 1) {
			if(checkPoint(i, j, POINT_UP) || checkPoint(i, j, POINT_RIGHT)) 
				setScore(i, j, 1);
			else
				setScore(i, j, 0);
		}
		else if(i == 0) {
			if(checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
				setScore(i, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_DOWN)) {
				setScore(i, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_UP)) {
				setScore(i, j, 1);
			}
			if(checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_UP)) {
				setScore(i, j, 0);
				setScore(i, j - 1, 1);
			}
			if(!checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_UP)) {
				setScore(i, j, 1);
				setScore(i, j - 1, 0);
			}
			if(!checkPoint(i, j, POINT_RIGHT) && !checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_UP)) {
				setScore(i, j, 0);
				setScore(i, j - 1, 0);
			}
		}
		else if(i == screenDetails.wCells) {
			if(checkPoint(i, j, POINT_LEFT)) {
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_DOWN)) {
				setScore(i - 1, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_UP)) {
				setScore(i - 1, j, 1);
			}
			if(checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_UP)) {
				setScore(i - 1, j, 0);
				setScore(i - 1, j - 1, 1);
			}
			if(!checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_UP)) {
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 0);
			}
			if(!checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_UP)) {
				setScore(i - 1, j, 0);
				setScore(i - 1, j - 1, 0);
			}
		}
		else if(j == 1) {
			if(checkPoint(i, j, POINT_UP)) {
				setScore(i, j, 1);
				setScore(i - 1, j, 1);
			}
			if(checkPoint(i, j, POINT_LEFT)) {
				setScore(i - 1, j, 1);
			}
			if(checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
			}
			if(checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i - 1, j, 1);
				setScore(i, j, 0);
			}
			if(!checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT)) {
				setScore(i - 1, j, 0);
				setScore(i, j, 1);
			}
			if(!checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i - 1, j, 0);
				setScore(i, j, 0);
			}
		}
		else if(j == screenDetails.hCells) {
			if(checkPoint(i, j, POINT_DOWN)) {
				setScore(i, j - 1, 1);
				setScore(i - 1, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_LEFT)) {
				setScore(i - 1, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j - 1, 1);
			}
			if(!checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT)) {
				setScore(i - 1, j - 1, 0);
				setScore(i, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 0);
			}
			if(!checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i - 1, j - 1, 0);
				setScore(i, j - 1, 0);
			}
		}
		else {
			if(!checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 0);
				setScore(i - 1, j, 0);
				setScore(i - 1, j - 1, 0);
				setScore(i, j - 1, 0);
			}
			if(!checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 0);
				setScore(i - 1, j, 0);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 0);
				setScore(i, j - 1, 0);
			}
			if(!checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 0);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 0);
			}
			if(!checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
				setScore(i - 1, j, 0);
				setScore(i - 1, j - 1, 0);
				setScore(i, j - 1, 1);
			}
			
			if(!checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 0);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 1);
			}
			if(!checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
				setScore(i - 1, j, 0);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 1);
			}
			if(checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 0);
			}
			if(checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT)) {
				setScore(i, j, 1);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 0);
				setScore(i, j - 1, 1);
			}
			if((checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT))
				|| (!checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT))) {
				setScore(i, j, 1);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 1);
			}
			
			if((checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && !checkPoint(i, j, POINT_RIGHT))
				|| (checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && !checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT))
				|| !(checkPoint(i, j, POINT_UP) && checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT))
				|| (checkPoint(i, j, POINT_UP) && !checkPoint(i, j, POINT_DOWN) && checkPoint(i, j, POINT_LEFT) && checkPoint(i, j, POINT_RIGHT))) {
				setScore(i, j, 1);
				setScore(i - 1, j, 1);
				setScore(i - 1, j - 1, 1);
				setScore(i, j - 1, 1);
			}
		}
	}
	
	public boolean checkPoint(int i, int j, int direction) {
		if(direction == POINT_UP) 
			return gridPointType[i][j] == -gridPointType[i][j + 1];
		else if(direction == POINT_DOWN) 
			return gridPointType[i][j] == -gridPointType[i][j - 1];
		else if(direction == POINT_LEFT) 
			return gridPointType[i][j] == -gridPointType[i - 1][j];
		else if(direction == POINT_RIGHT) 
			return gridPointType[i][j] == -gridPointType[i + 1][j];
		else
			return false;
	}
	
	public void setScore(int i, int j, int scoreT) {
		if(scoreT == 0) {
			if(bubbleType[i][j] == Bubble.BUBBLE_NORMAL) {
				scoreType[i][j] = SCORE_NORMAL_NORMAL;
				bubbleType[i][j] = Bubble.BUBBLE_DROP;
				if(!Settings.mute)
					soundManager.play(SoundManager.BUBBLE_POP1);
			}
			else if(bubbleType[i][j] == Bubble.BUBBLE_BLACK) {
				scoreType[i][j] = SCORE_BLACK_NORMAL;
				bubbleType[i][j] = Bubble.BUBBLE_DROP;
				if(!Settings.mute)
					soundManager.play(SoundManager.BUBBLE_POP1);
			}
			else if(bubbleType[i][j] == Bubble.POWERDOWN_NORMAL_SCORE) {
				activatePower(Bubble.POWERDOWN_NORMAL_SCORE);
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
				if(!Settings.mute)
					soundManager.play(SoundManager.POWERDOWN);
			}
			else if(bubbleType[i][j] == Bubble.POWERDOWN_BLACK_SCORE) {
				int tempBubbleType = bubbleType[i][j];
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
				if(!Settings.mute)
					soundManager.play(SoundManager.POWERDOWN);
				activatePower(tempBubbleType);
			}
			else if(bubbleType[i][j] == Bubble.SUPERPOP_BLACK_NEGATIVE) {
				int tempBubbleType = bubbleType[i][j];
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
				if(!Settings.mute)
					soundManager.play(SoundManager.POWERDOWN);
				activatePower(tempBubbleType);
			}
			else if(bubbleType[i][j] == Bubble.CONVERT_ALL_TO_BLACK) {
				int tempBubbleType = bubbleType[i][j];
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
				if(!Settings.mute)
					soundManager.play(SoundManager.POWERHIT);
				activatePower(tempBubbleType);
			}
		}
		else if(scoreT == 1) {
			if(bubbleType[i][j] == Bubble.BUBBLE_NORMAL) {
				scoreType[i][j] = SCORE_NORMAL_INTERPOP;
				bubbleType[i][j] = Bubble.BUBBLE_DROP;
				if(!Settings.mute)
					soundManager.play(SoundManager.BUBBLE_POP1);
			}
			else if(bubbleType[i][j] == Bubble.BUBBLE_BLACK) {
				scoreType[i][j] = SCORE_BLACK_INTERPOP;
				bubbleType[i][j] = Bubble.BUBBLE_DROP;
				if(!Settings.mute)
					soundManager.play(SoundManager.BUBBLE_POP1);
			}
			else if(bubbleType[i][j] == Bubble.POWERDOWN_NORMAL_SCORE) {
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
			}
			else if(bubbleType[i][j] == Bubble.POWERDOWN_BLACK_SCORE) {
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
			}
			else if(bubbleType[i][j] == Bubble.SUPERPOP_BLACK_NEGATIVE) {
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
			}
			else if(bubbleType[i][j] == Bubble.CONVERT_ALL_TO_BLACK) {
				countOrbs--;
				bubbleType[i][j] = Bubble.BUBBLE_NONE;
			}
		}
		
		if(bubbleType[i][j] == Bubble.POWERUP_BOUNDARY || bubbleType[i][j] == Bubble.POWERUP_NORMAL_SCORE ||
		   bubbleType[i][j] == Bubble.POWERUP_BLACK_SCORE || bubbleType[i][j] ==  Bubble.SUPERPOP_NORMAL ||
		   bubbleType[i][j] == Bubble.SUPERPOP_BLACK || bubbleType[i][j] == Bubble.INVERT_BUBBLE ||
		   bubbleType[i][j] == Bubble.CONVERT_ALL_TO_NORMAL) {
			int tempBubbleType = bubbleType[i][j];
			if(bubbleType[i][j] == Bubble.INVERT_BUBBLE || bubbleType[i][j] == Bubble.CONVERT_ALL_TO_NORMAL) {
				if(!Settings.mute)
					soundManager.play(SoundManager.POWERHIT);
			}
			else {
				if(!Settings.mute)
					soundManager.play(SoundManager.POWERUP);
			}
			countOrbs--;
			bubbleType[i][j] = Bubble.BUBBLE_NONE;
			activatePower(tempBubbleType);
		}
			
		bubbleFade[i][j] = 1.0F;
	}
	
	public boolean checkWave(int i, int j) {
		if(gridPointType[i][j] == DRAWING_POINT_WAVE_DOWN || gridPointType[i][j] == DRAWING_POINT_WAVE_UP 
		 || gridPointType[i + 1][j] == DRAWING_POINT_WAVE_DOWN || gridPointType[i + 1][j] == DRAWING_POINT_WAVE_UP
		 || gridPointType[i + 1][j + 1] == DRAWING_POINT_WAVE_DOWN || gridPointType[i + 1][j + 1] == DRAWING_POINT_WAVE_UP
		 || gridPointType[i][j + 1] == DRAWING_POINT_WAVE_DOWN || gridPointType[i][j + 1] == DRAWING_POINT_WAVE_UP)
			return true;
		else
			return false;			
	}
	
	public void activatePower(int powerType) {
		switch(powerType) {
		case Bubble.POWERUP_BOUNDARY:
			powerUpBoundaryTime = 1;
			powerUpBoundaryTimeC += 5000000000L;
			probIndividual[2] -= 0.02f;
			if(probIndividual[2] < 0) {
				probIndividual[2] = 0;
			}
			boundaryPoints = true;
			break;
		case Bubble.POWERUP_NORMAL_SCORE:
			powerUpDownNormalTime = 1;
			powerUpDownNormalTimeC += 5000000000L;
			probIndividual[3] -= 0.02f;
			if(probIndividual[3] < 0) {
				probIndividual[3] = 0;
			}
			probIndividual[5] = 0f;
			scoreNormalNormal = 20;
			scoreNormalInterpop = 30;
			break;
		case Bubble.POWERUP_BLACK_SCORE:
			powerUpDownBlackTime = 1;
			powerUpDownBlackTimeC += 5000000000L;
			probIndividual[4] -= 0.02f;
			if(probIndividual[4] < 0) {
				probIndividual[4] = 0;
			}
			probIndividual[6] = 0f;
			scoreBlackNormal = -5;
			scoreBlackInterpop = 10;
			break;
		case Bubble.POWERDOWN_NORMAL_SCORE:
			powerUpDownNormalTime = 1;
			powerUpDownNormalTimeC += 5000000000L;
			probIndividual[5] -= 0.02f;
			if(probIndividual[5] < 0) {
				probIndividual[5] = 0;
			}
			probIndividual[3] = 0f;
			scoreNormalNormal = 0;
			scoreNormalInterpop = 10;
			break;
		case Bubble.POWERDOWN_BLACK_SCORE:
			powerUpDownBlackTime = 1;
			powerUpDownBlackTimeC += 5000000000L;
			probIndividual[6] -= 0.02f;
			if(probIndividual[6] < 0) {
				probIndividual[6] = 0;
			}
			probIndividual[4] = 0f;
			scoreBlackNormal = -20;
			scoreBlackInterpop = -10;
			break;
		case Bubble.SUPERPOP_NORMAL:
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 0; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] == Bubble.BUBBLE_NORMAL) {
						score += scoreNormalInterpop;
						bubbleType[i][j] = Bubble.BUBBLE_DROP;
						bubbleFade[i][j] = 1.0f;
						scoreType[i][j] = SCORE_NORMAL_INTERPOP;
					}
				}
			break;
		case Bubble.SUPERPOP_BLACK:
			isSuperpopBlack = false;
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 0; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] == Bubble.BUBBLE_BLACK) {
						score += scoreBlackInterpop;
						bubbleType[i][j] = Bubble.BUBBLE_DROP;
						bubbleFade[i][j] = 1.0f;
						scoreType[i][j] = SCORE_BLACK_INTERPOP;
					}
					else if(bubbleType[i][j] == Bubble.SUPERPOP_BLACK)
						isSuperpopBlack = true;
				}
			break;
		case Bubble.SUPERPOP_BLACK_NEGATIVE:
			isSuperpopBlackNegative = false;
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 0; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] == Bubble.BUBBLE_BLACK) {
						score += scoreBlackNormal;
						bubbleType[i][j] = Bubble.BUBBLE_DROP;
						bubbleFade[i][j] = 1.0f;
						scoreType[i][j] = SCORE_BLACK_NORMAL;
					}
					else if(bubbleType[i][j] == Bubble.SUPERPOP_BLACK_NEGATIVE)
						isSuperpopBlackNegative = true;
				}
			break;
		case Bubble.INVERT_BUBBLE:
			isInvertBubble = false;
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 0; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] == Bubble.BUBBLE_NORMAL || bubbleType[i][j] == Bubble.BUBBLE_BLACK) {
						bubbleType[i][j] = -bubbleType[i][j];
					}
					else if(bubbleType[i][j] == Bubble.INVERT_BUBBLE)
						isInvertBubble = true;
				}
			break;
		case Bubble.CONVERT_ALL_TO_NORMAL:
			isConvertNormal = false;
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 0; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] == Bubble.BUBBLE_BLACK) {
						bubbleType[i][j] = Bubble.BUBBLE_NORMAL;
					}
					else if(bubbleType[i][j] == Bubble.CONVERT_ALL_TO_NORMAL)
						isConvertNormal = true;
				}
			break;
		case Bubble.CONVERT_ALL_TO_BLACK:
			isConvertBlack = false;
			for(int i = 0; i < screenDetails.wCells; i++)
				for(int j = 0; j < screenDetails.hCells; j++) {
					if(bubbleType[i][j] == Bubble.BUBBLE_NORMAL) {
						bubbleType[i][j] = Bubble.BUBBLE_BLACK;
					}
					else if(bubbleType[i][j] == Bubble.CONVERT_ALL_TO_BLACK)
						isConvertBlack = true;
				}
			break;
		}
	}
	
	public void deactivatePower(int powerType) {
		switch(powerType) {
		case Bubble.POWERUP_BOUNDARY:
			powerUpBoundaryTime = 0;
			powerUpBoundaryTimeC = 0;
			probIndividual[2] = probOPUBoundary;
			for(int i = 0; i <= screenDetails.wCells; i++) {
				unblockPoints(i, 1);
				unblockPoints(i, (int) (screenDetails.hCells));
			}
			for(int j = 0; j <= screenDetails.hCells; j++) {
				unblockPoints(0, j);
				unblockPoints((int) screenDetails.wCells, j);
			}
			boundaryPoints = false;
			break;
		case Bubble.POWERUP_NORMAL_SCORE:
		case Bubble.POWERDOWN_NORMAL_SCORE:
			powerUpDownNormalTime = 0;
			powerUpDownNormalTimeC = 0;
			probIndividual[3] = probOPUNormal;
			probIndividual[5] = probOPDNormal;
			scoreNormalNormal = 10;
			scoreNormalInterpop = 20;
			break;
		case Bubble.POWERUP_BLACK_SCORE:
		case Bubble.POWERDOWN_BLACK_SCORE:
			powerUpDownBlackTime = 0;
			powerUpDownBlackTimeC = 0;
			probIndividual[4] = probOPUBlack;
			probIndividual[6] = probOPDBlack;
			scoreBlackNormal = -15;
			scoreBlackInterpop = 0;
			break;
		}
	}
	
	public void normalizeProb() {
		float N = 0;
		
		for(int i = 0; i < probIndividual.length; i++)
			N += probIndividual[i];
		
		for(int i = 0; i < probCumulative.length; i++) {
			probCumulative[i] = 0;
			for(int j = 0; j <= i; j++)
				probCumulative[i] += (probIndividual[j] / N);
		}	
	}
	
	public void setProb() {
		switch(difficultyLevel) {
		case 1:
			probIndividual[0] = 0.775f;					//0 - bubbleNormal
			probIndividual[1] = 0.225f;					//1 - bubbleBlack
			probIndividual[2] = 0f;						//2 - PUBoundary
			probIndividual[3] = 0f;						//3 - PUNormal
			probIndividual[4] = 0f;						//4 - PUBlack
			probIndividual[5] = 0f;						//5 - PDNormal
			probIndividual[6] = 0f;						//6 - PDBlack
			probIndividual[7] = 0f;						//7 - SPNormal
			probIndividual[8] = 0f;						//8 - SPBlack
			probIndividual[9] = 0f;						//9 - SPBlackNegative
			probIndividual[10] = 0f;					//10 - invertBubble
			probIndividual[11] = 0f;					//11 - convertNormal
			probIndividual[12] = 0f;					//12 - convertBlack
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 2:
			probIndividual[0] = 0.7f;					
			probIndividual[1] = 0.225f;					
			probIndividual[2] = 0.075f;					
			probIndividual[3] = 0f;						
			probIndividual[4] = 0f;						
			probIndividual[5] = 0f;					
			probIndividual[6] = 0f;						
			probIndividual[7] = 0f;					
			probIndividual[8] = 0f;						
			probIndividual[9] = 0f;						
			probIndividual[10] = 0f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 3:
			probIndividual[0] = 0.625f;					
			probIndividual[1] = 0.25f;					
			probIndividual[2] = 0.055f;					
			probIndividual[3] = 0.035f;						
			probIndividual[4] = 0.035f;						
			probIndividual[5] = 0f;					
			probIndividual[6] = 0f;						
			probIndividual[7] = 0f;					
			probIndividual[8] = 0f;						
			probIndividual[9] = 0f;						
			probIndividual[10] = 0f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 4:
			probIndividual[0] = 0.6f;					
			probIndividual[1] = 0.275f;					
			probIndividual[2] = 0.04f;					
			probIndividual[3] = 0.0225f;						
			probIndividual[4] = 0.0225f;						
			probIndividual[5] = 0.02f;					
			probIndividual[6] = 0.02f;						
			probIndividual[7] = 0f;					
			probIndividual[8] = 0f;						
			probIndividual[9] = 0f;						
			probIndividual[10] = 0f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 5:
			probIndividual[0] = 0.57f;					
			probIndividual[1] = 0.3f;					
			probIndividual[2] = 0.03f;					
			probIndividual[3] = 0.02f;						
			probIndividual[4] = 0.02f;						
			probIndividual[5] = 0.02f;					
			probIndividual[6] = 0.02f;						
			probIndividual[7] = 0.02f;					
			probIndividual[8] = 0f;						
			probIndividual[9] = 0f;						
			probIndividual[10] = 0f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 6:
			probIndividual[0] = 0.5f;					
			probIndividual[1] = 0.3f;					
			probIndividual[2] = 0.025f;					
			probIndividual[3] = 0.025f;						
			probIndividual[4] = 0.025f;						
			probIndividual[5] = 0.025f;					
			probIndividual[6] = 0.025f;						
			probIndividual[7] = 0.025f;					
			probIndividual[8] = 0.025f;						
			probIndividual[9] = 0f;						
			probIndividual[10] = 0f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 7:
			probIndividual[0] = 0.45f;					
			probIndividual[1] = 0.33f;					
			probIndividual[2] = 0.0275f;					
			probIndividual[3] = 0.0275f;						
			probIndividual[4] = 0.0275f;						
			probIndividual[5] = 0.0275f;					
			probIndividual[6] = 0.0275f;						
			probIndividual[7] = 0.0275f;					
			probIndividual[8] = 0.0275f;						
			probIndividual[9] = 0.0275f;						
			probIndividual[10] = 0f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 8:
			probIndividual[0] = 0.4025f;					
			probIndividual[1] = 0.35f;					
			probIndividual[2] = 0.0275f;					
			probIndividual[3] = 0.0275f;						
			probIndividual[4] = 0.0275f;						
			probIndividual[5] = 0.0275f;					
			probIndividual[6] = 0.0275f;						
			probIndividual[7] = 0.0275f;					
			probIndividual[8] = 0.0275f;						
			probIndividual[9] = 0.0275f;						
			probIndividual[10] = 0.0275f;					
			probIndividual[11] = 0f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 9:
			probIndividual[0] = 0.36f;					
			probIndividual[1] = 0.34f;					
			probIndividual[2] = 0.03f;					
			probIndividual[3] = 0.03f;						
			probIndividual[4] = 0.03f;						
			probIndividual[5] = 0.03f;					
			probIndividual[6] = 0.03f;						
			probIndividual[7] = 0.035f;					
			probIndividual[8] = 0.03f;						
			probIndividual[9] = 0.03f;						
			probIndividual[10] = 0.03f;					
			probIndividual[11] = 0.03f;					
			probIndividual[12] = 0f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		case 10:
			probIndividual[0] = 0.33f;					
			probIndividual[1] = 0.37f;					
			probIndividual[2] = 0.027f;					
			probIndividual[3] = 0.027f;						
			probIndividual[4] = 0.027f;						
			probIndividual[5] = 0.027f;					
			probIndividual[6] = 0.027f;						
			probIndividual[7] = 0.027f;					
			probIndividual[8] = 0.027f;						
			probIndividual[9] = 0.027f;						
			probIndividual[10] = 0.027f;					
			probIndividual[11] = 0.027f;					
			probIndividual[12] = 0.027f;					
			probOPUBoundary = probIndividual[2];
			probOPUNormal = probIndividual[3];
			probOPUBlack = probIndividual[4];
			probOPDNormal = probIndividual[5];
			probOPDNormal = probIndividual[6];
			break;
		}
	}
	
	public void setProbNoOrbs() {
		for(int i = 2; i < probIndividual.length; i++)
			probIndividual[i] = 0;						
	}
	
	public boolean setScoreAndTime() {
		if(utilities.checkTouch(touchedX, touchedY, rectGame1M)) {
			gameMode = GAME_TIME_1M;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGame2M)) {
			gameMode = GAME_TIME_2M;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGame5M)) {
			gameMode = GAME_TIME_3M;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGame1000S)) {
			gameMode = GAME_SCORE_500;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGame2000S)) {
			gameMode = GAME_SCORE_1000;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGame5000S)) {
			gameMode = GAME_SCORE_2000;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGameNoOrbs)) {
			gameMode = GAME_NO_ORBS;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGameNoLimits)) {
			gameMode = GAME_NO_LIMITS;
			return true;
		}
		else if(utilities.checkTouch(touchedX, touchedY, rectGameScorePos)) {
			gameMode = GAME_SCORE_POSITIVE;
			return true;
		}
		return false;
			
	}
	
	
}