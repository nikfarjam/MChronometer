package com.mehdinf.chronometer;

import java.io.Serializable;

public class ChronometerState implements Serializable {

	private static final long serialVersionUID = 170882475232709501L;

	private final static int STOP = 0;
	private final static int START = 1;
	private final static int SUSPENDED = 2;

	private int state;
	private long startTime;
	private long lastDuration;
	private boolean hasStarted;

	public ChronometerState() {
		super();
		reset();
	}

	public void reset() {
		state = STOP;
		hasStarted = false;
		startTime = 0L;
	}

	public synchronized boolean isWorking() {
		return state == START;
	}

	public synchronized boolean isPaused() {
		return state == SUSPENDED;
	}

	public synchronized boolean isStoped() {
		return state == STOP;
	}

	public synchronized void startWorking() {
		state = START;
	}

	public synchronized void pauseWorking() {
		state = SUSPENDED;
	}

	public synchronized void stopWorking() {
		state = STOP;
	}

	public long getLastDuration() {
		return lastDuration;
	}

	public void setLastDuration(long lastDuration) {
		this.lastDuration = lastDuration;
	}

	public boolean isStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
