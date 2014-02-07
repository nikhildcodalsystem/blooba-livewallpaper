/**
 * ImageForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import android.graphics.Bitmap;

/**
 */
public class ImageForegroundProvider implements ForegroundProvider {

	private Bitmap texture;

	public ImageForegroundProvider(Bitmap texture) {
		this.texture = texture;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public void initForSize(int size) {
		texture = Bitmap.createScaledBitmap(texture, size, size, false);
	}

	@Override
	public Bitmap getTexture(float x, float y, int rad) {
		return texture;
	}

	@Override
	public void destroy() {
		texture.recycle();
		texture = null;
	}

}
