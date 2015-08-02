package com.inflectionPoint.interpop;

import android.graphics.PointF;

public interface TouchUtilities {

	public boolean touchedRegionCheck(PointF touchedPoint, PointF targetPoint, float bounds);
	
	public float setXToGrid(float x, boolean boundaryPoints);
	
	public float setYToGrid(float y, boolean boundaryPoints);
	
	public int getIndexX(float x);
	
	public int getIndexY(float y);
	
}
