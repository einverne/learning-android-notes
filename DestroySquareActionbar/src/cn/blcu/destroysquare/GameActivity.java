package cn.blcu.destroysquare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import cn.domob.android.ads.DomobAdManager.ErrorCode;
import cn.domob.android.ads.DomobInterstitialAd;
import cn.domob.android.ads.DomobInterstitialAdListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class GameActivity extends SherlockActivity {
	private static final String TAG = "EV_DEBUG";
	SharedPreferences.Editor editor;
	SquareView myView;
	int refreshChance = 2;
	SharedPreferences sharedPreferences;
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	private long start;
	private long end;

	DomobInterstitialAd domobInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		myView = new SquareView(GameActivity.this);
		setContentView(myView);

		ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		domobInterstitialAd = new DomobInterstitialAd(this,
				MainActivity.PUBLISHER_ID, MainActivity.InterstitialPPID,
				DomobInterstitialAd.INTERSITIAL_SIZE_600X500);

		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_bar);
		sharedPreferences = this.getSharedPreferences("flag",
				Context.MODE_PRIVATE);
		// if (sharedPreferences.getBoolean("bgflag", true)) {
		// playMusic = new SoundPlayer(this);
		// playMusic.playBgSound(R.raw.chuyin);
		// }
		// Get the GoogleAnalytics singleton. Note that the SDK uses
		// the application context to avoid leaking the current context.
		mGaInstance = GoogleAnalytics.getInstance(this);

		// Use the GoogleAnalytics singleton to get a Tracker.
		mGaTracker = mGaInstance.getTracker("UA-40648258-1"); // Placeholder
																// tracking ID.

		domobInterstitialAd
				.setInterstitialAdListener(new DomobInterstitialAdListener() {

					@Override
					public void onLandingPageOpen() {
						Log.i("DomobSDKDemo", "onLandingPageOpen");
					}

					@Override
					public void onLandingPageClose() {
						Log.i("DomobSDKDemo", "onLandingPageClose");
					}

					@Override
					public void onInterstitialAdReady() {
						Log.i("DomobSDKDemo", "onAdReady");
					}

					@Override
					public void onInterstitialAdPresent() {
						Log.i("DomobSDKDemo", "onInterstitialAdPresent");
					}

					@Override
					public void onInterstitialAdLeaveApplication() {
						Log.i("DomobSDKDemo",
								"onInterstitialAdLeaveApplication");
					}

					@Override
					public void onInterstitialAdFailed(ErrorCode arg0) {
						Log.i("DomobSDKDemo", "onInterstitialAdFailed");
					}

					@Override
					public void onInterstitialAdDismiss() {
						domobInterstitialAd.loadInterstitialAd();
						Log.i("DomobSDKDemo", "onInterstitialAdDismiss");
					}
				});

		domobInterstitialAd.loadInterstitialAd();

		// findViewById(R.id.button_pause).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// myView.pause();
		// }
		// });
		// refresh = (Button) findViewById(R.id.button_refresh);
		// refresh.setText(refreshChance + "置换");
		// refresh.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// myView.refresh();
		// refreshChance--;
		// setRefreshChance();
		// }
		// });

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/**
		 * 增加中间过渡特效
		 */
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			if (myView.playMusic != null) {
				myView.playMusic.pauseBgSound();
			}
			myView.pause();
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (myView.playMusic != null) {
				myView.playMusic.stopBgSound();
			}
			myView.timer.cancel();
			myView.timertask.cancel();
			finish();
			this.overridePendingTransition(R.anim.in_from_left,
					R.anim.out_to_right);// 参数:读取res中的XML文件实现效果
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Add this method.
		// Send a screen view when the Activity is displayed to the user.
		mGaTracker.sendView("/GameActivity");
		start = System.currentTimeMillis();
		Log.d(TAG, "start:" + start);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method.
		end = System.currentTimeMillis();
		Log.d(TAG, "end:" + end);
		long gameTime = end - start;
		mGaTracker.sendTiming("ui_time", gameTime, "game_time", "gameview");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (myView.playMusic != null) {
				myView.playMusic.stopBgSound();
			}
			myView.timer.cancel();
			myView.timertask.cancel();
			finish();
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.pause:
			myView.pause();
			if (domobInterstitialAd.isInterstitialAdReady()) {
				domobInterstitialAd.showInterstitialAd(this);
			} else {
				Log.i("DomobSDKDemo", "Interstitial Ad is not ready");
				domobInterstitialAd.loadInterstitialAd();
			}
			break;
		case R.id.refresh:
			if (refreshChance > 0) {
				myView.refresh();
				refreshChance--;
				item.setTitle("置换:" + refreshChance);
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
