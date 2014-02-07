/**
 * ReflectionForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import android.graphics.Bitmap;

/**
 */
public class ReflectionForegroundProvider implements ForegroundProvider {

	public ReflectionForegroundProvider(Bitmap front, Bitmap back) {

	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void initForSize(int size) {
		// TODO
	}

	@Override
	public Bitmap getTexture(float x, float y, int size) {
		return null;
	}

	@Override
	public void destroy() {
		// TODO
	}

}
