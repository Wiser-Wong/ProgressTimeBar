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
	        implementation 'com.github.Wiser-Wong:ProgressTimeBar:1.3'
	}


## 使用控件

  * ProgressTimeBar
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
            app:progressTimeMode="HAS_TIME"
            app:progressTimePadding="30dp"
            app:progressTimeTextSize="15sp"
            app:progressBarSrc="@mipmap/play"
            app:progressUnPlayCenterColor="@color/colorPrimary"
            app:progressUnPlayColor="#00ffff"
            app:progressUnPlayEndColor="#ffff00"
            app:progressUnPlaySrc="@drawable/progress_unplay_bg"
            app:progressUnPlayStartColor="@color/colorAccent" />

## 操作手册

* progressBarSrc：进度条拖动bar图片Drawable或mipmap下图片资源
* progressBarColor:进度条拖动bar颜色
* progressBarHeight：进度条拖动bar高度
* progressBarShadowColor：进度条拖动bar阴影颜色
* progressBarShadowPadding：进度条拖动bar阴影与拖动bar间距离
* progressBufferColor：进度条缓冲颜色
* progressBufferSrc：进度条缓冲图片Drawable或者mipmap下图片资源
* progressBufferStartColor：进度条缓冲渐变颜色开始颜色值
* progressBufferCenterColor：进度条缓冲渐变颜色中间颜色值
* progressBufferEndColor：进度条缓冲渐变颜色结尾颜色值
* 进度条缓冲渐变颜色值可设置一种、两种以及三种颜色值progressBufferStartColor、progressBufferCenterColor、progressBufferEndColor可以不同时设置
* progressHeight：进度条高度
* progressIsBarShadow：进度条是否显示拖动bar阴影
* progressIsHasBar：进度条是否有拖动bar
* progressIsHasBuffer：进度条是否有缓存进度
* progressPlayColor：进度条播放中进度颜色
* progressPlaySrc：进度条播放中进度图片Drawable或者mipmap下图片
* progressPlayStartColor：进度条播放中进度渐变开始颜色值
* progressPlayCenterColor：进度条播放中进度渐变中间颜色值
* progressPlayEndColor：进度条播放中进度渐变结尾颜色值
* 进度条播放渐变颜色值可设置一种、progressPlayStartColor、progressPlayCenterColor、progressPlayEndColor可以不同时设置
* progressRoundRadius：进度条弧度半径
* progressTimeColor：进度条左右时间颜色
* progressTimeMode：进度条是否有左右时间显示
* progressTimePadding：进度条左右时间与进度条间距
* progressTimeTextSize：进度条左右时间文本大小
* progressUnPlayColor：进度条未播放进度颜色
* progressUnPlaySrc：进度条未播放进度图片Drawable或mipmap下图片资源
* progressUnPlayStartColor：进度条未播放渐变颜色开始颜色值
* progressUnPlayCenterColor：进度条未播放渐变颜色中间颜色值
* progressUnPlayEndColor：进度条未播放渐变颜色值结尾颜色值
* 进度条未播放渐变颜色值可设置一种、progressUnPlayStartColor、progressUnPlayCenterColor、progressUnPlayEndColor可以不同时设置

## 截图
![images](https://github.com/Wiser-Wong/ProgressTimeBar/blob/master/images/timebar.gif)
