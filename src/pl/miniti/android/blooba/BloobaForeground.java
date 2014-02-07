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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 */
public class BloobaForeground extends Activity implements OnItemClickListener {

	private final Miniature[] minis = new Miniature[]{
			new Miniature(R.drawable.bieber_xs, R.string.f_bieber,
					R.drawable.bieber, Type.IMAGE),
			new Miniature(R.drawable.earth_xs, R.string.f_earth,
					R.drawable.earth, Type.IMAGE),
			new Miniature(R.drawable.ironman_xs, R.string.f_ironman,
					R.drawable.ironman, Type.IMAGE),
			new Miniature(R.drawable.kenny_xs, R.string.f_kenny,
					R.drawable.kenny, Type.IMAGE),
			new Miniature(R.drawable.squish_xs, R.string.f_squishy,
					R.drawable.squish, Type.IMAGE)};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(new ImageAdapter(this, minis));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Miniature mini = minis[position];
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("foreground", mini.getResource());
		editor.putInt("foreground_type", mini.getType().ordinal());
		editor.commit();
		super.finish();
	}

}
