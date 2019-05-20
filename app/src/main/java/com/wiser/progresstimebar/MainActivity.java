package com.wiser.progresstimebar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wiser.timebar.ProgressTimeBar;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Wiser
 * 
 *         模拟播放暂停
 */
public class MainActivity extends AppCompatActivity implements ProgressTimeBar.SeekListener, View.OnClickListener {

	private long			maxDuration		= 60000;// 最大时间

	private long			currentDuration	= 0;	// 当前时间

	private long			bufferDuration	= 0;	// 缓冲时间

	private ProgressTimeBar	timeBar1;

	private ProgressTimeBar	timeBar2;

	private ProgressTimeBar	timeBar3;

	private ProgressTimeBar	timeBar4;

	private ProgressTimeBar	timeBar5;

	private ProgressTimeBar	timeBar6;

	private ProgressTimeBar	timeBar7;

	private Button			btnPlay;

	private Button			btnPause;

	private TextView		tvPlayState;

	private TimeHandler		timeHandler;

	private final int		UPDATE_DURATION	= 111;

	private boolean			isPause;

	@Override public void startDraggingBar(ProgressTimeBar timeBar, long duration) {
		System.out.println("-------开始--->>" + getMinuteSecondStrForLong(duration));
	}

	@Override public void seekToDuration(ProgressTimeBar timeBar, long duration) {
		this.currentDuration = duration;
		if (!isPause) {
			tvPlayState.setText("播放中");
			timeBar1.setCurrentDuration(currentDuration);
			timeBar1.setBufferDuration(bufferDuration);
			timeBar2.setCurrentDuration(currentDuration);
			timeBar2.setBufferDuration(bufferDuration);
			timeBar3.setCurrentDuration(currentDuration);
			timeBar3.setBufferDuration(bufferDuration);
			timeBar4.setCurrentDuration(currentDuration);
			timeBar4.setBufferDuration(bufferDuration);
			timeBar5.setCurrentDuration(currentDuration);
			timeBar5.setBufferDuration(bufferDuration);
			timeBar6.setBufferDuration(bufferDuration);
			timeBar6.setBufferDuration(bufferDuration);
			timeBar7.setBufferDuration(bufferDuration);
			timeBar7.setBufferDuration(bufferDuration);
			timeHandler.removeMessages(UPDATE_DURATION);
			timeHandler.sendEmptyMessage(UPDATE_DURATION);
		} else {
			tvPlayState.setText("暂停中");
			timeBar1.setCurrentDuration(currentDuration);
			timeBar1.setBufferDuration(bufferDuration);
			timeBar2.setCurrentDuration(currentDuration);
			timeBar2.setBufferDuration(bufferDuration);
			timeBar3.setCurrentDuration(currentDuration);
			timeBar3.setBufferDuration(bufferDuration);
			timeBar4.setCurrentDuration(currentDuration);
			timeBar4.setBufferDuration(bufferDuration);
			timeBar5.setCurrentDuration(currentDuration);
			timeBar5.setBufferDuration(bufferDuration);
			timeBar6.setBufferDuration(bufferDuration);
			timeBar6.setBufferDuration(bufferDuration);
			timeBar7.setBufferDuration(bufferDuration);
			timeBar7.setBufferDuration(bufferDuration);
		}
		if (currentDuration == maxDuration) tvPlayState.setText("播放结束");
	}

	@Override public void moveDraggingBar(ProgressTimeBar timeBar, long duration) {
		System.out.println("-------移动--->>" + getMinuteSecondStrForLong(duration));
	}

	@Override public void stopDraggingBar(ProgressTimeBar timeBar, long duration) {
		System.out.println("-------停止--->>" + getMinuteSecondStrForLong(duration));
	}

	@Override public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_play:// 播放
				if (isPause) {
					tvPlayState.setText("播放中");
					isPause = false;
					timeHandler.removeMessages(UPDATE_DURATION);
					timeHandler.sendEmptyMessage(UPDATE_DURATION);
				}
				break;
			case R.id.btn_pause:// 暂停
				if (!isPause) {
					tvPlayState.setText("暂停中");
					isPause = true;
					timeHandler.removeMessages(UPDATE_DURATION);
				}
				break;
		}
	}

	private static class TimeHandler extends Handler {

		WeakReference<MainActivity> reference;

		TimeHandler(MainActivity activity) {
			reference = new WeakReference<>(activity);
		}

		@Override public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (reference != null && reference.get() != null) {
				if (msg.what == reference.get().UPDATE_DURATION) {
					reference.get().currentDuration += 1000;
					if (reference.get().currentDuration >= reference.get().maxDuration) {
						reference.get().currentDuration = reference.get().maxDuration;
						reference.get().timeBar1.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar2.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar3.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar4.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar5.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar6.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar7.setCurrentDuration(reference.get().currentDuration);
					}
					if (reference.get().currentDuration <= 0) {
						reference.get().currentDuration = 0;
						reference.get().timeBar1.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar2.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar3.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar4.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar5.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar6.setCurrentDuration(reference.get().currentDuration);
						reference.get().timeBar7.setCurrentDuration(reference.get().currentDuration);
					}
					reference.get().bufferDuration += 2500;
					if (reference.get().bufferDuration >= reference.get().maxDuration) {
						reference.get().bufferDuration = reference.get().maxDuration;
						reference.get().timeBar1.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar2.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar3.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar4.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar5.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar6.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar7.setBufferDuration(reference.get().bufferDuration);
					}
					if (reference.get().bufferDuration <= 0) {
						reference.get().bufferDuration = 0;
						reference.get().timeBar1.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar2.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar3.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar4.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar5.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar6.setBufferDuration(reference.get().bufferDuration);
						reference.get().timeBar7.setBufferDuration(reference.get().bufferDuration);
					}
					if (reference.get().currentDuration == reference.get().maxDuration && reference.get().bufferDuration == reference.get().maxDuration) {
						reference.get().tvPlayState.setText("播放结束");
						Toast.makeText(reference.get().getApplicationContext(), "播放结束", Toast.LENGTH_SHORT).show();
						removeMessages(reference.get().UPDATE_DURATION);
						return;
					}
					reference.get().timeBar1.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar1.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeBar2.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar2.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeBar3.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar3.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeBar4.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar4.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeBar5.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar5.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeBar6.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar6.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeBar7.setBufferDuration(reference.get().bufferDuration);
					reference.get().timeBar7.setCurrentDuration(reference.get().currentDuration);
					reference.get().timeHandler.sendEmptyMessageDelayed(reference.get().UPDATE_DURATION, 1000);
				}
			}
		}
	}

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		timeBar1 = findViewById(R.id.timeBar1);
		timeBar2 = findViewById(R.id.timeBar2);
		timeBar3 = findViewById(R.id.timeBar3);
		timeBar4 = findViewById(R.id.timeBar4);
		timeBar5 = findViewById(R.id.timeBar5);
		timeBar6 = findViewById(R.id.timeBar6);
		timeBar7 = findViewById(R.id.timeBar7);

		btnPlay = findViewById(R.id.btn_play);
		btnPause = findViewById(R.id.btn_pause);
		tvPlayState = findViewById(R.id.tv_play_state);

		timeBar1.setMaxDuration(maxDuration);
		timeBar2.setMaxDuration(maxDuration);
		timeBar3.setMaxDuration(maxDuration);
		timeBar4.setMaxDuration(maxDuration);
		timeBar5.setMaxDuration(maxDuration);
		timeBar6.setMaxDuration(maxDuration);
		timeBar7.setMaxDuration(maxDuration);

		timeBar1.setSeekListener(this);
		timeBar2.setSeekListener(this);
		timeBar3.setSeekListener(this);
		timeBar4.setSeekListener(this);
		timeBar5.setSeekListener(this);
		timeBar6.setSeekListener(this);
		timeBar7.setSeekListener(this);

		btnPlay.setOnClickListener(this);
		btnPause.setOnClickListener(this);

		timeHandler = new TimeHandler(this);

		timeHandler.sendEmptyMessageDelayed(UPDATE_DURATION, 1000);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		if (timeHandler != null) {
			timeHandler.removeMessages(UPDATE_DURATION);
			timeHandler.reference.clear();
			timeHandler = null;
		}
		timeBar1 = null;
		timeBar2 = null;
		timeBar3 = null;
		timeBar4 = null;
		timeBar5 = null;
		timeBar6 = null;
		timeBar7 = null;
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
}
