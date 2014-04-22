/**
 * Preferences.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba.base;

import java.util.HashMap;
import java.util.Map;

import pl.miniti.android.blooba.BloobaBackground;
import pl.miniti.android.blooba.BloobaForeground;
import pl.miniti.android.blooba.R;
import pl.miniti.android.blooba.preferences.Miniature;

/**
 * Stores constants and other statically loaded contents related to Blooba
 */
public final class Preferences {

	/**
	 * boolean key for enabling touch screen sensor
	 */
	public static final String ENABLE_TOUCH = "touch";

	/**
	 * boolean key for inverting the gravity direction
	 */
	public static final String INVERT_GRAVITY = "invert";

	/**
	 * integer key for defining blooba squishiness
	 */
	public static final String RELAX_FACTOR = "relax";

	/**
	 * integer key for defining animation speed
	 */
	public static final String SPEED = "speed";

	/**
	 * string key for describing the blooba foreground
	 */
	public static final String FOREGROUND_NAME = "foreground_name";

	/**
	 * string key for describing the blooba background
	 */
	public static final String BACKGROUND_NAME = "background_name";

	/**
	 * Statically map all resources available in the preferences
	 */
	private static final Map<String, Integer> resources = new HashMap<String, Integer>();

	static {
		// load all foreground resources
		for (Miniature m : BloobaForeground.minis) {
			resources.put(m.getPreferenceValue(), m.getBitmapResource());
		}

		// load all background resources
		for (Miniature m : BloobaBackground.minis) {
			resources.put(m.getPreferenceValue(), m.getBitmapResource());
		}
	}

	/**
	 * Resolve foreground resource property with default if not found
	 * 
	 * @param name
	 *            name of the resource
	 * @return bitmap identifier
	 */
	public static int resolveForegroundResource(String name) {
		if (resources.containsKey(name)) {
			return resources.get(name);
		}

		// assume 'earth' as default front
		return R.drawable.earth;
	}

	/**
	 * Resolve background resource property with default if not found
	 * 
	 * @param name
	 *            name of the resource
	 * @return bitmap identifier
	 */
	public static int resolveBackgroundResource(String name) {
		if (resources.containsKey(name)) {
			return resources.get(name);
		}

		// assume 'stars' as default background
		return R.drawable.bg_stars;
	}

}
