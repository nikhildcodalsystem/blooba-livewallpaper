/**
 * BloobaPreferencesWrapper.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 5, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base;

import pl.miniti.android.blooba.preferences.Miniature;
import android.content.SharedPreferences;

/**
 * Class wrapping user prefernces for the Blooba
 */
public class BloobaPreferencesWrapper {

	/**
	 * If true blooba registers touch events
	 */
	private boolean touchEnabled;

	/**
	 * If true the Y gravity is upwards
	 */
	private boolean gravityInverted;

	/**
	 * Number of blooba points used in rendering
	 */
	private int quality;

	/**
	 * Speed of the animation
	 */
	private int speed;

	/**
	 * Size of the blooba
	 */
	private float size;

	/**
	 * Describes squishiness of the blooba
	 */
	private float relaxFactor;

	/**
	 * Foreground resource id
	 */
	private String foreground;

	/**
	 * Type of the foreground resource. See {@see Miniature.Type}
	 */
	private int foregroundType;

	/**
	 * Background resource id
	 */
	private String background;

	/**
	 * Type of the background resource. See {@see Miniature.Type}
	 */
	private int backgroundType;

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
		settings.touchEnabled = prefs.getBoolean(Preferences.ENABLE_TOUCH,
				Boolean.TRUE);
		settings.gravityInverted = prefs.getBoolean(Preferences.INVERT_GRAVITY,
				Boolean.FALSE);
		settings.quality = Integer.valueOf(prefs.getString("quality", "40"));
		settings.size = Float.valueOf(prefs.getString("size", "0.5"));
		settings.relaxFactor = Float.valueOf(prefs.getString(
				Preferences.RELAX_FACTOR, "0.9"));
		settings.speed = Integer.valueOf(prefs.getString(Preferences.SPEED,
				"10"));
		settings.foreground = prefs.getString(Preferences.FOREGROUND_NAME,
				"earth");
		settings.foregroundType = prefs.getInt("foreground_type",
				Miniature.Type.IMAGE.ordinal());
		settings.background = prefs.getString(Preferences.BACKGROUND_NAME,
				"stars");
		settings.backgroundType = prefs.getInt("background_type",
				Miniature.Type.IMAGE.ordinal());
		return settings;
	}

	/**
	 * @return the touchEnabled
	 */

	public boolean isTouchEnabled() {
		return touchEnabled;
	}

	/**
	 * @return the gravityInverted
	 */

	public boolean isGravityInverted() {
		return gravityInverted;
	}

	/**
	 * @return the quality
	 */

	public int getQuality() {
		return quality;
	}

	/**
	 * @return the speed
	 */

	public int getSpeed() {
		return speed;
	}

	/**
	 * @return the size
	 */

	public float getSize() {
		return size;
	}

	/**
	 * @return the relaxFactor
	 */

	public float getRelaxFactor() {
		return relaxFactor;
	}

	/**
	 * @return the foreground
	 */

	public String getForeground() {
		return foreground;
	}

	/**
	 * @return the foregroundType
	 */

	public int getForegroundType() {
		return foregroundType;
	}

	/**
	 * @return the background
	 */

	public String getBackground() {
		return background;
	}

	/**
	 * @return the backgroundType
	 */

	public int getBackgroundType() {
		return backgroundType;
	}

	/**
	 * @return true if the background bitmap is a custom gallery image. false
	 *         otherwise
	 */
	public boolean isBackgroundUserDefined() {
		return this.backgroundType == Miniature.Type.GALLERY.ordinal();
	}

	/**
	 * @return true if the foreground bitmap is a custom gallery image. false
	 *         otherwise
	 */
	public boolean isForegroundUserDefined() {
		return this.foregroundType == Miniature.Type.GALLERY.ordinal();
	}

}
