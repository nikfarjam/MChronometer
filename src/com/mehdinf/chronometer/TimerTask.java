package com.mehdinf.chronometer;

import android.os.AsyncTask;

public class TimerTask extends AsyncTask<String, Void, Void> {

	IChronometer chronometer;

	public TimerTask(IChronometer chronometer) {
		super();
		this.chronometer = chronometer;
	}

	@Override
	protected Void doInBackground(String... params) {
		while (chronometer.isWorking()) {
			try {
				Thread.sleep(chronometer.getDelay());
			} catch (Exception e) {
			}
			// update UI
			chronometer.updateTimeTxt();
		}
		return null;
	}

}
