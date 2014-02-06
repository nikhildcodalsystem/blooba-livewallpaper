/**
 * BloobaForeground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 6, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import pl.miniti.android.blooba.preferences.ImageAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

/**
 */
public class BloobaForeground extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);

		GridView gridView = (GridView) findViewById(R.id.grid_view);

		// Instance of ImageAdapter Class
		gridView.setAdapter(new ImageAdapter(this, new Integer[]{
				R.drawable.bieber_xs, R.drawable.earth_xs,
				R.drawable.ironman_xs, R.drawable.kenny_xs,
				R.drawable.squish_xs}));
	}
}
