/**
 * Miniature.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.preferences;

/**
 * Represents a front or back bitmap resource, it's type, miniature, description
 * and potentially other options.
 */
public class Miniature {

	/**
	 * Type of the resource
	 */
	public static enum Type {

		/**
		 * Plain bitmap image
		 */
		IMAGE,

		/**
		 * Custom reflection foreground effect
		 */
		REFLECTION,

		/**
		 * 'Pick from gallery' action
		 */
		GALLERY
	};

	/**
	 * Bitmap id of the miniature drawable resource
	 */
	private int miniatureResource;

	/**
	 * Name of the resource for the preferences object
	 */
	private String preference;

	/**
	 * Miniature description string resource
	 */
	private int description;

	/**
	 * Bitmap id of the actual drawable resource if applicable
	 */
	private int resource;

	/**
	 * Type of the resources as defined in the enumeration
	 */
	private Type type;

	/**
	 * Default object constructor
	 * 
	 * @param miniature
	 *            bitmap resource for the miniature
	 * @param resource
	 *            actual bitmap resource
	 * @param description
	 *            item description resource
	 * @param preference
	 *            value for the preferences object
	 * @param type
	 *            type of resource
	 */
	public Miniature(int miniature, int resource, int description,
			String preference, Type type) {
		super();
		this.miniatureResource = miniature;
		this.preference = preference;
		this.resource = resource;
		this.description = description;
		this.type = type;
	}

	/**
	 * @return bitmap id of the miniature drawable resource
	 */
	public int getMiniatureResource() {
		return miniatureResource;
	}

	/**
	 * @return name of the resource for the preferences object
	 */
	public String getPreferenceValue() {
		return preference;
	}

	/**
	 * @return bitmap id of the actual drawable resource if applicable
	 */
	public int getBitmapResource() {
		return resource;
	}

	/**
	 * @return type of the resources as defined in the enumeration
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return id of the string resource for miniature description
	 */
	public int getDescriptionResource() {
		return description;
	}

}
