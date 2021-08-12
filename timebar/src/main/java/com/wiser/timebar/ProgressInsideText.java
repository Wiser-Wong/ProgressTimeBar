package com.wiser.timebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Wiser
 * 
 *         进度条内文本ProgressText
 */
public class ProgressInsideText extends View {

	private Paint			progressUnPlayPaint;									// 进度条未播放画笔

	private Paint			progressPlayPaint;										// 进度条播放画笔

	private Paint			progressInsideTextPaint;								// 进度条内文本画笔

	private int				progressUnPlayColor				= Color.RED;			// 进度条未播放颜色

	private int				progressPlayColor				= Color.BLUE;			// 进度条播放颜色

	private float			progressHeight					= 14;					// 进度条高度

	private int				progressRoundRadius;									// 进度条矩形圆角半径

	private RectF			progressUnPlayRect;										// 进度条未播放矩阵

	private RectF			progressPlayRect;										// 进度条播放矩阵

	private Path			progressUnPlayPath;										// 进度条未播放Path路径

	private Path			progressPlayPath;										// 进度条播放Path路径

	private long			currentProgress;										// 当前进度

	private long			maxProgress;											// 总进度

	private LinearGradient	linearGradientUnPlay;									// 未播放渐变组件

	private LinearGradient	linearGradientPlay;										// 播放渐变组件

	private int				unPlayStartColor, unPlayCenterColor, unPlayEndColor;	// 未播放渐变颜色值

	private int				playStartColor, playCenterColor, playEndColor;			// 播放渐变颜色值

	private boolean			isWrapProgressInsideTextWidth	= false;				// 是否适配进度条内文本宽度

	private String			progressInsideText				= "";					// 进度条内文本

	private float			progressInsideTextSize			= 20;					// 进度条内文本大小

	private int				progressInsideTextColor			= Color.RED;			// 进度条内文本颜色

	private float			progressInsidePadding;									// 进度条内文本padding

	private float			progressInsidePaddingStart;								// 进度条内文本paddingStart

	private float			progressInsidePaddingTop;								// 进度条内文本paddingTop

	private float			progressInsidePaddingEnd;								// 进度条内文本paddingEnd

	private float			progressInsidePaddingBottom;							// 进度条内文本paddingBottom

	private boolean			isProgressRadius;										// 进度条右侧切割进度是否是圆角

	public ProgressInsideText(Context context) {
		super(context);
		init(context, null);
	}

	public ProgressInsideText(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
		this.setWillNotDraw(false); // 调用此方法后，才会执行 onDraw(Canvas) 方法

		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressInsideText);
			progressUnPlayColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsideUnPlayColor, getResources().getColor(android.R.color.white));
			progressPlayColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsidePlayColor, getResources().getColor(android.R.color.holo_blue_dark));
			isWrapProgressInsideTextWidth = typedArray.getBoolean(R.styleable.ProgressInsideText_progressIsInsideTextWrapWidth, isWrapProgressInsideTextWidth);
			progressInsideText = typedArray.getString(R.styleable.ProgressInsideText_progressInsideText);
			progressInsideTextSize = typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideTextSize, progressInsideTextSize);
			progressInsideTextColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsideTextColor, progressInsideTextColor);
			progressInsidePadding = typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideTextPadding, progressInsidePadding);
			progressInsidePaddingStart = typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideTextPaddingStart, progressInsidePaddingStart);
			progressInsidePaddingTop = typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideTextPaddingTop, progressInsidePaddingTop);
			progressInsidePaddingEnd = typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideTextPaddingEnd, progressInsidePaddingEnd);
			progressInsidePaddingBottom = typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideTextPaddingBottom, progressInsidePaddingBottom);
			progressRoundRadius = (int) typedArray.getDimension(R.styleable.ProgressInsideText_progressInsideRoundRadius, progressRoundRadius);
			unPlayStartColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsideUnPlayStartColor, 0);
			unPlayCenterColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsideUnPlayCenterColor, 0);
			unPlayEndColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsideUnPlayEndColor, 0);
			playStartColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsidePlayStartColor, 0);
			playCenterColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsidePlayCenterColor, 0);
			playEndColor = typedArray.getColor(R.styleable.ProgressInsideText_progressInsidePlayEndColor, 0);
			isProgressRadius = typedArray.getBoolean(R.styleable.ProgressInsideText_progressInsideIsRadius, isProgressRadius);
			maxProgress = typedArray.getInt(R.styleable.ProgressInsideText_progressInsideMaxProgress, 0);
			currentProgress = typedArray.getInt(R.styleable.ProgressInsideText_progressInsideCurrentProgress, 0);
			typedArray.recycle();
		}

		initPaint();

		progressHeight = getTextValue(progressInsideTextPaint, progressInsideText)[1] + getPaddingTopAndBottomDistance();

		initRect();

		initPath();
	}

	// 初始化画笔
	private void initPaint() {
		progressUnPlayPaint = new Paint();
		progressUnPlayPaint.setStyle(Paint.Style.FILL);
		progressUnPlayPaint.setAntiAlias(true);
		progressUnPlayPaint.setColor(progressUnPlayColor);
		progressUnPlayPaint.setDither(true); // 启用抗颜色抖动（可以让渐变更平缓）
		progressUnPlayPaint.setTextAlign(Paint.Align.CENTER);

		progressPlayPaint = new Paint();
		progressPlayPaint.setStyle(Paint.Style.FILL);
		progressPlayPaint.setAntiAlias(true);
		progressPlayPaint.setColor(progressPlayColor);
		progressPlayPaint.setDither(true); // 启用抗颜色抖动（可以让渐变更平缓）
		progressPlayPaint.setTextAlign(Paint.Align.CENTER);

		progressInsideTextPaint = new Paint();
		progressInsideTextPaint.setStyle(Paint.Style.FILL);
		progressInsideTextPaint.setAntiAlias(true);
		progressInsideTextPaint.setColor(progressInsideTextColor);
		progressInsideTextPaint.setTextSize(progressInsideTextSize);
		progressInsideTextPaint.setTextAlign(Paint.Align.CENTER);
	}

	// 初始化矩阵
	private void initRect() {
		progressUnPlayRect = new RectF();
		progressPlayRect = new RectF();
	}

	// 初始化Path
	private void initPath() {
		progressUnPlayPath = new Path();
		progressPlayPath = new Path();
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();

		refreshRect();

		// 画进度条
		canvasProgress(canvas);

		// 画进度内文本
		canvasProgressInsideText(canvas);

		canvas.restore();

	}

	// 画进度条
	private void canvasProgress(Canvas canvas) {
		// 画未播放进度
		canvas.drawPath(progressUnPlayPath, progressUnPlayPaint);
		// 画播放进度
		canvas.drawPath(progressPlayPath, progressPlayPaint);
	}

	// 画进度条内文本
	private void canvasProgressInsideText(Canvas canvas) {
		if (TextUtils.isEmpty(progressInsideText)) return;
		// 计算baseline
		Paint.FontMetrics fontMetrics = progressInsideTextPaint.getFontMetrics();
		float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
		if (isHasProgressInsidePadding()) {
			canvas.drawText(progressInsideText, progressUnPlayRect.centerX(), progressUnPlayRect.centerY() + distance, progressInsideTextPaint);
		} else {
			float baselineX = progressUnPlayRect.centerX() - progressUnPlayRect.width() / 2 + getTextValue(progressInsideTextPaint, progressInsideText)[0] / 2f + progressInsidePaddingStart;
			float baselineY = progressUnPlayRect.centerY() + distance - progressUnPlayRect.height() / 2 + getTextValue(progressInsideTextPaint, progressInsideText)[1] / 2f + progressInsidePaddingTop;
			canvas.drawText(progressInsideText, baselineX, baselineY, progressInsideTextPaint);
		}
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int width = right - left;
		// 未播放矩形坐标
		progressUnPlayRect.set(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), getPaddingBottom() + progressHeight);
		// 播放矩形坐标与未播放相同
		progressPlayRect.set(progressUnPlayRect);

		refreshRect();
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int height = (int) (heightMode == MeasureSpec.UNSPECIFIED ? (progressHeight + getPaddingTop() + getPaddingBottom())
				: heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(progressHeight + getPaddingTop() + getPaddingBottom(), heightSize));
		int width;
		if (isWrapProgressInsideTextWidth && !TextUtils.isEmpty(progressInsideText)) {
			width = (int) (widthMode == MeasureSpec.UNSPECIFIED
					? (getPaddingStartAndEndDistance() + getTextValue(progressInsideTextPaint, progressInsideText)[0] + getPaddingLeft() + getPaddingRight())
					: widthMode == MeasureSpec.EXACTLY ? widthSize
							: Math.min(getPaddingStartAndEndDistance() + getTextValue(progressInsideTextPaint, progressInsideText)[0] + getPaddingLeft() + getPaddingRight(), widthSize));
		} else {
			width = MeasureSpec.getSize(widthMeasureSpec);
		}
		setMeasuredDimension(width, height);
	}

	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 设置渐变
		setLinearGradient(1, unPlayStartColor, unPlayCenterColor, unPlayEndColor);
		setLinearGradient(2, playStartColor, playCenterColor, playEndColor);
	}

	// 设置渐变以及计算color以及权重
	private void setLinearGradient(int mode, int startColor, int centerColor, int endColor) {
		int count = 0;
		if (startColor != 0) {
			count++;
		}
		if (centerColor != 0) {
			count++;
		}
		if (endColor != 0) {
			count++;
		}
		if (count == 0) return;
		int[] gradientColors = new int[count];
		float[] weights = new float[count];
		switch (count) {
			case 1:
				gradientColors[0] = startColor != 0 ? startColor : centerColor != 0 ? centerColor : endColor;
				weights[0] = 1;
				break;
			case 2:
				gradientColors[0] = startColor != 0 ? startColor : centerColor;
				gradientColors[1] = endColor != 0 ? endColor : centerColor;
				weights[0] = 0;
				weights[1] = 1;
				break;
			case 3:
				gradientColors[0] = startColor;
				gradientColors[1] = centerColor;
				gradientColors[2] = endColor;
				weights[0] = 0;
				weights[1] = 0.5f;
				weights[2] = 1;
				break;
		}
		switch (mode) {
			case 1:
				linearGradientUnPlay = new LinearGradient(0, 0, getMeasuredWidth(), 0, gradientColors, weights, Shader.TileMode.CLAMP);
				progressUnPlayPaint.setShader(linearGradientUnPlay);
				break;
			case 2:
				linearGradientPlay = new LinearGradient(0, 0, getMeasuredWidth(), 0, gradientColors, weights, Shader.TileMode.CLAMP);
				progressPlayPaint.setShader(linearGradientPlay);
				break;
		}
	}

	// 刷新rect
	private void refreshRect() {
		progressPlayPath.reset();
		progressUnPlayPath.reset();
		progressPlayRect.right = progressUnPlayRect.left + this.currentProgress * (progressUnPlayRect.right - progressUnPlayRect.left) / maxProgress;
		progressUnPlayPath.addRoundRect(progressUnPlayRect,
				new float[] { progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius, progressRoundRadius },
				Path.Direction.CW);
		float radius = 0;
		if (isProgressRadius) {
			radius = progressRoundRadius;
		}
		progressPlayPath.addRoundRect(progressPlayRect, new float[] { progressRoundRadius, progressRoundRadius, radius, radius, radius, radius, progressRoundRadius, progressRoundRadius },
				Path.Direction.CW);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			progressPlayPath.op(progressUnPlayPath, Path.Op.INTERSECT); // 交集
		}
	}

	private boolean isHasProgressInsidePadding() {
		return progressInsidePadding != 0;
	}

	// 获取padding上下距离
	private float getPaddingTopAndBottomDistance() {
		if (progressInsidePadding != 0) {
			return 2 * progressInsidePadding;
		}
		return progressInsidePaddingTop + progressInsidePaddingBottom;
	}

	// 获取padding左右距离
	private float getPaddingStartAndEndDistance() {
		if (progressInsidePadding != 0) {
			return 2 * progressInsidePadding;
		}
		return progressInsidePaddingStart + progressInsidePaddingEnd;
	}

	// 设置总时间
	public void setMaxProgress(long maxProgress) {
		this.maxProgress = maxProgress;
	}

	// 设置当前时间/当前显示的进度内文本内容
	public void setCurrentDuration(long currentDuration, String text) {
		this.currentProgress = currentDuration;
		this.progressInsideText = text;
		postInvalidate();
	}

	// 获取最大时间
	public long getMaxProgress() {
		return maxProgress;
	}

	// 获取当前时间
	public long getCurrentProgress() {
		return currentProgress;
	}

	// 获取文字宽高
	private int[] getTextValue(Paint paint, String text) {
		int[] values = new int[2];
		if (TextUtils.isEmpty(text) || paint == null) {
			values[0] = 0;
			values[1] = 0;
			return values;
		}
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		values[0] = rect.width();
		values[1] = rect.height();
		return values;
	}

	@Override protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		// detach();
	}

	public void detach() {
		progressUnPlayPaint = null;
		progressPlayPaint = null;
		progressUnPlayRect = null;
		progressPlayRect = null;
		linearGradientPlay = null;
		linearGradientUnPlay = null;
	}

}
