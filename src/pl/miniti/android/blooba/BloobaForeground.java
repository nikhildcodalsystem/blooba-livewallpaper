/**
 * BloobaForeground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import pl.miniti.android.blooba.preferences.Miniature.Type;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * Foreground selection activity
 */
public class BloobaForeground extends Activity implements OnItemClickListener {

	/**
	 * Action code
	 */
	private static final int PICK_IMAGE = 404;

	/**
	 * Statically defined array of available foregrounds
	 */
	public final static Miniature[] minis = new Miniature[]{
			new Miniature(R.drawable.earth_xs, R.drawable.earth,
					R.string.f_earth, "earth", Type.IMAGE),
			new Miniature(R.drawable.moon_xs, R.drawable.moon, R.string.f_moon,
					"moon", Type.IMAGE),
			new Miniature(R.drawable.kenny_xs, R.drawable.kenny,
					R.string.f_kenny, "kenny", Type.IMAGE),
			new Miniature(R.drawable.squish_xs, R.drawable.squish,
					R.string.f_squishy, "squish", Type.IMAGE),
			new Miniature(R.drawable.bubble_xs, R.drawable.bubble,
					R.string.f_bubble, "bubble", Type.IMAGE),
			new Miniature(R.drawable.water_xs, R.drawable.bubble,
					R.string.f_water, "bubble", Type.REFLECTION),
			new Miniature(R.drawable.basketball_xs, R.drawable.basketball,
					R.string.f_basketball, "basketball", Type.IMAGE),
			new Miniature(R.drawable.nemo_xs, R.drawable.nemo, R.string.f_nemo,
					"nemo", Type.IMAGE),
			new Miniature(R.drawable.america_xs, R.drawable.america,
					R.string.f_america, "america", Type.IMAGE),
			new Miniature(R.drawable.balloon_pink_xs, R.drawable.balloon_pink,
					R.string.f_bpink, "bpink", Type.IMAGE),
			new Miniature(R.drawable.balloon_black_xs,
					R.drawable.balloon_black, R.string.f_bblack, "bblack",
					Type.IMAGE),
			new Miniature(R.drawable.donut_xs, R.drawable.donut,
					R.string.f_donut, "donut", Type.IMAGE),
			new Miniature(R.drawable.penny_xs, R.drawable.penny,
					R.string.f_penny, "penny", Type.IMAGE),
			new Miniature(R.drawable.spider_xs, R.drawable.spider,
					R.string.f_spider, "spider", Type.IMAGE),
			new Miniature(R.drawable.gallery_xs, 0, R.string.own, null,
					Type.GALLERY)};

	/**
	 * Resolve foreground resource property with default if not found
	 * 
	 * @param name
	 *            name of the resource
	 * @return bitmap identifier
	 */
	public static int resolveResource(String name) {
		for (Miniature m : minis) {
			if (name.equals(m.getPreferenceValue())) {
				return m.getBitmapResource();
			}
		}

		// assume 'earth' as default front
		return R.drawable.earth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(new ImageAdapter(this, minis));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Miniature mini = minis[position];

		if (mini.getType() == Miniature.Type.GALLERY) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
			return;
		} else {
			storeForegroundPreference(mini.getPreferenceValue(), mini.getType()
					.ordinal(), -1, -1, -1);
		}

		super.finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			Uri _uri = data.getData();

			Cursor cursor = getContentResolver()
					.query(_uri,
							new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
							null, null, null);
			cursor.moveToFirst();

			// TODO crop to circle
			storeForegroundPreference(cursor.getString(0),
					Miniature.Type.GALLERY.ordinal(), -1, -1, -1);
			cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Helper method to store the selected value in user preferences
	 * 
	 * @param foregroundName
	 *            foreground resource value
	 * @param foregroundType
	 *            type of the resource
	 * @param x
	 *            x-axis blooba center
	 * @param y
	 *            y-axis blooba center
	 * @param r
	 *            blooba radius
	 */
	private void storeForegroundPreference(String foregroundName,
			int foregroundType, int x, int y, int r) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("foreground_name", foregroundName);
		editor.putInt("foreground_type", foregroundType);

		if (x > 0 && y > 0 && r > 0) {
			// editor.putInt("foreground_x", x);
			// editor.putInt("foreground_y", y);
			// editor.putInt("foreground_r", r);
		} else {
			editor.remove("foreground_x");
			editor.remove("foreground_y");
			editor.remove("foreground_z");
		}
		editor.commit();
	}

}
