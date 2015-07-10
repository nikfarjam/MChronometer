package com.mehdinf.chronometer;

import com.mehdinf.sample.R;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.Log;

public class MainActivity extends Activity {
	private Button btnStart;
	private Button btnPause;
	private TextView txtTimer;

	private int state;
	private long startTime;
	private long lastDuration;
	
	private boolean hasStarted;
	private Handler showTimeHandler;
	private Runnable showTimeThread;
	private AsyncTask<String, Void, Void> timerTask;

	private final static int STOP = 0;
	private final static int START = 1;
	private final static int SUSPENDED = 2;
	private final static long DELAY = 200;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		state = STOP;
		hasStarted = false;
		btnPause = (Button) findViewById(R.id.pause_btn);
		btnStart = (Button) findViewById(R.id.start_btn);
		txtTimer = (TextView) findViewById(R.id.time_txt);
		if (savedInstanceState != null) {
			state = (Integer) savedInstanceState.get("state");
			startTime = (Long) savedInstanceState.get("startTime");
			lastDuration = (Long) savedInstanceState.get("lastWorkingTime");
			startTime = System.currentTimeMillis() - lastDuration;
			showCurrentTime();
			if (state == START) {
				doStart();
			}
		} else {
			txtTimer.setText("00:00:00:000");
		}
		
		btnStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (state == START || state == SUSPENDED) {
					state = STOP;
					showCurrentTime();
					doStop();
					return;
				} else if (state == STOP) {
					// start working
					startTime = System.currentTimeMillis();
					state = START;
					doStart();
					return;
				}
			}
		});

		btnPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (state == START) {
					lastDuration = System.currentTimeMillis() - startTime;
					state = SUSPENDED;
					btnPause.setText(getString(R.string.resume));
					showCurrentTime();
					return;
				} else if (state == SUSPENDED) {
					startTime = System.currentTimeMillis() - lastDuration;
					state = START;
					doStart();
					btnPause.setText(getString(R.string.pause));
					return;
				}
			}
		});

	}

	private void showCurrentTime() {
		long duration = System.currentTimeMillis() - startTime;
		txtTimer.setText(formatTime(duration));
	}

	private void doStart() {
		Log.w("start at ", "" + timerTask);
		btnStart.setText(getString(R.string.stop));
		btnPause.setEnabled(true);
		if (hasStarted) {
			timerTask.cancel(true);
			timerTask = new TimerTask();
			timerTask.execute(new String[] { "" });
			return;
		}
		hasStarted = true;
		showTimeHandler = new Handler(getApplicationContext().getMainLooper());
		showTimeThread = new Runnable() {
			public void run() {
				showCurrentTime();
			}
		};
		timerTask = new TimerTask();
		timerTask.execute(new String[] { "" });
	}

	private void doStop() {
		Log.w("stop at ", "" + timerTask);
		btnStart.setText(getString(R.string.start));
		btnPause.setText(getString(R.string.pause));
		btnPause.setEnabled(false);
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

	private class TimerTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			while (state == START) {
				try {
					Thread.sleep(DELAY);
				} catch (Exception e) {
				}
				showTimeHandler.post(showTimeThread);
			}
			return null;
		}

	}
}
