package com.wiser.timebar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Wiser
 * 
 *         自定义万能进度条ProgressTimeBar
 */
public class ProgressTimeBar extends View {

	private final int		HAS_TIME						= 1;							// 显示左右两侧时间

	private final int		NO_HAS_TIME						= 0;							// 不显示左右两侧时间

	private int				timeBarMode						= NO_HAS_TIME;					// 时间模式

	private final int		CANVAS_BAR_COLOR				= 10;							// 绘制Bar颜色

	private final int		CANVAS_BAR_DRAWABLE				= 11;							// 绘制BarDrawable

	private int				barCanvasModel					= CANVAS_BAR_COLOR;				// 绘制游标Bar模式

	private final int		CANVAS_PROGRESS_UNPLAY_COLOR	= 12;							// 绘制未播放进度条Color

	private final int		CANVAS_PROGRESS_UNPLAY_DRAWABLE	= 13;							// 绘制未播放进度条Drawable

	private final int		CANVAS_PROGRESS_BUFFER_COLOR	= 14;							// 绘制缓冲进度条Color

	private final int		CANVAS_PROGRESS_BUFFER_DRAWABLE	= 15;							// 绘制缓冲进度条Drawable

	private final int		CANVAS_PROGRESS_PLAY_COLOR		= 16;							// 绘制播放进度条Color

	private final int		CANVAS_PROGRESS_PLAY_DRAWABLE	= 17;							// 绘制播放进度条Drawable

	private int				progressUnPlayCanvasModel		= CANVAS_PROGRESS_UNPLAY_COLOR;	// 绘制未播放模式

	private int				progressBufferCanvasModel		= CANVAS_PROGRESS_BUFFER_COLOR;	// 绘制缓冲模式

	private int				progressPlayCanvasModel			= CANVAS_PROGRESS_PLAY_COLOR;	// 绘制播放模式

	private Paint			progressBarPaint;												// 进度条Bar画笔

	private Paint			progressUnPlayPaint;											// 进度条未播放画笔

	private Paint			progressPlayPaint;												// 进度条播放画笔

	private Paint			progressBufferPaint;											// 进度条缓冲画笔

	private Paint			timePaint;														// 时间画笔

	private int				timeColor						= Color.WHITE;					// 时间颜色

	private float			timeTextSize					= 20;							// 时间文本大小

	private float			progressTimePadding				= 20;							// 时间与进度条间的padding

	private int				progressUnPlayColor				= Color.RED;					// 进度条未播放颜色

	private int				progressPlayColor				= Color.BLUE;					// 进度条播放颜色

	private int				progressBufferColor				= Color.GRAY;					// 进度条缓冲颜色

	private int				barColor						= Color.GREEN;					// bar颜色

	private int				barShadowColor					= Color.WHITE;					// bar阴影颜色

	private float			barShadowPadding				= 20;							// 阴影Padding

	private float			touchBarHeight;													// 按下Bar高度

	private float			progressHeight					= 14;							// 进度条高度

	private float			barHeight						= progressHeight + 14;			// bar的高度

	private int				progressRoundRadius;											// 进度条矩形圆角半径

	private boolean			isBarShadow;													// 是否Bar有阴影

	private Drawable		barDrawable;													// 游标图片

	private Drawable		unPlayDrawable;													// 未播放进度图片

	private Drawable		bufferDrawable;													// 缓冲进度图片

	private Drawable		playDrawable;													// 播放进度图片

	private RectF			progressUnPlayRect;												// 进度条未播放矩阵

	private RectF			progressPlayRect;												// 进度条播放矩阵

	private RectF			progressBufferRect;												// 进度条缓冲矩阵

	private RectF			barRect;														// bar矩阵

	private RectF			lTimeRect;														// 左侧跟踪时间

	private RectF			rTimeRect;														// 右侧总时间

	private long			currentDuration;												// 当前时间

	private long			bufferDuration;													// 当前缓冲时间

	private long			maxDuration;													// 总时间

	private float			downX, downY, lastMoveX;										// 按下坐标XY 以及记录移动位置

	private boolean			isPressBar;														// 是否按在bar上

	private long			pressBarPlayDuration;											// 按下bar的时候正在播放的时间 为了处理按下的情况不进行播放绘制bar

	private long			lastMoveToDuration;												// 记录时间

	private boolean			isMove							= false;						// 是否移动Progress

	private boolean			isHasBar						= true;							// 是否有bar显示

	private boolean			isHasBuffer;													// 是否有缓冲

	private SeekListener	seekListener;													// 设置播放进度监听

	private LinearGradient	linearGradientUnPlay;											// 未播放渐变组件

	private LinearGradient	linearGradientPlay;												// 播放渐变组件

	private LinearGradient	linearGradientBuffer;											// 缓冲渐变组件

	private int				unPlayStartColor, unPlayCenterColor, unPlayEndColor;			// 未播放渐变颜色值

	private int				bufferStartColor, bufferCenterColor, bufferEndColor;			// 缓冲渐变颜色值

	private int				playStartColor, playCenterColor, playEndColor;					// 播放渐变颜色值

	public ProgressTimeBar(Context context) {
		super(context);
		init(context, null);
	}

	public ProgressTimeBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

		setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
		this.setWillNotDraw(false); // 调用此方法后，才会执行 onDraw(Canvas) 方法

		@SuppressLint("CustomViewStyleable")
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressTimeBar);
		timeColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressTimeColor, getResources().getColor(android.R.color.white));
		progressUnPlayColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayColor, getResources().getColor(android.R.color.white));
		progressPlayColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressPlayColor, getResources().getColor(android.R.color.holo_blue_dark));
		progressBufferColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBufferColor, getResources().getColor(android.R.color.darker_gray));
		barColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBarColor, getResources().getColor(android.R.color.holo_green_dark));
		isBarShadow = typedArray.getBoolean(R.styleable.ProgressTimeBar_progressIsBarShadow, isBarShadow);
		barShadowColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBarShadowColor, getResources().getColor(android.R.color.white));
		isHasBar = typedArray.getBoolean(R.styleable.ProgressTimeBar_progressIsHasBar, isHasBar);
		isHasBuffer = typedArray.getBoolean(R.styleable.ProgressTimeBar_progressIsHasBuffer, isHasBuffer);
		int barSrcId = typedArray.getResourceId(R.styleable.ProgressTimeBar_progressBarSrc, -1);
		int unPlaySrcId = typedArray.getResourceId(R.styleable.ProgressTimeBar_progressUnPlaySrc, -1);
		int bufferSrcId = typedArray.getResourceId(R.styleable.ProgressTimeBar_progressBufferSrc, -1);
		int playSrcId = typedArray.getResourceId(R.styleable.ProgressTimeBar_progressPlaySrc, -1);
		timeTextSize = typedArray.getDimension(R.styleable.ProgressTimeBar_progressTimeTextSize, timeTextSize);
		barShadowPadding = typedArray.getDimension(R.styleable.ProgressTimeBar_progressBarShadowPadding, barShadowPadding);
		progressTimePadding = typedArray.getDimension(R.styleable.ProgressTimeBar_progressTimePadding, progressTimePadding);
		progressHeight = (int) typedArray.getDimension(R.styleable.ProgressTimeBar_progressHeight, progressHeight);
		barHeight = (int) typedArray.getDimension(R.styleable.ProgressTimeBar_progressBarHeight, barHeight);
		progressRoundRadius = (int) typedArray.getDimension(R.styleable.ProgressTimeBar_progressRoundRadius, progressRoundRadius);
		timeBarMode = typedArray.getInt(R.styleable.ProgressTimeBar_progressTimeMode, NO_HAS_TIME);
		unPlayStartColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayStartColor, 0);
		unPlayCenterColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayCenterColor, 0);
		unPlayEndColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayEndColor, 0);
		bufferStartColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBufferStartColor, 0);
		bufferCenterColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBufferCenterColor, 0);
		bufferEndColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBufferEndColor, 0);
		playStartColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayStartColor, 0);
		playCenterColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayCenterColor, 0);
		playEndColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayEndColor, 0);
		typedArray.recycle();

		initPaint();

		initProgressMode(unPlaySrcId, bufferSrcId, playSrcId);

		initBarCanvasMode(barSrcId);

		initRect();
	}

	// 初始化画笔
	private void initPaint() {
		progressBarPaint = new Paint();
		progressBarPaint.setStyle(Paint.Style.FILL);
		progressBarPaint.setAntiAlias(true);
		progressBarPaint.setColor(barColor);
		progressBarPaint.setTextAlign(Paint.Align.CENTER);

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

		progressBufferPaint = new Paint();
		progressBufferPaint.setStyle(Paint.Style.FILL);
		progressBufferPaint.setAntiAlias(true);
		progressBufferPaint.setColor(progressBufferColor);
		progressBufferPaint.setDither(true); // 启用抗颜色抖动（可以让渐变更平缓）
		progressBufferPaint.setTextAlign(Paint.Align.CENTER);

		timePaint = new Paint();
		timePaint.setStyle(Paint.Style.FILL);
		timePaint.setAntiAlias(true);
		timePaint.setColor(timeColor);
		timePaint.setTextSize(timeTextSize);
		timePaint.setTextAlign(Paint.Align.CENTER);
	}

	// 初始化进度条绘制模式
	private void initProgressMode(int progressUnPlaySrcId, int progressBufferSrcId, int progressPlaySrcId) {
		// 未播放进度条图片判断
		if (progressUnPlaySrcId > 0) {
			unPlayDrawable = getResources().getDrawable(progressUnPlaySrcId);
			if (unPlayDrawable == null) {
				progressUnPlayCanvasModel = CANVAS_PROGRESS_UNPLAY_COLOR;
			} else {
				progressUnPlayCanvasModel = CANVAS_PROGRESS_UNPLAY_DRAWABLE;
			}
		} else {
			progressUnPlayCanvasModel = CANVAS_PROGRESS_UNPLAY_COLOR;
		}
		// 缓冲进度条图片判断
		if (progressBufferSrcId > 0) {
			bufferDrawable = getResources().getDrawable(progressBufferSrcId);
			if (bufferDrawable == null) {
				progressBufferCanvasModel = CANVAS_PROGRESS_BUFFER_COLOR;
			} else {
				progressBufferCanvasModel = CANVAS_PROGRESS_BUFFER_DRAWABLE;
			}
		} else {
			progressBufferCanvasModel = CANVAS_PROGRESS_BUFFER_COLOR;
		}
		// 播放进度条图片判断
		if (progressPlaySrcId > 0) {
			playDrawable = getResources().getDrawable(progressPlaySrcId);
			if (playDrawable == null) {
				progressPlayCanvasModel = CANVAS_PROGRESS_PLAY_COLOR;
			} else {
				progressPlayCanvasModel = CANVAS_PROGRESS_PLAY_DRAWABLE;
			}
		} else {
			progressPlayCanvasModel = CANVAS_PROGRESS_PLAY_COLOR;
		}

		// 计算进度条高度
		progressHeight = unPlayDrawable != null ? unPlayDrawable.getIntrinsicHeight()
				: bufferDrawable != null ? bufferDrawable.getIntrinsicHeight() : playDrawable != null ? playDrawable.getIntrinsicHeight() : progressHeight;
	}

	// 初始化Bar绘制模式
	private void initBarCanvasMode(int barSrcId) {
		if (isHasBar) {
			if (barSrcId > 0) {
				barDrawable = getResources().getDrawable(barSrcId);
				// 游标drawable下资源Drawable
				if (barDrawable == null) {
					barCanvasModel = CANVAS_BAR_COLOR;
				} else {
					barCanvasModel = CANVAS_BAR_DRAWABLE;
					barHeight = barDrawable.getIntrinsicHeight();
				}
			} else {
				barCanvasModel = CANVAS_BAR_COLOR;
			}
			// 有阴影总高度需要加上阴影Padding
			if (isBarShadow) touchBarHeight = (int) (barHeight + barShadowPadding);
			else touchBarHeight = barHeight;
		} else {
			touchBarHeight = progressHeight;
		}
	}

	// 初始化矩阵
	private void initRect() {
		progressUnPlayRect = new RectF();
		progressPlayRect = new RectF();
		progressBufferRect = new RectF();
		barRect = new RectF();
		lTimeRect = new RectF();
		rTimeRect = new RectF();
	}

	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.save();

		// 画时间
		canvasTime(canvas);
		// 画进度条
		canvasProgress(canvas);

		canvas.restore();

		setRect();

		postInvalidate();
	}

	// 画进度条
	private void canvasProgress(Canvas canvas) {
		// 画未播放进度
		switch (progressUnPlayCanvasModel) {
			case CANVAS_PROGRESS_UNPLAY_COLOR:
				canvas.drawRoundRect(progressUnPlayRect, progressRoundRadius, progressRoundRadius, progressUnPlayPaint);
				break;
			case CANVAS_PROGRESS_UNPLAY_DRAWABLE:
				unPlayDrawable.setBounds((int) progressUnPlayRect.left, (int) progressUnPlayRect.top, (int) progressUnPlayRect.right, (int) progressUnPlayRect.bottom);
				unPlayDrawable.draw(canvas);
				break;
		}

		// 是否有缓冲显示
		if (isHasBuffer) {
			switch (progressBufferCanvasModel) {
				case CANVAS_PROGRESS_BUFFER_COLOR:
					// 画缓冲进度
					canvas.drawRoundRect(progressBufferRect, progressRoundRadius, progressRoundRadius, progressBufferPaint);
					break;
				case CANVAS_PROGRESS_BUFFER_DRAWABLE:
					bufferDrawable.setBounds((int) progressBufferRect.left, (int) progressBufferRect.top, (int) progressBufferRect.right, (int) progressBufferRect.bottom);
					bufferDrawable.draw(canvas);
					break;
			}
		}

		// 画播放进度
		switch (progressPlayCanvasModel) {
			case CANVAS_PROGRESS_PLAY_COLOR:
				canvas.drawRoundRect(progressPlayRect, progressRoundRadius, progressRoundRadius, progressPlayPaint);
				break;
			case CANVAS_PROGRESS_PLAY_DRAWABLE:
				playDrawable.setBounds((int) progressPlayRect.left, (int) progressPlayRect.top, (int) progressPlayRect.right, (int) progressPlayRect.bottom);
				playDrawable.draw(canvas);
				break;
		}

		// 是否有Bar
		if (isHasBar) {
			// 画bar
			if (isBarShadow) {// 阴影
				progressBarPaint.setShadowLayer(barShadowPadding, 0, 0, barShadowColor);
				// progressBarPaint.setMaskFilter(new BlurMaskFilter(10,
				// BlurMaskFilter.Blur.NORMAL));
				if (barDrawable != null) canvas.drawRoundRect(barRect, (barRect.right - barRect.left) / 2, (barRect.right - barRect.left) / 2, progressBarPaint);
			}
			switch (barCanvasModel) {
				case CANVAS_BAR_DRAWABLE:// 绘制Drawable
					if (barDrawable == null) canvas.drawCircle(barRect.left + barHeight / 2, barRect.top + barHeight / 2, barHeight / 2, progressBarPaint);
					else {
						barDrawable.setBounds((int) barRect.left, (int) barRect.top, (int) barRect.right, (int) barRect.bottom);
						barDrawable.draw(canvas);
					}
					break;
				case CANVAS_BAR_COLOR:// 绘制颜色
					canvas.drawCircle(barRect.left + barHeight / 2, barRect.top + barHeight / 2, barHeight / 2, progressBarPaint);
					break;
			}

			if (isBarShadow)
				// 清楚阴影
				progressBarPaint.clearShadowLayer();
		}
	}

	// 画时间
	private void canvasTime(Canvas canvas) {
		// 当前播放时间
		String currentDuration = getMinuteSecondStrForLong(this.currentDuration);
		// 总时间
		String totalDuration = getMinuteSecondStrForLong(this.maxDuration);
		// 画左侧时间
		canvas.drawText(currentDuration, lTimeRect.left + timePaint.measureText(currentDuration) / 2, lTimeRect.bottom, timePaint);
		// 画右侧
		canvas.drawText(totalDuration, rTimeRect.right - timePaint.measureText(totalDuration) / 2, rTimeRect.bottom, timePaint);
	}

	@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int width = right - left;
		// 显示时间
		if (timeBarMode == HAS_TIME) {
			String lTime = getMinuteSecondStrForLong(this.currentDuration);
			String rTime = getMinuteSecondStrForLong(this.maxDuration);
			int[] value = getTextValue(timePaint, lTime);
			// 左侧时间矩形坐标
			lTimeRect.set(getPaddingLeft(), getPaddingTop() + touchBarHeight / 2 - (float) value[1] / 2, (int) (getPaddingLeft() + timePaint.measureText(rTime)),
					getPaddingBottom() + touchBarHeight / 2 + (float) value[1] / 2);
			// 右侧时间矩形坐标
			rTimeRect.set((int) (width - getPaddingRight() - timePaint.measureText(rTime) - progressTimePadding), getPaddingTop() + touchBarHeight / 2 - (float) value[1] / 2,
					width - getPaddingRight(), getPaddingBottom() + touchBarHeight / 2 + (float) value[1] / 2);
			// 未播放矩形坐标
			progressUnPlayRect.set((int) (lTimeRect.right + progressTimePadding), getPaddingTop() + (touchBarHeight - progressHeight) / 2, rTimeRect.left,
					getPaddingBottom() + (touchBarHeight + progressHeight) / 2);
		} else {// 不显示时间
			// 未播放矩形坐标
			progressUnPlayRect.set(getPaddingLeft(), getPaddingTop() + (touchBarHeight - progressHeight) / 2, width - getPaddingRight(), getPaddingBottom() + (touchBarHeight + progressHeight) / 2);
		}
		// bar矩形坐标
		barRect.set(progressUnPlayRect.left, getPaddingTop() + (touchBarHeight - barHeight) / 2, progressUnPlayRect.left + barHeight, getPaddingBottom() + (touchBarHeight + barHeight) / 2);
		// 播放矩形坐标与未播放相同
		progressPlayRect.set(progressUnPlayRect);
		// 缓冲矩形坐标与未播放相同
		progressBufferRect.set(progressUnPlayRect);

		setRect();
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int height = (int) (heightMode == MeasureSpec.UNSPECIFIED ? (touchBarHeight + getPaddingTop() + getPaddingBottom())
				: heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(touchBarHeight + getPaddingTop() + getPaddingBottom(), heightSize));
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
	}

	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 设置渐变
		setLinearGradient(1, unPlayStartColor, unPlayCenterColor, unPlayEndColor);
		setLinearGradient(2, bufferStartColor, bufferCenterColor, bufferEndColor);
		setLinearGradient(3, playStartColor, playCenterColor, playEndColor);
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
		String time = getMinuteSecondStrForLong(this.currentDuration);
		float timeWidth = timePaint.measureText(time);
		switch (mode) {
			case 1:
				linearGradientUnPlay = new LinearGradient(timeWidth + progressTimePadding, 0, getMeasuredWidth() - timeWidth - progressTimePadding, 0, gradientColors, weights, Shader.TileMode.CLAMP);
				progressUnPlayPaint.setShader(linearGradientUnPlay);
				break;
			case 2:
				linearGradientBuffer = new LinearGradient(timeWidth + progressTimePadding, 0, getMeasuredWidth() - timeWidth - progressTimePadding, 0, gradientColors, weights, Shader.TileMode.CLAMP);
				progressBufferPaint.setShader(linearGradientBuffer);
				break;
			case 3:
				linearGradientPlay = new LinearGradient(timeWidth + progressTimePadding, 0, getMeasuredWidth() - timeWidth - progressTimePadding, 0, gradientColors, weights, Shader.TileMode.CLAMP);
				progressPlayPaint.setShader(linearGradientPlay);
				break;
		}
	}

	// 更新播放进度和缓冲进度矩阵
	private void setRect() {
		// 缓冲进度
		progressBufferRect.right = progressUnPlayRect.left + calculateBufferRect();
		// bar进度
		barRect.left = progressUnPlayRect.left + calculateBarRect();
		barRect.right = barRect.left + barHeight;
		// 播放进度
		progressPlayRect.right = progressUnPlayRect.left + calculatePlayRect();
	}

	// 计算播放进度
	private float calculatePlayRect() {
		int rectRight;
		if (isPressBar) {
			rectRight = (int) (((float) (pressBarPlayDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left));
		} else {
			rectRight = (int) (((float) (this.currentDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left));
		}
		if (rectRight >= (progressUnPlayRect.right - progressUnPlayRect.left)) return progressUnPlayRect.right - progressUnPlayRect.left;
		return rectRight;
	}

	// 计算缓冲进度
	private float calculateBufferRect() {
		int rectRight = (int) (((float) bufferDuration / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left));
		if (rectRight >= (progressUnPlayRect.right - progressUnPlayRect.left)) return progressUnPlayRect.right - progressUnPlayRect.left;
		return rectRight;
	}

	// 计算bar进度
	private float calculateBarRect() {
		int rectRight;
		if (isPressBar) {
			rectRight = (int) (((float) (pressBarPlayDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left - barHeight));
		} else {
			rectRight = (int) (((float) (this.currentDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left - barHeight));
		}
		if (rectRight >= (progressUnPlayRect.right - progressUnPlayRect.left - barHeight)) return progressUnPlayRect.right - progressUnPlayRect.left - barHeight;
		return rectRight;
	}

	// 设置总时间
	public void setMaxDuration(long maxDuration) {
		this.maxDuration = maxDuration;
	}

	// 设置当前时间
	public void setCurrentDuration(long currentDuration) {
		this.currentDuration = currentDuration;
	}

	// 设置当前缓冲时间
	public void setBufferDuration(long bufferDuration) {
		this.bufferDuration = bufferDuration;
	}

	// 获取文字宽高
	private int[] getTextValue(Paint paint, String text) {
		int[] values = new int[2];
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		values[0] = rect.width();
		values[1] = rect.height();
		return values;
	}

	/**
	 * 根据long类型转分秒类型字符串
	 *
	 * @param mill
	 *            long类型的时间戳
	 * @return 返回字符串类型时间
	 */
	public static String getMinuteSecondStrForLong(long mill) {
		Date date = new Date(mill);
		String dateStr = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.CHINA);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			// 进行格式化
			dateStr = sdf.format(date);
			return dateStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	@SuppressLint("ClickableViewAccessibility") @Override public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downTouch();
				downX = lastMoveX = event.getX();
				downY = event.getY();
				// 是否按下bar
				isPressBar = isPressBar();
				// 没有bar的情况下 不允许拖动进度条哦
				if (!isHasBar) isPressBar = false;
				if (isPressBar) {
					pressBarPlayDuration = this.currentDuration;
					Toast.makeText(getContext(), "游标", Toast.LENGTH_SHORT).show();
					if (seekListener != null) {
						seekListener.startDraggingBar(this, pressBarPlayDuration);
					}
				}
				// 是否按下进度条
				// 是否按在进度条上
				boolean isPressProgress = isPressProgress();
				// 当没有按下bar 并且按下进度条时 直接播放到按下位置进度
				if (!isPressBar && isPressProgress) {
					long pressDuration = (long) ((this.maxDuration * (downX - progressPlayRect.right)) / (progressUnPlayRect.right - progressUnPlayRect.left));
					if (seekListener != null) seekListener.seekToDuration(this, currentDuration + pressDuration);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				isMove = true;
				if (isPressBar) {
					float moveX = event.getX() - lastMoveX;
					// 记录位置
					lastMoveX = event.getX();
					// 计算移动时间
					long moveToDuration = (long) ((this.maxDuration * moveX) / (progressUnPlayRect.right - progressUnPlayRect.left));
					// 记录上一次时间
					lastMoveToDuration += moveToDuration;
					// 滑动游标bar最小时间临界值
					if (lastMoveToDuration + pressBarPlayDuration <= 0) {
						lastMoveToDuration = -pressBarPlayDuration;
					}
					// 滑动游标bar最大时间临界值
					if (lastMoveToDuration + pressBarPlayDuration >= maxDuration) {
						lastMoveToDuration = maxDuration - pressBarPlayDuration;
					}
					if (seekListener != null) {
						seekListener.moveDraggingBar(this, pressBarPlayDuration + lastMoveToDuration);
					}
					postInvalidate();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				upTouch();
				if (isPressBar && isMove) {// 按下游标 并且移动了游标才进行抬起设置新的播放位置
					isPressBar = false;
					if (seekListener != null) {
						seekListener.seekToDuration(this, pressBarPlayDuration + lastMoveToDuration);
						seekListener.stopDraggingBar(this, pressBarPlayDuration + lastMoveToDuration);
					}
					pressBarPlayDuration = 0;
					lastMoveToDuration = 0;
					postInvalidate();
				}
				break;
		}
		return true;
	}

	// 按下控件处理拦截事件
	private void downTouch() {
		ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(true);
		}
	}

	// 抬起控件处理拦截事件
	private void upTouch() {
		ViewParent parent = getParent();
		if (parent != null) {
			parent.requestDisallowInterceptTouchEvent(false);
		}
	}

	// 计算按下的位置是否是在游标上
	private boolean isPressBar() {
		return downX >= barRect.left && downX <= barRect.right && downY >= barRect.top && downY <= barRect.bottom;
	}

	// 计算按下的位置是否在进度条上
	private boolean isPressProgress() {
		return downX >= progressUnPlayRect.left && downX <= progressUnPlayRect.right && downY >= progressUnPlayRect.top && downY < progressUnPlayRect.bottom;
	}

	@Override protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		detach();
	}

	private void detach() {
		progressBarPaint = null;
		progressUnPlayPaint = null;
		progressBufferPaint = null;
		progressPlayPaint = null;
		timePaint = null;
		barDrawable = null;
		progressUnPlayRect = null;
		progressPlayRect = null;
		progressBufferRect = null;
		barRect = null;
		lTimeRect = null;
		rTimeRect = null;
		seekListener = null;
		linearGradientPlay = null;
		linearGradientUnPlay = null;
		linearGradientBuffer = null;
	}

	public void setSeekListener(SeekListener seekListener) {
		this.seekListener = seekListener;
	}

	public interface SeekListener {

		void startDraggingBar(ProgressTimeBar timeBar, long duration);

		void seekToDuration(ProgressTimeBar timeBar, long duration);

		void moveDraggingBar(ProgressTimeBar timeBar, long duration);

		void stopDraggingBar(ProgressTimeBar timeBar, long duration);

	}

}
