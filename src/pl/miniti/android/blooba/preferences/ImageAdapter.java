/**
 * ImageAdapter.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.preferences;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Adapter for the miniatures in the Grid View
 */
public class ImageAdapter extends BaseAdapter {

	/**
	 * Android context
	 */
	private Context mContext;

	/**
	 * Array of miniatures
	 */
	private Miniature[] thumbs;

	/**
	 * Default constructor
	 * 
	 * @param c
	 *            android context
	 * @param thumbs
	 *            array of miniatures
	 */
	public ImageAdapter(Context c, Miniature[] thumbs) {
		mContext = c;
		this.thumbs = thumbs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return thumbs.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return thumbs[position];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout layout = new RelativeLayout(mContext);

		RelativeLayout.LayoutParams lpi = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpi.addRule(RelativeLayout.CENTER_HORIZONTAL);

		ImageView imageView = new ImageView(mContext);
		imageView.setImageResource(thumbs[position].getMiniatureResource());
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setId(1);

		RelativeLayout.LayoutParams lpt = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpt.addRule(RelativeLayout.BELOW, imageView.getId());
		lpt.addRule(RelativeLayout.CENTER_HORIZONTAL);

		TextView textView = new TextView(mContext);
		textView.setText(thumbs[position].getDescriptionResource());

		layout.addView(imageView, lpi);
		layout.addView(textView, lpt);

		return layout;
	}
}