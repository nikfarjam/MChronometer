package com.mehdinf.chronometer;

import com.mehdinf.sample.R;

import android.app.*;
import android.graphics.Typeface;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.Log;

public class ChronometerActivity extends Activity implements IChronometer {

	// UI elements
	private Button btnStart;
	private Button btnPause;
	private Button btnReset;
	private TextView txtTime;
	private TextView txtMili;

	private ChronometerState state;
	private long lastBackPress;

	// threads objects
	private Handler showTimeHandler;
	private Runnable showTimeThread;
	private AsyncTask<String, Void, Void> timerTask;

	// constants
	private final static long BACK_DELAY = 2000;
	private final static long SLOW_DELAY = 500;
	private final static long NORMAL_DELAY = 100;
	private final static long FAST_DELAY = 20;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// get UI elements
		btnPause = (Button) findViewById(R.id.btn_pause);
		btnStart = (Button) findViewById(R.id.btn_start);
		btnReset = (Button) findViewById(R.id.btn_reset);
		txtTime = (TextView) findViewById(R.id.txt_time);
		txtMili = (TextView) findViewById(R.id.txt_mili);

		Typeface cssFont = Typeface.createFromAsset(getAssets(),
				"fontawesome-webfont.ttf");
		btnPause.setTypeface(cssFont);
		btnStart.setTypeface(cssFont);
		btnReset.setTypeface(cssFont);

		// init state
		state = new ChronometerState();
		state.setDelay(NORMAL_DELAY);
		reset();
		lastBackPress = 0;

		btnStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (isWorking() || isPaused()) {
					// stop working
					stopTimer();
				} else if (isStoped()) {
					// start working
					startTimer();
				}
			}
		});

		btnPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isWorking()) {
					pauseTimer();
				} else if (isPaused()) {
					resumeTimer();
					btnPause.setText(getString(R.string.pause));
				}
			}
		});

		btnReset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				reset();
			}
		});
	}

	/*
	 * show the current time on the label
	 */
	private void showCurrentTime() {
		if (state.getStartTime() > 0) {
			long duration = System.currentTimeMillis() - state.getStartTime();
			txtTime.setText(formatTime(duration));
			txtMili.setText(formatMiliSeconds(duration));
		}
	}

	/*
	 * start running
	 */
	private void startTimer() {
		state.setStartTime(System.currentTimeMillis());
		state.startWorking();
		setStartButton();
		startThreads();
		Log.w("start at ", "" + timerTask);
	}

	/*
	 * pause the chronometer
	 */
	private void pauseTimer() {
		state.setLastDuration(System.currentTimeMillis() - state.getStartTime());
		state.pauseWorking();
		btnPause.setText(getString(R.string.resume));
		showCurrentTime();
	}

	/*
	 * start again
	 */
	private void resumeTimer() {
		state.setStartTime(System.currentTimeMillis() - state.getLastDuration());
		state.startWorking();
		setStartButton();
		startThreads();
	}

	/*
	 * stop working
	 */
	private void stopTimer() {
		state.stopWorking();
		if (isWorking()) {
			showCurrentTime();
		}
		setStopButton();
		Log.w("stop at ", "" + timerTask);
	}

	private void reset() {
		state.reset();
		setStopButton();
		txtTime.setText("00:00:00.");
		txtMili.setText("000");
	}

	private void setStartButton() {
		btnStart.setText(getString(R.string.stop));
		btnPause.setEnabled(true);
		btnPause.setText(getString(R.string.pause));
	}

	private void setStopButton() {
		btnStart.setText(getString(R.string.start));
		btnPause.setText(getString(R.string.pause));
		btnPause.setEnabled(false);
	}

	@Override
	public void updateTimeTxt() {
		if (showTimeHandler == null) {
			showTimeHandler = new Handler(getApplicationContext()
					.getMainLooper());
		}
		if (showTimeThread == null) {
			showTimeThread = new Runnable() {
				public void run() {
					showCurrentTime();
				}
			};
		}
		showTimeHandler.post(showTimeThread);
	}

	/*
	 * start UI threads
	 */
	private void startThreads() {
		if (state.isStarted()) {
			timerTask = new TimerTask(this);
			timerTask.execute(new String[] { "" });
			return;
		}
		state.setHasStarted(true);
		timerTask = new TimerTask(this);
		timerTask.execute(new String[] { "" });
	}

	@Override
	public void onBackPressed() {
		if (lastBackPress + BACK_DELAY < System.currentTimeMillis()) {
			stopTimer();
			reset();
			super.onBackPressed();
		} else {
			finish();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("state", state);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		state = (ChronometerState) savedInstanceState.get("state");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.setting_option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		/*
		MenuItem slowItem = menu.findItem(R.id.menu_slow);
		MenuItem normalItem = menu.findItem(R.id.menu_normal);
		MenuItem fastItem = menu.findItem(R.id.menu_fast);
		
		if (state.getDelay() == SLOW_DELAY) {
			Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
			slowItem.setChecked(true);
			normalItem.setChecked(false);
			fastItem.setChecked(false);
		} else if (state.getDelay() == NORMAL_DELAY) {
			Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
			slowItem.setChecked(false);
			normalItem.setChecked(true);
			fastItem.setChecked(false);
		} else if (state.getDelay() == FAST_DELAY) {
			Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
			slowItem.setChecked(false);
			normalItem.setChecked(false);
			fastItem.setChecked(true);
		}
		*/
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			Toast.makeText(this, getString(R.string.about_comment),
					Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_slow:
			if (!item.isChecked()) {
				item.setChecked(true);
				state.setDelay(SLOW_DELAY);
			}
			break;
		case R.id.menu_normal:
			if (!item.isChecked()) {
				item.setChecked(true);
				state.setDelay(NORMAL_DELAY);
			}
			break;
		case R.id.menu_fast:
			if (!item.isChecked()) {
				item.setChecked(true);
				state.setDelay(FAST_DELAY);
			}
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * convert milli seconds to human readable time
	 */
	private String formatTime(long duration) {
		StringBuffer temp = new StringBuffer();
		long hour = duration / (60 * 60 * 1000);
		duration = duration % (60 * 60 * 1000);
		long min = duration / (60 * 1000);
		duration = duration % (60 * 1000);
		long sec = duration / (1000);
		if (hour / 10 == 0) {
			temp.append("0");
		}
		temp.append(hour);
		temp.append(":");
		if (min / 10 == 0) {
			temp.append("0");
		}
		temp.append(min);
		temp.append(":");
		if (sec / 10 == 0) {
			temp.append("0");
		}
		temp.append(sec);
		temp.append(".");
		return temp.toString();
	}

	private String formatMiliSeconds(long duration) {
		StringBuffer temp = new StringBuffer();
		long milli = duration % 1000;
		if (milli == 0) {
			temp.append("000");
		} else {
			if (milli / 10 == 0) {
				temp.append("00");
			} else if (milli / 100 == 0) {
				temp.append("0");
			}
			temp.append(milli);
		}
		return temp.toString();
	}

	@Override
	public boolean isWorking() {
		return state.isWorking();
	}

	public boolean isPaused() {
		return state.isPaused();
	}

	public boolean isStoped() {
		return state.isStoped();
	}

	@Override
	public long getDelay() {
		return state.getDelay();
	}
}
