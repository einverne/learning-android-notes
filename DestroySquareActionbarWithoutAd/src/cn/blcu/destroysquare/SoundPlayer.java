package cn.blcu.destroysquare;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer implements MediaPlayer.OnCompletionListener,
		MediaPlayer.OnErrorListener {

	private MediaPlayer bgPlayer;
	private Context context;

	public SoundPlayer(Context context) {
		this.context = context;
	}

	// 当音乐播放完毕时
	@Override
	public void onCompletion(MediaPlayer mp) {
		stopBgSound();
	}

	// 当音乐播放发生错误时
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		stopBgSound();
		return false;
	}

	// 播放背景音乐，传入的paramInt为res/raw/目录下的音频文件的引用
	public void playBgSound(int paramInt) {
		stopBgSound();

		try {
			// 利用音频文件创建一个MeidaPlayer

			bgPlayer = MediaPlayer.create(context, paramInt);
			bgPlayer.setOnCompletionListener(this);
			// 设置是否循环播放
			bgPlayer.setLooping(true);
			// 开始播放
			bgPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	// 停止播放背景音乐
	public void stopBgSound() {
		if (bgPlayer == null)
			return;
		if (bgPlayer.isPlaying())
			bgPlayer.stop();
		bgPlayer.release();
		bgPlayer = null;
	}

	public void pauseBgSound() {
		bgPlayer.pause();
	}

	public void startAgainBgSound() {
		bgPlayer.start();
	}
}