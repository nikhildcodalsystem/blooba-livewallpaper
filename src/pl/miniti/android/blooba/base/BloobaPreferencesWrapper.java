/**
 * BloobaPreferencesWrapper.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 5, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base;

import android.content.SharedPreferences;

/**
 * Class wrapping user prefernces for the Blooba
 */
public class BloobaPreferencesWrapper {

	private boolean touchEnabled;
	private boolean gravityEnabled;
	private boolean gravityInverted;
	private int quality;
	private int speed;
	private float size;
	private float relaxFactor;

	/**
	 * Private constructor - use factory method
	 */
	private BloobaPreferencesWrapper() {
	}

	/**
	 * Creates new object based on user preferences
	 * 
	 * @param prefs
	 *            shared prefences
	 * @return initialized object with all preferences in place
	 */
	public static BloobaPreferencesWrapper fromPreferences(
			SharedPreferences prefs) {
		BloobaPreferencesWrapper settings = new BloobaPreferencesWrapper();
		settings.touchEnabled = prefs.getBoolean("touch", Boolean.TRUE);
		settings.gravityEnabled = prefs.getBoolean("gravity", Boolean.TRUE);
		settings.gravityInverted = prefs.getBoolean("invert", Boolean.FALSE);
		settings.quality = Integer.valueOf(prefs.getString("quality", "40"));
		settings.size = Float.valueOf(prefs.getString("size", "0.8"));
		settings.relaxFactor = Float.valueOf(prefs.getString("relax", "0.9"));
		settings.speed = Integer.valueOf(prefs.getString("speed", "10"));
		return settings;
	}

	public boolean isTouchEnabled() {
		return touchEnabled;
	}

	public boolean isGravityEnabled() {
		return gravityEnabled;
	}

	public boolean isGravityInverted() {
		return gravityInverted;
	}

	public int getQuality() {
		return quality;
	}

	public float getSize() {
		return size;
	}

	public float getRelaxFactor() {
		return relaxFactor;
	}

	public void setTouchEnabled(boolean touchEnabled) {
		this.touchEnabled = touchEnabled;
	}

	public void setGravityEnabled(boolean gravityEnabled) {
		this.gravityEnabled = gravityEnabled;
	}

	public void setGravityInverted(boolean gravityInverted) {
		this.gravityInverted = gravityInverted;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public void setRelaxFactor(float relaxFactor) {
		this.relaxFactor = relaxFactor;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
