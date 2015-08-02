package com.inflectionPoint.interpop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class InscribedPolygon {
	
	private float x;
	private float y;
	private float r;

	static final int MODE_FILL = 0;
	static final int MODE_RADIAL_LINES = 1;
	static final int MODE_BOUNDARY = 2;
	
	static final int SEMICIRCLE_UP = 10;
	static final int SEMICIRCLE_DOWN = 11;
	static final int SEMICIRCLE_LEFT = 12;
	static final int SEMICIRCLE_RIGHT = 13;
	
	public InscribedPolygon (float x, float y, float r){
		this.x = x;
		this.y = y;		
		this.r = r;
	}
	
	public void drawInscribedPolygon (GL10 gl, int points, int mode, float width, float color[]){
		
		float anglePerPoint = (float) (2 * Math.PI / points);
		
		int strideSize = (2 + 4) * 4;
		
		ByteBuffer cbuffer = ByteBuffer.allocateDirect((points+2)*6*4);
		cbuffer.order(ByteOrder.nativeOrder());
		FloatBuffer cfBuffer = cbuffer.asFloatBuffer();
		
		ByteBuffer sbuffer = ByteBuffer.allocateDirect(points*3*2);
		sbuffer.order(ByteOrder.nativeOrder());
		ShortBuffer indices = sbuffer.asShortBuffer();
		
		int countF = 0;
		int countS = 0;
		
		float[] vertexPoints = new float[(points+2) * 6];
		short[] sIndices = new short[points * 3];
		
		vertexPoints[countF++] = x;
		vertexPoints[countF++] = y;
		vertexPoints[countF++] = color[0];
		vertexPoints[countF++] = color[1];
		vertexPoints[countF++] = color[2];
		vertexPoints[countF++] = color[3];
		vertexPoints[countF++] = (float) (x + r * Math.cos(0));
		vertexPoints[countF++] = (float) (y + r * Math.sin(0));
		vertexPoints[countF++] = color[0];
		vertexPoints[countF++] = color[1];
		vertexPoints[countF++] = color[2];
		vertexPoints[countF++] = color[3];
		
		for (int i = 1; i <= points; i++){
			vertexPoints[countF++] = (float) (x + r * Math.cos(i * anglePerPoint));
			vertexPoints[countF++] = (float) (y + r * Math.sin(i * anglePerPoint));
			vertexPoints[countF++] = color[0];
			vertexPoints[countF++] = color[1];
			vertexPoints[countF++] = color[2];
			vertexPoints[countF++] = color[3];
							
			if (i != points){
				sIndices[countS++] = 0;
				sIndices[countS++] = (short) i;
				sIndices[countS++] = (short) (i+1);
			}
			else{
				sIndices[countS++] = 0;
				sIndices[countS++] = (short) points;
				sIndices[countS++] = (short) 1;
			}
		}
		
		cfBuffer.clear();
		cfBuffer.put(vertexPoints);
		cfBuffer.flip();
		
		indices.clear();
		indices.put(sIndices);
		indices.flip();
			
		switch (mode) {
		case MODE_FILL:
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			cfBuffer.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, strideSize, cfBuffer);
			cfBuffer.position(2);
			gl.glColorPointer(4, GL10.GL_FLOAT, strideSize, cfBuffer);
			gl.glDrawElements(GL10.GL_TRIANGLE_FAN, points * 3, GL10.GL_UNSIGNED_SHORT, indices);
			break;
		case MODE_RADIAL_LINES:
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			cfBuffer.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, strideSize, cfBuffer);
			cfBuffer.position(2);
			gl.glColorPointer(4, GL10.GL_FLOAT, strideSize, cfBuffer);
			gl.glDrawElements(GL10.GL_LINE_LOOP, points * 3, GL10.GL_UNSIGNED_SHORT, indices);
			break;
		case MODE_BOUNDARY:
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			cfBuffer.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, strideSize, cfBuffer);
			cfBuffer.position(2);
			gl.glColorPointer(4, GL10.GL_FLOAT, strideSize, cfBuffer);
			gl.glLineWidth(width);
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 1, points);
			gl.glLineWidth(1);
			break;
		default:
			break;
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
	
}
