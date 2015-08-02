package com.inflectionPoint.interpop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;

public class StartingRenderer implements GLSurfaceView.Renderer{
	
	private Context context;
	public ScreenDetails screenDetails;
	private DrawingUtilities utilities;
	private SoundManager soundManager;
	private MediaPlayer cracklePlayer;
	private MediaPlayer wavePlayer;
	
	private boolean isGamesSoundPlayed;
	private boolean isCompleted;
	private int phase;
	
	private final String inflection = "INFLECTION";
	private final String point = "POINT";
	private float amplitude;
	
	private int maxBubbles;
	private int flag;
	private float rndRadius;
	private float rndX;
	
	private ArrayList<Bubble> bubbles;
	private float color[];
	private Typeface font;
	private Paint textPaint;
	private Rect textBounds;
	
	private ArrayList<Float> touchRadius;
	private long touchTime;
	private RectF rectScreen;
	private RectF rectJellyfish;
	
	private Animator animator;
	private Animator textAnimator;
	private float alpha;
	private float textAlpha;
	private float textDisplacement;
	
	private Random rSeed;
	private Random random;

	private Random randomPop;
	private boolean hasPopped;
	
	private long startTime;
	private long elapsedTime;
	private long phasePopTime;
	private long phaseTime;
	
	public StartingRenderer(Context context, StartingView startingView, ScreenDetails screenDetails) {
		
		this.context = context;
		this.screenDetails = screenDetails;
		this.utilities = new DrawingUtilities(context, screenDetails, "background_main_512x1024.png");
		this.soundManager = new SoundManager(context, 20);
		this.cracklePlayer = new MediaPlayer();
		this.wavePlayer = new MediaPlayer();
		
		this.isGamesSoundPlayed = false;
		
		this.isCompleted = false;
		this.phase = 1;
		
		this.amplitude = 0;
		
		this.maxBubbles = 60;
		this.flag = -1;
		this.rndRadius = -1;
		this.rndX = -1;
		
		this.bubbles = new ArrayList<Bubble>();
			
		this.color = new float[4];
		color[0] = color[1] = color[2] = color[3] = 1f;
		this.textPaint = new Paint();
		this.textPaint.setTextAlign(Align.CENTER);
		this.textBounds = new Rect(0, 0, 0, 0);
		
		this.rectScreen = new RectF(0, screenDetails.metrics.heightPixels, screenDetails.metrics.widthPixels, 0);
		this.rectJellyfish = new RectF(screenDetails.metrics.widthPixels - 3 * screenDetails.cellDim, 
									   3 * screenDetails.cellDim, 
									   screenDetails.metrics.widthPixels, 
									   0);
		
		this.touchRadius = new ArrayList<Float>();
		this.touchRadius.clear();
		this.touchTime = 0;
		
		this.animator = new Animator();
		this.animator.setAlpha(0);
		this.animator.setStartTime(0);
		this.textAnimator = new Animator();
		this.textAnimator.setAlpha(0);
		this.textAnimator.setStartTime(0);
		this.alpha = 0;
		this.textAlpha = 0;
		this.textDisplacement = screenDetails.cellDim / 3;
		
		this.rSeed = new Random();
		this.random = new Random();
		
		this.randomPop = new Random();
		this.hasPopped = false;
		
		this.startTime = 0;
		this.elapsedTime = 0;
		this.phaseTime = 0;
		this.phasePopTime = 0;
		
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		startTime = System.nanoTime();
		
		gl.glViewport(0, 0, screenDetails.metrics.widthPixels, screenDetails.metrics.heightPixels);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, screenDetails.metrics.widthPixels, 0, screenDetails.metrics.heightPixels, 10, -1);
	
		utilities.createBitmap();
	
		if(touchTime == 0) {
			touchRadius.add((float) 0.0);
			touchTime += elapsedTime;
		}
		else if(touchTime >= 500000000) {
			touchRadius.add((float) 0.0);
			touchTime = 0;
		}
		else 
			touchTime += elapsedTime;
		
		color[3] = 1f;
		
		switch(phase){
		case 1: phaseTime += elapsedTime;
				utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_PAWSSENH, 1);
				
				if(phasePopTime >= 500000000L) {
					if(!Settings.mute)
						soundManager.play(SoundManager.METAL);
					phasePopTime = 0;
				}
				
				if(animator.getStartTime() == 0) {
					animator.setStartTime(startTime);
					animator.setAlpha(0);
					try {
						AssetManager assetManger = context.getAssets();
						AssetFileDescriptor descriptor = assetManger.openFd("crackle.ogg");
						cracklePlayer.setDataSource(descriptor.getFileDescriptor(), 
												  descriptor.getStartOffset(),
												  descriptor.getLength());
						cracklePlayer.prepare();
						cracklePlayer.setLooping(false);
						if(!Settings.mute)
							cracklePlayer.start();
					} catch (IOException e) {
						e.printStackTrace();
						cracklePlayer = null;
					}
				}
				if(phaseTime <= 2000000000L) 
					alpha = animator.fadeIn(System.nanoTime(), 2000);
				else if(phaseTime > 2000000000L && phaseTime <= 4000000000L) {
					alpha = 1;
					animator.setAlpha(1);
				}
				else if(phaseTime > 4000000000L && phaseTime <= 6000000000L) 
					alpha = animator.fadeOut(System.nanoTime() - 4000000000L, 2000);
				else if(phaseTime > 6000000000L && phaseTime <= 8000000000L)
					alpha = 0;
				else {
					phaseTime = 0;
					phasePopTime = 0;
					phase++;
					animator.setStartTime(0);
					animator.setAlpha(0);
					cracklePlayer.stop();
					cracklePlayer.release();
					textAnimator.setStartTime(0);
					textAnimator.setAlpha(0);
				}
				
				if(textAnimator.getStartTime() == 0) {
					textAnimator.setStartTime(startTime);
					textAnimator.setAlpha(0);
					
					font = Typeface.createFromAsset(context.getAssets(), "Angles Octagon.ttf");
					
					textPaint.setTextSize((60 / 88.0f) * screenDetails.cellDim);
					textPaint.setTypeface(font);
				}
				else if(phaseTime <= 3000000000L)
					textAlpha = 0;
				else if(phaseTime > 3000000000L && phaseTime <= 4500000000L) {
					phasePopTime += elapsedTime;
					textAlpha = textAnimator.fadeIn(System.nanoTime() - 3000000000L, 1500);
					textPaint.setARGB((int) (textAlpha * 255), 255, 0, 0);
					utilities.drawTextToBitmap("PAWSSENH", screenDetails.metrics.widthPixels / 2, 
													screenDetails.metrics.heightPixels / 2, textPaint);
				}
				else if(phaseTime > 4500000000L && phaseTime <= 5000000000L) {
					phasePopTime += elapsedTime;
					textAnimator.setAlpha(1);
					textPaint.setARGB(255, 255, 0, 0);
					utilities.drawTextToBitmap("PAWSSENH", screenDetails.metrics.widthPixels / 2, 
													screenDetails.metrics.heightPixels / 2, textPaint);
				}
				else if(phaseTime > 5000000000L && phaseTime <= 7000000000L){
					textAlpha = animator.fadeOut(System.nanoTime() - 5000000000L, 2000);
					textPaint.setARGB(255, (int) (textAlpha * 255), 0, 0);
					utilities.drawTextToBitmap("PAWSSENH", screenDetails.metrics.widthPixels / 2, 
													screenDetails.metrics.heightPixels / 2, textPaint);
				}
				else if(phaseTime > 7000000000L && phaseTime <= 8000000000L){
					textPaint.setARGB(255, 0, 0, 0);
					utilities.drawTextToBitmap("PAWSSENH", screenDetails.metrics.widthPixels / 2, 
													screenDetails.metrics.heightPixels / 2, textPaint);
				}
				
				utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_HOTMETAL, alpha);
				break;
		case 2: if(phaseTime == 0) {
					phaseTime = 1;
					textAnimator.setAlpha(0);
					textAnimator.setStartTime(System.nanoTime());
					try {
						AssetManager assetManger = context.getAssets();
						AssetFileDescriptor descriptor = assetManger.openFd("wave_ip.ogg");
						wavePlayer.setDataSource(descriptor.getFileDescriptor(), 
										  	      descriptor.getStartOffset(),
										  	      descriptor.getLength());
						wavePlayer.prepare();
						wavePlayer.setLooping(false);
						if(!Settings.mute)
							wavePlayer.start();
					} catch (IOException e) {
						e.printStackTrace();
						wavePlayer = null;
					}
					textPaint.setTextSize((35 / 88.0f) * screenDetails.cellDim);
				}
				
				phaseTime += elapsedTime;
				
				font = Typeface.createFromAsset(context.getAssets(), "Digital_tech.ttf");
				textPaint.setTypeface(font);
				textPaint.setARGB(255, 0, 80, 0);
			
				utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_INFLECTIONPOINT, 1);
				for(int i = 0; i < inflection.length(); i++) {
					utilities.drawTextToBitmap(Character.toString(inflection.charAt(i)), (20.0F / 88 * screenDetails.cellDim) * (i + 1), 
									(float) (screenDetails.metrics.heightPixels / 2 - amplitude * 0.75 * Math.sin(i * Math.PI / (inflection.length() - 1))), textPaint);
				}
				for(int i = 0; i < point.length(); i++) {
					utilities.drawTextToBitmap(Character.toString(point.charAt(i)), (20.0F / 88 * screenDetails.cellDim) * (i + 2 + inflection.length()), 
									(float) (screenDetails.metrics.heightPixels / 2 + amplitude * 0.5 * Math.sin(i * Math.PI / (point.length() - 1))), textPaint);
				}
				
				if(phaseTime <= 2000000000L) {
					if(amplitude <= screenDetails.cellDim)
						amplitude += 1.0 * elapsedTime / 33333333;
				}
				else if(phaseTime > 2000000000L && phaseTime <= 3000000000L) {
					
				}
				else if(phaseTime > 3000000000L && phaseTime <= 3500000000L) {
					if(!isGamesSoundPlayed) {
						if(!Settings.mute)
							soundManager.play(SoundManager.GAMES);
						isGamesSoundPlayed = true;
					}
					textAlpha = textAnimator.fadeIn(System.nanoTime(), 500);
					textPaint.setARGB((int) textAlpha * 255, 0, 80, 0);
					utilities.drawTextToBitmap("GAMES", screenDetails.metrics.widthPixels - screenDetails.cellDim, 
										screenDetails.metrics.heightPixels / 2 , textPaint);
				}
				else if(phaseTime > 3500000000L && phaseTime <= 4500000000L) {
					utilities.drawTextToBitmap("GAMES", screenDetails.metrics.widthPixels - screenDetails.cellDim, 
										screenDetails.metrics.heightPixels / 2 , textPaint);
				}
				else {
					utilities.drawTextToBitmap("GAMES", screenDetails.metrics.widthPixels - screenDetails.cellDim, 
										screenDetails.metrics.heightPixels / 2 , textPaint);
					phaseTime = 0;
					phase++;
					animator.setAlpha(0);
					animator.setStartTime(0);
					textAnimator.setAlpha(0);
					textAnimator.setStartTime(0);
					wavePlayer.release();
				}
				break;
		case 3:	if(phaseTime == 0) {
					phaseTime = 1;
					textAnimator.setAlpha(0);
					textAnimator.setStartTime(System.nanoTime());
				}
		
				phaseTime += elapsedTime;
				utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);	
				utilities.drawTexture(gl, rectJellyfish, DrawingUtilities.TEXTURE_JELLYFISH, 0.5F);
				
				textAlpha = textAnimator.fadeIn(System.nanoTime(), 2000);
				
				font = Typeface.createFromAsset(context.getAssets(), "The Perfect Wave.ttf");
				
				textPaint.setTypeface(font);
				textPaint.setTextSize((120 / 88.0f) * screenDetails.cellDim);
				textPaint.setTextAlign(Align.CENTER);
				textPaint.getTextBounds("INTER", 0, 5, textBounds);
				textPaint.setARGB((int) (255 * textAlpha), 238, 233, 233);
				
				utilities.drawTextToBitmap("INTER", (float) (screenDetails.metrics.widthPixels / 3), 
														screenDetails.metrics.heightPixels / 2 + textBounds.height() / 2 + textDisplacement, textPaint);
				
				font = Typeface.createFromAsset(context.getAssets(), "Definitely Maybe.ttf");
				textPaint.setTypeface(font);
				textPaint.setTextSize((100 / 88.0f) * screenDetails.cellDim);
				textPaint.setTextAlign(Align.CENTER);
				textPaint.getTextBounds("POP", 0, 3, textBounds);
				textPaint.setARGB((int) (255 * textAlpha), 238, 233, 233);
				
				utilities.drawTextToBitmap("POP", (float) (screenDetails.metrics.widthPixels * 3.0 / 4), 
														screenDetails.metrics.heightPixels / 2 + textBounds.height() / 2 + textDisplacement, textPaint);
				textDisplacement = (1 - textAlpha) * textDisplacement;
				if(textAlpha >= 1) {
					phaseTime = 0;
					phase++;
					animator.setAlpha(0);
					animator.setStartTime(0);
					textAnimator.setAlpha(0);
					textAnimator.setStartTime(0);
				}
				break;	
		case 4: phasePopTime += elapsedTime;
				phaseTime += elapsedTime;
				
				if(!hasPopped) {
					float randomP = randomPop.nextFloat();
					if(randomP <= 0.25) 
						if(!Settings.mute)
							soundManager.play(SoundManager.BUBBLE_POP1); 
					else if(randomP > 0.25 && randomP <= 0.5) 
						if(!Settings.mute)
							soundManager.play(SoundManager.BUBBLE_POP2); 
					else if(randomP > 0.5 && randomP <= 0.75) 
						if(!Settings.mute)
							soundManager.play(SoundManager.BUBBLE_POP3); 
					else if(randomP > 0.75 && randomP <= 1) 
						if(!Settings.mute)
							soundManager.play(SoundManager.BUBBLE_POP4); 
					hasPopped = true;
				}
				
				if(phasePopTime >= 15000000) {
					hasPopped = false;
					phasePopTime = 0;
				}
				
				if(phaseTime >= 2000000000L) {
					font = Typeface.createFromAsset(context.getAssets(), "Gasalt-Black.ttf");
					textPaint.setTypeface(font);
					textPaint.setTextSize((40 / 88.0f) * screenDetails.cellDim);
					
					utilities.drawTextToBitmap("Tap Anywhere to Continue...", 
											   screenDetails.metrics.widthPixels / 2, 
											   screenDetails.metrics.heightPixels / 2 + screenDetails.cellDim * 1.5f, 
											   textPaint);
				}					
				
				utilities.drawTexture(gl, rectScreen, DrawingUtilities.TEXTURE_BACKGROUND, 1);	
				utilities.drawTexture(gl, rectJellyfish, DrawingUtilities.TEXTURE_JELLYFISH, 0.5F);
				isCompleted = true;
				random.setSeed(rSeed.nextInt());
				if(flag == -1){
					flag = 0;
					rndRadius = (screenDetails.cellDim / 4) + random.nextInt(30);
					rndX = rndRadius + random.nextInt((int) (screenDetails.metrics.widthPixels - rndRadius * 2));	
				
					for(ListIterator<Bubble> it = bubbles.listIterator(); it.hasNext();) {
						Bubble tempBubble = it.next();
						float distC = (float) Math.sqrt(Math.pow(rndX - tempBubble.getBubbleX(), 2)
										+ Math.pow(tempBubble.getBubbleY(), 2));
						float radiiSum = rndRadius + tempBubble.getBubbleRadius() + 100;
						for(int j = 0; j < maxBubbles; j++)
							if(distC <= radiiSum){
								flag = -1;
								break;
							}
					}
				}
				
				else if(flag == 0){
					
					if(random.nextFloat() <= 0.85) 
						this.bubbles.add(new Bubble(rndX, 0, rndRadius, Bubble.BUBBLE_NORMAL));
					else 
						this.bubbles.add(new Bubble(rndX, 0, rndRadius, Bubble.BUBBLE_BLACK));
					
					rndRadius = rndX = flag = -1;
				}
				
				for(ListIterator<Bubble> it = bubbles.listIterator(); it.hasNext();){
					Bubble tempBubble = it.next();
					if(tempBubble.getBubbleRadius() > 0 
					  && (tempBubble.getBubbleY() <= screenDetails.metrics.heightPixels + tempBubble.getBubbleRadius() * 2)){
						tempBubble.setBubbleY(tempBubble.getBubbleY() + 1.5F / 33333333 * elapsedTime);
						utilities.drawBubble(gl, tempBubble.getBubbleX(), tempBubble.getBubbleY() - tempBubble.getBubbleRadius(), 
								tempBubble.getBubbleRadius() * tempBubble.getBubbleY() / screenDetails.metrics.heightPixels, tempBubble.getBubbleType(), 0.8f);
					}
						
					else if((tempBubble.getBubbleY() > screenDetails.metrics.heightPixels + tempBubble.getBubbleRadius() * 2)){
						it.remove();
					}
				}
				
				font = Typeface.createFromAsset(context.getAssets(), "The Perfect Wave.ttf");
				
				textPaint.setColor(Color.WHITE);
				textPaint.setTypeface(font);
				textPaint.setTextAlign(Align.CENTER);
				textPaint.setTextSize((120 / 88.0f) * screenDetails.cellDim);
				textPaint.getTextBounds("INTER", 0, 5, textBounds);
				textPaint.setARGB(255, 238, 233, 233);
				
				utilities.drawTextToBitmap("INTER", (float) (screenDetails.metrics.widthPixels / 3), 
														screenDetails.metrics.heightPixels / 2 + textBounds.height() / 2, textPaint);
				
				font = Typeface.createFromAsset(context.getAssets(), "Definitely Maybe.ttf");
				textPaint.setTypeface(font);
				textPaint.setTextAlign(Align.CENTER);
				textPaint.setTextSize((100 / 88.0f) * screenDetails.cellDim);
				textPaint.getTextBounds("POP", 0, 3, textBounds);
				textPaint.setARGB(255, 238, 233, 233);
				
				utilities.drawTextToBitmap("POP", (float) (screenDetails.metrics.widthPixels * 3.0 / 4), 
														screenDetails.metrics.heightPixels / 2 + textBounds.height() / 2, textPaint);
				
				for(float indexRadius : touchRadius) {
					color[0] = 0;
					color[1] = 1;
					color[2] = 1;
					color[3] = (float) (1 - Math.pow(indexRadius / screenDetails.cellDim / 3.0 * 2, 1.5));
					utilities.drawCircularElement(gl, screenDetails.metrics.widthPixels / 2, 
												  screenDetails.metrics.heightPixels / 2, indexRadius, 
												  InscribedPolygon.MODE_BOUNDARY, 2, color);
					color[0] = 1;
					color[1] = 1;
					color[2] = 1;
					color[3] = 1;
					
				}
				
				for(ListIterator<Float> it = touchRadius.listIterator(); it.hasNext();) {
					float radius = it.next();
					it.set((float) (radius + 1.0 * elapsedTime / 33333333));
					if(radius >= screenDetails.cellDim * 3.0 / 2)
						it.remove();
				}
				
				break;				
		}
		
		utilities.drawText(gl);
		utilities.recycleBitmap();
		
		elapsedTime = System.nanoTime() - startTime;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.2f, 0.2f, 0.7f, 0f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		utilities.loadTextureId(gl);
	}
	
	public boolean isCompleted() {
		if(isCompleted)
			return true;
		else
			return false;
	}
	
	public void increaseLimit(int amount) {
		maxBubbles += amount;
	}

}
