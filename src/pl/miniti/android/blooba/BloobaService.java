/**
 * BloobaService.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 4, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Wallpaper service for Blooba which handles instantiation of the Blooba object
 * according to user prefernces and callbacks in regards to the external systems
 * like touch events, sensors, etc.
 */
public class BloobaService extends WallpaperService {

	private SensorManager sensorManager;
	private Sensor gravitySensor;

	/**
	 * 
	 */
	@Override
	public Engine onCreateEngine() {
		return new BloobaEngine();
	}

	/**
	 */
	private class BloobaEngine extends Engine implements SensorEventListener {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {

			@Override
			public void run() {
				draw();
			}

		};
		private Blooba blooba;
		private BloobaPreferencesWrapper bloobaPreferences;
		private boolean visible = true;
		private int frontResource = R.drawable.ball;

		/**
		 * 
		 */
		private BloobaEngine() {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(BloobaService.this);
			bloobaPreferences = BloobaPreferencesWrapper.fromPreferences(preferences);
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			if (bloobaPreferences.isGravityEnabled()) {
				gravitySensor = sensorManager
						.getDefaultSensor(Sensor.TYPE_GRAVITY);
				if (gravitySensor == null) {
					bloobaPreferences.setGravityEnabled(false);
				} else {
					sensorManager.registerListener(this, gravitySensor,
							SensorManager.SENSOR_DELAY_FASTEST);
				}
			}
			handler.post(drawRunner);
		}
		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
				if (bloobaPreferences.isGravityEnabled()) {
					if (gravitySensor != null) {
						sensorManager.registerListener(this, gravitySensor,
								SensorManager.SENSOR_DELAY_FASTEST);
					}
				}
			} else {
				handler.removeCallbacks(drawRunner);
				sensorManager.unregisterListener(this);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			// TODO battery level implementation
			this.blooba = new Blooba(BitmapFactory.decodeResource(
					getResources(), frontResource), width, height, bloobaPreferences);
			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (bloobaPreferences.isTouchEnabled()) {
				blooba.registerMotionEvent(event);
			} else {
				super.onTouchEvent(event);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// do nothing
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (bloobaPreferences.isGravityEnabled()) {
				blooba.registerSensorEvent(event);
			}
		}

		/**
		 * Renders the background and invokes Blooba callback. Afterwards a new
		 * rendering frame will be requested by the engine.
		 */
		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null && blooba != null) {
					canvas.drawColor(Color.BLACK);
					blooba.requestAnimationFrame(canvas);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 41);
			}
		}

	}

}
