package com.inflectionPoint.interpop;

import android.util.DisplayMetrics;

public class ScreenDetails {

	public DisplayMetrics metrics;
	public float wCells;
	public float hCells;
	public float margin;
	public float cellDim;
	public float extraHeight;
	
	public ScreenDetails(DisplayMetrics metrics, float wCells) {
		this.metrics = metrics;
		this.wCells = wCells;
		this.margin = (40 / 480.0f) * metrics.widthPixels;
		this.cellDim = (float) (metrics.widthPixels - margin) / wCells;
		this.hCells = (int) (Math.floor(metrics.heightPixels - margin) / cellDim);
		this.extraHeight = metrics.heightPixels - (hCells * cellDim + margin);
	}
}
