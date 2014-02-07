/**
 * ReflectionForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

/**
 */
public class ReflectionForegroundProvider implements ForegroundProvider {

	private Bitmap front;
	private Bitmap background;

	public ReflectionForegroundProvider(Bitmap front, Bitmap back) {
		this.front = front;
		this.background = back;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void initForSize(int size) {
		front = Bitmap.createScaledBitmap(front, size, size, false);
	}

	@Override
	public Bitmap getTexture(float x, float y, int size) {
		Bitmap texture = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas c = new Canvas(texture);
		c.drawBitmap(front, 0, 0, null);
		return texture;
	}

	@Override
	public void destroy() {
		front.recycle();
	}

}
