package com.example.coinscanner;

import java.io.Serializable;

public class MyCircle implements Serializable {

	public MyCircle() {
		centerX = 0;
		centerY = 0;
		radius = 1;
	}

	public MyCircle(int centerX, int centerY, int radius) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
	}

	public int getCenterX() {
		return this.centerX;
	}

	public int getCenterY() {
		return this.centerY;
	}

	public int getRadius() {
		return this.radius;
	}

	public boolean isEquals(MyCircle other) {
		return this.centerX == other.getCenterX() && this.centerY == other.getCenterY()
				&& this.radius == other.getRadius();
	}

	private int centerX;
	private int centerY;
	private int radius;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
