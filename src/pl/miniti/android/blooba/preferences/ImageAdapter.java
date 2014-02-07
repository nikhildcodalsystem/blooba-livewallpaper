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
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	private Miniature[] thumbs;

	public ImageAdapter(Context c, Miniature[] thumbs) {
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
		RelativeLayout layout = new RelativeLayout(mContext);

		RelativeLayout.LayoutParams lpi = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpi.addRule(RelativeLayout.CENTER_HORIZONTAL);

		ImageView imageView = new ImageView(mContext);
		imageView.setImageResource(thumbs[position].getResource());
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setId(1);

		RelativeLayout.LayoutParams lpt = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpt.addRule(RelativeLayout.BELOW, imageView.getId());
		lpt.addRule(RelativeLayout.CENTER_HORIZONTAL);

		TextView textView = new TextView(mContext);
		textView.setText(thumbs[position].getDescription());

		layout.addView(imageView, lpi);
		layout.addView(textView, lpt);

		return layout;
	}
}