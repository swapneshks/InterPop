package com.inflectionPoint.interpop;

public class Animator implements Animations {
	
	private long startTime;
	private float initialAlpha;
	
	@Override
	public float fadeIn(long currentTime, int animationTime) {
		float newAlpha = (float) (initialAlpha + ((currentTime - this.startTime) / (animationTime * 1000000.0)));
		
		if(currentTime - this.startTime >= (animationTime * 1000000l))
			return 1;
		else
			return newAlpha;
		}
	
	@Override
	public float fadeOut(long currentTime, int animationTime) {
		float newAlpha = initialAlpha - ((currentTime - this.startTime) / (animationTime * 1000000.0F));
			
		if(currentTime - this.startTime >= (animationTime * 1000000l))
			return 0;
		else
			return newAlpha;
	}
	
	@Override
	public void scaleIn() {
		
	}
	
	@Override
	public void scaleOut() {
		
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public void setAlpha(float alpha) {
		this.initialAlpha = alpha;
	}

}