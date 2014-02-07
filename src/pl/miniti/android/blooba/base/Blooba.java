/**
 * Blooba.java
 * Author: marek.brodziak@gmail.com
 * Created: Feb 4, 2014
 * Copyright 2014 by miniti
 */
package pl.miniti.android.blooba.base;

import pl.miniti.android.blooba.base.foreground.ForegroundProvider;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.hardware.SensorEvent;
import android.view.MotionEvent;

/**
 * Blooba object can handle itself within a canvas, when initialized by the
 * service. Controls calculations of gravity and squishiness according to user
 * preferences.
 * 
 * Currently Blooba objects only support rectangular textures with circular
 * contents.
 */
public class Blooba {

	private static final float EPS = 0.0001f;
	private static final double tStep = 1.0 / 10.0;
	private static final int perimIters = 5;
	private static final float mouseRad = 10f;
	private static final int resolution = 10;

	private double[] x, y, xLast, yLast, ax, ay;
	private double blobAreaTarget;
	private double sideLength;
	private int width;
	private int height;
	private int radius;
	private float[] mousePos;
	private float gravityForceY = 9.8f;
	private float gravityForceX = 0;
	private float relaxFactor;
	private int nParts;
	private boolean invertGravity;
	private int speed;
	private ForegroundProvider fProvider;

	/**
	 * Public constructor for creating Blooba objects according to user
	 * preferences
	 * 
	 * @param texture
	 *            bitmap texture selected by the user
	 * @param width
	 *            width of the rendering canvas
	 * @param height
	 *            height of the rendering canvas
	 * @param settings
	 *            user settings of the Blooba
	 */
	public Blooba(ForegroundProvider fProvider, int width, int height,
			BloobaPreferencesWrapper settings) {
		// initialize the variable that we'll need later
		this.width = width;
		this.height = height;

		this.nParts = settings.getQuality();
		this.invertGravity = settings.isGravityInverted();
		this.relaxFactor = settings.getRelaxFactor();
		this.speed = settings.getSpeed();

		// radius is basically half of the texture width
		int size = (int) (Math.min(width, height) * settings.getSize());
		this.fProvider = fProvider;
		this.fProvider.initForSize(size);

		radius = size / 2;

		// initialize all arrays
		x = new double[nParts];
		y = new double[nParts];
		xLast = new double[nParts];
		yLast = new double[nParts];
		ax = new double[nParts];
		ay = new double[nParts];

		// intialize number of points around a circle (as many as required
		// according to the blob quality)
		double cx = width / (2 * resolution);
		double cy = height / (2 * resolution);
		for (int i = 0; i < nParts; ++i) {
			double ang = i * 2 * Math.PI / nParts * 1.0;
			x[i] = cx + Math.sin(ang) * radius / resolution;
			y[i] = cy + Math.cos(ang) * radius / resolution;
			xLast[i] = x[i];
			yLast[i] = y[i];
			ax[i] = 0;
			ay[i] = 0;
		}

		// calculate length of the blob side
		sideLength = Math.sqrt((x[1] - x[0]) * (x[1] - x[0]) + (y[1] - y[0])
				* (y[1] - y[0]));

		// get blob area
		blobAreaTarget = getArea();
		fixPerimeter();
	}

	/**
	 * Callback method invoked by the {@see Engine} on every rendering frame
	 * 
	 * @param canvas
	 *            locked Canvas object on which Blooba is rendered
	 */
	public void requestAnimationFrame(Canvas canvas) {
		for (int i = 0; i < speed; ++i) {
			integrateParticles(tStep);
			constrainBlobEdges();
			collideWithEdge();
			collideWithMouse();
		}
		draw(canvas);
	}

	/**
	 * Callback method invoked by the {@see Engine} on every motion event if
	 * available
	 * 
	 * @param event
	 *            {@MotionEvent} being user tap or slide
	 */
	public void registerMotionEvent(MotionEvent event) {
		float[] pos = mapInv(event.getX(), event.getY());
		if (!isPointInBlob(pos)) {
			mousePos = pos;
		}
	}

	/**
	 * Callback method invoked by the {@see Engine} on every gravity sensor
	 * event
	 * 
	 * @param event
	 *            {@SensorEvent} with gravity information
	 */
	public void registerSensorEvent(SensorEvent event) {
		gravityForceX = event.values[0];
		gravityForceY = event.values[1];
	}

	/**
	 * Calculates the are of the blob
	 * 
	 * @return area of the blob
	 */
	private double getArea() {
		double area = 0.0;
		area += x[nParts - 1] * y[0] - x[0] * y[nParts - 1];
		for (int i = 0; i < nParts - 1; ++i) {
			area += x[i] * y[i + 1] - x[i + 1] * y[i];
		}
		area *= 0.5;
		return area;
	}

	/**
	 *  
	 */
	private void fixPerimeter() {
		double[] diffx = new double[nParts];
		double[] diffy = new double[nParts];
		for (int i = 0; i < nParts; ++i) {
			diffx[i] = 0;
			diffy[i] = 0;
		}

		for (int j = 0; j < perimIters; ++j) {
			for (int i = 0; i < nParts; ++i) {
				int next = (i == nParts - 1) ? 0 : i + 1;
				double dx = x[next] - x[i];
				double dy = y[next] - y[i];
				double distance = Math.sqrt(dx * dx + dy * dy);
				if (distance < EPS) {
					distance = 1.0;
				}
				double diffRatio = 1.0 - sideLength / distance;
				diffx[i] += 0.5 * relaxFactor * dx * diffRatio;
				diffy[i] += 0.5 * relaxFactor * dy * diffRatio;
				diffx[next] -= 0.5 * relaxFactor * dx * diffRatio;
				diffy[next] -= 0.5 * relaxFactor * dy * diffRatio;
			}

			for (int i = 0; i < nParts; ++i) {
				x[i] += diffx[i];
				y[i] += diffy[i];
				diffx[i] = 0;
				diffy[i] = 0;
			}
		}
	}

	/**
	 * Applies gravity to the blob
	 * 
	 * @param dt
	 *            speed of the animation, i.e. period of time for each frane
	 */
	private void integrateParticles(double dt) {
		double dtSquared = dt * dt;
		double gravityAddX = -gravityForceX * dtSquared;
		double gravityAddY = (invertGravity ? -gravityForceY : gravityForceY)
				* dtSquared;
		for (int i = 0; i < nParts; ++i) {
			double bufferX = x[i];
			double bufferY = y[i];
			x[i] = 2 * x[i] - xLast[i] + ax[i] * dtSquared + gravityAddX;
			y[i] = 2 * y[i] - yLast[i] + ay[i] * dtSquared + gravityAddY;
			xLast[i] = bufferX;
			yLast[i] = bufferY;
			ax[i] = 0;
			ay[i] = 0;
		}
	}

	/**
	 * Handles blob collisions with the screen edges
	 */
	private void collideWithEdge() {
		for (int i = 0; i < nParts; ++i) {
			if (x[i] < 0) {
				x[i] = 0;
				yLast[i] = y[i];
			} else if (x[i] > width / resolution) {
				x[i] = width / resolution;
				yLast[i] = y[i];
			}
			if (y[i] < 0) {
				y[i] = 0;
				xLast[i] = x[i];
			} else if (y[i] > height / resolution) {
				y[i] = height / resolution;
				xLast[i] = x[i];
			}
		}
	}

	/**
	 * Handles blob colllisions with the mouse/touch
	 */
	private void collideWithMouse() {
		if (mousePos == null) {
			return;
		}
		if (isPointInBlob(mousePos)) {
			mousePos[1] = 1000;
		}
		float mx = mousePos[0];
		float my = mousePos[1];
		for (int i = 0; i < nParts; ++i) {
			double dx = mx - x[i];
			double dy = my - y[i];
			double distSqr = dx * dx + dy * dy;
			if (distSqr > mouseRad * mouseRad)
				continue;
			if (distSqr < EPS * EPS)
				continue;
			double distance = Math.sqrt(distSqr);
			x[i] -= dx * (mouseRad / distance - 1.0);
			y[i] -= dy * (mouseRad / distance - 1.0);
		}
		mousePos = null;
	}

	/**
	 * Tests if a given point is located inside the blob
	 * 
	 * @param p
	 *            two-dimentional array defining a point
	 * @return true if this point is located inside the blob, false otherwise
	 */
	@SuppressWarnings("unused")
	private boolean isPointInBlob(float[] p) {
		boolean c = false;
		int i = -1;
		int l = nParts;
		for (int j = l - 1; ++i < l; j = i) {
			boolean temp = ((y[i] <= p[1] && p[1] < y[j]) || (y[j] <= p[1] && p[1] < y[i]))
					&& (p[0] < (x[j] - x[i]) * (p[1] - y[i]) / (y[j] - y[i])
							+ x[i]) && (c = !c);
		}
		return c;
	}

	/**
	 * Makes sure all points are in order to keep the blob area constant
	 */
	private void constrainBlobEdges() {
		fixPerimeter();
		double perimeter = 0.0;
		double[] nx = new double[nParts];
		double[] ny = new double[nParts];
		for (int i = 0; i < nParts; ++i) {
			int next = (i == nParts - 1) ? 0 : i + 1;
			double dx = x[next] - x[i];
			double dy = y[next] - y[i];
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance < EPS)
				distance = 1.0;
			nx[i] = dy / distance;
			ny[i] = -dx / distance;
			perimeter += distance;
		}

		double deltaArea = blobAreaTarget - getArea();
		double toExtrude = 0.5 * deltaArea / perimeter;

		for (int i = 0; i < nParts; ++i) {
			int next = (i == nParts - 1) ? 0 : i + 1;
			x[next] += toExtrude * (nx[i] + nx[next]);
			y[next] += toExtrude * (ny[i] + ny[next]);
		}
	}

	/**
	 * @param canvas
	 */
	private void draw(Canvas canvas) {
		double center_x = 0;
		double center_y = 0;
		for (int i = 0; i < nParts; ++i) {
			center_x += x[i];
			center_y += y[i];
		}
		center_x /= nParts * 1.0;
		center_y /= nParts * 1.0;
		double[] p1 = map(center_x, center_y);

		Bitmap texture = fProvider.getTexture((float) (center_x * resolution),
				(float) (center_y * resolution), radius * 2);

		int n = nParts / 2;
		for (int i = 0; i < n; ++i) {
			int j = i * nParts / n;
			int k = (i + 1) * nParts / n;
			if (k == nParts) {
				k = 0;
			}
			double[] p2 = map(x[j], y[j]);
			double[] p3 = map(x[k], y[k]);
			double a1 = 2 * Math.PI * (i * 1.0 / n);
			double a2 = 2 * Math.PI * ((i * 1.0 + 1) / n);
			double[] p4 = new double[]{radius + Math.sin(a1) * radius,
					radius + Math.cos(a1) * radius};
			double[] p5 = new double[]{radius + Math.sin(a2) * radius,
					radius + Math.cos(a2) * radius};

			textureMap(texture, canvas, new double[][]{
					{p1[0], p1[1], radius, radius},
					{p2[0], p2[1], p4[0], p4[1]}, {p3[0], p3[1], p5[0], p5[1]}});

		}

		if (fProvider.isDynamic()) {
			texture.recycle();
		}
	}

	/**
	 * Map a given point to the full resolution of the screen
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private double[] map(double x, double y) {
		return new double[]{x * resolution, y * resolution};
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private float[] mapInv(float x, float y) {
		return new float[]{x / resolution, y / resolution};
	}

	/**
	 * @param texture
	 * @param canvas
	 * @param pts
	 */
	private void textureMap(Bitmap texture, Canvas canvas, double[][] pts) {
		double x0 = pts[0][0], x1 = pts[1][0], x2 = pts[2][0];
		double y0 = pts[0][1], y1 = pts[1][1], y2 = pts[2][1];
		double u0 = pts[0][2], u1 = pts[1][2], u2 = pts[2][2];
		double v0 = pts[0][3], v1 = pts[1][3], v2 = pts[2][3];

		double delta = u0 * v1 + v0 * u2 + u1 * v2 - v1 * u2 - v0 * u1 - u0
				* v2;
		double delta_a = x0 * v1 + v0 * x2 + x1 * v2 - v1 * x2 - v0 * x1 - x0
				* v2;
		double delta_b = u0 * x1 + x0 * u2 + u1 * x2 - x1 * u2 - x0 * u1 - u0
				* x2;
		double delta_c = u0 * v1 * x2 + v0 * x1 * u2 + x0 * u1 * v2 - x0 * v1
				* u2 - v0 * u1 * x2 - u0 * x1 * v2;
		double delta_d = y0 * v1 + v0 * y2 + y1 * v2 - v1 * y2 - v0 * y1 - y0
				* v2;
		double delta_e = u0 * y1 + y0 * u2 + u1 * y2 - y1 * u2 - y0 * u1 - u0
				* y2;
		double delta_f = u0 * v1 * y2 + v0 * y1 * u2 + y0 * u1 * v2 - y0 * v1
				* u2 - v0 * u1 * y2 - u0 * y1 * v2;

		canvas.save();

		Path path = new Path();
		path.moveTo((float) x0, (float) y0);
		path.lineTo((float) (x1 + (x1 - x0)), (float) (y1 + (y1 - y0)));
		path.lineTo((float) (x2 + (x2 - x0)), (float) (y2 + (y2 - y0)));
		path.close();
		canvas.clipPath(path);

		Matrix matrix = new Matrix();

		matrix.setValues(new float[]{(float) (delta_a / delta),
				(float) (delta_b / delta), (float) (delta_c / delta),
				(float) (delta_d / delta), (float) (delta_e / delta),
				(float) (delta_f / delta), 0f, 0f, 1f});

		canvas.drawBitmap(texture, matrix, null);

		canvas.restore();
	}

	public void destroy() {
		fProvider.destroy();
	}

}
