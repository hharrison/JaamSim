/*
 * JaamSim Discrete Event Simulation
 * Copyright (C) 2002-2011 Ausenco Engineering Canada Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.sandwell.JavaSimulation;

/**
 * Holder class for event data used by the event monitor to schedule future
 * events.
 */
class Event {
	final long addedTick; // The tick at which this event was queued to execute
	final long schedTick; // The tick at which this event will execute
	final int priority;   // The schedule priority of this event

	final Process process;
	final Entity caller;

	/**
	 * Constructs a new event object.
	 * @param currentTick the current simulation tick
	 * @param scheduleTick the simulation tick the event is schedule for
	 * @param prio the event priority for scheduling purposes
	 * @param caller
	 * @param process
	 */
	Event(long currentTick, long scheduleTick, int prio, Entity caller, Process process) {
		addedTick = currentTick;
		schedTick = scheduleTick;
		priority = prio;

		this.process = process;
		this.caller = caller;
	}

	public String getClassMethod() {
		StackTraceElement[] callStack = process.getStackTrace();

		for (int i = 0; i < callStack.length; i++) {
			if (callStack[i].getClassName().equals("com.sandwell.JavaSimulation.Entity")) {
				return String.format("%s.%s", caller.getClass().getSimpleName(), callStack[i + 1].getMethodName());
			}
		}

		// Possible the process hasn't started running yet, check the Process target
		// state
		return process.getClassMethod();
	}

	public String getFileLine() {
		StackTraceElement[] callStack = process.getStackTrace();

		for (int i = 0; i < callStack.length; i++) {
			if (callStack[i].getClassName().equals("com.sandwell.JavaSimulation.Entity")) {
				return String.format("%s:%s", callStack[i + 1].getFileName(), callStack[i + 1].getLineNumber());
			}
		}
		return "Unknown method state";
	}

	public String[] getData(int state) {
		String[] data = new String[10];

		data[0] = String.format("%15d", schedTick);
		data[1] = String.format("%15.3f", schedTick / Process.getSimTimeFactor());
		data[2] = String.format("%5d", priority);
		data[3] = String.format("%s", caller.getName());
		data[4] = String.format("%s", caller.getInputName());
		data[5] = String.format("%s", "");
		data[6] = getClassMethod();
		data[7] = getFileLine();
		data[8] = String.format("%15.3f", addedTick / Process.getSimTimeFactor());
		data[9] = "Unknown";

		switch (state) {
		case EventManager.STATE_WAITING:
			data[9] = "Waiting";
			break;
		case EventManager.STATE_EXITED:
			data[9] = "Ran Normally";
			break;
		case EventManager.STATE_INTERRUPTED:
			data[9] = "Interrupted";
			break;
		case EventManager.STATE_TERMINATED:
			data[9] = "Terminated";
			break;
		}

		return data;
	}
}
