package com.inflectionPoint.interpop;

public interface Animations {
	
	public float fadeIn(long elapsedTime, int animationTime);
	
	public float fadeOut(long elapsedTime, int animationTime);
	
	public void scaleIn();
	
	public void scaleOut();

}
