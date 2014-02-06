/**
 * ForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import android.graphics.Bitmap;

/**
 */
public interface ForegroundProvider {

	boolean isDynamic();

	void initForSize(int size);

	Bitmap getTexture(float x, float y, int size);

}
