package com.mehdinf.chronometer;

import com.mehdinf.sample.R;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.Log;

public class ChronometerActivity extends Activity implements IChronometer {

	// UI elements
	private Button btnStart;
	private Button btnPause;
	private TextView txtTime;

	// state variables
	private int state;
	private long startTime;
	private long lastDuration;
	private boolean hasStarted;

	// threads objects
	private Handler showTimeHandler;
	private Runnable showTimeThread;
	private AsyncTask<String, Void, Void> timerTask;

	// constants
	private final static int STOP = 0;
	private final static int START = 1;
	private final static int SUSPENDED = 2;
	private final static long DELAY = 200;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// get UI elements
		btnPause = (Button) findViewById(R.id.btn_pause);
		btnStart = (Button) findViewById(R.id.btn_start);
		txtTime = (TextView) findViewById(R.id.txt_time);

		if (savedInstanceState != null) {
			// restore state
			state = (Integer) savedInstanceState.get("state");
			startTime = (Long) savedInstanceState.get("startTime");
			lastDuration = (Long) savedInstanceState.get("lastWorkingTime");
			startTime = System.currentTimeMillis() - lastDuration;
			showCurrentTime();
			if (state == START) {
				startTimer();
			}
		} else {
			// init state
			hasStarted = false;
			state = STOP;
			txtTime.setText("00:00:00:000");
		}

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

	}

	/*
	 * show the current time on the label
	 */
	private void showCurrentTime() {
		long duration = System.currentTimeMillis() - startTime;
		txtTime.setText(formatTime(duration));
	}

	/*
	 * start running
	 */
	private void startTimer() {
		startTime = System.currentTimeMillis();
		state = START;
		btnStart.setText(getString(R.string.stop));
		btnPause.setEnabled(true);
		startThreads();
		Log.w("start at ", "" + timerTask);
	}

	/*
	 * pause the chronometer
	 */
	private void pauseTimer() {
		lastDuration = System.currentTimeMillis() - startTime;
		state = SUSPENDED;
		btnPause.setText(getString(R.string.resume));
		showCurrentTime();
	}

	/*
	 * start again
	 */
	private void resumeTimer() {
		startTime = System.currentTimeMillis() - lastDuration;
		state = START;
		btnStart.setText(getString(R.string.stop));
		btnPause.setEnabled(true);
		startThreads();
	}

	/*
	 * stop working
	 */
	private void stopTimer() {
		state = STOP;
		if (isWorking()) {
			showCurrentTime();
		}
		btnStart.setText(getString(R.string.start));
		btnPause.setText(getString(R.string.pause));
		btnPause.setEnabled(false);
		Log.w("stop at ", "" + timerTask);
	}

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
		if (hasStarted) {
			timerTask = new TimerTask(this);
			timerTask.execute(new String[] { "" });
			return;
		}
		hasStarted = true;
		timerTask = new TimerTask(this);
		timerTask.execute(new String[] { "" });
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("state", state);
		outState.putLong("startTime", startTime);
		outState.putLong("lastWorkingTime", lastDuration);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		state = (Integer) savedInstanceState.get("state");
		startTime = (Long) savedInstanceState.get("startTime");
		lastDuration = (Long) savedInstanceState.get("lastWorkingTime");
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
		long milli = duration % 1000;
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

	public boolean isWorking() {
		return state == START;
	}

	public boolean isPaused() {
		return state == SUSPENDED;
	}

	public boolean isStoped() {
		return state == STOP;
	}

	public long getDelay() {
		return DELAY;
	}

	/*
	 * A thread which updates the UI every DELAY mili seconds
	 */
	/*
	 * private class TimerTask extends AsyncTask<String, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(String... params) { while
	 * (isWorking()) { try { Thread.sleep(getDelay()); } catch (Exception e) { }
	 * // update UI updateTimeTxt(); } return null; }
	 * 
	 * }
	 */
}