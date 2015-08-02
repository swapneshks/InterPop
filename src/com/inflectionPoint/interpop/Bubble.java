package com.inflectionPoint.interpop;

public class Bubble {
	
	static final int BUBBLE_NONE = -100;
	static final int BUBBLE_NORMAL = 200;
	static final int BUBBLE_BLACK = -200;
	static final int BUBBLE_DROP = 300;
	static final int POWERUP_BOUNDARY = 400;
	static final int POWERUP_NORMAL_SCORE = 500;
	static final int POWERUP_BLACK_SCORE = 600;
	static final int POWERDOWN_NORMAL_SCORE = 700;
	static final int POWERDOWN_BLACK_SCORE = 800;
	static final int SUPERPOP_NORMAL = 900;
	static final int SUPERPOP_BLACK = 1000;
	static final int SUPERPOP_BLACK_NEGATIVE = 1100;
	static final int INVERT_BUBBLE = 1200;
	static final int CONVERT_ALL_TO_NORMAL = 1300;
	static final int CONVERT_ALL_TO_BLACK = 1400;
	
	private float bubbleRadius;
	private float bubbleX;
	private float bubbleY;
	private int bubbleType;
	
	public Bubble(float x, float y, float r, int type) {
		this.setBubbleX(x);
		this.setBubbleY(y);
		this.setBubbleRadius(r);
		this.setBubbleType(type);
	}

	public float getBubbleRadius() {
		return bubbleRadius;
	}

	public void setBubbleRadius(float bubbleRadius) {
		this.bubbleRadius = bubbleRadius;
	}

	public float getBubbleX() {
		return bubbleX;
	}

	public void setBubbleX(float bubbleX) {
		this.bubbleX = bubbleX;
	}

	public float getBubbleY() {
		return bubbleY;
	}

	public void setBubbleY(float bubbleY) {
		this.bubbleY = bubbleY;
	}

	public int getBubbleType() {
		return bubbleType;
	}

	public void setBubbleType(int bubbleType) {
		this.bubbleType = bubbleType;
	}
		
}
