/**
 * BloobaBackground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import pl.miniti.android.blooba.base.BloobaPreferencesWrapper;
import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import pl.miniti.android.blooba.preferences.Miniature.Type;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
			new Miniature(R.drawable.bg_stars_xs, R.drawable.bg_stars,
					R.string.b_stars, "stars", Type.IMAGE),
			new Miniature(R.drawable.bg_boards_xs, R.drawable.bg_boards,
					R.string.b_boards, "boards", Type.IMAGE),
			new Miniature(R.drawable.bg_green_xs, R.drawable.bg_green,
					R.string.b_green, "green", Type.IMAGE),
			new Miniature(R.drawable.bg_beach_xs, R.drawable.bg_beach,
					R.string.b_beach, "beach", Type.IMAGE),
			new Miniature(R.drawable.bg_underwater_xs,
					R.drawable.bg_underwater, R.string.b_underwater,
					"underwater", Type.IMAGE),
			new Miniature(R.drawable.gallery_xs, 0, R.string.own, null,
					Type.GALLERY)};

	private static final Map<String, Integer> counts = new HashMap<String, Integer>();

	private static final Map<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	/**
	 * Resolve background resource property with default if not found
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

		// assume 'stars' as default background
		return R.drawable.bg_stars;
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

	public static final SoftReference<Bitmap> getBitmap(Resources resources,
			BloobaPreferencesWrapper prefs, int width, int height) {

		final String name = prefs.getBackground();

		if (cache.containsKey(name)) {
			SoftReference<Bitmap> ref = cache.get(name);
			if (ref != null && ref.get() != null) {
				if (counts.containsKey(name)) {
					counts.put(name, counts.get(name) + 1);
				} else {
					counts.put(name, 1);
				}
				return ref;
			}

			cache.remove(name);
			counts.remove(name);
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		int resource = -1;
		if (prefs.isBackgroundUserDefined()) {
			BitmapFactory.decodeFile(name, options);
		} else {
			resource = resolveResource(name);
			BitmapFactory.decodeResource(resources, resource, options);
		}

		boolean vertical = options.outHeight > options.outWidth;
		boolean rotate = false;
		if (height > width) {
			// screen vertical
			rotate = !vertical;
		} else {
			// screen horizontal
			rotate = vertical;
		}

		if (rotate) {
			// switch w with h
			int temp = width;
			width = height;
			height = temp;
		}

		options = new BitmapFactory.Options();

		final int h = options.outHeight;
		final int w = options.outWidth;
		int inSampleSize = 1;

		if (h > height || w > width) {
			final int halfHeight = h / 2;
			final int halfWidth = w / 2;
			while ((halfHeight / inSampleSize) > height
					&& (halfWidth / inSampleSize) > width) {
				inSampleSize *= 2;
			}
		}

		options.inSampleSize = inSampleSize;

		Bitmap bitmap = null;
		if (prefs.isBackgroundUserDefined()) {
			bitmap = BitmapFactory.decodeFile(name, options);
		} else {
			bitmap = BitmapFactory.decodeResource(resources, resource, options);
		}

		if (rotate) {
			Bitmap old = bitmap;
			Matrix matrix = new Matrix();
			matrix.setRotate(-90f, bitmap.getWidth() / 2,
					bitmap.getHeight() / 2);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, false);
			old.recycle();
		}

		SoftReference<Bitmap> ref = new SoftReference<Bitmap>(bitmap);
		cache.put(name, ref);
		counts.put(name, 1);

		return ref;
	}

	public static void free(String name) {
		int count = counts.remove(name);

		if (count > 1) {
			counts.put(name, count - 1);
			return;
		}

		if (cache.containsKey(name)) {
			SoftReference<Bitmap> ref = cache.remove(name);
			if (ref != null && ref.get() != null) {
				ref.get().recycle();
			}
		}
	}
}
