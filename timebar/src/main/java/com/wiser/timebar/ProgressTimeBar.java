package com.wiser.timebar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Wiser
 * <p>
 * 自定义TimeBar
 */
public class ProgressTimeBar extends View {

    private final int HAS_TIME = 1;//显示左右两侧时间

    private final int NO_HAS_TIME = 0;//不显示左右两侧时间

    private int timeBarModel = NO_HAS_TIME;//时间模式

    private final int CANVAS_COLOR = 10;//绘制Bar颜色

    private final int CANVAS_BITMAP = 11;//绘制Bar图片

    private final int CANVAS_DRAWABLE = 12;//绘制BarDrawable

    private int barCanvasModel = CANVAS_COLOR;//绘制游标Bar模式

    private Paint progressPaint;//进度条画笔

    private Paint timePaint;//时间画笔

    private int timeColor = Color.WHITE;//时间颜色

    private float timeTextSize = 20;//时间文本大小

    private float progressTimePadding = 20;//时间与进度条间的padding

    private int progressUnPlayColor = Color.RED;//进度条未播放颜色

    private int progressPlayColor = Color.BLUE;//进度条播放颜色

    private int progressBufferColor = Color.GRAY;//进度条缓冲颜色

    private int barColor = Color.GREEN;//bar颜色

    private int barShadowColor = Color.WHITE;//bar阴影颜色

    private float barShadowPadding = 20;//阴影Padding

    private int touchBarHeight;//按下Bar高度

    private int progressHeight = 14;//进度条高度

    private int barHeight = progressHeight + 14;//bar的高度

    private boolean isBarShadow;//是否Bar有阴影

    private Bitmap barBitmap;//游标图片

    private Drawable barDrawable;//游标图片

    private Rect progressUnPlayRect;//进度条未播放矩阵

    private Rect progressPlayRect;//进度条播放矩阵

    private Rect progressBufferRect;//进度条缓冲矩阵

    private RectF barRect;//bar矩阵

    private Rect lTimeRect;//左侧跟踪时间

    private Rect rTimeRect;//右侧总时间

    private long currentDuration;//当前时间

    private long bufferDuration;//当前缓冲时间

    private long maxDuration;//总时间

    private float downX, downY, lastMoveX;//按下坐标XY 以及记录移动位置

    private boolean isPressBar;//是否按在bar上

    private long pressBarPlayDuration;//按下bar的时候正在播放的时间 为了处理按下的情况不进行播放绘制bar

    private long lastMoveToDuration;//记录时间

    private boolean isMove = false;//是否移动Progress

    private SeekListener seekListener;//设置播放进度监听

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

        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressTimeBar);
        timeColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressTimeColor, getResources().getColor(android.R.color.white));
        progressUnPlayColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressUnPlayColor, getResources().getColor(android.R.color.white));
        progressPlayColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressPlayColor, getResources().getColor(android.R.color.holo_blue_dark));
        progressBufferColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBufferColor, getResources().getColor(android.R.color.darker_gray));
        barColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBarColor, getResources().getColor(android.R.color.holo_green_dark));
        isBarShadow = typedArray.getBoolean(R.styleable.ProgressTimeBar_progressIsBarShadow, isBarShadow);
        barShadowColor = typedArray.getColor(R.styleable.ProgressTimeBar_progressBarShadowColor, getResources().getColor(android.R.color.white));
        int barSrcId = typedArray.getResourceId(R.styleable.ProgressTimeBar_progressBarSrc, -1);
        timeTextSize = typedArray.getDimension(R.styleable.ProgressTimeBar_progressTimeTextSize, timeTextSize);
        barShadowPadding = typedArray.getDimension(R.styleable.ProgressTimeBar_progressBarShadowPadding, barShadowPadding);
        progressTimePadding = typedArray.getDimension(R.styleable.ProgressTimeBar_progressTimePadding, progressTimePadding);
        progressHeight = (int) typedArray.getDimension(R.styleable.ProgressTimeBar_progressHeight, progressHeight);
        barHeight = (int) typedArray.getDimension(R.styleable.ProgressTimeBar_progressBarHeight, barHeight);
        timeBarModel = typedArray.getInt(R.styleable.ProgressTimeBar_progressTimeModel, NO_HAS_TIME);
        typedArray.recycle();

        initPaint();

        initBarCanvasModel(barSrcId);

        initRect();
    }

    //初始化Bar绘制模式
    private void initBarCanvasModel(int barSrcId) {
        if (barSrcId > 0) {
            barBitmap = BitmapFactory.decodeResource(getResources(), barSrcId);
            barDrawable = getResources().getDrawable(barSrcId);
            //游标mipmap下图片Bitmap
            if (barBitmap == null) {
                barCanvasModel = CANVAS_COLOR;
            } else {
                barCanvasModel = CANVAS_BITMAP;
                barHeight = barBitmap.getHeight();
            }
            //游标drawable下资源Drawable
            if (barDrawable == null) {
                barCanvasModel = CANVAS_COLOR;
            } else {
                barCanvasModel = CANVAS_DRAWABLE;
                barHeight = barDrawable.getIntrinsicHeight();
            }
        } else {
            barCanvasModel = CANVAS_COLOR;
        }
        //有阴影总高度需要加上阴影Padding
        if (isBarShadow)
            touchBarHeight = (int) (barHeight + barShadowPadding);
        else touchBarHeight = barHeight;
    }

    //初始化画笔
    private void initPaint() {
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressUnPlayColor);
        progressPaint.setTextAlign(Paint.Align.CENTER);

        timePaint = new Paint();
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setAntiAlias(true);
        timePaint.setColor(timeColor);
        timePaint.setTextSize(timeTextSize);
        timePaint.setTextAlign(Paint.Align.CENTER);
    }

    //初始化矩阵
    private void initRect() {
        progressUnPlayRect = new Rect();
        progressPlayRect = new Rect();
        progressBufferRect = new Rect();
        barRect = new RectF();
        lTimeRect = new Rect();
        rTimeRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        //画时间
        canvasTime(canvas);
        //画进度条
        canvasProgress(canvas);

        canvas.restore();

        setRect();

        postInvalidate();
    }

    //画进度条
    private void canvasProgress(Canvas canvas) {
        //画未播放进度
        progressPaint.setColor(progressUnPlayColor);
        canvas.drawRect(progressUnPlayRect, progressPaint);
        //画缓冲进度
        progressPaint.setColor(progressBufferColor);
        canvas.drawRect(progressBufferRect, progressPaint);
        //画播放进度
        progressPaint.setColor(progressPlayColor);
        canvas.drawRect(progressPlayRect, progressPaint);
        //画bar
        if (isBarShadow) {//阴影
            progressPaint.setShadowLayer(barShadowPadding, 0, 0, barShadowColor);
//            progressPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
            if (barBitmap != null || barDrawable != null)
                canvas.drawRoundRect(barRect, (barRect.right - barRect.left) / 2, (barRect.right - barRect.left) / 2, progressPaint);
        }
        progressPaint.setColor(barColor);
        switch (barCanvasModel) {
            case CANVAS_BITMAP://绘制图片
                if (barBitmap == null)
                    canvas.drawCircle(barRect.left + (float) barHeight / 2, barRect.top + (float) barHeight / 2, (float) barHeight / 2, progressPaint);
                else {
                    canvas.drawRoundRect(barRect, (barRect.right - barRect.left) / 2, (barRect.right - barRect.left) / 2, progressPaint);
                    canvas.drawBitmap(barBitmap, barRect.left, barRect.top, progressPaint);
                }
                break;
            case CANVAS_DRAWABLE://绘制Drawable
                if (barDrawable == null)
                    canvas.drawCircle(barRect.left + (float) barHeight / 2, barRect.top + (float) barHeight / 2, (float) barHeight / 2, progressPaint);
                else {
                    barDrawable.draw(canvas);
                    barDrawable.setBounds((int) barRect.left, (int) barRect.top, (int) barRect.right, (int) barRect.bottom);
                }
                break;
            case CANVAS_COLOR://绘制颜色
                canvas.drawCircle(barRect.left + (float) barHeight / 2, barRect.top + (float) barHeight / 2, (float) barHeight / 2, progressPaint);
                break;
        }

        if (isBarShadow)
            //清楚阴影
            progressPaint.clearShadowLayer();
    }

    //画时间
    private void canvasTime(Canvas canvas) {
        //当前播放时间
        String currentDuration = getMinuteSecondStrForLong(this.currentDuration);
        //总时间
        String totalDuration = getMinuteSecondStrForLong(this.maxDuration);
        //画左侧时间
        canvas.drawText(currentDuration, lTimeRect.left + timePaint.measureText(currentDuration) / 2, lTimeRect.bottom, timePaint);
        //画右侧
        canvas.drawText(totalDuration, rTimeRect.right - timePaint.measureText(totalDuration) / 2, rTimeRect.bottom, timePaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        //显示时间
        if (timeBarModel == HAS_TIME) {
            String lTime = getMinuteSecondStrForLong(this.currentDuration);
            String rTime = getMinuteSecondStrForLong(this.maxDuration);
            int[] value = getTextValue(timePaint, lTime);
            //左侧时间矩形坐标
            lTimeRect.set(getPaddingLeft(), getPaddingTop() + touchBarHeight / 2 - value[1] / 2, (int) (getPaddingLeft() + timePaint.measureText(rTime)), getPaddingBottom() + touchBarHeight / 2 + value[1] / 2);
            //右侧时间矩形坐标
            rTimeRect.set((int) (width - getPaddingRight() - timePaint.measureText(rTime) - progressTimePadding), getPaddingTop() + touchBarHeight / 2 - value[1] / 2, width - getPaddingRight(), getPaddingBottom() + touchBarHeight / 2 + value[1] / 2);
            //未播放矩形坐标
            progressUnPlayRect.set((int) (lTimeRect.right + progressTimePadding), getPaddingTop() + (touchBarHeight - progressHeight) / 2,
                    rTimeRect.left, getPaddingBottom() + (touchBarHeight + progressHeight) / 2);
        } else {//不显示时间
            //未播放矩形坐标
            progressUnPlayRect.set(getPaddingLeft(), getPaddingTop() + (touchBarHeight - progressHeight) / 2,
                    width - getPaddingRight(), getPaddingBottom() + (touchBarHeight + progressHeight) / 2);
        }
        //bar矩形坐标
        barRect.set(progressUnPlayRect.left, getPaddingTop() + (float) (touchBarHeight - barHeight) / 2, progressUnPlayRect.left + barHeight, getPaddingBottom() + (float) (touchBarHeight + barHeight) / 2);
        //播放矩形坐标与未播放相同
        progressPlayRect.set(progressUnPlayRect);
        //缓冲矩形坐标与未播放相同
        progressBufferRect.set(progressUnPlayRect);

        setRect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = heightMode == MeasureSpec.UNSPECIFIED ? touchBarHeight
                : heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(touchBarHeight + getPaddingTop() + getPaddingBottom(), heightSize);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    //更新播放进度和缓冲进度矩阵
    private void setRect() {
        //缓冲进度
        progressBufferRect.right = progressUnPlayRect.left + calculateBufferRect();
        //bar进度
        barRect.left = progressUnPlayRect.left + calculateBarRect();
        barRect.right = barRect.left + barHeight;
        //播放进度
        progressPlayRect.right = progressUnPlayRect.left + calculatePlayRect();
    }

    //计算播放进度
    private int calculatePlayRect() {
        int rectRight;
        if (isPressBar) {
            rectRight = (int) (((float) (pressBarPlayDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left));
        } else {
            rectRight = (int) (((float) (this.currentDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left));
        }
        if (rectRight >= (progressUnPlayRect.right - progressUnPlayRect.left))
            return progressUnPlayRect.right - progressUnPlayRect.left;
        return rectRight;
    }

    //计算缓冲进度
    private int calculateBufferRect() {
        int rectRight = (int) (((float) bufferDuration / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left));
        if (rectRight >= (progressUnPlayRect.right - progressUnPlayRect.left))
            return progressUnPlayRect.right - progressUnPlayRect.left;
        return rectRight;
    }

    //计算bar进度
    private int calculateBarRect() {
        int rectRight;
        if (isPressBar) {
            rectRight = (int) (((float) (pressBarPlayDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left - barHeight));
        } else {
            rectRight = (int) (((float) (this.currentDuration + lastMoveToDuration) / this.maxDuration) * (progressUnPlayRect.right - progressUnPlayRect.left - barHeight));
        }
        if (rectRight >= (progressUnPlayRect.right - progressUnPlayRect.left - barHeight))
            return progressUnPlayRect.right - progressUnPlayRect.left - barHeight;
        return rectRight;
    }

    //设置总时间
    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    //设置当前时间
    public void setCurrentDuration(long currentDuration) {
        this.currentDuration = currentDuration;
    }

    //设置当前缓冲时间
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
     * @return
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = lastMoveX = event.getX();
                downY = event.getY();
                //是否按下bar
                isPressBar = isPressBar();
                if (isPressBar) {
                    pressBarPlayDuration = this.currentDuration;
                    Toast.makeText(getContext(), "游标", Toast.LENGTH_SHORT).show();
                }
                //是否按下进度条
                //是否按在进度条上
                boolean isPressProgress = isPressProgress();
                //当没有按下bar 并且按下进度条时 直接播放到按下位置进度
                if (!isPressBar && isPressProgress) {
                    long pressDuration = (long) ((this.maxDuration * (downX - progressPlayRect.right)) / (progressUnPlayRect.right - progressUnPlayRect.left));
                    if (seekListener != null)
                        seekListener.seekToDuration(currentDuration + pressDuration);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                if (isPressBar) {
                    float moveX = event.getX() - lastMoveX;
                    //记录位置
                    lastMoveX = event.getX();
                    //计算移动时间
                    long moveToDuration = (long) ((this.maxDuration * moveX) / (progressUnPlayRect.right - progressUnPlayRect.left));
                    //记录上一次时间
                    lastMoveToDuration += moveToDuration;
                    //滑动游标bar最小时间临界值
                    if (lastMoveToDuration + pressBarPlayDuration <= 0) {
                        lastMoveToDuration = -pressBarPlayDuration;
                    }
                    //滑动游标bar最大时间临界值
                    if (lastMoveToDuration + pressBarPlayDuration >= maxDuration) {
                        lastMoveToDuration = maxDuration - pressBarPlayDuration;
                    }
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isPressBar && isMove) {//按下游标 并且移动了游标才进行抬起设置新的播放位置
                    isPressBar = false;
                    if (seekListener != null)
                        seekListener.seekToDuration(pressBarPlayDuration + lastMoveToDuration);
                    pressBarPlayDuration = 0;
                    lastMoveToDuration = 0;
                    postInvalidate();
                }
                break;
        }
        return true;
    }

    //计算按下的位置是否是在游标上
    private boolean isPressBar() {
        return downX >= barRect.left && downX <= barRect.right && downY >= barRect.top && downY <= barRect.bottom;
    }

    //计算按下的位置是否在进度条上
    private boolean isPressProgress() {
        return downX >= progressUnPlayRect.left && downX <= progressUnPlayRect.right && downY >= progressUnPlayRect.top && downY < progressUnPlayRect.bottom;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detach();
    }

    private void detach() {
        progressPaint = null;
        timePaint = null;
        if (barBitmap != null) barBitmap.recycle();
        barBitmap = null;
        barDrawable = null;
        progressUnPlayRect = null;
        progressPlayRect = null;
        progressBufferRect = null;
        barRect = null;
        lTimeRect = null;
        rTimeRect = null;
        seekListener = null;
    }

    public void setSeekListener(SeekListener seekListener) {
        this.seekListener = seekListener;
    }

    public interface SeekListener {
        void seekToDuration(long duration);
    }

}
