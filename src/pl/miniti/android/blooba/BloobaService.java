/**
 * BloobaService.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 4, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba;

import java.lang.ref.SoftReference;

import pl.miniti.android.blooba.base.Blooba;
import pl.miniti.android.blooba.base.BloobaPreferencesWrapper;
import pl.miniti.android.blooba.base.Preferences;
import pl.miniti.android.blooba.base.foreground.ForegroundProvider;
import pl.miniti.android.blooba.base.foreground.ImageForegroundProvider;
import pl.miniti.android.blooba.base.foreground.ReflectionForegroundProvider;
import pl.miniti.android.blooba.preferences.Miniature;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
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

	/**
	 * Instance of the sensor manager
	 */
	private SensorManager sensorManager;

	/**
	 * Instance of the gravity sensor
	 */
	private Sensor gravitySensor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.service.wallpaper.WallpaperService#onCreateEngine()
	 */
	@Override
	public Engine onCreateEngine() {
		return new BloobaEngine();
	}

	/**
	 * Implementation of the blooba live wallpaper engine
	 */
	private class BloobaEngine extends Engine
			implements
				SensorEventListener,
				OnSharedPreferenceChangeListener {
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
		private SoftReference<Bitmap> background;
		private int width;
		private int height;

		/**
		 * 
		 */
		private BloobaEngine() {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(BloobaService.this);
			preferences.registerOnSharedPreferenceChangeListener(this);
			handler.post(drawRunner);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.SharedPreferences.OnSharedPreferenceChangeListener
		 * #onSharedPreferenceChanged(android.content.SharedPreferences,
		 * java.lang.String)
		 */
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {

			String currentBackground = bloobaPreferences.getBackground();

			bloobaPreferences = BloobaPreferencesWrapper
					.fromPreferences(sharedPreferences);
			if (key.equals(Preferences.ENABLE_TOUCH)) {
				// do nothing -- handled in the onTouchEvent() method
			} else if (key.equals(Preferences.INVERT_GRAVITY)) {
				blooba.setInvertGravity(bloobaPreferences.isGravityInverted());
			} else if (key.equals(Preferences.RELAX_FACTOR)) {
				blooba.setRelaxFactor(bloobaPreferences.getRelaxFactor());
			} else if (key.equals(Preferences.SPEED)) {
				blooba.setSpeed(bloobaPreferences.getSpeed());
			} else if (key.equals(Preferences.FOREGROUND_NAME)) {
				blooba.setForegroundProvider(getForegroundProvider());
			} else if (key.equals(Preferences.BACKGROUND_NAME)) {
				BloobaBackground.free(currentBackground);
				loadBackground();
				blooba.getForegroundProvider().setBackground(background.get());
			} else {
				// size && quality
				newBlooba();
			}
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.service.wallpaper.WallpaperService.Engine#onVisibilityChanged
		 * (boolean)
		 */
		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
				if (gravitySensor != null) {
					sensorManager.registerListener(this, gravitySensor,
							SensorManager.SENSOR_DELAY_FASTEST);
				}
			} else {
				handler.removeCallbacks(drawRunner);
				sensorManager.unregisterListener(this);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.service.wallpaper.WallpaperService.Engine#onSurfaceDestroyed
		 * (android.view.SurfaceHolder)
		 */
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.service.wallpaper.WallpaperService.Engine#onSurfaceChanged
		 * (android.view.SurfaceHolder, int, int, int)
		 */
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			this.width = width;
			this.height = height;
			newBlooba();
			super.onSurfaceChanged(holder, format, width, height);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.service.wallpaper.WallpaperService.Engine#onTouchEvent(android
		 * .view.MotionEvent)
		 */
		@Override
		public void onTouchEvent(MotionEvent event) {
			if (blooba != null && bloobaPreferences.isTouchEnabled()) {
				blooba.registerMotionEvent(event);
			} else {
				super.onTouchEvent(event);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
		 * .Sensor, int)
		 */
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// do nothing
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
		 * .SensorEvent)
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (blooba != null) {
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
				if (canvas != null && blooba != null
						&& background.get() != null) {

					Bitmap bitmap = background.get();
					canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(),
							bitmap.getHeight()), new Rect(0, 0, width, height),
							null);

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
		/**
		 *  
		 */
		private void newBlooba() {
			if (blooba != null) {
				BloobaBackground.free(bloobaPreferences.getBackground());
				background = null;

				blooba.destroy();
				blooba = null;
			}

			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(BloobaService.this);
			bloobaPreferences = BloobaPreferencesWrapper
					.fromPreferences(preferences);
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			gravitySensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (gravitySensor != null) {
				sensorManager.registerListener(this, gravitySensor,
						SensorManager.SENSOR_DELAY_FASTEST);
			}

			loadBackground();

			blooba = new Blooba(getForegroundProvider(), width, height,
					bloobaPreferences);
		}

		/**
		 *  
		 */
		private void loadBackground() {
			background = BloobaBackground.getBitmap(getResources(),
					bloobaPreferences, width, height);
		}

		/**
		 * @return
		 */
		private ForegroundProvider getForegroundProvider() {
			Miniature.Type fType = Miniature.Type.values()[bloobaPreferences
					.getForegroundType()];
			switch (fType) {
				case REFLECTION :
					return new ReflectionForegroundProvider(
							BloobaForeground.getFrontBitmap(bloobaPreferences,
									getResources()), this.background.get());
				case IMAGE :
				default :
					return new ImageForegroundProvider(
							BloobaForeground.getFrontBitmap(bloobaPreferences,
									getResources()));
			}
		}

	}

}
