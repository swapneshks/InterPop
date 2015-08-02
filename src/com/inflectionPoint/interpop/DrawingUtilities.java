package com.inflectionPoint.interpop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLUtils;

public class DrawingUtilities implements TouchUtilities{
	
	static final int TEXTURE_PAWSSENH = 1000;
	static final int TEXTURE_HOTMETAL = 1001;
	static final int TEXTURE_INFLECTIONPOINT = 1002;
	static final int TEXTURE_BACKGROUND = 1003;
	static final int TEXTURE_NEWGAME = 1004;
	static final int TEXTURE_SETTINGS = 1005;
	static final int TEXTURE_HIGHSCORES = 1006;
	static final int TEXTURE_HELP =	1007;
	static final int TEXTURE_BACKBUTTON = 1008;
	static final int TEXTURE_FWDBUTTON = 1009;
	static final int TEXTURE_GAMEBUTTON = 1010;
	static final int TEXTURE_PAUSEBUTTON = 1011;
	static final int TEXTURE_PLAYBUTTON = 1012;
	static final int TEXTURE_RESTARTBUTTON = 1013;
	static final int TEXTURE_GAMEBACKBUTTON = 1014;
	static final int TEXTURE_HEADSDOWN = 1015;
	static final int TEXTURE_GAMEMODE = 1016;
	static final int TEXTURE_RESTARTPRESSED = 1017;
	static final int TEXTURE_BACKPRESSED = 1018;
	static final int TEXTURE_GAMEOVER = 1019;
	static final int TEXTURE_JELLYFISH = 1020;
	static final int TEXTURE_ORBUNLOCKED = 1021;
	static final int TEXTURE_TICK = 1022;
	static final int TEXTURE_CROSS = 1023;
	static final int TEXTURE_VIEWORB = 1024;
	static final int TEXTURE_ORBAVAILABLE = 1025;
	static final int TEXTURE_SOUNDON = 1026;
	static final int TEXTURE_SOUNDOFF = 1027;
	static final int TEXTURE_RESET = 1028;
	static final int TEXTURE_RESETPRESSED = 1029;
	static final int TEXTURE_CREDITS = 1030;
	static final int TEXTURE_CREDITSBUTTON = 1040;
	
	private ScreenDetails screenDetails;
	private FileManager fileManager;
	private float color[];
	
	private final int strideSize = 8 * 4;
	
	private ByteBuffer byteBufferTexture;
	private FloatBuffer floatBufferTexture;
	
	private ByteBuffer byteBufferText;
	private FloatBuffer floatBufferText;
	
	private ByteBuffer byteBufferIndices;
	private ShortBuffer shortBuffer;
	
	private int POTWidth;
	private int POTHeight;
	private float widthRatio;
	private float heightRatio;
	
	private int textureIds[];
	private int textureId;
	
	private Bitmap textBitmap;
	private Canvas canvas;
	private static Bitmap lPP;
	private static Bitmap hotMetal;
	private static Bitmap inflectionPoint;
	private Bitmap bG;
	private static Bitmap newGame;
	private static Bitmap settings;
	private static Bitmap highscores;
	private static Bitmap help;
	private static Bitmap backButton;
	private static Bitmap fwdButton;
	private static Bitmap gameButton;
	private static Bitmap pauseButton;
	private static Bitmap playButton;
	private static Bitmap restartButton;
	private static Bitmap gameBackButton;
	private static Bitmap bubbleNormal;
	private static Bitmap bubbleBlack;
	private static Bitmap PUBoundary;
	private static Bitmap PUBubbleNormal;
	private static Bitmap PUBubbleBlack;
	private static Bitmap PDBubbleNormal;
	private static Bitmap PDBubbleBlack;
	private static Bitmap SPNormal;
	private static Bitmap SPBlack;
	private static Bitmap SPBlackNegative;
	private static Bitmap invertBubble;
	private static Bitmap convertNormal;
	private static Bitmap convertBlack;
	private static Bitmap bubbleDrops;
	private static Bitmap headsDownDisplay;
	private static Bitmap gameMode;
	private static Bitmap restartPressed;
	private static Bitmap backPressed;
	private static Bitmap gameOver;
	private static Bitmap jellyfish;
	private static Bitmap orbUnlocked;
	private static Bitmap tick;
	private static Bitmap cross;
	private static Bitmap viewOrb;
	private static Bitmap orbsAvailable;
	private static Bitmap soundOn;
	private static Bitmap soundOff;
	private static Bitmap resetBitmap;
	private static Bitmap resetPressed;
	private static Bitmap credits;
	private static Bitmap creditsButton;
	
	public DrawingUtilities (Context context, ScreenDetails screenDetails, String background) {
		this.screenDetails = screenDetails;
		this.fileManager = new FileManager(context);
		if(background == null) 
			background = "background_main_512x1024.png";
		this.color = new float[4];
		
		this.byteBufferTexture = ByteBuffer.allocateDirect(8 * 4 * 4);
		this.byteBufferTexture.order(ByteOrder.nativeOrder());
		this.floatBufferTexture = this.byteBufferTexture.asFloatBuffer();
		
		this.byteBufferIndices = ByteBuffer.allocateDirect(6 * 2);
		this.byteBufferIndices.order(ByteOrder.nativeOrder());
		this.shortBuffer = this.byteBufferIndices.asShortBuffer();
		this.shortBuffer.put(new short[] { 0, 1, 2, 
				  2, 3, 0 });
		this.shortBuffer.flip();
		
		this.byteBufferText = ByteBuffer.allocateDirect(4 * 4 * 4);
	    this.byteBufferText.order(ByteOrder.nativeOrder());
	    this.floatBufferText = byteBufferText.asFloatBuffer();
		this.floatBufferText.clear();
	    this.floatBufferText.put(new float[] { 0 , 0, 0f, 1.0f,
	    							  screenDetails.metrics.widthPixels, 0, 1.0f, 1.0f,
	    							  screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels, 1.0f, 0f,
	    							  0, screenDetails.metrics.heightPixels, 0f, 0f });
	    this.floatBufferText.flip();
		
		this.POTWidth = (int) Math.pow(2, Math.ceil(Math.log(screenDetails.metrics.widthPixels)/Math.log(2)));
		this.POTHeight = (int) Math.pow(2, Math.ceil(Math.log(screenDetails.metrics.heightPixels)/Math.log(2)));
		this.widthRatio = POTWidth / (float) this.screenDetails.metrics.widthPixels;
		this.heightRatio = POTHeight / (float) this.screenDetails.metrics.heightPixels;
		
		DrawingUtilities.lPP = this.loadBitmap("pawssenh_512x1024.png", Config.ARGB_8888);
		DrawingUtilities.hotMetal = this.loadBitmap("hotmetal_512x1024.png", Config.ARGB_8888);
		DrawingUtilities.inflectionPoint = this.loadBitmap("inflectionPoint_512x1024.png", Config.ARGB_8888);
		this.bG = this.loadBitmap(background, Config.ARGB_8888);
		DrawingUtilities.newGame = this.loadBitmap("newgame.png", Config.ARGB_8888);
		DrawingUtilities.settings = this.loadBitmap("settings.png", Config.ARGB_8888);
		DrawingUtilities.highscores = this.loadBitmap("highscores.png", Config.ARGB_8888);
		DrawingUtilities.help = this.loadBitmap("help.png", Config.ARGB_8888);
		DrawingUtilities.backButton = this.loadBitmap("helpback_128x128.png", Config.ARGB_8888);
		DrawingUtilities.fwdButton = this.loadBitmap("helpfwd_128x128.png", Config.ARGB_8888);
		DrawingUtilities.gameButton = this.loadBitmap("gamebutton_512x256.png", Config.ARGB_8888);
		DrawingUtilities.pauseButton = this.loadBitmap("pause_128x128.png", Config.ARGB_8888);
		DrawingUtilities.playButton = this.loadBitmap("play_128x128.png", Config.ARGB_8888);
		DrawingUtilities.restartButton = this.loadBitmap("restart_128x128.png", Config.ARGB_8888);
		DrawingUtilities.gameBackButton = this.loadBitmap("back_128x128.png", Config.ARGB_8888);
		DrawingUtilities.bubbleNormal = this.loadBitmap("bubble_normal_1.png", Config.ARGB_8888);
		DrawingUtilities.bubbleBlack = this.loadBitmap("bubble_black.png", Config.ARGB_8888);
		DrawingUtilities.PUBoundary = this.loadBitmap("pubnd_128x128.png", Config.ARGB_8888);
		DrawingUtilities.PUBubbleNormal = this.loadBitmap("pun_128x128.png", Config.ARGB_8888);
		DrawingUtilities.PUBubbleBlack  = this.loadBitmap("pub_128x128.png", Config.ARGB_8888);
		DrawingUtilities.PDBubbleNormal = this.loadBitmap("pdn_128x128.png", Config.ARGB_8888);
		DrawingUtilities.PDBubbleBlack = this.loadBitmap("pdb_128x128.png", Config.ARGB_8888);
		DrawingUtilities.SPNormal = this.loadBitmap("sn_128x128.png", Config.ARGB_8888);
		DrawingUtilities.SPBlack = this.loadBitmap("sb_128x128.png", Config.ARGB_8888);
		DrawingUtilities.SPBlackNegative = this.loadBitmap("sbn_128x128.png", Config.ARGB_8888);
		DrawingUtilities.invertBubble = this.loadBitmap("invert_128x128.png", Config.ARGB_8888);
		DrawingUtilities.convertNormal = this.loadBitmap("cn_128x128.png", Config.ARGB_8888);
		DrawingUtilities.convertBlack = this.loadBitmap("cb_128x128.png", Config.ARGB_8888);
		DrawingUtilities.bubbleDrops = this.loadBitmap("drops_256x256.png", Config.ARGB_8888);
		DrawingUtilities.headsDownDisplay = this.loadBitmap("heads_down_display_512x256.png", Config.ARGB_8888);
		DrawingUtilities.gameMode = this.loadBitmap("game_mode_512x1024.png", Config.ARGB_8888);
		DrawingUtilities.restartPressed = this.loadBitmap("restart_yes_no.png", Config.ARGB_8888);
		DrawingUtilities.backPressed = this.loadBitmap("back_yes_no.png", Config.ARGB_8888);
		DrawingUtilities.gameOver = this.loadBitmap("game_over.png", Config.ARGB_8888);
		DrawingUtilities.jellyfish = this.loadBitmap("jellyfish_512x512.png", Config.ARGB_8888);
		DrawingUtilities.orbUnlocked = this.loadBitmap("orbUnlocked.png", Config.ARGB_8888);
		DrawingUtilities.tick = this.loadBitmap("tick.png", Config.ARGB_8888);
		DrawingUtilities.cross = this.loadBitmap("cross.png", Config.ARGB_8888);
		DrawingUtilities.viewOrb = this.loadBitmap("vieworb.png", Config.ARGB_8888);
		DrawingUtilities.orbsAvailable = this.loadBitmap("orbs.png", Config.ARGB_8888);
		DrawingUtilities.soundOn = this.loadBitmap("soundon.png", Config.ARGB_8888);
		DrawingUtilities.soundOff = this.loadBitmap("soundoff.png", Config.ARGB_8888);
		DrawingUtilities.resetBitmap = this.loadBitmap("reset.png", Config.ARGB_8888);
		DrawingUtilities.resetPressed = this.loadBitmap("reset_yes_no.png", Config.ARGB_8888);
		DrawingUtilities.credits = this.loadBitmap("credits.png", Config.ARGB_8888);
		DrawingUtilities.creditsButton = this.loadBitmap("creditsbutton.png", Config.ARGB_8888);
	}
	
	public void drawGrid(GL10 gl) {
			
		ByteBuffer buffer = ByteBuffer.allocateDirect(6 * 4);
		buffer.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = buffer.asFloatBuffer();
			
		float[] endPoints = new float [4];

		double extraHeight = screenDetails.extraHeight;
		
		for (int i = 1; i < screenDetails.wCells; i++) {
				endPoints[0] = (float) (i * screenDetails.cellDim + screenDetails.margin / 2);
				endPoints[1] = (float) (screenDetails.cellDim + extraHeight);
				endPoints[2] = (float) (i * screenDetails.cellDim + screenDetails.margin / 2);
				endPoints[3] = (float) (screenDetails.metrics.heightPixels);
				floatBuffer.clear();
				floatBuffer.put(endPoints);
				floatBuffer.flip();
				
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				
				gl.glVertexPointer(2, GL10.GL_FLOAT, 0, floatBuffer);
				gl.glDrawArrays(GL10.GL_LINES, 0, 2);
				
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				
	    }
		
		for (int j = 1; j < screenDetails.hCells; j++) {
				endPoints[0] = 0F;
				endPoints[1] = (float) ((j * screenDetails.cellDim) + screenDetails.margin / 2 + extraHeight);
				endPoints[2] = (float) (screenDetails.metrics.widthPixels);
				endPoints[3] = (float) ((j * screenDetails.cellDim) + screenDetails.margin / 2 + extraHeight);
				floatBuffer.clear();
				floatBuffer.put(endPoints);
				floatBuffer.flip();
				
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
				gl.glVertexPointer(2, GL10.GL_FLOAT, 0, floatBuffer);
				gl.glDrawArrays(GL10.GL_LINES, 0, 2);
				
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}	
	}	
	
	public void drawGridPoint(GL10 gl, int i, int j, int pointType, int originalPointType, float radius) {
		if(pointType == GameRenderer.TOUCHED_POINT_WAVE_UP) {
			color[0] = 0.9f;
			color[1] = 0.05f;
			color[2] = 0.05f;
			color[3] = 1f;
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 2, InscribedPolygon.MODE_FILL, 1, color);
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		}
		else if(pointType == GameRenderer.TOUCHED_POINT_WAVE_DOWN) {
			color[0] = 0f;
			color[1] = 0f;
			color[2] = 0f;
			color[3] = 1f;
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 2, InscribedPolygon.MODE_FILL, 1, color);
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		}
		else if(pointType == GameRenderer.UNTOUCHED_POINT || pointType == GameRenderer.BOUNDARY_POINT_ON) {
			color[0] = color[1] = color[2] = color[3] = 1f;
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 2, InscribedPolygon.MODE_FILL, 1, color);
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		}
		else if(pointType == GameRenderer.DRAWING_POINT_WAVE_UP) {
			color[0] = 0.9f;
			color[1] = 0.05f;
			color[2] = 0.05f;
			if(originalPointType == GameRenderer.BOUNDARY_POINT_ON) {
				color[3] = (float) (1 - Math.pow(radius / screenDetails.cellDim, 3));
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 
						radius, InscribedPolygon.MODE_BOUNDARY, 2, color);
				color[3] = 1f;
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_FILL, 1, color);
			
			}
			else {
				color[3] = (float) (1 - Math.pow(radius / screenDetails.cellDim, 4));
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 
						radius, InscribedPolygon.MODE_BOUNDARY, 2, color);
				color[3] = 1f;
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_FILL, 1, color);
			}
		}
		else if(pointType == GameRenderer.DRAWING_POINT_WAVE_DOWN) {
			color[0] = 0f;
			color[1] = 0f;
			color[2] = 0f;
			color[3] = 1f;
			if(originalPointType == GameRenderer.BOUNDARY_POINT_ON) {
				color[3] = (float) (1 - Math.pow(radius / screenDetails.cellDim, 5));
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 
						radius, InscribedPolygon.MODE_BOUNDARY, 2, color);
				color[3] = 1f;
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_FILL, 1, color);
			}
			else {
				color[3] = (float) (1 - Math.pow(radius / screenDetails.cellDim, 5));
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 
						radius, InscribedPolygon.MODE_BOUNDARY, 2, color);
				color[3] = 1f;
				drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
						j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_FILL, 1, color);
			}
		}
		else if(pointType == GameRenderer.BLOCKED_POINT){
			color[0] = 1f;
			color[1] = 0.5f;
			color[2] = 0.0f;
			color[3] = 1f;
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 2, InscribedPolygon.MODE_FILL, 1, color);
			drawCircularElement(gl, i * screenDetails.cellDim + screenDetails.margin / 2,
					j * screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight, 6, InscribedPolygon.MODE_BOUNDARY, 1, color);
		
		}
	}
	
	public void drawTextToBitmap(String string, float stringX, float stringY, Paint paint) {
		canvas.drawText(string, stringX * widthRatio, stringY * heightRatio, paint);
	}
	
	public void drawText(GL10 gl){
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureId );
	
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR );
		   
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE );
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );
	
		GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, textBitmap, 0 );

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		floatBufferText.position(0);
		gl.glVertexPointer(2, GL10.GL_FLOAT, strideSize / 2, floatBufferText);
		floatBufferText.position(2);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, strideSize / 2, floatBufferText);
		
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, shortBuffer);		
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	public void drawCircularElement(GL10 gl, float x, float y, float r, int mode, int width, float color[]){
		InscribedPolygon circle = new InscribedPolygon(x, y, r);
		circle.drawInscribedPolygon(gl, 30, mode, width, color);
	}
	
	@Override
	public boolean touchedRegionCheck(PointF touchedPoint, PointF targetPoint, float bounds) {
		return (touchedPoint.x >= targetPoint.x - bounds && touchedPoint.x <= targetPoint.x + bounds &&
			    touchedPoint.y >= targetPoint.y - bounds && touchedPoint.y <= targetPoint.y + bounds);
	}
	
	public boolean touchedRegionCheckRect(PointF touchedPoint, PointF targetPoint, float boundsX, float boundsY) {
		return (touchedPoint.x >= targetPoint.x - boundsX && touchedPoint.x <= targetPoint.x + boundsX &&
			    touchedPoint.y >= targetPoint.y - boundsY && touchedPoint.y <= targetPoint.y + boundsY);
	}
	
	@Override
	public float setXToGrid(float x, boolean boundaryPoints) {
		if(boundaryPoints) {
			if(x < screenDetails.margin / 2) {
				return (float) screenDetails.margin / 2;
			}
			else if (x > screenDetails.metrics.widthPixels - screenDetails.margin / 2) {
				return (float) screenDetails.metrics.widthPixels - screenDetails.margin / 2;
			}
			else {
				return (float) (screenDetails.cellDim * Math.ceil(x / screenDetails.cellDim -  0.5) 
										+ screenDetails.margin / 2);
			}
		}
		else {
			if(x < screenDetails.cellDim + screenDetails.margin / 2) {
				return (float) screenDetails.cellDim + screenDetails.margin / 2;
			}
			else if (x > screenDetails.cellDim * (screenDetails.wCells - 1) + screenDetails.margin / 2) {
				return (float) screenDetails.cellDim * (screenDetails.wCells - 1) + screenDetails.margin / 2;
			}
			else {
				return (float) (screenDetails.cellDim * Math.ceil(x / screenDetails.cellDim -  0.5) 
										+ screenDetails.margin / 2);
			}
		}
	}

	@Override
	public float setYToGrid(float y, boolean boundaryPoints) {
		if(boundaryPoints) {
			if(y < screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight &&
						y > screenDetails.cellDim + screenDetails.extraHeight) {
				return (float) screenDetails.cellDim + screenDetails.margin / 2 + screenDetails.extraHeight;
			}
			else if (y < screenDetails.cellDim + screenDetails.extraHeight){
				return y;
			}
			else if (y > screenDetails.metrics.heightPixels - screenDetails.margin / 2) {
				return screenDetails.metrics.heightPixels - screenDetails.margin / 2;
			}
			else {
				return (float) (screenDetails.cellDim * Math.ceil((y - screenDetails.extraHeight - screenDetails.margin / 2) / screenDetails.cellDim -  0.5) 
										+ screenDetails.extraHeight + screenDetails.margin / 2);
			}
		}
		else {
			if(y < screenDetails.cellDim * 2 + screenDetails.margin / 2 + screenDetails.extraHeight &&
						y > screenDetails.cellDim + screenDetails.extraHeight) {
				return (float) screenDetails.cellDim * 2 + screenDetails.margin / 2 + screenDetails.extraHeight;
			}
			else if(y < screenDetails.cellDim + screenDetails.extraHeight) {
				return y;
			}
		//	else if (y > screenDetails.cellDim * (screenDetails.hCells - 1) + screenDetails.extraHeight + screenDetails.margin / 2){
		//		return screenDetails.cellDim * (screenDetails.hCells - 1) + screenDetails.extraHeight + screenDetails.margin / 2;
		//	}
			else if (y > screenDetails.metrics.heightPixels - screenDetails.margin / 2 - screenDetails.cellDim) {
				return (float) screenDetails.metrics.heightPixels - screenDetails.margin / 2 - screenDetails.cellDim;
			} 
			else {
				return (float) (screenDetails.cellDim * Math.ceil((y - screenDetails.extraHeight - screenDetails.margin / 2) / screenDetails.cellDim -  0.5) 
										+ screenDetails.extraHeight + screenDetails.margin / 2);
			}
		}
	}
	
	public Bitmap loadBitmap(String fileName, Config config) {
		
		InputStream in = null;
		try {
			in = fileManager.openAssets(fileName);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = config;
			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
			return bitmap;
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load texture '" + fileName + "'", e);
		} finally {
			if(in != null)
				try {in.close();} catch (IOException e) {}
		}
	}
	
	public void drawTexture(GL10 gl, RectF rect, int texture, float alpha) {
		floatBufferTexture.clear();
	    floatBufferTexture.put(new float[] { rect.left, rect.bottom, 1, 1, 1, alpha, 0f, 1.0f,
	    							  		 rect.right, rect.bottom, 1, 1, 1, alpha, 1.0f, 1.0f,
	    							  		 rect.right, rect.top, 1, 1, 1, alpha, 1.0f, 0f,
	    							  		 rect.left, rect.top, 1, 1, 1, alpha, 0f, 0f });
	    floatBufferTexture.flip();
	    
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureId );
		
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR );
		   
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE );
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );
		
		if(texture == DrawingUtilities.TEXTURE_PAWSSENH)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, lPP, 0 );
		else if(texture == DrawingUtilities.TEXTURE_INFLECTIONPOINT)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, inflectionPoint, 0 );		
		else if(texture == DrawingUtilities.TEXTURE_HOTMETAL)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, hotMetal, 0 );		
		else if(texture == DrawingUtilities.TEXTURE_BACKGROUND)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bG, 0 );
		else if(texture == DrawingUtilities.TEXTURE_NEWGAME)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, newGame, 0 );
		else if(texture == DrawingUtilities.TEXTURE_SETTINGS)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, settings, 0 );
		else if(texture == DrawingUtilities.TEXTURE_HIGHSCORES)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, highscores, 0 );
		else if(texture == DrawingUtilities.TEXTURE_HELP)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, help, 0 );
		else if(texture == DrawingUtilities.TEXTURE_BACKBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, backButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_FWDBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, fwdButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_GAMEBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, gameButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_PAUSEBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, pauseButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_PLAYBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, playButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_RESTARTBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, restartButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_GAMEBACKBUTTON)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, gameBackButton, 0 );
		else if(texture == DrawingUtilities.TEXTURE_HEADSDOWN)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, headsDownDisplay, 0 );
		else if(texture == DrawingUtilities.TEXTURE_GAMEMODE)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, gameMode, 0 );
		else if(texture == DrawingUtilities.TEXTURE_RESTARTPRESSED)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, restartPressed, 0 );
		else if(texture == DrawingUtilities.TEXTURE_BACKPRESSED)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, backPressed, 0 );
		else if(texture == DrawingUtilities.TEXTURE_GAMEOVER)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, gameOver, 0 );
		else if(texture == DrawingUtilities.TEXTURE_JELLYFISH)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, jellyfish, 0 );
		else if(texture == DrawingUtilities.TEXTURE_ORBUNLOCKED)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, orbUnlocked, 0 );
		else if(texture == DrawingUtilities.TEXTURE_TICK)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, tick, 0 );
		else if(texture == DrawingUtilities.TEXTURE_CROSS)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, cross, 0 );
		else if(texture == DrawingUtilities.TEXTURE_VIEWORB)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, viewOrb, 0 );
		else if(texture == DrawingUtilities.TEXTURE_ORBAVAILABLE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, orbsAvailable, 0 );
		else if(texture == DrawingUtilities.TEXTURE_SOUNDON)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, soundOn, 0 );
		else if(texture == DrawingUtilities.TEXTURE_SOUNDOFF)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, soundOff, 0 );
		else if(texture == DrawingUtilities.TEXTURE_RESET)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, resetBitmap, 0 );
		else if(texture == DrawingUtilities.TEXTURE_RESETPRESSED)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, resetPressed, 0 );
		else if(texture == DrawingUtilities.TEXTURE_CREDITS)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, credits, 0 );
		else if(texture == DrawingUtilities.TEXTURE_CREDITSBUTTON)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, creditsButton, 0 );
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		floatBufferTexture.position(0);
		gl.glVertexPointer(2, GL10.GL_FLOAT, strideSize, floatBufferTexture);
		floatBufferTexture.position(2);
		gl.glColorPointer(4, GL10.GL_FLOAT, strideSize, floatBufferTexture);
		floatBufferTexture.position(6);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, strideSize, floatBufferTexture);
		
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, shortBuffer);	
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
	
	public void drawBubble(GL10 gl, float x, float y, float radius, int bubbleType, float alpha) {
		floatBufferTexture.clear();
	    floatBufferTexture.put(new float[] { x - radius - 15.0F * radius / 600, y - radius - 15.0F * radius / 600, 1, 1, 1, alpha, 0f, 1.0f,
	    							  x + radius + 15.0F * radius / 600, y - radius - 15.0F * radius / 600, 1, 1, 1, alpha, 1.0f, 1.0f,
	    							  x + radius + 15.0F * radius / 600, y + radius + 15.0F * radius / 600, 1, 1, 1, alpha, 1.0f, 0f,
	    							  x - radius - 15.0F * radius / 600, y + radius + 15.0F * radius / 600, 1, 1, 1, alpha, 0f, 0f });
	    floatBufferTexture.flip();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureId );
		
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR );
		   
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE );
		gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );
		
		if(bubbleType == Bubble.BUBBLE_NORMAL)
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bubbleNormal, 0 );
	    else if(bubbleType == Bubble.BUBBLE_BLACK)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bubbleBlack, 0 );
	    else if(bubbleType == Bubble.POWERUP_BOUNDARY)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, PUBoundary, 0 );
	    else if(bubbleType == Bubble.POWERUP_NORMAL_SCORE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, PUBubbleNormal, 0 );
	    else if(bubbleType == Bubble.POWERUP_BLACK_SCORE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, PUBubbleBlack, 0 );
	    else if(bubbleType == Bubble.POWERDOWN_NORMAL_SCORE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, PDBubbleNormal, 0 );
	    else if(bubbleType == Bubble.POWERDOWN_BLACK_SCORE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, PDBubbleBlack, 0 );
	    else if(bubbleType == Bubble.SUPERPOP_NORMAL)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, SPNormal, 0 );
	    else if(bubbleType == Bubble.SUPERPOP_BLACK)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, SPBlack, 0 );
	    else if(bubbleType == Bubble.SUPERPOP_BLACK_NEGATIVE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, SPBlackNegative, 0 );
	    else if(bubbleType == Bubble.INVERT_BUBBLE)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, invertBubble, 0 );
	    else if(bubbleType == Bubble.CONVERT_ALL_TO_NORMAL)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, convertNormal, 0 );
	    else if(bubbleType == Bubble.CONVERT_ALL_TO_BLACK)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, convertBlack, 0 );		
	    else if(bubbleType == Bubble.BUBBLE_DROP)
	    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bubbleDrops, 0 );

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		floatBufferTexture.position(0);
		gl.glVertexPointer(2, GL10.GL_FLOAT, strideSize, floatBufferTexture);
		floatBufferTexture.position(2);
		gl.glColorPointer(4, GL10.GL_FLOAT, strideSize, floatBufferTexture);
		floatBufferTexture.position(6);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, strideSize, floatBufferTexture);
		
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, shortBuffer);	
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}

	@Override
	public int getIndexX(float x) {
		return (int) ((x - screenDetails.margin / 2) / screenDetails.cellDim) ;
	}

	@Override
	public int getIndexY(float y) {
		return (int) ((y - screenDetails.margin / 2 - screenDetails.extraHeight) / screenDetails.cellDim) ;
	}
	
	public int getPointType(int i, int j) {
		if(i == 0)
			return InscribedPolygon.SEMICIRCLE_RIGHT;
		else if(i == screenDetails.wCells)
			return InscribedPolygon.SEMICIRCLE_LEFT;
		else if(j == 1)
			return InscribedPolygon.SEMICIRCLE_UP;
		else if(j == screenDetails.hCells)
			return InscribedPolygon.SEMICIRCLE_DOWN;
		else
			return 0;
	}
	
	public void loadTextureId(GL10 gl) {
		textureIds = new int[1];
		gl.glGenTextures(1, textureIds, 0);
		textureId = textureIds[0];
	}
	
	public void createBitmap() {
		if(textBitmap == null || textBitmap.isRecycled()) {
			textBitmap = Bitmap.createBitmap(POTWidth, POTHeight, Config.ARGB_8888);
			canvas = new Canvas(textBitmap);
			canvas.drawARGB(0, 0, 0, 0);
		}
	}
	
	public void recycleBitmap() {
		textBitmap.recycle();
	}
	
	public void setWidthRatio(float width) {
		widthRatio = width / screenDetails.metrics.widthPixels;
	}
	
	public void setHeightRatio(float height) {
		heightRatio = height / screenDetails.metrics.heightPixels;
	}
	
	public boolean checkTouch(float touchedX, float touchedY, RectF rect) {
		return touchedRegionCheck(new PointF(touchedX, touchedY), 
								  new PointF(rect.centerX(), rect.centerY()), 
								  rect.width() / 2);
	}

}

