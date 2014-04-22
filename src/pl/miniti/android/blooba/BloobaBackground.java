/**
 * BloobaBackground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
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
 * Background selection activity
 */
public class BloobaBackground extends Activity implements OnItemClickListener {

	/**
	 * Action code
	 */
	private static final int PICK_IMAGE = 304;

	/**
	 * Statically defined array of available backgrounds
	 */
	public final static Miniature[] minis = new Miniature[]{
			new Miniature(R.drawable.bg_stars_xs, R.string.b_stars, "stars",
					Type.IMAGE),
			new Miniature(R.drawable.bg_boards_xs, R.string.b_boards, "boards",
					Type.IMAGE),
			new Miniature(R.drawable.bg_green_xs, R.string.b_green, "green",
					Type.IMAGE),
			new Miniature(R.drawable.bg_beach_xs, R.string.b_beach, "beach",
					Type.IMAGE),
			new Miniature(R.drawable.bg_underwater_xs, R.string.b_underwater,
					"underwater", Type.IMAGE),
			new Miniature(R.drawable.gallery_xs, R.string.own, null,
					Type.GALLERY)};

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
			storeBackgroundPreference(mini.getPreferenceValue(), mini.getType()
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
		if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
			Uri _uri = data.getData();

			Cursor cursor = getContentResolver()
					.query(_uri,
							new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
							null, null, null);
			cursor.moveToFirst();
			storeBackgroundPreference(cursor.getString(0),
					Miniature.Type.GALLERY.ordinal());
			cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Helper method to store the selected value in user preferences
	 * 
	 * @param backgroundName
	 *            background resource value
	 * @param backgroundType
	 *            type of the resource
	 */
	private void storeBackgroundPreference(String backgroundName,
			int backgroundType) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("background_name", backgroundName);
		editor.putInt("background_type", backgroundType);
		editor.commit();
	}

}
