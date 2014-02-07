/**
 * BloobaBackground.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 7, 2014
 * Copyright 2014 by miniti
 */

package pl.miniti.android.blooba;

import pl.miniti.android.blooba.preferences.ImageAdapter;
import pl.miniti.android.blooba.preferences.Miniature;
import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

/**
 */
public class BloobaBackground extends Activity {

	private static Miniature[] minis = new Miniature[]{};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_layout);

		GridView gridView = (GridView) findViewById(R.id.grid_view);

		gridView.setAdapter(new ImageAdapter(this, minis));
	}
}
