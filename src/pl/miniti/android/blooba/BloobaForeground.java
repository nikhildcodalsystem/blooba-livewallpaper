/**
 * BloobaForeground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import java.io.File;
import java.io.IOException;

import pl.miniti.android.blooba.base.BloobaPreferencesWrapper;
import pl.miniti.android.blooba.base.Preferences;
import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import pl.miniti.android.blooba.preferences.Miniature.Type;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Foreground selection activity
 */
public class BloobaForeground extends Activity implements OnItemClickListener {

	/**
	 * File name for the custom user-selected foreground image
	 */
	private static final String FOREGROUND_JPG = "foreground.jpg";

	/**
	 * Action code for selecting image
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
	private static int resolveResource(String name) {

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

			deleteFile(FOREGROUND_JPG);
			try {
				getFileStreamPath(FOREGROUND_JPG).createNewFile();
			} catch (IOException e) {
				// display an error message
				String errorMessage = "We're are facing a problem using the storage :/";
				Toast toast = Toast.makeText(this, errorMessage,
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			intent.putExtra("scale", true);
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					getFileStreamPath(FOREGROUND_JPG));
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());

			try {
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						PICK_IMAGE);
			} catch (ActivityNotFoundException anfe) {
				// display an error message
				String errorMessage = "Unfortunately your device doesn't support the crop action";
				Toast toast = Toast.makeText(this, errorMessage,
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			return;
		} else {
			storeForegroundPreference(mini.getPreferenceValue(), mini.getType()
					.ordinal());
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

		if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

			// custom foreground
			File custom = getFileStreamPath(FOREGROUND_JPG);
			storeForegroundPreference(custom.getAbsolutePath(),
					Miniature.Type.GALLERY.ordinal());

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
	 */
	private void storeForegroundPreference(String foregroundName,
			int foregroundType) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Preferences.FOREGROUND_NAME, foregroundName);
		editor.putInt(Preferences.FOREGROUND_TYPE, foregroundType);

		editor.commit();
	}

	/**
	 * Load the foreground bitmap based on user preferences
	 * 
	 * @param prefs
	 *            blooba preferences
	 * @param resources
	 *            app resources
	 * @return bitmap resource for the blooba foreground
	 */
	public static final Bitmap getFrontBitmap(BloobaPreferencesWrapper prefs,
			Resources resources) {
		if (prefs.isForegroundUserDefined()) {
			return BitmapFactory.decodeFile(prefs.getForeground());
		} else {
			return BitmapFactory.decodeResource(resources,
					BloobaForeground.resolveResource(prefs.getForeground()));
		}
	}
}
