/**
 * ForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;

/**
 * Interface for classes delivering functionality to serve as a provider of the
 * blooba bitmap.
 */
public interface ForegroundProvider {

	/**
	 * @return true, if the contents of the foreground depend on the location of
	 *         the blob. false otherwise
	 */
	boolean isDynamic();

	/**
	 * Initialize object for the given size of the blooba
	 * 
	 * @param size
	 *            size of the blooba square
	 */
	void initForSize(int size);

	/**
	 * Create the texture to be displayed on the blooba
	 * 
	 * @param x
	 *            location of the blooba centre on the X-axis
	 * @param y
	 *            location of the blooba centre on the Y-axis
	 * @param size
	 *            size of the blooba
	 * @return bitmap texture to be displayed
	 */
	Bitmap getTexture(float x, float y, int size);

	/**
	 * Invoked on updating the blooba background in order to update the
	 * foreground
	 * 
	 * @param background
	 *            new blooba background
	 */
	void setBackground(SoftReference<Bitmap> background);

	/**
	 * Called on destroying the blooba
	 */
	void destroy();

}
