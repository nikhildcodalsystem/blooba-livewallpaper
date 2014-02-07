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
				new Miniature(R.drawable.bieber_xs, "Bieber", this),
				new Miniature(R.drawable.earth_xs, "Earth", this),
				new Miniature(R.drawable.ironman_xs, "Iron Man", this),
				new Miniature(R.drawable.kenny_xs, "Kenny", this),
				new Miniature(R.drawable.squish_xs, "Squishy", this)};

		GridView gridView = (GridView) findViewById(R.id.grid_view);

		gridView.setAdapter(new ImageAdapter(this, minis));
	}

	@Override
	public void onClick(View v) {
		super.finish();
	}

}
