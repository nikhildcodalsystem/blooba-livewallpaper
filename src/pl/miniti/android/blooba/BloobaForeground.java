/**
 * BloobaForeground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.miniti.android.blooba.base.BloobaPreferencesWrapper;
import pl.miniti.android.blooba.base.Preferences;
import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import pl.miniti.android.blooba.preferences.Miniature.Type;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	 * Action code for cropping an image
	 */
	private static final int CROP_IMAGE = 405;

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

			// Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// TODO check available

			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);

			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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
		File file = new File(getFilesDir(), FOREGROUND_JPG);

		if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
			Uri _uri = data.getData();

			Cursor cursor = getContentResolver()
					.query(_uri,
							new String[]{android.provider.MediaStore.Images.ImageColumns.DATA},
							null, null, null);
			cursor.moveToFirst();
			String fileName = cursor.getString(0);
			cursor.close();

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(Uri.fromFile(new File(fileName)),
					"image/*");
			cropIntent.putExtra("crop", "true");
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("scale", true);
			cropIntent.putExtra("outputX", 400);
			cropIntent.putExtra("outputY", 400);
			cropIntent.putExtra("return-data", true);

			startActivityForResult(cropIntent, CROP_IMAGE);

			return;

		} else if (requestCode == CROP_IMAGE
				&& (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED)) {

			if (data != null) {
				Bundle extras = data.getExtras();
				Bitmap selectedBitmap = extras.getParcelable("data");

				FileOutputStream stream = null;
				try {
					file.delete();
					file.createNewFile();

					Bitmap cropped = cropToCircle(selectedBitmap);
					selectedBitmap.recycle();

					stream = openFileOutput(FOREGROUND_JPG,
							Context.MODE_PRIVATE);
					cropped.compress(Bitmap.CompressFormat.PNG, 90, stream);

					cropped.recycle();

				} catch (IOException e) {
					Toast.makeText(this, R.string.error_crop, Toast.LENGTH_LONG)
							.show();
				} finally {
					if (stream != null) {
						try {
							stream.close();
						} catch (Throwable ignore) {
						}
					}
				}
			}

			storeForegroundPreference(
					String.valueOf(System.currentTimeMillis()),
					Miniature.Type.GALLERY.ordinal());

			super.finish();
			return;
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
	public static final Bitmap getFrontBitmap(Context context,
			BloobaPreferencesWrapper prefs, Resources resources) {
		if (prefs.isForegroundUserDefined()) {
			Bitmap bitmap = BitmapFactory.decodeFile(new File(context
					.getFilesDir(), FOREGROUND_JPG).getAbsolutePath());
			if (bitmap != null) {
				return bitmap;
			}
		}

		return BitmapFactory.decodeResource(resources,
				BloobaForeground.resolveResource(prefs.getForeground()));
	}

	/**
	 * Crop the given bitmap to a circle
	 * 
	 * @param bitmap
	 *            squared circle
	 * @return cropped bitmap
	 */
	private static Bitmap cropToCircle(Bitmap bitmap) {
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		final Path path = new Path();
		path.addCircle((float) (width / 2), (float) (height / 2),
				(float) Math.min(width, (height / 2)), Path.Direction.CCW);

		final Canvas canvas = new Canvas(outputBitmap);
		canvas.clipPath(path);
		canvas.drawBitmap(bitmap, 0, 0, null);

		return outputBitmap;
	}
}
