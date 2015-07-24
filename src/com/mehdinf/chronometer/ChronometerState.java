package com.mehdinf.chronometer;

import java.io.Serializable;

public class ChronometerState implements Serializable{

	private static final long serialVersionUID = 170882475232709501L;
	
	private final static int STOP = 0;
	private final static int START = 1;
	private final static int SUSPENDED = 2;
	
	private int state;
	
	public ChronometerState() {
		super();
		state = STOP;
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
	
	public void startWorking() {
		state = START;
	}
	
	public void pauseWorking() {
		state = SUSPENDED;
	}

	public void stopWorking() {
		state = STOP;
	}
}
