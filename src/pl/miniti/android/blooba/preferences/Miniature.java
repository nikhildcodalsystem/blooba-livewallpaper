/**
 * Miniature.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba.preferences;

/**
 */
public class Miniature {

	private int resource;

	private String description;

	/**
	 * @param resource
	 * @param description
	 */
	public Miniature(int resource, String description) {
		super();
		this.resource = resource;
		this.description = description;
	}

	public int getResource() {
		return resource;
	}

	public String getDescription() {
		return description;
	}

}
