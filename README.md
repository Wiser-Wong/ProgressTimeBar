# ProgressTimeBar
万能进度条ProgressTimeBar

## 环境配置
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
    dependencies {
	        implementation 'com.github.Wiser-Wong:ProgressTimeBar:1.1'
	}


## 使用控件

    设置最大时间
    timeBar.setMaxDuration(maxDuration);
    
    更新你的当前播放时间以及缓冲时间
    timeBar.setCurrentDuration(currentDuration);
    timeBar.setBufferDuration(bufferDuration);
    
    设置拖动bar进度监听
    timeBar.setSeekListener(this);
	
	
        <com.wiser.timebar.ProgressTimeBar
            android:id="@+id/timeBar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#000000"
            android:padding="15dp"
            app:progressBarColor="#ffff00"
            app:progressBarHeight="20dp"
            app:progressBarShadowColor="@color/colorAccent"
            app:progressBarShadowPadding="10dp"
            app:progressBufferCenterColor="#ffffff"
            app:progressBufferColor="#CCCCCC"
            app:progressBufferEndColor="#555555"
            app:progressBufferSrc="@drawable/progress_buffer_play_bg"
            app:progressBufferStartColor="#9932CC"
            app:progressHeight="5dp"
            app:progressIsBarShadow="true"
            app:progressIsHasBar="true"
            app:progressIsHasBuffer="true"
            app:progressPlayCenterColor="#E0E0E0"
            app:progressPlayColor="#00ff00"
            app:progressPlayEndColor="#D2691E"
            app:progressPlaySrc="@drawable/progress_buffer_play_bg"
            app:progressPlayStartColor="#0000EE"
            app:progressRoundRadius="10dp"
            app:progressTimeColor="@color/colorPrimary"
            app:progressTimeModel="HAS_TIME"
            app:progressTimePadding="30dp"
            app:progressTimeTextSize="15sp"
            app:progressBarSrc="@mipmap/play"
            app:progressUnPlayCenterColor="@color/colorPrimary"
            app:progressUnPlayColor="#00ffff"
            app:progressUnPlayEndColor="#ffff00"
            app:progressUnPlaySrc="@drawable/progress_unplay_bg"
            app:progressUnPlayStartColor="@color/colorAccent" />



## 截图
![images](https://github.com/Wiser-Wong/ProgressTimeBar/blob/master/images/timebar.gif)
