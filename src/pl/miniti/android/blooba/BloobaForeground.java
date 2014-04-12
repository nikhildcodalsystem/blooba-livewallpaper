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
			new Miniature(R.drawable.earth_xs, R.string.f_earth, "earth",
					Type.IMAGE),
			new Miniature(R.drawable.moon_xs, R.string.f_moon, "moon",
					Type.IMAGE),
			new Miniature(R.drawable.kenny_xs, R.string.f_kenny, "kenny",
					Type.IMAGE),
			new Miniature(R.drawable.squish_xs, R.string.f_squishy, "squish",
					Type.IMAGE),
			new Miniature(R.drawable.bubble_xs, R.string.f_bubble, "bubble",
					Type.IMAGE),
			new Miniature(R.drawable.water_xs, R.string.f_water, "bubble",
					Type.REFLECTION),
			new Miniature(R.drawable.basketball_xs, R.string.f_basketball,
					"basketball", Type.IMAGE),
			new Miniature(R.drawable.nemo_xs, R.string.f_nemo, "nemo",
					Type.IMAGE),
			new Miniature(R.drawable.america_xs, R.string.f_america, "america",
					Type.IMAGE),
			new Miniature(R.drawable.balloon_pink_xs, R.string.f_bpink,
					"bpink", Type.IMAGE),
			new Miniature(R.drawable.balloon_black_xs, R.string.f_bblack,
					"bblack", Type.IMAGE),
			new Miniature(R.drawable.donut_xs, R.string.f_donut, "donut",
					Type.IMAGE),
			new Miniature(R.drawable.penny_xs, R.string.f_penny, "penny",
					Type.IMAGE),
			new Miniature(R.drawable.spider_xs, R.string.f_spider, "spider",
					Type.IMAGE)};

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
		editor.putString("foreground_name", mini.getResource());
		editor.putInt("foreground_type", mini.getType().ordinal());
		editor.commit();
		super.finish();
	}

}
