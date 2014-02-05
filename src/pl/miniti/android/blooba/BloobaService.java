/**
 * BloobaService.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 4, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
		private boolean visible = true;
		private boolean touchEnabled = true;
		private boolean gravityEnabled = true;
		private int quality = 40;
		private float size = .8f;
		private int frontResource = R.drawable.ball;

		/**
		 * 
		 */
		private BloobaEngine() {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(BloobaService.this);

			touchEnabled = prefs.getBoolean("touch", true);
			gravityEnabled = prefs.getBoolean("gravity", true);
			quality = prefs.getInt("quality", 40);
			size = prefs.getFloat("size", .8f);

			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			if (gravityEnabled) {
				gravitySensor = sensorManager
						.getDefaultSensor(Sensor.TYPE_GRAVITY);
				if (gravitySensor == null) {
					gravityEnabled = false;
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
				if (gravityEnabled) {
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
			int blobSize = (int) (Math.min(width, height) * size);
			this.blooba = new Blooba(
					Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
							getResources(), frontResource), blobSize, blobSize,
							false), width, height, quality, 0.9f); // TODO
			super.onSurfaceChanged(holder, format, width, height);
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (touchEnabled) {
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
			if (gravityEnabled) {
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
