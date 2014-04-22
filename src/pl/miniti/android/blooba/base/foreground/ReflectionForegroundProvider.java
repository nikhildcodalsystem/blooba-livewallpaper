/**
 * ReflectionForegroundProvider.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base.foreground;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Foreground provider which reclects the background of the blooba in its
 * foreground
 */
public class ReflectionForegroundProvider implements ForegroundProvider {

	/**
	 * Bitmap resource for the front of the blooba
	 */
	private SoftReference<Bitmap> front;

	/**
	 * Reference to the blooba background bitmap
	 */
	private SoftReference<Bitmap> background;

	/**
	 * Contructor with front and back bitmap resources
	 * 
	 * @param front
	 *            front bitmap
	 * @param back
	 *            background bitmap
	 */
	public ReflectionForegroundProvider(SoftReference<Bitmap> front,
			SoftReference<Bitmap> back) {
		this.front = front;
		this.background = back;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#setBackground
	 * (java.lang.ref.SoftReference)
	 */
	@Override
	public void setBackground(SoftReference<Bitmap> back) {
		background = back;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
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
		// TODO to be improved
		front = new SoftReference<Bitmap>(Bitmap.createScaledBitmap(
				front.get(), size, size, false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#getTexture
	 * (float, float, int)
	 */
	@Override
	public Bitmap getTexture(float x, float y, int size) {
		Bitmap texture = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas c = new Canvas(texture);
		c.drawARGB(0, 0, 0, 0);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		c.drawCircle(size / 2, size / 2, size / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		float fxl = x - size / 4;
		if (fxl < 0) {
			fxl = 0;
		}
		float fxr = fxl + size / 2;
		if (fxr > background.get().getWidth()) {
			fxr = background.get().getWidth() - 1;
			fxl = fxr - size / 2;
		}

		float fyt = y - size / 4;
		if (fyt < 0) {
			fyt = 0;
		}
		float fyb = fyt + size / 2;
		if (fyt > background.get().getHeight()) {
			fyt = background.get().getHeight() - 1;
			fyb = fyt - size / 2;
		}

		c.drawBitmap(background.get(), new Rect((int) fxl, (int) fyt,
				(int) fxr, (int) fyb), new Rect(0, 0, size, size), paint);

		c.drawBitmap(front.get(), 0, 0, null);
		return texture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.miniti.android.blooba.base.foreground.ForegroundProvider#destroy()
	 */
	@Override
	public void destroy() {
		front.get().recycle();
		front = null;
		background = null;
	}

}
