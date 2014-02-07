/**
 * Miniature.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba.preferences;

import android.view.View;

/**
 */
public class Miniature {

	private int resource;

	private int description;

	private View.OnClickListener listener;

	/**
	 * @param resource
	 * @param description
	 */
	public Miniature(int resource, int description,
			View.OnClickListener listener) {
		super();
		this.resource = resource;
		this.description = description;
		this.listener = listener;
	}

	public int getResource() {
		return resource;
	}

	public int getDescription() {
		return description;
	}

	public View.OnClickListener getListener() {
		return listener;
	}

}
