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

	public static enum Type {
		IMAGE, REFLECTION
	};

	private int mini;
	private String resource;
	private int description;
	private Type type;

	/**
	 * @param resource
	 * @param description
	 */
	public Miniature(int mini, int description, String resource, Type type) {
		super();
		this.mini = mini;
		this.resource = resource;
		this.description = description;
		this.type = type;
	}

	public int getMini() {
		return mini;
	}

	public String getResource() {
		return resource;
	}

	public int getDescription() {
		return description;
	}

	public Type getType() {
		return type;
	}

}
