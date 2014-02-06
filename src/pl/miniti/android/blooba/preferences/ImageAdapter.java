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
import android.widget.GridView;
import android.widget.ImageView;

/**
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	private Integer[] thumbs;

	// Constructor
	public ImageAdapter(Context c, Integer[] thumbs) {
		mContext = c;
		this.thumbs = thumbs;
	}

	@Override
	public int getCount() {
		return thumbs.length;
	}

	@Override
	public Object getItem(int position) {
		return thumbs[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);
		imageView.setImageResource(thumbs[position]);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
		return imageView;
	}

}