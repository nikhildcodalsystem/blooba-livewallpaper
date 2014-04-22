/**
 * ImageForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import android.graphics.Bitmap;

/**
 * Foreground provider which displays a given static bitmap
 */
public class ImageForegroundProvider implements ForegroundProvider {

	/**
	 * Bitmap texture returned on each request
	 */
	private Bitmap texture;

	/**
	 * Constructor based on a given bitmap texture
	 * 
	 * @param texture
	 *            bitmap texture
	 */
	public ImageForegroundProvider(Bitmap texture) {
		this.texture = texture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#initForSize
	 * (int)
	 */
	@Override
	public void initForSize(int size) {
		texture = Bitmap.createScaledBitmap(texture, size, size, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#getTexture
	 * (float, float, int)
	 */
	@Override
	public Bitmap getTexture(float x, float y, int rad) {
		return texture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#setBackground
	 * (android.graphics.Bitmap)
	 */
	@Override
	public void setBackground(Bitmap background) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#destroy()
	 */
	@Override
	public void destroy() {
		texture.recycle();
		texture = null;
	}

}
