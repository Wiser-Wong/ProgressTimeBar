package com.wiser.timebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author Wiser
 * 
 *         进度Color分割
 */
public class ProgressValueColor extends View {

	private int				progressColor	= Color.GRAY;	// 默认颜色

	private float			progressHeight	= 14;			// 进度条高度

	private float			progressRoundRadius;			// 进度圆弧

	private RectF			progressRectF;					// 进度条矩阵

	private Paint			progressPaint;					// 进度条画笔

	private Path			progressPath;					// 进度条默认path

	private Path[]			paths;

	private RectF[]			rectFS;

	private @ColorRes int[]	colors;

	private float[]			values;

	private float			maxValue;

	private boolean			isFirst			= true;

	public ProgressValueColor(Context context) {
		super(context);
		init(context, null);
	}

	public ProgressValueColor(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
		this.setWillNotDraw(false); // 调用此方法后，才会执行 onDraw(Canvas) 方法

		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressValueColor);
			progressHeight = typedArray.getDimension(R.styleable.ProgressValueColor_progressValueColorHeight, progressHeight);
			progressRoundRadius = typedArray.getDimension(R.styleable.ProgressValueColor_progressValueColorRoundRadius, 0);
			progressColor = typedArray.getColor(R.styleable.ProgressValueColor_progressValueColorBackgroundColor, getResources().getColor(android.R.color.darker_gray));
			maxValue = typedArray.getFloat(R.styleable.ProgressValueColor_progressValueColorMaxValue, 0);
			typedArray.recycle();
		}

		initPaint();
	}

	private void initPaint() {
		progressPaint = new Paint();
		progressPaint.setStyle(Paint.Style.FILL);
		progressPaint.setAntiAlias(true);
		progressPaint.setColor(progressColor);
		progressPaint.setTextAlign(Paint.Align.CENTER);

		progressRectF = new RectF();

		progressPath = new Path();
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		progressPaint.setColor(progressColor);
		canvas.drawPath(progressPath, progressPaint);

		if (paths == null || paths.length == 0 || colors == null || colors.length == 0 || paths.length != colors.length) return;
		for (int i = 0; i < paths.length; i++) {
			progressPaint.setColor(colors[i]);
			canvas.drawPath(paths[i], progressPaint);
		}
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int width = right - left;
		progressRectF.set(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), getPaddingBottom() + progressHeight);
		colorValues(colors, values, maxValue);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int height = (int) (heightMode == MeasureSpec.UNSPECIFIED ? (progressHeight + getPaddingTop() + getPaddingBottom())
				: heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(progressHeight + getPaddingTop() + getPaddingBottom(), heightSize));
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
	}

	/**
	 * 设置分段颜色
	 * 
	 * @param colors
	 *            颜色数组
	 * @param values
	 *            值数组
	 * @param maxValue
	 *            最大值
	 */
	public void setColorValues(@ColorRes int[] colors, float[] values, float maxValue) {
		colorValues(colors, values, maxValue);
	}

	/**
	 * 设置分段颜色
	 * 
	 * @param colors
	 *            颜色数组
	 * @param values
	 *            值数组
	 */
	public void setColorValues(@ColorRes int[] colors, float[] values) {
		colorValues(colors, values, this.maxValue);
	}

	private void colorValues(@ColorRes int[] colors, float[] values, float maxValue) {
		if (colors == null || values == null || values.length == 0 || colors.length != values.length) {
			return;
		}
		this.colors = colors;
		this.values = values;
		this.maxValue = maxValue;
		if (isFirst) {
			isFirst = false;
			return;
		}
		if (paths == null) paths = new Path[values.length];
		else {
			paths = null;
			paths = new Path[values.length];
		}
		if (rectFS == null) rectFS = new RectF[values.length];
		else {
			rectFS = null;
			rectFS = new RectF[values.length];
		}
		float totalValue = 0;
		boolean isHasNegative = false;
		for (int i = 0; i < values.length; i++) {
			if (values[i] < 0) {
				isHasNegative = true;
				break;
			}
			paths[i] = new Path();
			rectFS[i] = new RectF();
			totalValue += values[i];
		}
		if (isHasNegative) {// 是否存在负数值
			paths = null;
			rectFS = null;
			Log.e(ProgressValueColor.class.getName(), "数值中不能存在负数值");
			return;
		}
		if (totalValue > maxValue) {
			paths = null;
			rectFS = null;
			Log.e(ProgressValueColor.class.getName(), "总值不能超过最大值");
			return;
		}

		progressPath.addRoundRect(progressRectF,
				new float[] { progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius },
				Path.Direction.CW);
		for (int i = 0; i < rectFS.length; i++) {
			paths[i].reset();
			if (i == 0) rectFS[i].set(progressRectF.left, progressRectF.top, progressRectF.left + (values[i] * (progressRectF.right - progressRectF.left) / maxValue), progressRectF.bottom);
			else if (i == rectFS.length - 1) {
				if (totalValue == maxValue) rectFS[i].set(rectFS[i - 1].right, progressRectF.top, progressRectF.right, progressRectF.bottom);
				else rectFS[i].set(rectFS[i - 1].right, progressRectF.top, rectFS[i - 1].right + (values[i] * (progressRectF.right - progressRectF.left) / maxValue), progressRectF.bottom);
			} else rectFS[i].set(rectFS[i - 1].right, progressRectF.top, rectFS[i - 1].right + (values[i] * (progressRectF.right - progressRectF.left) / maxValue), progressRectF.bottom);

			if (i == 0 && i == rectFS.length - 1) paths[i].addRoundRect(rectFS[i], new float[] { progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius,
					progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius }, Path.Direction.CCW);
			else if (i == 0) paths[i].addRoundRect(rectFS[i], new float[] { progressRoundRadius, progressRoundRadius, 0, 0, 0, 0, progressRoundRadius, progressRoundRadius }, Path.Direction.CCW);
			else if (i == rectFS.length - 1) {
				if (totalValue == maxValue)
					paths[i].addRoundRect(rectFS[i], new float[] { 0, 0, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, 0, 0 }, Path.Direction.CCW);
				else paths[i].addRoundRect(rectFS[i], 0, 0, Path.Direction.CW);
			} else paths[i].addRoundRect(rectFS[i], 0, 0, Path.Direction.CW);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				paths[i].op(progressPath, Path.Op.INTERSECT); // 交集
			}
		}

		postInvalidate();
	}

	@Override protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// detach();
	}

	public void detach() {
		progressRectF = null;
		progressPaint = null;
		paths = null;
		rectFS = null;
		colors = null;
		values = null;
	}

}
