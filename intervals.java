package project3;

import java.text.*;
import java.util.*;

/**
 * @author tyler spring
 *  This is the intervals class. All it really does it
 *         create an object used to track the current time and assign time
 *         stamps for whatever thread it is currently on.
 *
 */
public class intervals implements Runnable {
	// Boolean created in order to check if the simulation is running. Then creates
	// objects for current time and time stamps.
	private final boolean appRun;
	private final String timeSlots = "hh:mm a";
	private final SimpleDateFormat timesTimed = new SimpleDateFormat(timeSlots);
	Date day = new Date(System.currentTimeMillis());

	public intervals() {
		this.appRun = Thread.currentThread().isAlive();
	}

	public String timer() {
		day = new Date(System.currentTimeMillis());
		return timesTimed.format(day);
	}

	// Overridden run method used to set time and passing back timer to be displayed
	// in GUI.
	@Override
	public void run() {
		while (appRun) {
			mainGUI.timer.setText(timer());
		}

	}

}
