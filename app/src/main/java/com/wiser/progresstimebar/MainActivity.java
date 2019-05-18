package com.wiser.progresstimebar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wiser.timebar.ProgressTimeBar;

import java.lang.ref.WeakReference;

/**
 * @author Wiser
 */
public class MainActivity extends AppCompatActivity implements ProgressTimeBar.SeekListener {

    private long maxDuration = 60000;//最大时间

    private long currentDuration = 0;//当前时间

    private long bufferDuration = 0;//缓冲时间

    private ProgressTimeBar timeBar;

    private TimeHandler timeHandler;

    private final int UPDATE_DURATION = 111;

    @Override
    public void seekToDuration(long duration) {
        this.currentDuration = duration;
        this.bufferDuration = duration;
        timeBar.setCurrentDuration(currentDuration);
        timeBar.setBufferDuration(bufferDuration);
        timeHandler.removeMessages(UPDATE_DURATION);
        timeHandler.sendEmptyMessage(UPDATE_DURATION);
    }

    private static class TimeHandler extends Handler {

        WeakReference<MainActivity> reference;

        TimeHandler(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference != null && reference.get() != null) {
                if (msg.what == reference.get().UPDATE_DURATION) {
                    reference.get().currentDuration += 1000;
                    if (reference.get().currentDuration >= reference.get().maxDuration) {
                        reference.get().currentDuration = reference.get().maxDuration;
                        reference.get().timeBar.setCurrentDuration(reference.get().currentDuration);
                    }
                    if (reference.get().currentDuration <= 0) {
                        reference.get().currentDuration = 0;
                        reference.get().timeBar.setCurrentDuration(reference.get().currentDuration);
                    }
                    reference.get().bufferDuration += 1500;
                    if (reference.get().bufferDuration >= reference.get().maxDuration) {
                        reference.get().bufferDuration = reference.get().maxDuration;
                        reference.get().timeBar.setBufferDuration(reference.get().bufferDuration);
                    }
                    if (reference.get().bufferDuration <= 0) {
                        reference.get().bufferDuration = 0;
                        reference.get().timeBar.setBufferDuration(reference.get().bufferDuration);
                    }
                    if (reference.get().currentDuration == reference.get().maxDuration && reference.get().bufferDuration == reference.get().maxDuration) {
                        Toast.makeText(reference.get().getApplicationContext(), "播放结束", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reference.get().timeBar.setBufferDuration(reference.get().bufferDuration);
                    reference.get().timeBar.setCurrentDuration(reference.get().currentDuration);
                    reference.get().timeHandler.sendEmptyMessageDelayed(reference.get().UPDATE_DURATION, 1000);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeBar = findViewById(R.id.timeBar);

        timeBar.setMaxDuration(maxDuration);

        timeHandler = new TimeHandler(this);

        timeBar.setSeekListener(this);

        timeHandler.sendEmptyMessageDelayed(UPDATE_DURATION, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeHandler != null) {
            timeHandler.removeMessages(UPDATE_DURATION);
            timeHandler.reference.clear();
            timeHandler = null;
        }
        timeBar = null;
    }
}
