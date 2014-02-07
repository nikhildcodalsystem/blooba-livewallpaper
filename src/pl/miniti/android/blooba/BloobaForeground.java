/**
 * BloobaForeground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

/**
 */
public class BloobaForeground extends Activity implements View.OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);

		Miniature[] minis = new Miniature[]{
				new Miniature(R.drawable.bieber_xs, R.string.f_bieber, this),
				new Miniature(R.drawable.earth_xs, R.string.f_earth, this),
				new Miniature(R.drawable.ironman_xs, R.string.f_ironman, this),
				new Miniature(R.drawable.kenny_xs, R.string.f_kenny, this),
				new Miniature(R.drawable.squish_xs, R.string.f_squishy, this)};

		GridView gridView = (GridView) findViewById(R.id.grid_view);

		gridView.setAdapter(new ImageAdapter(this, minis));
	}

	@Override
	public void onClick(View v) {
		super.finish();
	}

}
